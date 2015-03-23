This package is the backport of android.bluetooth API, introduced in Android 2.0(or higher) to older Android platforms.

**IMPORTANT**

new version(ver.2) developing on
trunk/backport-android-bluetooth201




## Android 2.0 Bluetooth API ##

See android.bluetooth | Android Developers

http://developer.android.com/reference/android/bluetooth/package-summary.html

## Requirements ##

Android 1.5


## Package Names ##

Since user libraries cannot define classes in android. packages, all the original package names have been prefixed with backport. For instance, android.bluetooth.Hoge becomes backport.android.bluetooth.Hoge.


## Install ##

1. download backport-android-bluetooth2.jar, and put into your projects's reference libraries.


2. put backport\_android\_bluetooth.properties in to your src directory.


3. add following line to your AndroidManifest.xml.



### backport\_android\_bluetooth.properties ###
```
#permission_name = ${your package name).PERMISSION_BLUETOOTH
permission_name = com.example.bluetooth.PERMISSION_BLUETOOTH

#request_enable = ${your package name}.action.REQUEST_ENABLE
request_enable = com.example.bluetooth.action.REQUEST_ENABLE

#request_discoverable = ${your package name}.action.REQUEST_DISCOVERABLE
request_discoverable = com.example.bluetooth.action.REQUEST_DISCOVERABLE
```


### AndroidManifest.xml ###
```
<application...>
		

  <activity android:name="backport.android.bluetooth.RequestEnableActivity"
    android:label="Bluetooth Permission Request" android:noHistory="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:configChanges="orientation|keyboardHidden">
      <intent-filter>

        <!-- ${your package name}.action.REQUEST_ENABLE -->
        <action android:name="com.example.bluetooth.action.REQUEST_ENABLE" />

        <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
  </activity>


  <activity android:name="backport.android.bluetooth.RequestDiscoverableActivity"
    android:label="Bluetooth Permission Request" android:noHistory="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:configChanges="orientation|keyboardHidden">
      <intent-filter>

        <!-- ${your package name}.action.REQUEST_DISCOVERABLE -->
        <action android:name="com.example.bluetooth.action.REQUEST_DISCOVERABLE" />

        <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
  </activity>


  <receiver android:name="backport.android.bluetooth.BluetoothIntentRedirector">
    <intent-filter>
      <action android:name="android.bluetooth.intent.action.DISCOVERY_COMPLETED" />
      <action android:name="android.bluetooth.intent.action.DISCOVERY_STARTED" />
      <action android:name="android.bluetooth.intent.action.NAME_CHANGED" />
      <action android:name="android.bluetooth.intent.action.SCAN_MODE_CHANGED" />
      <action android:name="android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED" />
      <action android:name="android.bluetooth.intent.action.REMOTE_DEVICE_CONNECTED" />
      <action android:name="android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECTED" />
      <action android:name="android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECT_REQUESTED" />
      <action android:name="android.bluetooth.intent.action.BOND_STATE_CHANGED" />
      <action android:name="android.bluetooth.intent.action.PAIRING_REQUEST" />
      <action android:name="android.bluetooth.intent.action.PAIRING_CANCEL" />
      <action android:name="android.bluetooth.intent.action.REMOTE_DEVICE_CLASS_UPDATED" />
      <action android:name="android.bluetooth.intent.action.REMOTE_DEVICE_FOUND" />
      <action android:name="android.bluetooth.intent.action.REMOTE_NAME_UPDATED" />
    </intent-filter>
  </receiver>

</application>


<uses-permission android:name="android.permission.BLUETOOTH"></uses-permission>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"></uses-permission>

<!-- ${your package name}.PERMISSION -->
<uses-permission android:name="com.example.bluetooth.PERMISSION"></uses-permission>

```



## Getting Started ##

```
BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
```

## Build instructions ##
If you would like to build your own backport-android-bluetooth2.jar.

Select the build.xml, "run as ant" to run from Eclipse.
Then, in the root directory of the project will backport-android-bluetooth2.jar has been made


## Limitations ##

  1. BluetoothAdapter#listenUsingRfcommWithServiceRecord() method does not update Bluetooth service record.

## History ##
### ver.2.2.2 ###
fix [issue #10](https://code.google.com/p/backport-android-bluetooth/issues/detail?id=#10)

### ver.2.2.1 ###
implements BluetoothClass.

### ver.2.2 ###
supported for Droid Eris.

### ver.2.1.2 ###

fix bug throw NPE when backport\_android\_bluetooth.properties is not exists.

### ver.2.1.1 ###

fix bug when open enable/discoverable dialog on 1.5(cupcake)

### ver.2.1 ###

now supported android1.5(cupcake)

### ver.2 ###

trunk/backport-android-bluetooth201

fix many bugs.



