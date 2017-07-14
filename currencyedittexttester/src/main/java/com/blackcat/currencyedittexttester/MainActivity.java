package com.blackcat.currencyedittexttester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.blackcat.currencyedittext.CurrencyTextFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @BindView(R.id.cet)
    CurrencyEditText cet;

    @BindView(R.id.et_raw_val)
    TextView et_raw_val;

    @BindView(R.id.et_formatted_val)
    TextView et_formatted_val;

    @BindView(R.id.testable_locales_locale_info)
    TextView testable_locales_locale_data;

    @BindView(R.id.spinner_testable_locales)
    Spinner testable_locales_spinner;

    @BindView(R.id.testable_locales_cet)
    CurrencyEditText testable_locales_cet;

    @BindView(R.id.decimal_digits_tool_cet)
    CurrencyEditText decimal_digits_tool_cet;

    @BindView(R.id.decimal_digits_tool_number_picker)
    NumberPicker decimal_digits_tool_number_picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ButterKnife.bind(this);

        configureTestableLocalesTool();
        configureDecimalDigitsTool();
    }

    @OnClick(R.id.cet_reset_button)
    void onResetClicked(){
        cet.setText("");
    }

    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    @OnClick(R.id.button)
    void onRefreshClicked(){
        Log.d("MainActivity", "Locale: " + getResources().getConfiguration().locale.toString());
        Log.d("MainActivity", "DefaultLocale: " + Locale.getDefault());

        long maxRange = 15000000;
        long randNum = (long) (new Random().nextDouble() * maxRange);
        et_raw_val.setText(Long.toString(randNum));

        String result = "oops";
        try{
            Locale l = Locale.getDefault();
            result = CurrencyTextFormatter.formatText(Long.toString(randNum), l, Locale.getDefault());
        }
        catch(IllegalArgumentException e){
            Log.e("MainActivity", e.getLocalizedMessage());
        }

        et_formatted_val.setText(result);
    }


    private void configureTestableLocalesTool(){

        Locale[] locales = Locale.getAvailableLocales();
        List<String> spinnerContents = new ArrayList<>();

        for (Locale locale : locales) {
            if(locale.getLanguage().equals("") || locale.getCountry().equals("")){
                continue;
            }
            spinnerContents.add(locale.getDisplayName() + ", " + locale.getLanguage() + ", " + locale.getCountry());
        }

        int startingPosition = 0;

        for (int i = 0; i < spinnerContents.size(); i++){
            if (spinnerContents.get(i).equals("en,US")){
                startingPosition = i;
                break;
            }
        }

        Collections.sort(spinnerContents, String::compareToIgnoreCase);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerContents);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        testable_locales_spinner.setAdapter(spinnerArrayAdapter);
        testable_locales_spinner.setSelection(startingPosition);

        configureViewForLocale((String) testable_locales_spinner.getSelectedItem());

        testable_locales_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                configureViewForLocale((String) testable_locales_spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                configureViewForLocale("US");
            }
        });
    }


    private void configureViewForLocale(String locale){
        //Using english for testing as not setting the language field results in odd formatting. Recommend not
        //building locales this way in a production environment if possible
        String[] localeParts = locale.split(", ");
        Locale localeInQuestion = new Locale.Builder().setRegion(localeParts[2]).setLanguage(localeParts[1]).build();
        String localeInfo = "Country: " +
                            localeInQuestion.getDisplayCountry() +
                            System.lineSeparator() +
                            "Country Code: " +
                            localeInQuestion.getCountry() +
                            System.lineSeparator() +
                            "Currency: " +
                            Currency.getInstance(localeInQuestion).getDisplayName() +
                            System.lineSeparator() +
                            "Currency Code: " +
                            Currency.getInstance(localeInQuestion).getCurrencyCode() +
                            System.lineSeparator() +
                            "Currency Symbol: " + Currency.getInstance(localeInQuestion).getSymbol();

        testable_locales_locale_data.setText(localeInfo);
        testable_locales_cet.configureViewForLocale(localeInQuestion);
    }

    private void configureDecimalDigitsTool(){
        decimal_digits_tool_number_picker.setMinValue(0);
        decimal_digits_tool_number_picker.setMaxValue(340);

        decimal_digits_tool_number_picker.setValue(2);

        decimal_digits_tool_cet.setDecimalDigits(decimal_digits_tool_number_picker.getValue());

        decimal_digits_tool_number_picker.setOnValueChangedListener(
            (picker, oldVal, newVal) -> decimal_digits_tool_cet.setDecimalDigits(newVal)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
