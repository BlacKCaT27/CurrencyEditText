package com.blackcat.currencyedittexttester;

import android.annotation.SuppressLint;
import android.os.Build;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.mnw.dataset.DataSet;
import com.mnw.dataset.DataSetRule;
import com.mnw.dataset.InvalidDataSetException;
import com.mnw.dataset.SimpleTestVectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A collection of test cases for CurrencyEditText
 * Note that since CurrencyEditText is a custom view,
 * dependency injection in its typical form is somewhat difficult
 * to implement without including 3rd party libraries which this
 * project attempts to avoid. Therefore, aside from robolectric being
 * used to mock the android APIs, the internal classes behind CurrencyEditText
 * are NOT mocked.
 *
 * As such, these classes are technically more functional tests than unit tests,
 * but are crucial all-the-same.
 */
@SuppressLint("SetTextI18n")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class CurrencyEditTextTests {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.cet)
    CurrencyEditText currencyEditText;

    @Before
    public void setup() {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, mainActivity);
    }

    @Test
    public void EnteringValidPositiveStringShouldGiveValidRawValueTest() {
        currencyEditText.setText("$1,000");

        long result = currencyEditText.getRawValue();

        assertThat(result, is(equalTo(1000L)));
    }

    @Test
    public void EnteringValidNegativeStringShouldGiveValidRawValueWhenNegativesAllowedTest() {
        currencyEditText.setAllowNegativeValues(true);
        currencyEditText.setText("-$1,000");

        long result = currencyEditText.getRawValue();

        assertThat(result, is(equalTo(-1000L)));
    }

    @Test
    public void NegativeSignsAreIgnoredIfNegativeValuesAreNotAllowedTest() {
        currencyEditText.setAllowNegativeValues(false);
        currencyEditText.setText("-$1,000");

        long result = currencyEditText.getRawValue();

        assertThat(result, is(equalTo(1000L)));
    }

    @Test
    public void FormatUSDCurrencyWithValidLongParametersTest() {

        String result = currencyEditText.formatCurrency(100000);

        assertThat(result, is(equalTo("$1,000.00")));
    }

    @Test
    public void FormatUSDCurrencyWithValidStringParametersTest() {

        String result = currencyEditText.formatCurrency("100000");

        assertThat(result, is(equalTo("$1,000.00")));
    }

    @Test
    public void FormatGPBCurrencyWithValidLongParametersTest() {
        Locale gbLocale = Locale.UK;
        currencyEditText.configureViewForLocale(gbLocale);

        String hint = currencyEditText.getHintString();

        assertThat(hint, is(equalTo("GBP")));

        String result = currencyEditText.formatCurrency(10000);

        assertThat(result, is(equalTo("£100.00")));
    }

    @Test
    public void FormatGPBCurrencyWithValidStringParametersTest() {
        Locale gbLocale = Locale.UK;
        currencyEditText.configureViewForLocale(gbLocale);

        String hint = currencyEditText.getHintString();

        assertThat(hint, is(equalTo("GBP")));

        String result = currencyEditText.formatCurrency("10000");

        assertThat(result, is(equalTo("£100.00")));
    }

    @Test
    public void FormatEuroCurrencyWithValidLongParametersTest() {
        Locale euroLocale = Locale.FRANCE;
        currencyEditText.configureViewForLocale(euroLocale);

        String hint = currencyEditText.getHintString();

        assertThat(hint, is(equalTo("EUR")));

        String result = currencyEditText.formatCurrency(100000);
        result = result.replace('\u00A0', ' ');

        String expectedResult = "1 000,00 €";
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void FormatEuroCurrencyWithValidStringParametersTest() {
        Locale euroLocale = Locale.FRANCE;
        currencyEditText.configureViewForLocale(euroLocale);

        String hint = currencyEditText.getHintString();

        assertThat(hint, is(equalTo("EUR")));

        String result = currencyEditText.formatCurrency("100000");
        result = result.replace('\u00A0', ' ');

        String expectedResult = "1 000,00 €";
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void SetHintAndVerifyDefaultHintIsOverriddenTest() {
        String defaultHint = currencyEditText.getHintString();
        assertThat(defaultHint, is(equalTo("$")));

        currencyEditText.setHint("Test");
        String result = currencyEditText.getHintString();
        assertThat(result, is(equalTo("Test")));
    }

    @Test
    public void SetValueStoresProperValueTest(){
        currencyEditText.setValue(1000000);
        long result = currencyEditText.getRawValue();
        assertThat(result, is(equalTo(1000000L)));
    }

    @Test
    public void CanGetAndSetLocaleValueTest(){
        currencyEditText.setLocale(Locale.UK);

        Locale result = currencyEditText.getLocale();

        assertThat(result, is(equalTo(Locale.UK)));
    }

    @Test
    public void SettingNewLocaleUpdatesViewTest(){
        currencyEditText.setValue(100000);
        String currentValue = currencyEditText.getText().toString();

        assertThat(currentValue, is(equalTo("$1,000.00")));

        currencyEditText.setLocale(Locale.UK);
        String newValue = currencyEditText.getText().toString();

        assertThat(newValue, is(equalTo("£1,000.00")));
    }

    @Rule
    public DataSetRule decimalDigitRule = new DataSetRule();

    public static class DecimalDigitDataSet extends SimpleTestVectors{
        @Override
        protected Object[][] generateTestVectors(){
            return new Object[][]{
                    {-1},
                    {341}
            };
        }
    }

    @Test(expected = IllegalArgumentException.class)
    @DataSet(testData = DecimalDigitDataSet.class)
    public void SetDecimalDigitsThrowsExceptionForIllegalArgumentTest() throws InvalidDataSetException {
        int testInput = decimalDigitRule.getInt(0);
        currencyEditText.setDecimalDigits(testInput);
    }

    @Test
    public void GetDecimalDigitsReturnsCorrectNumberOfDefaultDigitsWhenNoneAreSetTest(){
        int result = currencyEditText.getDecimalDigits();

        assertThat(result, is(equalTo(2)));
    }

    @Test
    public void CanGetAndSetLocaleFieldTest(){
        currencyEditText.setLocale(Locale.CANADA);

        Locale result = currencyEditText.getLocale();

        assertThat(result, is(equalTo(Locale.CANADA)));
    }

    @Test
    public void CanGetAndSetDefaultLocaleFieldTest(){
        currencyEditText.setDefaultLocale(Locale.CANADA);

        Locale result = currencyEditText.getDefaultLocale();

        assertThat(result, is(equalTo(Locale.CANADA)));
    }

    @Test
    public void ConfigureViewForLocaleUsesDefaultLocaleForInvalidLocaleInputTest(){
        currencyEditText.configureViewForLocale(null);

        //If the method didn't fallback to default, an exception would've been thrown.
        int decimalDigits = currencyEditText.getDecimalDigits();

        assertThat(decimalDigits, is(equalTo(2)));
    }

    @Test
    public void ConfigureViewForLocaleDefaultsToUSDForInvalidLocaleAndDefaultLocaleTest(){

        currencyEditText.setDefaultLocale(null);
        currencyEditText.configureViewForLocale(null);

        int decimalDigits = currencyEditText.getDecimalDigits();

        assertThat(decimalDigits, is(equalTo(2)));
    }

    @Test
    public void CanEnterSeparatorCharactersWhenViewWasResetTest(){
        currencyEditText.setText("");
        currencyEditText.setText(".");

        assertThat(currencyEditText.getText().toString(), is(equalTo("")));
    }
}
