package com.blackcat.currencyedittext;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;


class CurrencyTextWatcher implements TextWatcher {

    Locale mLocale;
    private CurrencyEditText mEditText;
    boolean mIgnoreIteration;

    DecimalFormat mCurrencyFormatter;

    final double CURRENCY_DECIMAL_DIVISOR;
    final int CURSOR_SPACING_COMPENSATION = 2;

    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     */
    public CurrencyTextWatcher(CurrencyEditText textBox){
        mEditText = textBox;
        mLocale = textBox.getLocale();

        mIgnoreIteration = false;

        mCurrencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(mLocale);
        CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, Currency.getInstance(mLocale).getDefaultFractionDigits());

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    /**
     * After each letter is typed, this method will take in the current text, process it, and take the resulting
     * formatted string and place it back in the EditText box the TextWatcher is applied to
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {

        //Use the mIgnoreIteration flag to stop our edits to the text field from triggering an endlessly recursive call to afterTextChanged
        if(!mIgnoreIteration){
            //Start by converting the editable to something easier to work with, then remove all non-digit characters
            String newText = editable.toString();
            newText = newText.replaceAll("[^0-9]", "");

            //if there's nothing left, that means we were handed an empty string
            if(newText != "") {


                double newTextValue = Double.valueOf(newText);
                int newTextValueInInt = Integer.valueOf(newText);
                mEditText.setValueInLowestDenom(newTextValueInInt);
                /** Despite having a formatter, we actually need to place the decimal ourselves.
                  * IMPORTANT: This double division does have a small potential to introduce rounding errors (though the likelihood is very small for two digits)
                  * Therefore, do not attempt to pull the numerical value out of the String text of this object. Instead, call getRawValue to retreive
                  * the actual number input by the user. See CurrencyEditText.getRawValue() for more information.
                  */
                newTextValue = newTextValue / CURRENCY_DECIMAL_DIVISOR;

                String formattedAmount = mCurrencyFormatter.format(newTextValue);

                mIgnoreIteration = true;
                mEditText.setText(formattedAmount);

                //locate the position to move the cursor to. The CURSOR_SPACING_COMPENSATION constant is to account for locales where the Euro is displayed as " €" (2 characters).
                //A more robust cursor strategy will be implemented at a later date.
                int cursorPosition = mEditText.getText().length();
                if(Character.isDigit(formattedAmount.charAt(0))) cursorPosition -= CURSOR_SPACING_COMPENSATION;

                //Move the cursor to the end of the numerical value to enter the next number in a right-to-left fashion, like you would on a calculator.
                mEditText.setSelection(cursorPosition);
            }
        }
        else{
            mIgnoreIteration = false;
        }
    }


}
