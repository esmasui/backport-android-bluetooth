package backport.android.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.util.Log;

abstract class BackportProperties {

	private static final String TAG = "BackportProperties";

	private static Properties sProperties = obtainProperties();

	public static final String getPackageName() {
		return sProperties.getProperty("package_name");
	}

	public static final String getRequestEnable() {
		String v = sProperties.getProperty("request_enable");

		if (v != null) {
			return v;
		}

		return "android.bluetooth.adapter.action.REQUEST_ENABLE";
	}

	public static final String getRequestDiscoverable() {
		String v = sProperties.getProperty("request_discoverable");

		if (v != null) {
			return v;
		}

		return "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
	}

	private static final Properties obtainProperties() {
		Properties tmp = new Properties();
		InputStream inStream = BackportProperties.class.getClassLoader()
				.getResourceAsStream("backport_android_bluetooth.properties");
		if (inStream == null) {
			Log
					.e(TAG,
							"can't locate backport_android_bluetooth.properties from classpath.");

			return null;
		}

		try {
			tmp.load(inStream);
			inStream.close();
		} catch (IOException e) {
		}

		return tmp;
	}
}
