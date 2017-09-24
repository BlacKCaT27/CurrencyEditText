package com.blackcat.currencyedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.Currency;
import java.util.Locale;

@SuppressWarnings("unused")
public class CurrencyEditText extends EditText {

    private Locale currentLocale;

    private Locale defaultLocale = Locale.US;

    private boolean allowNegativeValues = false;

    private long rawValue = 0L;

    private CurrencyTextWatcher textWatcher;
    private String hintCache = null;

    private int decimalDigits = 0;

    /*
    PUBLIC METHODS
     */
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        processAttributes(context, attrs);
    }

    /**
     * Enable the user to input negative values
     */
    public void setAllowNegativeValues(boolean negativeValuesAllowed){
        allowNegativeValues = negativeValuesAllowed;
    }

    /**
     * Returns whether or not negative values have been allowed for this CurrencyEditText field
     */
    public boolean areNegativeValuesAllowed(){
        return allowNegativeValues;
    }

    /**
     * Retrieve the raw value that was input by the user in their currencies lowest denomination (e.g. pennies).
     *
     * IMPORTANT: Remember that the location of the decimal varies by currentCurrency/Locale. This method
     *  returns the raw given value, and does not account for locality of the user. It is up to the
     *  calling application to handle that level of conversion.
     *  For example, if the text of the field is $13.37, this method will return a long with a
     *  value of 1337, as penny is the lowest denomination for USD. It will be up to the calling
     *  application to know that it needs to handle this value as pennies and not some other denomination.
     *
     * @return The raw value that was input by the user, in the lowest denomination of that users
     *  deviceLocale.
     */
    public long getRawValue() {
        return rawValue;
    }

    /**
     * Sets the value to be formatted and displayed in the CurrencyEditText view.
     *
     * @param value - The value to be converted, represented in the target currencies lowest denomination (e.g. pennies).
     */
    public void setValue(long value){
        String formattedText = format(value);
        setText(formattedText);
    }

    /**
     * The current locale used by this instance of CurrencyEditText. By default, will be the users
     * device locale unless that locale is not ISO 3166 compliant, in which case the defaultLocale will
     * be used.
     *
     * @return the Locale object for the given users configuration
     */
    public Locale getLocale() {
        return currentLocale;
    }

    /**
     * Override the locale used by CurrencyEditText (which is the users device locale by default).
     *
     * Will also update the hint text if a custom hint was not provided.
     *
     * IMPORTANT - This method does NOT update the currently set Currency object used by
     * this CurrencyEditText instance. If your use case dictates that Currency and Locale
     * should never break from their default pairing, use 'configureViewForLocale(locale)' instead
     * of this method.
     * @param locale The deviceLocale to set the CurrencyEditText box to adhere to.
     */
    public void setLocale(Locale locale){
        currentLocale = locale;
        refreshView();
    }

    /**
     * Convenience method to get the current Hint back as a string rather than a CharSequence
     */
    public String getHintString() {
        CharSequence result = super.getHint();
        if (result == null) return null;
        return super.getHint().toString();
    }

    /**
     * Returns the number of decimal digits this CurrencyEditText instance is currently configured
     * to use. This value will be based on the current Currency object unless the value
     * was overwritten by setDecimalDigits().
     */
    public int getDecimalDigits(){
        return decimalDigits;
    }

    /**
     * Sets the number of decimal digits the currencyTextFormatter will use, overriding
     * the number of digits specified by the current currency. Note, however,
     * that calls to setCurrency() and configureViewForLocale() will override this value.
     *
     * Note that this method will also invoke the formatter to update the current view if the current
     * value is not null/empty.
     *
     * @param digits The number of digits to be shown following the decimal in the formatted text.
     *               Value must be between 0 and 340 (inclusive).
     * @throws IllegalArgumentException If provided value does not fall within the range (0, 340) inclusive.
     */
    public void setDecimalDigits(int digits){
        if(digits < 0 || digits > 340){
            throw new IllegalArgumentException("Decimal Digit value must be between 0 and 340");
        }
        decimalDigits = digits;

        refreshView();
    }

    /**
     * Sets up the CurrencyEditText view to be configured for a given locale, using that
     * locales default currency (so long as the locale is ISO-3166 compliant). If there is
     * an issue retrieving the locales currency, the defaultLocale field will be used.
     *
     * This is the most 'fool proof' way of configuring a CurrencyEditText view when not
     * relying on the default implementation, and is the recommended approach for handling
     * locale/currency setup if you choose not to rely on the default behavior.
     *
     * Note that this method will set the decimalDigits field, potentially overriding
     * values from previous setDecimalDigits calls.
     */
    public void configureViewForLocale(Locale locale){
        this.currentLocale = locale;
        Currency currentCurrency = getCurrencyForLocale(locale);
        decimalDigits = currentCurrency.getDefaultFractionDigits();
        refreshView();
    }

    /**
     * Override the locale to be used in the event that the users device locale is not ISO 3166 compliant.
     * Defaults to Locale.US.
     * NOTE: Be absolutely sure that this value is supported by ISO 3166. See
     * Java.util.Locale.getISOCountries() for a list of currently supported ISO 3166 locales (note that this list
     * may not be identical on all devices)
     * @param locale The fallback locale used to recover gracefully in the event of the current locale value failing.
     */
    public void setDefaultLocale(Locale locale){
        this.defaultLocale = locale;
    }

    /**
     * The currently held default Locale to fall back on in the event of a failure with the Locale field (typically
     * due to the locale being set to a non-standards-compliant value.
     */
    public Locale getDefaultLocale(){
        return defaultLocale;
    }

    /**
     * Pass in a value to have it formatted using the same rules used during data entry.
     * @param val A string which represents the value you'd like formatted. It is expected that this string will be in the same format returned by the getRawValue() method (i.e. a series of digits, such as
     *            "1000" to represent "$10.00"). Note that formatCurrency will take in ANY string, and will first strip any non-digit characters before working on that string. If the result of that processing
     *            reveals an empty string, or a string whose number of digits is greater than the max number of digits, an exception will be thrown.
     * @return A deviceLocale-formatted string of the passed in value, represented as currentCurrency.
     */
    public String formatCurrency(String val){
        return format(val);
    }

    /**
     * Pass in a value to have it formatted using the same rules used during data entry.
     * @param rawVal A long which represents the value you'd like formatted. It is expected that this value will be in the same format returned by the getRawValue() method (i.e. a series of digits, such as
     *            "1000" to represent "$10.00").
     * @return A deviceLocale-formatted string of the passed in value, represented as currentCurrency.
     */
    public String formatCurrency(long rawVal){
        return format(rawVal);
    }

    /*
    PRIVATE HELPER METHODS
     */

    private void refreshView(){
        setText(format(getRawValue()));
        updateHint();
    }

    private String format(long val){
        return CurrencyTextFormatter.formatText(String.valueOf(val), currentLocale, defaultLocale, decimalDigits);
    }

    private String format(String val){
        return CurrencyTextFormatter.formatText(val, currentLocale, defaultLocale, decimalDigits);
    }

    private void init(){
        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        currentLocale = retrieveLocale();
        Currency currentCurrency = getCurrencyForLocale(currentLocale);
        decimalDigits = currentCurrency.getDefaultFractionDigits();
        initCurrencyTextWatcher();
    }

    private void initCurrencyTextWatcher(){
        if(textWatcher != null){
            this.removeTextChangedListener(textWatcher);
        }
        textWatcher = new CurrencyTextWatcher(this);
        this.addTextChangedListener(textWatcher);
    }

    private void processAttributes(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditText);
        this.hintCache = getHintString();
        updateHint();

        this.setAllowNegativeValues(array.getBoolean(R.styleable.CurrencyEditText_allow_negative_values, false));
        this.setDecimalDigits(array.getInteger(R.styleable.CurrencyEditText_decimal_digits, decimalDigits));

        array.recycle();
    }

    private void updateHint() {
        if(hintCache == null){
            setHint(getDefaultHintValue());
        }
    }

    private String getDefaultHintValue() {
        try {
            return Currency.getInstance(currentLocale).getSymbol();
        }
        catch(Exception e){
            Log.w("CurrencyEditText", String.format("An error occurred while getting currency symbol for hint using locale '%s', falling back to defaultLocale", currentLocale));
            try{
                return Currency.getInstance(defaultLocale).getSymbol();
            }
            catch(Exception e1){
                Log.w("CurrencyEditText", String.format("An error occurred while getting currency symbol for hint using default locale '%s', falling back to USD", defaultLocale));
                return Currency.getInstance(Locale.US).getSymbol();
            }

        }
    }

    private Locale retrieveLocale(){
        Locale locale;
        try{
            locale = getResources().getConfiguration().locale;
        }
        catch(Exception e){
            Log.w("CurrencyEditText", String.format("An error occurred while retrieving users device locale, using fallback locale '%s'", defaultLocale), e);
            locale = defaultLocale;
        }
        return locale;
    }

    private Currency getCurrencyForLocale(Locale locale){
        Currency currency;
        try {
            currency = Currency.getInstance(locale);
        }
        catch(Exception e){
            try{
                Log.w("CurrencyEditText", String.format("Error occurred while retrieving currency information for locale '%s'. Trying default locale '%s'...", currentLocale, defaultLocale));
                currency = Currency.getInstance(defaultLocale);
            }
            catch(Exception e1){
                Log.e("CurrencyEditText", "Both device and configured default locales failed to report currentCurrency data. Defaulting to USD.");
                currency = Currency.getInstance(Locale.US);
            }
        }
        return currency;
    }

    protected void setRawValue(long value) {
        rawValue = value;
    }
}
