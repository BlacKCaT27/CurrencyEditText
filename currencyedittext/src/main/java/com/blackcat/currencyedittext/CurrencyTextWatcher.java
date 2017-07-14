package com.blackcat.currencyedittext;

import android.text.Editable;
import android.text.TextWatcher;

class CurrencyTextWatcher implements TextWatcher {

    private CurrencyEditText editText;

    private boolean ignoreIteration;
    private String lastGoodInput;

    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     */
    CurrencyTextWatcher(CurrencyEditText textBox){
        editText = textBox;
        lastGoodInput = "";
        ignoreIteration = false;
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


            if (newText.length() < 1){
                lastGoodInput = "";
                editText.setRawValue(0);
                editText.setText("");
                return;
            }

            newText = (editText.areNegativeValuesAllowed()) ? newText.replaceAll("[^0-9/-]", "") : newText.replaceAll("[^0-9]", "");
            if(!newText.equals("") && !newText.equals("-")){
                //Store a copy of the raw input to be retrieved later by getRawValue
                editText.setRawValue(Long.valueOf(newText));
            }
            try{
                textToDisplay = CurrencyTextFormatter.formatText(newText, editText.getLocale(), editText.getDefaultLocale(), editText.getDecimalDigits());
            }
            catch(IllegalArgumentException exception){
                textToDisplay = lastGoodInput;
            }

            editText.setText(textToDisplay);
            //Store the last known good input so if there are any issues with new input later, we can fall back gracefully.
            lastGoodInput = textToDisplay;

            //locate the position to move the cursor to, which will always be the last digit.
            String currentText = editText.getText().toString();
            int cursorPosition = indexOfLastDigit(currentText) + 1;


            //Move the cursor to the end of the numerical value to enter the next number in a right-to-left fashion, like you would on a calculator.
            editText.setSelection(cursorPosition);

        }
        else{
            ignoreIteration = false;
        }

    }

    //Thanks to Lucas Eduardo for this contribution to update the cursor placement code.
    private int indexOfLastDigit(String str){
        int result = 0;

        for(int i = 0; i < str.length(); i++){
            if(Character.isDigit(str.charAt(i))){
                result = i;
            }
        }

        return result;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
}
