Smartomagneter
===========================

An app that allows users to measure the magnetic strength using their smartphone's magnetic sensor via Java or native implementation.

However, the real reason for making this app was to demonstrate:

* implementation of Model-View-Presenter (MVP) architectural pattern
* implementation of a layered architectural approach (framework, adapter, domain)
* how to use abstraction to achieve scalability and flexibility in your app's code *(though for small projects like this one can be considered as a little overkill :P)*
* how easy is to test your code when the app's architecture is allowing it **(to do)**
* how to use sensor.h or how to work with sensors on Android using Java and/or native code
* how to call APIs from the Android NDK using Java Native Interface

## How to switch between *Java* and *Native* implementation?

* Long press on an empty space. 

## How to calibrate the magnetic sensor?

* Move your phone following a figure 8 pattern **OR**
* Rotate 360deg around each of the three axes.

## Screenshots
![Running java implementation](https://github.com/mrmitew/Smartomagneter/blob/master/design/screenshot-java-impl-uncalibrated.png) ![Running native implementation](https://github.com/mrmitew/Smartomagneter/blob/master/design/screenshot-native-impl-calibrated.png)

## Requirements
* Min sdk is API level 9
* Android NDK. You can download it from the SDK Manager.

## Communication
* Author: Stefan Mitev
* E-mail: mr.mitew [at] gmail . com
* [Github issues](https://github.com/mrmitew/Smartomagneter/issues)

## Changelog
* 1.0.0 - Initial version
