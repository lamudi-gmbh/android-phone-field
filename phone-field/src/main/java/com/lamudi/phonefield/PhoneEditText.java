package com.lamudi.phonefield;

import android.content.Context;
import android.util.AttributeSet;
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
  public int getLayoutResId() {
    return R.layout.phone_edit_text;
  }
}
