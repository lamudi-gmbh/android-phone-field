package com.lamudi.phonefield;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Implementation of PhoneField that uses an {@link EditText}
 * Created by Ismail on 5/6/16.
 */
public class PhoneEditText extends PhoneField {

  public PhoneEditText(Context context) {
    this(context, null);
  }

  public PhoneEditText(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void updateLayoutAttributes() {
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    setGravity(Gravity.CENTER_VERTICAL);
    setOrientation(HORIZONTAL);
    setPadding(0, getContext().getResources().getDimensionPixelSize(R.dimen.padding_large), 0, 0);
  }

  @Override
  public int getLayoutResId() {
    return R.layout.phone_edit_text;
  }
}
