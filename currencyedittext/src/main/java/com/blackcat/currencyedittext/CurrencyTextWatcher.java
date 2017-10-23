package com.blackcat.currencyedittext;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

class CurrencyTextWatcher implements TextWatcher {

    private CurrencyEditText editText;
    private String lastGoodInput = "";

    /**
     * A specialized TextWatcher designed specifically for converting EditText values to a pretty-print string currency value.
     *
     * @param textBox The EditText box to which this TextWatcher is being applied.
     *                Used for replacing user-entered text with formatted text as well as handling cursor position for inputting monetary values
     */
    CurrencyTextWatcher(CurrencyEditText textBox) {
        editText = textBox;
    }

    /**
     * After each letter is typed, this method will take in the current text, process it, and take the resulting
     * formatted string and place it back in the EditText box the TextWatcher is applied to
     *
     * @param editable text to be transformed
     */
    @Override
    public void afterTextChanged(Editable editable) {
        final String currentInput = editable.toString();

        // Only process input if it has changed
        if (!lastGoodInput.equals(currentInput)) {
            //Start by converting the editable to something easier to work with, then remove all non-digit characters
            String textToDisplay;

            boolean containsAnyNumber = currentInput.matches(".*\\d+.*");
            final boolean inputIsEmpty = TextUtils.isEmpty(currentInput);
            if (inputIsEmpty || !containsAnyNumber) {
                // set to defaults
                lastGoodInput = "";
                editText.setRawValue(0);
                editText.setText(formatAsCurrency("0"));
            } else {
                String updatedInput;
                if (editText.areNegativeValuesAllowed()) {
                    updatedInput = currentInput.replaceAll("[^0-9/-]", "");
                } else {
                    updatedInput = currentInput.replaceAll("[^0-9]", "");
                }

                if (!TextUtils.isEmpty(updatedInput) && !updatedInput.equals("-")) {
                    //Store a copy of the raw input to be retrieved later by getRawValue
                    editText.setRawValue(Long.valueOf(updatedInput));
                }

                try {
                    textToDisplay = formatAsCurrency(updatedInput);
                    lastGoodInput = textToDisplay;
                } catch (IllegalArgumentException ignored) {
                    textToDisplay = lastGoodInput;
                }

                editText.setText(textToDisplay);
            }
            moveCursorAfterLastDigit();
        }
    }

    private void moveCursorAfterLastDigit() {
        //locate the position to move the cursor to, which will always be the last digit.
        String currentText = editText.getText().toString();
        int cursorPosition = indexOfLastDigit(currentText) + 1;

        //Move the cursor to the end of the numerical value to enter the next number in a right-to-left fashion, like you would on a calculator.
        if (currentText.length() >= cursorPosition) {
            editText.setSelection(cursorPosition);
        }
    }

    private String formatAsCurrency(String newText) {
        return CurrencyTextFormatter.formatText(newText, editText.getLocale(), editText.getDefaultLocale(), editText.getDecimalDigits());
    }

    //Thanks to Lucas Eduardo for this contribution to update the cursor placement code.
    private int indexOfLastDigit(String str) {
        int result = 0;

        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                result = i;
            }
        }

        return result;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    }
}
