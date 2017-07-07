package com.blackcat.currencyedittext;


import android.util.Log;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public final class CurrencyTextFormatter {

    //Setting a max length because after this length, java represents doubles in scientific notation which breaks the formatter
    private static final int MAX_RAW_INPUT_LENGTH = 15;

    private CurrencyTextFormatter(){}

    public static String formatText(String val, Currency currency, Locale locale){
        return formatText(val, currency, locale, Locale.US);
    }

    public static String formatText(String val, Currency currency, Locale locale, Locale defaultLocale){
        //special case for the start of a negative number
        if(val.equals("-")) return val;

        int currencyDecimalDigits;
        DecimalFormat currencyFormatter;
        try{
            currencyDecimalDigits = currency.getDefaultFractionDigits();
            currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        }
        catch(IllegalArgumentException e){
            Log.e("CurrencyTextFormatter", "Illegal argument detected for locale: " + locale + ", falling back to default value: " + defaultLocale);
            currencyDecimalDigits = Currency.getInstance(defaultLocale).getDefaultFractionDigits();
            currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(defaultLocale);
        }

        //if there's nothing left, that means we were handed an empty string. Also, cap the raw input so the formatter doesn't break.
        if(!val.equals("") && val.length() < MAX_RAW_INPUT_LENGTH && !val.equals("-")) {

            String preparedVal = new StringBuilder(val).insert(val.length() - currencyDecimalDigits, '.').toString();

            //Convert the string into a double, which will be passed into the currency formatter
            double newTextValue = Double.valueOf(preparedVal);

            val = currencyFormatter.format(newTextValue);
        }
        else {
            throw new IllegalArgumentException("Invalid amount of digits found (either zero or too many) in argument val");
        }
        return val;
    }

}
