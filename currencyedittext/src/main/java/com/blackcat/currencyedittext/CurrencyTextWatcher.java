package com.blackcat.currencyedittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.Currency;
import java.util.Locale;

@SuppressWarnings("unused")
class CurrencyTextWatcher implements TextWatcher {

    private CurrencyEditText editText;
    private Locale defaultLocale;

    private boolean ignoreIteration;
    private String lastGoodInput;

    double CURRENCY_DECIMAL_DIVISOR;

    //Setting a max length because after this length, java represents doubles in scientific notation which breaks the formatter
    final int MAX_RAW_INPUT_LENGTH = 15;


    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     */
    public CurrencyTextWatcher(CurrencyEditText textBox){
        this(textBox, Locale.US);
    }

    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     * @param defaultLocale optional locale to default to in the event that the provided CurrencyEditText locale fails due to being unsupported
     */
    public CurrencyTextWatcher(CurrencyEditText textBox, Locale defaultLocale){
        editText = textBox;
        lastGoodInput = "";
        ignoreIteration = false;
        this.defaultLocale = defaultLocale;

        //Different countries use different fractional values for denominations (0.999 <x> vs. 0.99 cents), therefore this must be defined at runtime
        try{
            CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, Currency.getInstance(editText.getLocale()).getDefaultFractionDigits());
        }
        catch(IllegalArgumentException e){
            Log.e("CurrencyTextWatcher", "Unsupported locale provided, defaulting to Locale.US. Error: " + e.getMessage());
            CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, Currency.getInstance(defaultLocale).getDefaultFractionDigits());
        }
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
                textToDisplay = CurrencyTextFormatter.formatText(newText, editText.getCurrency(), editText.getLocale(), defaultLocale);
            }
            catch(IllegalArgumentException exception){
                textToDisplay = lastGoodInput;
            }

            editText.setText(textToDisplay);
            //Store the last known good input so if there are any issues with new input later, we can fall back gracefully.
            lastGoodInput = textToDisplay;

            //locate the position to move the cursor to. After the last digit.
			String str = editText.getText().toString();
			int cursorPosition = indexOfLastDigit(str) + 1;

            //Move the cursor to the end of the numerical value to enter the next number in a right-to-left fashion, like you would on a calculator.
            editText.setSelection(cursorPosition);

        }
        else{
            ignoreIteration = false;
        }
    }

    private int indexOfLastDigit(String str)
    {
        int index = 0;

        for(int i=0;i<str.length();i++)
        {
            if(Character.isDigit(str.charAt(i)))
            {
                index = i;
            }
        }

        return index;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
}
