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

    private Locale locale = getResources().getConfiguration().locale;
    private Currency currency = Currency.getInstance(locale);

    private boolean defaultHintEnabled = true;
    private boolean allowNegativeValues = false;
    private long valueInLowestDenom = 0L;

    private String hintCache = null;
    /*
    PUBLIC METHODS
     */
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs);

        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        CurrencyTextWatcher currencyTextWatcher = new CurrencyTextWatcher(this);
        this.addTextChangedListener(currencyTextWatcher);
    }

    /**
     * Sets whether or or not the Default Hint (users local currency symbol) will be shown in the textbox when no value has yet been entered.
     * @param useDefaultHint - true to enable default hint, false to disable
     */
    public void setDefaultHintEnabled(boolean useDefaultHint) {
        this.defaultHintEnabled = useDefaultHint;
    }

    /**
     * Determine whether or not the default hint is currently enabled for this view.
     * @return true if the default hint is enabled, false if it is not.
     */
    public boolean getDefaultHintEnabled(){
        return this.defaultHintEnabled;
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
     * IMPORTANT: Remember that the location of the decimal varies by currency/Locale. This method
     *  returns the raw given value, and does not account for locality of the user. It is up to the
     *  calling application to handle that level of conversion.
     *  For example, if the text of the field is $13.37, this method will return a long with a
     *  value of 1337, as penny is the lowest denomination for USD. It will be up to the calling
     *  application to know that it needs to handle this value as pennies and not some other denomination.
     *
     * @return The raw value that was input by the user, in the lowest denomination of that users
     *  locale.
     */
    public long getRawValue() {
        return valueInLowestDenom;
    }

    /**
     * Convenience method to retrieve the users Locale object. The same as calling
     * getResources().getConfiguration().locale
     *
     * @return the Locale object for the given users configuration
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Override the default locale used by CurrencyEditText (which is the users device locale).
     * WARNING: If this method is used to set the locale to one not supported by ISO 3166,
     * formatting the text will throw an exception. Also keep in mind that calling this method
     * will set the hint based on the specified locale, which will override any previous hint value.
     * @param locale The locale to set the CurrencyEditText box to adhere to.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        this.currency = Currency.getInstance(locale);
        updateHint();
    }

    public void setCurrency(Currency currency, Locale locale) {
        this.currency = currency;
        this.locale = locale;

        updateHint();
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;

        updateHint();
    }

    public Currency getCurrency() {
        return currency;
    }


    private void updateHint() {
        if(hintCache != null){
            setHint(hintCache);
        }
        else{
            if(defaultHintEnabled){
                setHint(getDefaultHintValue());
            }
        }
    }

    /**
     * Pass in a value to have it formatted using the same rules used during data entry. 
     * @param val A string which represents the value you'd like formatted. It is expected that this string will be in the same format returned by the getRawValue() method (i.e. a series of digits, such as 
     *            "1000" to represent "$10.00"). Note that formatCuurrency will take in ANY string, and will first strip any non-digit characters before working on that string. If the result of that processing
     *            reveals an empty string, or a string whose number of digits is greater than the max number of digits, an exception will be thrown.
     * @return A locale-formatted string of the passed in value, represented as currency.
     */
    public String formatCurrency(String val){
        return CurrencyTextFormatter.formatText(val, currency, locale);
    }

    /**
     * Pass in a value to have it formatted using the same rules used during data entry.
     * @param rawVal A long which represents the value you'd like formatted. It is expected that this value will be in the same format returned by the getRawValue() method (i.e. a series of digits, such as
     *            "1000" to represent "$10.00").
     * @return A locale-formatted string of the passed in value, represented as currency.
     */
    public String formatCurrency(long rawVal){
        return CurrencyTextFormatter.formatText(String.valueOf(rawVal), currency, locale);
    }

    protected void setValueInLowestDenom(Long mValueInLowestDenom) {
        this.valueInLowestDenom = mValueInLowestDenom;
    }


    /*
    PRIVATE HELPER METHODS
     */

    private void processAttributes(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditText);

        boolean defaultHintAttrVal = array.getBoolean(R.styleable.CurrencyEditText_enable_default_hint, true);
        configureHint(defaultHintAttrVal);

        this.setAllowNegativeValues(array.getBoolean(R.styleable.CurrencyEditText_allow_negative_values, false));

        array.recycle();
    }

    private void configureHint(boolean defaultHintAttrVal){

        if(hintAlreadySet()){
            this.setDefaultHintEnabled(false);
            this.hintCache = getHint().toString();
            return;
        }
        else{
            this.setDefaultHintEnabled(defaultHintAttrVal);
        }

        if(getDefaultHintEnabled()) {
            this.setHint(getDefaultHintValue());
        }
        else{
            Log.i(this.getClass().getSimpleName(), "configureHint: Default Hint disabled; ignoring request.");
        }
    }

    private boolean hintAlreadySet(){
        return (this.getHint() != null && !this.getHint().equals(""));
    }

    private String getDefaultHintValue() {
        return currency.getSymbol();
    }
}
