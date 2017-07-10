package com.blackcat.currencyedittext;


import android.util.Log;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public final class CurrencyTextFormatter {

    private CurrencyTextFormatter(){}

    public static String formatText(String val, Locale locale){
        return formatText(val, locale, Locale.US, null);
    }

    public static String formatText(String val, Locale locale, Locale defaultLocale){
        return formatText(val, locale, defaultLocale, null);
    }

    public static String formatText(String val, Locale locale, Locale defaultLocale, Integer decimalDigits){
        //special case for the start of a negative number
        if(val.equals("-")) return val;

        int currencyDecimalDigits;
        if (decimalDigits != null){
            currencyDecimalDigits = decimalDigits;
        }
        else {
            Currency currency = Currency.getInstance(locale);
            try {
                currencyDecimalDigits = currency.getDefaultFractionDigits();
            } catch (Exception e) {
                Log.e("CurrencyTextFormatter", "Illegal argument detected for currency: " + currency + ", using currency from defaultLocale: " + defaultLocale);
                currencyDecimalDigits = Currency.getInstance(defaultLocale).getDefaultFractionDigits();
            }
        }

        DecimalFormat currencyFormatter;
        try {
            currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        } catch (Exception e) {
            try {
                Log.e("CurrencyTextFormatter", "Error detected for locale: " + locale + ", falling back to default value: " + defaultLocale);
                currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(defaultLocale);
            }
            catch(Exception e1){
                Log.e("CurrencyTextFormatter", "Error detected for defaultLocale: " + defaultLocale + ", falling back to USD.");
                currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.US);
            }
        }

        //retain information about the negativity of the value before stripping all non-digits
        boolean isNegative = false;
        if (val.contains("-")){
            isNegative = true;
        }

        //strip all non-digits so the formatter always has a 'clean slate' of numbers to work with
        val = val.replaceAll("[^\\d]", "");
        //if there's nothing left, that means we were handed an empty string. Also, cap the raw input so the formatter doesn't break.
        if(!val.equals("")) {

            //if we're given a value that's smaller than our decimal location, pad the value.
            if (val.length() <= currencyDecimalDigits){
                String formatString = "%" + currencyDecimalDigits + "s";
                val = String.format(formatString, val).replace(' ', '0');
            }

            //place the decimal in the proper location to construct a double which we will give the formatter.
            //This is NOT the decimal separator for the currency value, but for the double which drives it.
            String preparedVal = new StringBuilder(val).insert(val.length() - currencyDecimalDigits, '.').toString();

            //Convert the string into a double, which will be passed into the currency formatter
            double newTextValue = Double.valueOf(preparedVal);

            //reapply the negativity
            newTextValue *= isNegative ? -1 : 1;

            //finally, do the actual formatting
            currencyFormatter.setMinimumFractionDigits(currencyDecimalDigits);
            val = currencyFormatter.format(newTextValue);
        }
        else {
            throw new IllegalArgumentException("Invalid amount of digits found (either zero or too many) in argument val");
        }
        return val;
    }

}
