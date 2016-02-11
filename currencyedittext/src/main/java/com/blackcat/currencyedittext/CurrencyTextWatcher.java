package com.blackcat.currencyedittext;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.util.Currency;

@SuppressWarnings("unused")
class CurrencyTextWatcher implements TextWatcher {

    private CurrencyEditText editText;

    private boolean ignoreIteration;
    private String lastGoodInput;
    private DecimalFormat currencyFormatter;


    final double CURRENCY_DECIMAL_DIVISOR;
    final int CURSOR_SPACING_COMPENSATION = 2;

    //Setting a max length because after this length, java represents doubles in scientific notation which breaks the formatter
    final int MAX_RAW_INPUT_LENGTH = 15;



    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     */
    public CurrencyTextWatcher(CurrencyEditText textBox){
        editText = textBox;
        lastGoodInput = "";
        ignoreIteration = false;

        currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(editText.getLocale());

        //Different countries use different fractional values for denominations (0.999 <x> vs. 0.99 cents), therefore this must be defined at runtime
        CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, editText.getCurrency().getDefaultFractionDigits());

    }

    /**
     * After each letter is typed, this method will take in the current text, process it, and take the resulting
     * formatted string and place it back in the EditText box the TextWatcher is applied to
     * @param editable text to be transformed
     */
    @Override
    public void afterTextChanged(Editable editable) {
        //Use the ignoreIteration flag to stop our edits to the text field from triggering an endlessly recursive call to afterTextChanged
        if(!ignoreIteration){
            ignoreIteration = true;
            //Start by converting the editable to something easier to work with, then remove all non-digit characters
            String newText = editable.toString();
            String textToDisplay;

            newText = (editText.areNegativeValuesAllowed()) ? newText.replaceAll("[^0-9/-]", "") : newText.replaceAll("[^0-9]", "");
            if(!newText.equals("") && newText.length() < MAX_RAW_INPUT_LENGTH && !newText.equals("-")){
                //Store a copy of the raw input to be retrieved later by getRawValue
                editText.setValueInLowestDenom(Long.valueOf(newText));
            }
            try{
                textToDisplay = CurrencyTextFormatter.formatText(newText, editText.getCurrency(), editText.getLocale());
            }
            catch(IllegalArgumentException exception){
                textToDisplay = lastGoodInput;
            }

            editText.setText(textToDisplay);
            //Store the last known good input so if there are any issues with new input later, we can fall back gracefully.
            lastGoodInput = textToDisplay;

            //locate the position to move the cursor to. The CURSOR_SPACING_COMPENSATION constant is to account for locales where the Euro is displayed as " €" (2 characters).
            //A more robust cursor strategy will be implemented at a later date.
            int cursorPosition = editText.getText().length();
            if(textToDisplay.length() > 0 && Character.isDigit(textToDisplay.charAt(0))) cursorPosition -= CURSOR_SPACING_COMPENSATION;

            //Move the cursor to the end of the numerical value to enter the next number in a right-to-left fashion, like you would on a calculator.
            editText.setSelection(cursorPosition);

        }
        else{
            ignoreIteration = false;
        }
    }

    /**
     * @deprecated This protected method has been replaced with CurrencyTextFormatter.formatText(val, locale). This method will be removed in a future version. It
     * is no longer being maintained as of 2/6/2016.
     */
    @Deprecated
    public String formatCurrency(String val){
        String formattedAmount;
        val = (editText.areNegativeValuesAllowed()) ? "" : val.replaceAll("[^0-9]", "");
        //if there's nothing left, that means we were handed an empty string. Also, cap the raw input so the formatter doesn't break.
        if(!val.equals("") && val.length() < MAX_RAW_INPUT_LENGTH) {
            //Convert the string into a double, which will later be passed into the currency formatter
            double newTextValue = Double.valueOf(val);

            /** Despite having a formatter, we actually need to place the decimal ourselves.
             * IMPORTANT: This double division does have a small potential to introduce rounding errors (though the likelihood is very small for two digits)
             * Therefore, do not attempt to pull the numerical value out of the String text of this object. Instead, call getRawValue to retrieve
             * the actual number input by the user. See CurrencyEditText.getRawValue() for more information.
             */
            newTextValue = newTextValue / CURRENCY_DECIMAL_DIVISOR;
            formattedAmount = currencyFormatter.format(newTextValue);
        }
        else {
            throw new IllegalArgumentException("Invalid amount of digits found (either zero or too many) in argument val");
        }
        return formattedAmount;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
}
