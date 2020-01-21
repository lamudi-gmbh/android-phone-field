android-phone-field
===================

android-phone-field is A small UI library that allows you to create phone fields with corresponding country flags, and validate the phone number using [libphonenumber](https://github.com/googlei18n/libphonenumber) from google.

![alt text](https://raw.githubusercontent.com/lamudi-gmbh/android-phone-field/master/raw/phone-field.gif "Sample App")

The library has two different fields:

 * PhoneEditText : includes EditText alongside the flags spinner
 * PhoneInputLayout : includes a TextInputLayout from the design support library alongside the flags spinner 
 
## Features
 
 * Displays the correct country flag if the user enters a valid international phone number
 * Allows the user to choose the country manually and only enter a national phone number
 * Allows you to choose a default country, which the field will change to automatically if the user chose a different country then cleared the field.
 * Validates the phone number 
 * Returns the valid phone number including the country code
 
## Usage

In your module's gradle file add the following dependency, please make sure that you have jcenter in your repositories list

```
compile ('com.lamudi.phonefield:phone-field:0.1.3@aar') {
    transitive = true
}
```

 In your layout you can use the PhoneInputLayout 
 
```xml
<com.lamudi.phonefield.PhoneInputLayout
     android:id="@+id/phone_input_layout"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"/>
```
 
 or the PhoneEditText
 
```xml
 <com.lamudi.phonefield.PhoneEditText
     android:id="@+id/edit_text"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"/>
```

Then in your Activity/Fragment

 
```java 
final PhoneInputLayout phoneInputLayout = (PhoneInputLayout) findViewById(R.id.phone_input_layout);
final PhoneEditText phoneEditText = (PhoneEditText) findViewById(R.id.edit_text);
final Button button = (Button) findViewById(R.id.submit_button);

// you can set the hint as follows
phoneInputLayout.setHint(R.string.phone_hint);
phoneEditText.setHint(R.string.phone_hint);

// you can set the default country as follows
phoneInputLayout.setDefaultCountry("DE");
phoneEditText.setDefaultCountry("FR");

button.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View v) {
    boolean valid = true;
    
    // checks if the field is valid 
    if (phoneInputLayout.isValid()) {
      phoneInputLayout.setError(null);
    } else {
      // set error message
      phoneInputLayout.setError(getString(R.string.invalid_phone_number));
      valid = false;
    }

    if (phoneEditText.isValid()) {
      phoneEditText.setError(null);
    } else {
      phoneEditText.setError(getString(R.string.invalid_phone_number));
      valid = false;
    }

    if (valid) {
      Toast.makeText(MainActivity.this, R.string.valid_phone_number, Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(MainActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
    }
    
    // Return the phone number as follows
    String phoneNumber = phoneInputLayout.getPhoneNumber();
  }
});
 
```

## Customization

In case the default style doesn't match your app styles, you can extend the PhoneInputLayout, or PhoneEditText and provide your own xml, but keep in mind that you have to provide a valid xml file with at least an EditText (tag = com_lamudi_phonefield_edittext) and Spinner (tag = com_lamudi_phonefield_flag_spinner), otherwise the library will throw an IllegalStateException.

You can also create your own custom view by extending the PhoneField directly. 

## Countries generation
For better performance and to avoid using json data and then parse it to be used in the library, a simple nodejs is used to convert the countries.json file in raw/countries-generator/ into a plain java utility class that has static list of countries.

The generation script works as follows:
```
node gen.js

or

./gen.js
```

## Motivation

This is probably not the the first library with the same purpose, for instance before I started working on the library I came across [IntlPhoneInput](https://github.com/Rimoto/IntlPhoneInput) which provides almost most of the functionality this library provides, however I chose to develop a new library for the following reasons: 
 
 * This library provides two implementations of PhoneField using EditText and TextInputLayout
 * This library allows users to extend the functionality and use custom layouts if needed to match the application theme
 * This library uses a static list of countries generated from the countries.json file in the raw resources 

## Attributions  

 1. Inspired by [intl-tel-input for jQuery](https://github.com/jackocnr/intl-tel-input) and [IntlPhoneInput](https://github.com/Rimoto/IntlPhoneInput)
 2. Flag images from [GoSquared](https://www.gosquared.com/resources/flag-icons/)
 3. Original country data from mledoze's [World countries in JSON, CSV and XML](https://github.com/mledoze/countries) which is then used to generate a plain Java file
 4. Formatting/validation using [libphonenumber](https://github.com/googlei18n/libphonenumber)
