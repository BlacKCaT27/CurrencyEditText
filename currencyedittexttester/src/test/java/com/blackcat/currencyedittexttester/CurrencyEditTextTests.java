package com.blackcat.currencyedittexttester;

import android.annotation.SuppressLint;
import android.os.Build;

import com.blackcat.currencyedittext.CurrencyEditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

@SuppressLint("SetTextI18n")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class CurrencyEditTextTests {

    private CurrencyEditText currencyEditText;
    private CurrencyEditText currencyEditTextForXmlTests;

    @Before
    public void setup() {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);

        currencyEditText = (CurrencyEditText) mainActivity.findViewById(R.id.cet);
        currencyEditTextForXmlTests = (CurrencyEditText) mainActivity.findViewById(R.id.cet_for_testing);
    }

    @Test
    public void EnteringValidPositiveStringShouldGiveValidRawValueTest() {
        currencyEditText.setText("$1,000");
        long result = currencyEditText.getRawValue();
        Assert.assertEquals(1000, result);
    }

    @Test
    public void EnteringValidNegativeStringShouldGiveValidRawValueWhenNegativesAllowedTest() {
        currencyEditText.setAllowNegativeValues(true);
        currencyEditText.setText("-$1,000");
        long result = currencyEditText.getRawValue();
        Assert.assertEquals(-1000, result);
    }

    @Test
    public void NegativeSignsAreIgnoredIfNegativeValuesAreNotAllowedTest() {
        currencyEditText.setAllowNegativeValues(false);
        currencyEditText.setText("-$1,000");
        long result = currencyEditText.getRawValue();
        Assert.assertEquals(1000, result);
    }

    @Test
    public void FormatUSDCurrencyWithValidLongParametersTest() {
        String result = currencyEditText.formatCurrency(100000);
        Assert.assertEquals("$1,000.00", result);
    }

    @Test
    public void FormatGPBCurrencyWithValidLongParametersTest() {
        Locale gbLocale = Locale.UK;
        currencyEditText.configureViewForLocale(gbLocale);
        String hint = currencyEditText.getHintString();
        Assert.assertEquals("GBP", hint);
        String result = currencyEditText.formatCurrency(100000);
        Assert.assertEquals("£1,000.00", result);
    }

    @Test
    public void FormatEuroCurrencyWithValidLongParametersTest() {
        Locale euroLocale = Locale.FRANCE;
        currencyEditText.configureViewForLocale(euroLocale);

        String hint = currencyEditText.getHintString();
        Assert.assertEquals("EUR", hint);
        String result = currencyEditText.formatCurrency(100000);
        String expectedResult = "1 000,00 €";
        Assert.assertEquals(expectedResult, result.replace('\u00A0', ' '));
    }

    @Test
    public void SetHintAndVerifyDefaultHintIsOverriddenTest() {
        String defaultHint = currencyEditText.getHintString();
        Assert.assertEquals("$", defaultHint);

        currencyEditText.setHint("Test");
        Assert.assertEquals("Test", currencyEditText.getHint());
    }

    @Test
    public void SetHintInXMLAndVerifyDefaultHintIsOverriddenTest() {
        String hint = currencyEditTextForXmlTests.getHintString();
        Assert.assertEquals("TestHint", hint);
    }

    @Test
    public void SeparatorProperlyBreaksApartCurrencyAndValueForSwissFrancTest(){
        currencyEditText.configureViewForLocale(new Locale.Builder().setRegion("CH").build());

        String result = currencyEditText.formatCurrency(1000);

        Assert.assertEquals("CHF 10.00", result);
    }
}
