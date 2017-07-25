CurrencyEditText
================

`CurrencyEditText` is an extension of Android's EditText view object. It is a module designed to provide ease-of-use when using an EditText field for gathering currency information from a user. 

`CurrencyEditText` is designed to work with all ISO-3166 compliant locales (which *should* include all locales Android ships will).

If you find that a certain locale is causing issues, please open an Issue in the Issue Tracker.


Getting Started
================

Getting started is easy. Just add the library as a dependency in your projects build.gradle file. Be sure you've listed mavenCentral as a repository:

```Gradle
repositories {
    mavenCentral()
}
        
dependencies{
    compile 'com.github.blackcat27:library:2.0.1-SNAPSHOT'
}
```
        
Alternatively, if you're having issues with mavenCentral, try JitPack:

```Gradle
repositories{
    maven { url "https://jitpack.io" }
}
        
dependencies {
    compile 'com.github.BlacKCaT27:CurrencyEditText:2.0.1'
}
```

Note: Users of the latest Android Studio Gradle plugin should migrate their build.gradle files to use 'implementation' instead of 'compile'.


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

Hints
===============
By default, `CurrencyEditText` provides a 'hint' value for the text box. This default value is the Currency Code symbol for the users given Locale setting. This is 
useful for debugging purposes, as well as provides clean and easy to understand guidance to the user. 

If you'd prefer to set your own hint text, simply set the hint the same way you would for any other `EditText` field. You can do this either in your XML layout
or in code. To remove the hint entirely, set the hint to an empty string ("").



Attributes
===============



By default, `CurrencyEditText` does not allow negative number input. This is due to the fact that by far the most common use-case for currency input involves transaction information, where the absolute value of the transaction is entered separately from declaring a deposit or withdrawl. 

However, if you do need to support negative number input, you can enable it by setting the allow_negative_values attribute.

In xml:
```xml
<com.blackcat.currencyedittext.CurrencyEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:allow_negative_values="true"
    />
```

In java:

```java
CurrencyEditText tb = (CurrencyEditText) findViewById(R.id.test);
tb.setAllowNegativeValues(true);
```

You can also set the decimal digits position (see below) via xml or java

In xml:
```xml
<com.blackcat.currencyedittext.CurrencyEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:decimal_digits="0"
    />
```

In java:
```java
CurrencyEditText tb = (CurrencyEditText) findViewById(R.id.test);
tb.setDecimalDigits(0);
```
Retrieving and Handling Input
=============================

As `CurrencyEditText` is an extension of the `EditText` class, it contains all the same getters and setters that `EditText` provides. 

To retrieve the fully formatted String value as shown to the user, simply call your `CurrencyEditText` objects `getText()` method.

However, you'll likely need to actually do something useful with the users input. To help developers more easily retrieve this information, `CurrencyEditText` provides the `getRawValue()` method. This method provides back the raw numeric values as they were input by the user, and should be treated as if it were a whole value of the users local currency.
For example, if the text of the field is $13.37, this method will return a Long with a value of 1337, as penny is the lowest denomination for USD. 

It is the responsibility of the calling application to handle this value appropriately. Keep in mind that dividing this number to convert it to some other denomination
 could possibly result in floating point rounding errors, and should be done with great caution. 
 
To assist with needing to perform work on locale-specific values after retrieval, `CurrencyEditText` provides the getLocale() method which returns the locale currently being used by that instance for its formatting. 

Locales
=======

CurrencyEditText relies on a `Locale` object to properly format the given value. There are two `Locale` variables that are exposed via getters and setters on a given `CurrencyEditText` object: locale and defaultLocale.

`locale` is the users default locale setting based upon their Android configuration settings. This value is editable by the user in Android settings, as well as via the `CurrencyEditText` API. Note that this value, when retrieved from the end-users device, *is not* always compatible with ISO-3166. This is used as the "happy path" variable, but due to the potential lack of ISO-3166 compliance, `CurrencyEditText` will fall back to `defaultLocale` in the event of an error.
 
`defaultLocale` is a separate value which is treated as a fallback in the event that the provided locale value fails. This may occur due to the `locale` value not being part of the ISO-3166 standard. See `Java.util.Locale.getISOCountries()` for a list of supported values. Note that the list of supported values is hard-coded into each version of Java, therefore over time, the list of supported ISO's may change.

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

Decimal Digits
===============

By default, the `CurrencyEditText` text formatter will use the `locale` object to obtain information about the expected currency. This includes the location of the decimal separator for lower denominations (e.g. dollars vs. cents). If you would like to alter the decimal placement position, you can use the setDecimalDigits() method. This is very useful in some cases, for instance, if you only wish to show whole dollar amounts. 

```java
CurrencyEditText cet = new CurrencyEditText();
 
... user inputs 1000
 
//currentText is "$10.00"
String currentText = cet.getText();

cet.setDecimalDigits(0);

//newText is "$1,000"
String newText = cet.getText();

```

DecimalDigits can also be set in the XML layout if you don't want to obtain a java reference to the view.

Note that the valid range of DecimalDigits is 0 - 340. Any value outside of that range will throw an `IllegalArgumentException`.


Try it out
===========
This repo contains the currencyedittexttester project which provides a testing application to showcase `CurrencyEditText` functionality. You're encouraged to pull down and run the app to get a feel for how `CurrencyEditText` works.

Why doesn't CurrencyEditText do \<x\>?
====================================

`CurrencyEditText` is designed to be a small, lightweight module to provide ease-of-use to developers. If there is functionality missing that you would like to see added, submit a new Issue and label it as an Enhancement, and I will take a look. I make no guarantees that I will agree to implement it.

Use at your own risk!
=====================

As called out in the Apache license (which this project falls under), by using this software you agree to use it AS-IS. I make no claims that this code is 100% bug-free or otherwise without issue. While I've done my best to ensure that rounding errors don't come into play and that all codeflows have been tested, I cannot 
guarantee or provide any sort of warranty that this code will work for you. The onus is on you, and you alone, to analyze this software and determine if it's featureset and quality meet your needs.
