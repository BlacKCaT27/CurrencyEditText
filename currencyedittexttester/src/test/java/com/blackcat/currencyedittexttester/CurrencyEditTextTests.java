package com.blackcat.currencyedittexttester;

import android.annotation.SuppressLint;
import android.os.Build;

import com.blackcat.currencyedittext.CurrencyEditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

@SuppressLint("SetTextI18n")
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class CurrencyEditTextTests {

    protected MainActivity mainActivity;
    private CurrencyEditText currencyEditText;

    @Before
    public void setup(){
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        currencyEditText = (CurrencyEditText) mainActivity.findViewById(R.id.cet);
    }

    @Test
    public void EnteringValidPositiveStringShouldGiveValidRawValueTest(){
        currencyEditText.setText("$1,000");
        long result = currencyEditText.getRawValue();
        Assert.assertEquals(1000, result);
    }

    @Test
    public void EnteringValidNegativeStringShouldGiveValidRawValueTest(){
        currencyEditText.setText("-$1,000");
        long result = currencyEditText.getRawValue();
        Assert.assertEquals(-1000, result);
    }

    @Test
    public void FormatUSDCurrencyWithValidLongParametersTest(){
        String result = currencyEditText.formatCurrency(100000);
        Assert.assertEquals("$1,000.00", result);
    }

    @Test
    public void FormatGPBCurrencyWithValidLongParametersTest(){
        Locale gbLocale = Locale.UK;
        currencyEditText.setLocale(gbLocale);
        String hint = currencyEditText.getHint().toString();
        Assert.assertEquals("GBP", hint);
        String result = currencyEditText.formatCurrency(100000);
        Assert.assertEquals("£1,000.00", result);
    }

    @Test
    public void FormatEuroCurrencyWithValidLongParametersTest(){
        Locale euroLocale = Locale.FRANCE;
        currencyEditText.setLocale(euroLocale);
        String hint = currencyEditText.getHint().toString();
        Assert.assertEquals("EUR", hint);
        String result = currencyEditText.formatCurrency(100000);
        String expectedResult = "1 000,00 €";
        Assert.assertEquals(expectedResult, result.replace('\u00A0',' '));
    }
}
