package tech.zekon.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FractionButton  extends ConstraintLayout implements View.OnClickListener {
    private TextView mText, mNumerator, mDenominator;
    private Button button;
    private boolean isCheckbox = false;
    private String text, Numerator, Denominator;
    private boolean isChecked = false;
    private boolean showHelpButton = false;
    private OnClickListener _wrappedOnClickListener;
    private OnClickListener _wrappedOnClickHelp = null;
    private int overlapPercent;
    /** Background tint when checked; defaults to the historical tomato so existing apps are unchanged. */
    private int checkedColor = Color.rgb(255, 99, 71);

    public FractionButton(Context context) {
        this(context, null);
    }

    public FractionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FractionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), getLayoutResId(), this);
        updateLayoutAttributes();
        prepareView(attrs);
    }

    protected void updateLayoutAttributes() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //setGravity(Gravity.TOP);
        //setOrientation(HORIZONTAL);
    }

    public int getLayoutResId() {
        return R.layout.fraction_button;
    }

    @SuppressLint("ResourceType")
    protected void prepareView(AttributeSet attrSet) {
        mText = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_integer));
        mNumerator = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_numerator));
        mDenominator = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_denominator));
        button = findViewWithTag("Frac_button");
        button.setOnClickListener(view -> {
            if (_wrappedOnClickHelp != null) _wrappedOnClickHelp.onClick(view);
        });
        if (mText == null || mNumerator == null || mDenominator == null) {
            throw new IllegalStateException("Please provide a valid xml layout");
        }

        TypedArray ta = getContext().obtainStyledAttributes(attrSet, R.styleable.FractionButton);
        isCheckbox = ta.getBoolean(R.styleable.FractionButton_isCheckbox, false);
        showHelpButton = ta.getBoolean(R.styleable.FractionButton_showHelpButton, false);
        isChecked = ta.getBoolean(R.styleable.FractionButton_Checked, false);
        text = ta.getString(R.styleable.FractionButton_android_text);
        if (text == null) text = "";
        Numerator = ta.getString(R.styleable.FractionButton_Numerator);
        if (Numerator == null) Numerator = "";
        Denominator = ta.getString(R.styleable.FractionButton_Denominator);
        if (Denominator == null) Denominator = "";
        int textColor = ta.getColor(R.styleable.FractionButton_android_textColor, Color.BLACK);
        float textSize = ta.getDimension(R.styleable.FractionButton_android_textSize, 20);
        overlapPercent = ta.getInt(R.styleable.FractionButton_overlapPercent, 20);
        checkedColor = ta.getColor(R.styleable.FractionButton_checkedColor, Color.rgb(255, 99, 71));
        ta.recycle();

        mText.setTextColor(textColor);
        mNumerator.setTextColor(textColor);
        mDenominator.setTextColor(textColor);


        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {textColor, textColor};
        CheckBox c = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_checkbox));
        c.setButtonTintList(new ColorStateList(states, colors));

        mNumerator.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mDenominator.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ConstraintLayout view = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_view));
                    int w = getWidth() - getPaddingStart() - getPaddingEnd() - mText.getLeft() - view.getPaddingStart() - view.getPaddingEnd();
                    int w2 = mNumerator.getWidth();
                    if (mDenominator.getWidth() > w2) w2 = mDenominator.getWidth();
                    w = w - w2;
                    if (mText.getWidth() > w) {
                        correctWidth(mText, w);
                        //float scale = (1.0f * w) / mText.getWidth();
                        //scale = 0.5f;
                        //mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, scale * textSize);
                    }
                }
            });
        }
        setShowHelpButton(showHelpButton);
        setCheckbox(isCheckbox);
        setChecked(isChecked);

        setText(text);
        setNumerator(Numerator);
        setDenominator(Denominator);

        this.setClickable(true);
        super.setOnClickListener(this);

        if (mDenominator.getVisibility() == VISIBLE) {
            mNumerator.setTranslationY(textSize/(100f/overlapPercent));
            mDenominator.setTranslationY(-textSize/(100f/overlapPercent));
        }
    }

    public void correctWidth(TextView textView, int desiredWidth)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int i = 0;
        while (i < 50 && bounds.width() > desiredWidth)
        {
            i++;
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public TextView getTextView() {
        return mText;
    }

    public TextView getNumeratorView() {
        return mNumerator;
    }

    public TextView getDenominatorView() {
        return mDenominator;
    }

    public boolean isCheckbox() {
        return isCheckbox;
    }

    public void setCheckbox(boolean checkbox) {
        isCheckbox = checkbox;
        CheckBox c = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_checkbox));
        if (isCheckbox) {
            c.setVisibility(VISIBLE);
        }
        else {
            c.setVisibility(GONE);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String strInteger) {
        this.text = strInteger;
        mText.setText(strInteger);
        if (text.isEmpty()) mText.setVisibility(GONE);
        else mText.setVisibility(VISIBLE);
    }

    public String getNumerator() {
        return Numerator;
    }

    public void setNumerator(String strNumerator) {
        Numerator = strNumerator;
        mNumerator.setText(Numerator);
        if (Numerator.isEmpty()) mNumerator.setVisibility(GONE);
        else mNumerator.setVisibility(VISIBLE);
    }

    public String getDenominator() {
        return Denominator;
    }

    public void setDenominator(String strDenominator) {
        Denominator = strDenominator;
        mDenominator.setText(Denominator);
        if (Denominator.isEmpty()) mDenominator.setVisibility(GONE);
        else mDenominator.setVisibility(VISIBLE);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean showHelpButton() {
        return showHelpButton;
    }

    public void setShowHelpButton(boolean checked) {
        showHelpButton = checked;
        if (showHelpButton) {
            button.setVisibility(VISIBLE);
            Drawable d = getBackground().getConstantState().newDrawable().mutate();
            button.setBackground(d);
        } else {
            button.setVisibility(GONE);
        }
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        try {
            getBackground().setColorFilter(null);
            button.getBackground().setColorFilter(new PorterDuffColorFilter(Color.rgb(10, 10, 10), PorterDuff.Mode.ADD));
        } catch (Exception ignored) {}
        if (!isCheckbox) return;
        CheckBox c = findViewWithTag(getResources().getString(R.string.tech_zekon_guiComponents_FractionButton_checkbox));
        c.setChecked(isChecked);
        try {
            if (isChecked) {
                getBackground().setColorFilter(new PorterDuffColorFilter(checkedColor, PorterDuff.Mode.SRC));
                button.getBackground().setColorFilter(new PorterDuffColorFilter(checkedColor, PorterDuff.Mode.SRC));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onClick(View v) {
        setChecked(!isChecked);
        if (_wrappedOnClickListener != null) _wrappedOnClickListener.onClick(v);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        _wrappedOnClickListener = l;
    }

    public void setOnHelpClick(OnClickListener l) {
        _wrappedOnClickHelp = l;
    }

    public int getOverlapPercent() {
        return overlapPercent;
    }

    public void setOverlapPercent(int overlapPercent) {
        this.overlapPercent = overlapPercent;
    }
}
