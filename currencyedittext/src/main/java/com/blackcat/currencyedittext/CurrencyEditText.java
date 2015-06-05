package com.blackcat.currencyedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.Currency;
import java.util.Locale;


public class CurrencyEditText extends EditText {

    private Locale mLocale = getResources().getConfiguration().locale;
    private CurrencyTextWatcher ctw;
    
    private boolean mDefaultHintEnabled = true;
    private long mValueInLowestDenom = 0L;

    /*
    PUBLIC METHODS
     */
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs);

        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        
        ctw = new CurrencyTextWatcher(this);
        this.addTextChangedListener(ctw);
    }

    public void setDefaultHintEnabled(boolean useDefaultHint) {
        this.mDefaultHintEnabled = useDefaultHint;
    }

    /**
     * Determine whether or not the default hint is currently enabled for this view.
     * @return true if the default hint is enabled, false if it is not.
     */
    public boolean getDefaultHintEnabled(){
        return this.mDefaultHintEnabled;
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
        return mValueInLowestDenom;
    }

    /**
     * Convenience method to retrieve the users Locale object. The same as calling
     * getResources().getConfiguration().locale
     *
     * @return the Locale object for the given users configuration
     */
    public Locale getLocale() {
        return mLocale;
    }

    /**
     * Pass in a value to have it formatted using the same rules used during data entry. 
     * @param val A string which represents the value you'd like formatted. It is expected that this string will be in the same format returned by the getRawValue() method (i.e. a series of digits, such as 
     *            "1000" to represent "$10.00"). Note that format currency will take in ANY string, and will first strip any non-digit characters before working on that string. If the result of that processing
     *            reveals an empty string, or a string whose number of digits is greater than the max number of digits, an exception will be thrown.
     * @return A locale-formatted string of the passed in value, represented as currency.
     */
    public String formatCurrency(String val){
        return CurrencyTextFormatter.formatText(val, mLocale);
    }
    
    

    protected void setValueInLowestDenom(Long mValueInLowestDenom) {
        this.mValueInLowestDenom = mValueInLowestDenom;
    }

    /*
    PRIVATE HELPER METHODS
     */

    private void processAttributes(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditText);

        boolean defaultHintAttr = array.getBoolean(R.styleable.CurrencyEditText_enable_default_hint, true);
        this.setDefaultHintEnabled(defaultHintAttr);
        configureHint();

        array.recycle();
    }

    private void configureHint(){
        //Check to see if a hint has been set by the calling application. If not, fall back to the default hint if it's enabled.
        CharSequence hintText = this.getHint();
        if(hintText == null){
            if(getDefaultHintEnabled()){
                this.setHint(Currency.getInstance(getResources().getConfiguration().locale).getSymbol());
            }
            else{
                Log.i(this.getClass().getSimpleName(), "configureHint: Default Hint disabled; ignoring request.");
            }
        }
    }
}
