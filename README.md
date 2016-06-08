CurrencyEditText
================

`CurrencyEditText` is an extension of Android's EditText view object. It is a module designed to provide ease-of-use when using an EditText field for gathering currency information from a user. 

`CurrencyEditText` provides support for a large number of localities/currencies. USD/GPB/Euro are officially supported, though all currencies as supported by ISO 4217 should work.

If you find that a certain currency is causing issues, please open an Issue in the Issue Tracker.


Getting Started
================

Getting started is easy. Just add the library as a dependency in your projects build.gradle file. Be sure you've listed mavenCentral as a repository:

```Gradle
repositories {
    mavenCentral()
}
        
dependencies{
    compile 'com.github.blackcat27:library:1.4.0'
}
```
        
Alternatively, if you're having issues with mavenCentral, try JitPack:

```Gradle
repositories{
    maven { url "https://jitpack.io" }
}
        
dependencies {
    compile 'com.github.BlacKCaT27:CurrencyEditText:v1.4.0'
}
```


Using The Module
================

Using the module is not much different from using any other EditText view. Simply define the view in your XML layout:

```xml
<com.blackcat.currencyedittext.CurrencyEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    />
```

You're done! The `CurrencyEditText` module handles all the string manipulation and input monitoring required to allow for a clean, easy-to-use
currency entry system.

In Action
===============

In its default state, `CurrencyEditText` view appears as an `EditText` box with the hint set to the users local currency symbol.

![Default State](/../screenshots/screenshots/CurrencyEditText.PNG?raw=true)

As a user enters additional values, they will appear starting with the right-most digit, pushing older digit entries left as they type.

![Entered Text](/../screenshots/screenshots/CurrencyEditText_show_formatting.PNG?raw=true)


Depending on the users Locale and Language settings, the displayed text will automatically be formatted to the users local standard. For example, when the users selects
  "German", the Euro symbol appears on the right, as seen below.

![Entered Text](/../screenshots/screenshots/CurrencyEditText_show_formatting_in_german.PNG?raw=true)


Attributes
===============

By default, `CurrencyEditText` provides a 'hint' value for the text box. This default value is the Currency Code symbol for the users given Locale setting. This is 
useful for debugging purposes, as well as provides clean and easy to understand guidance to the user. 

If you'd prefer to set your own hint text, simply set the hint the same way you would for any other `EditText` field. You can do this either in your XML layout
or in code.

If you'd like to disable the default hint entirely, simply set the `DefaultHint` attribute in your XML layout to false:

```xml
<com.blackcat.currencyedittext.CurrencyEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    CurrencyTextBox:enable_default_hint="false"
    />
```

Alternatively, you can also call `setDefaultHintEnabled(false)` on the `CurrencyEditText` object in your code:

```java
CurrencyEditText tb = (CurrencyEditText) findViewById(R.id.test);
tb.setDefaultHintEnabled(false);
```



Additionally, you can enable the input of negative numbers by setting the allow_negative_values attribute.

```xml
<com.blackcat.currencyedittext.CurrencyEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:allow_negative_values="true"
    />
```

From java:

```java
CurrencyEditText tb = (CurrencyEditText) findViewById(R.id.test);
tb.setAllowNegativeValues(true);
```

Retrieving and Handling Input
=============================

As `CurrencyEditText` is an extension of the `EditText` class, it contains all the same getters and setters that `EditText` provides. 

To retrieve the fully formatted String value as shown to the user, simply call your `CurrencyEditText` objects `getText()` method.

However, you'll likely need to actually do something useful with the users input. To help developers more easily retrieve this information, `CurrencyEditText` provides the `getRawValue()` method. This method provides back the raw numeric values as they were input by the user, and should be treated as if it were a whole value of the users local currency.
For example, if the text of the field is $13.37, this method will return a Long with a value of 1337, as penny is the lowest denomination for USD. 

It is the responsibility of the calling application to handle this value appropriately. Keep in mind that dividing this number to convert it to some other denomination
 could possibly result in floating point rounding errors, and should be done with great caution. 
 
To help developers better facilitate the need to properly handle locale differences in currency, `CurrencyEditText` also provides a `getLocale()` method.

This method is really just a convenience method for retrieving information about a users locale. It is functionally equivalent to pulling the locale from the users configuration on their device. It is recommended 
that developers take a look at what the Locale and Currency classes offer in terms of denominations, decimal placement, string formatting, etc. 


Locales
=======

CurrencyEditText relies on a `Locale` object to properly format the given value. There are two `Locale` variables that are exposed via getters and setters on a given `CurrencyEditText` object: locale and defaultLocale.

locale is the users default locale setting based upon their Android configuration settings. This value is editable by the user in Android settings. Note that this value *is not* always compatible with ISO-3166. This is used as the "happy path" variable.
 
defaultLocale is a separate value which is treated as a fallback in the event that the provided locale value fails. This may occur due to that value not being part of the ISO-3166 standard. See `Java.util.Locale.getISOCountries()` for a list of supported values. Note that the list of supported values is hard-coded into each version of Java, therefore over time, the list of supported ISO's may change.

The default value for defaultLocale is `Locale.US`. Both this, and the locale value, can be overwritten using setters found on the `CurrencyEditText` object. Be very careful to ensure that should you override defaultLocale's value, you only use values supported by ISO-3166, or an IllegalArgumentException will be thrown by the formatter.

Formatting Values
=================

If you'd like to retrieve a formatted version of a raw value you previously accepted from a user, use the `formatCurrency()` method of `CurrencyEditText`. It takes one parameter: a string representing the value you'd
like to have formatted. It is expected that this value will be in the same format as the returning value from the `getRawValue()` method. For example:

```java
//rawVal contains "1000"
CurrencyEditText cet = new CurrencyEditText();

... user inputs "$10.00"
    
//rawVal is 1000
Long rawVal = cet.getRawValue();

//formattedVal accepts "1000" and returns "$10.00"
String formattedVal = cet.formatCurrency(Long.toString(rawVal));

//or

String formattedVal = cet.formatCurrency(rawVal);
```
        

Why doesn't CurrencyEditText do \<x\>?
====================================

`CurrencyEditText` is designed to be a small, lightweight module to provide ease-of-use to developers. If there is functionality missing that you would like to see added, submit a new Issue and label it as an Enhancement, and I will take a look. I make no guarantees that I will agree to implement it.

Use at your own risk!
=====================

As called out in the Apache license (which this project falls under), by using this software you agree to use it AS-IS. I make no claims that this code is 100% bug-free or otherwise without issue. While I've done my best to ensure that rounding errors don't come into play and that all codeflows have been tested, I cannot 
guarantee or provide any sort of warranty that this code will work for you. The onus is on you, and you alone, to analyze this software and determine if it's featureset and quality meet your needs.
