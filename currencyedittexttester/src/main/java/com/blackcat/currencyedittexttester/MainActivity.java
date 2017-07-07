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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.blackcat.currencyedittext.CurrencyTextFormatter;

import java.util.Currency;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends Activity {

    CurrencyEditText cet;
    TextView raw_val;
    TextView string_val;
    TextView et_raw_val;
    TextView et_formatted_val;
    TextView testable_locales_locale_data;
    Spinner testable_locales_spinner;
    CurrencyEditText testable_locales_cet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cet = (CurrencyEditText) findViewById(R.id.cet);
        raw_val = (TextView) findViewById(R.id.raw_val);
        string_val = (TextView) findViewById(R.id.string_val);
        et_raw_val = (TextView) findViewById(R.id.et_raw_val);
        et_formatted_val = (TextView) findViewById(R.id.et_formatted_val);
        testable_locales_spinner = (Spinner) findViewById(R.id.spinner_testable_locales);
        testable_locales_locale_data = (TextView) findViewById(R.id.testable_locales_locale_info);
        testable_locales_cet = (CurrencyEditText) findViewById(R.id.testable_locales_cet);

        configureRefreshButton();
        configureTestableLocalesTool();

    }

    private void configureRefreshButton(){
        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                raw_val.setText(Long.toString(cet.getRawValue()));
                string_val.setText(cet.formatCurrency(Long.toString(cet.getRawValue())));
                Log.d("MainActivity", "Locale: " + getResources().getConfiguration().locale.toString());
                Log.d("MainActivity", "DefaultLocale: " + Locale.getDefault());

                long maxRange = 15000000;
                long randNum = (long) (new Random().nextDouble() * maxRange);
                et_raw_val.setText(Long.toString(randNum));

                String result = "oops";
                try{
                    Locale l = Locale.getDefault();
                    result = CurrencyTextFormatter.formatText(Long.toString(randNum), Currency.getInstance(l), l, Locale.getDefault());
                }
                catch(IllegalArgumentException e){
                    Log.e("MainActivity", e.getLocalizedMessage());
                }

                et_formatted_val.setText(result);
            }
        });
    }

    private void configureTestableLocalesTool(){

        String[] spinnerContents = Locale.getISOCountries();

        int startingPosition = 0;

        for (int i = 0; i < spinnerContents.length; i++){
            if (spinnerContents[i].equals("US")){
                startingPosition = i;
                break;
            }
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerContents);
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

        Locale localeInQuestion = new Locale.Builder().setRegion(locale).build();
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
                             Currency.getInstance(localeInQuestion).getCurrencyCode();

        testable_locales_locale_data.setText(localeInfo);
        testable_locales_cet.configureViewForLocale(localeInQuestion);
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
