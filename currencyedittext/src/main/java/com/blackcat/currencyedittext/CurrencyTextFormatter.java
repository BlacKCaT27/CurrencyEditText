package com.blackcat.currencyedittext;


import android.util.Log;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public final class CurrencyTextFormatter {

    //Setting a max length because after this length, java represents doubles in scientific notation which breaks the formatter
    static final int MAX_RAW_INPUT_LENGTH = 15;

    private CurrencyTextFormatter(){}

    public static String formatText(String val, Currency currency, Locale locale){
        return formatText(val, currency, locale, Locale.US);
    }

    public static String formatText(String val, Currency currency, Locale locale, Locale defaultLocale){
        //special case for the start of a negative number
        if(val.equals("-")) return val;

        double CURRENCY_DECIMAL_DIVISOR;
        DecimalFormat currencyFormatter = null;
        try{
            CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, currency.getDefaultFractionDigits());
            currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        }
        catch(IllegalArgumentException e){
            Log.e("CurrencyTextFormatter", "Illegal argument detected for locale: " + locale + ", falling back to default value: " + defaultLocale);
            CURRENCY_DECIMAL_DIVISOR = (int) Math.pow(10, Currency.getInstance(defaultLocale).getDefaultFractionDigits());
            currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(defaultLocale);
        }

        //if there's nothing left, that means we were handed an empty string. Also, cap the raw input so the formatter doesn't break.
        if(!val.equals("") && val.length() < MAX_RAW_INPUT_LENGTH && !val.equals("-")) {
            //Convert the string into a double, which will later be passed into the currency formatter
            double newTextValue = Double.valueOf(val);

            /** Despite having a formatter, we actually need to place the decimal ourselves.
             * IMPORTANT: This double division does have a small potential to introduce rounding errors (though the likelihood is very small for two digits)
             * Therefore, do not attempt to pull the numerical value out of the String text of this object. Instead, call getRawValue to retrieve
             * the actual number input by the user. See CurrencyEditText.getRawValue() for more information.
             */
            newTextValue = newTextValue / CURRENCY_DECIMAL_DIVISOR;
            val = currencyFormatter.format(newTextValue);
        }
        else {
            throw new IllegalArgumentException("Invalid amount of digits found (either zero or too many) in argument val");
        }
        return val;
    }

}
