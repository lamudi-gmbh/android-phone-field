package tech.zekon.gui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class AppRate {
    private static boolean AlreadyRan = false;

    public static void Start(Activity mContext, int DaysUntilPrompt, int LaunchesUntilPrompt) {
        if (AlreadyRan) return;
        AlreadyRan = true;
        SharedPreferences prefs = mContext.getSharedPreferences("AppRate", 0);
        if (prefs.getBoolean("dontshowagain", false)) return;

        SharedPreferences.Editor editor = prefs.edit();

        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        editor.apply();

        if (launch_count >= LaunchesUntilPrompt || System.currentTimeMillis() >= date_firstLaunch + ((long) DaysUntilPrompt * 24 * 60 * 60 * 1000)) {
            //showRateDialog(mContext, theme, AppTitle, PackageName);
            defaultRateDialog(mContext);
        }
    }

    public static void Start(Activity mContext) {
        ReviewManager manager = ReviewManagerFactory.create(mContext);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> launch = manager.launchReviewFlow(mContext, reviewInfo);
                launch.addOnCompleteListener(task1 -> {});
            }
        });
    }

    public static void defaultRateDialog(final Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("AppRate", 0);
        SharedPreferences.Editor editor = prefs.edit();

        ReviewManager manager = ReviewManagerFactory.create(mContext);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> launch = manager.launchReviewFlow(mContext, reviewInfo);
                launch.addOnCompleteListener(task1 -> {
                    editor.putBoolean("dontshowagain", true);
                    editor.apply();
                });
            } else {
                //Reset all details
                editor.putLong("launch_count", 1);
                long date_firstLaunch = System.currentTimeMillis();
                editor.putLong("date_firstlaunch", date_firstLaunch);
                editor.apply();
            }
        });
    }

    public static void showRateDialog(final Activity mContext, int theme, String AppTitle, String PackageName) {
        SharedPreferences prefs = mContext.getSharedPreferences("AppRate", 0);
        SharedPreferences.Editor editor = prefs.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, theme);
        builder.setCancelable(false);
        builder.setMessage("If you enjoy using this app please take a moment to rate it. Thanks for your support!")
                .setPositiveButton("Rate", (dialog, id) -> {
                    try {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PackageName)));
                    } catch (Exception ignored) {
                        Snackbar snack;
                        snack = Snackbar.make(mContext.findViewById(android.R.id.content), "No appstore found!", Snackbar.LENGTH_SHORT);
                        snack.setTextColor(Color.WHITE);
                        snack.setBackgroundTint(Color.rgb(0xf0, 0, 0x37));
                        snack.setBackgroundTintMode(PorterDuff.Mode.SRC);
                        snack.show();
                    }
                    editor.putBoolean("dontshowagain", true);
                    editor.apply();
                    dialog.dismiss();
                })
                .setNeutralButton("Later", (dialog, which) -> {
                    //Reset all details
                    editor.putLong("launch_count", 1);
                    long date_firstLaunch = System.currentTimeMillis();
                    editor.putLong("date_firstlaunch", date_firstLaunch);
                    editor.apply();
                    dialog.dismiss();
                })
                .setNegativeButton("No thanks", (dialog, id) -> {
                    editor.putBoolean("dontshowagain", true);
                    editor.apply();
                    dialog.dismiss();
                });
        builder.create().show();
    }
}
