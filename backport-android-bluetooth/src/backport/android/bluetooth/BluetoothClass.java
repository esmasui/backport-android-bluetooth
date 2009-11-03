/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (C) 2009, backport-android-bluetooth - http://code.google.com/p/backport-android-bluetooth/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package backport.android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Bluetooth class.
 * 
 * Bluetooth Class is a 32 bit field. The format of these bits is defined at
 * http://www.bluetooth.org/Technical/AssignedNumbers/baseband.htm (login
 * required). This class contains that 32 bit field, and provides constants and
 * methods to determine which Service Class(es) and Device Class are encoded in
 * that field.
 * 
 * Every Bluetooth Class is composed of zero or more service classes, and
 * exactly one device class. The device class is further broken down into major
 * and minor device class components.
 * 
 * Class is useful as a hint to roughly describe a device (for example to show
 * an icon in the UI), but does not reliably describe which Bluetooth profiles
 * or services are actually supported by a device. Accurate service discovery is
 * done through SDP requests.
 * 
 * Use getBluetoothClass() to retrieve the class for a remote device.
 * 
 */
public final class BluetoothClass implements Parcelable {

	// Classes
	/**
	 * Bluetooth device classes.
	 * 
	 * Each BluetoothClass encodes exactly one device class, with major and
	 * minor components.
	 * 
	 * The constants in BluetoothClass.Device represent a combination of major
	 * and minor components (the complete device class). The constants in
	 * BluetoothClass.Device.Major represent just the major device classes.
	 * 
	 */
	public static final class Device {

		public static final class Major {

			public static final int AUDIO_VIDEO = 0x0400;
			public static final int BITMASK = 0x1f00;
			public static final int COMPUTER = 0x0100;
			public static final int HEALTH = 0x0900;
			public static final int IMAGING = 0x0600;
			public static final int MISC = 0x00;
			public static final int NETWORKING = 0x0300;
			public static final int PERIPHERAL = 0x0500;
			public static final int PHONE = 0x0200;
			public static final int TOY = 0x0800;
			public static final int UNCATEGORIZED = 0x1f00;
			public static final int WEARABLE = 0x0700;
		}

		public static final int AUDIO_VIDEO_CAMCORDER = 0x0434;
		public static final int AUDIO_VIDEO_CAR_AUDIO = 0x0420;
		public static final int AUDIO_VIDEO_HANDSFREE = 0x0408;
		public static final int AUDIO_VIDEO_HEADPHONES = 0x0418;
		public static final int AUDIO_VIDEO_HIFI_AUDIO = 0x0428;
		public static final int AUDIO_VIDEO_LOUDSPEAKER = 0x0414;
		public static final int AUDIO_VIDEO_MICROPHONE = 0x0410;
		public static final int AUDIO_VIDEO_PORTABLE_AUDIO = 0x041c;
		public static final int AUDIO_VIDEO_SET_TOP_BOX = 0x0424;
		public static final int AUDIO_VIDEO_UNCATEGORIZED = 0x0400;
		public static final int AUDIO_VIDEO_VCR = 0x042c;
		public static final int AUDIO_VIDEO_VIDEO_CAMERA = 0x0430;
		public static final int AUDIO_VIDEO_VIDEO_CONFERENCING = 0x0440;
		public static final int AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER = 0x043c;
		public static final int AUDIO_VIDEO_VIDEO_GAMING_TOY = 0x0448;
		public static final int AUDIO_VIDEO_VIDEO_MONITOR = 0x0438;
		public static final int AUDIO_VIDEO_WEARABLE_HEADSET = 0x0404;
		private static final int BITMASK = 0x1ffc;
		public static final int COMPUTER_DESKTOP = 0x0104;
		public static final int COMPUTER_HANDHELD_PC_PDA = 0x0110;
		public static final int COMPUTER_LAPTOP = 0x010c;
		public static final int COMPUTER_PALM_SIZE_PC_PDA = 0x0114;
		public static final int COMPUTER_SERVER = 0x0108;
		public static final int COMPUTER_UNCATEGORIZED = 0x0100;
		public static final int COMPUTER_WEARABLE = 0x0118;
		public static final int HEALTH_BLOOD_PRESSURE = 0x0904;
		public static final int HEALTH_DATA_DISPLAY = 0x091c;
		public static final int HEALTH_GLUCOSE = 0x0910;
		public static final int HEALTH_PULSE_OXIMETER = 0x0914;
		public static final int HEALTH_PULSE_RATE = 0x0918;
		public static final int HEALTH_THERMOMETER = 0x0908;
		public static final int HEALTH_UNCATEGORIZED = 0x0900;
		public static final int HEALTH_WEIGHING = 0x090c;
		public static final int PHONE_CELLULAR = 0x0204;
		public static final int PHONE_CORDLESS = 0x0208;
		public static final int PHONE_ISDN = 0x0214;
		public static final int PHONE_MODEM_OR_GATEWAY = 0x0210;
		public static final int PHONE_SMART = 0x020c;
		public static final int PHONE_UNCATEGORIZED = 0x0200;
		public static final int TOY_CONTROLLER = 0x0810;
		public static final int TOY_DOLL_ACTION_FIGURE = 0x080c;
		public static final int TOY_GAME = 0x0814;
		public static final int TOY_ROBOT = 0x0804;
		public static final int TOY_UNCATEGORIZED = 0x0800;
		public static final int TOY_VEHICLE = 0x0808;
		public static final int WEARABLE_GLASSES = 0x0714;
		public static final int WEARABLE_HELMET = 0x0710;
		public static final int WEARABLE_JACKET = 0x070c;
		public static final int WEARABLE_PAGER = 0x0708;
		public static final int WEARABLE_UNCATEGORIZED = 0x0700;

		public static final int WEARABLE_WRIST_WATCH = 0x0704;
	}

	/**
	 * Bluetooth service classes.
	 * 
	 * Each BluetoothClass encodes zero or more service classes.
	 * 
	 */
	public static final class Service {

		public static final int AUDIO = 0x200000;
		private static final int BITMASK = 0xffe000;
		public static final int CAPTURE = 0x080000;
		public static final int INFORMATION = 0x800000;
		public static final int LIMITED_DISCOVERABILITY = 0x2000;
		public static final int NETWORKING = 0x020000;
		public static final int OBJECT_TRANSFER = 0x100000;
		public static final int POSITIONING = 0x010000;
		public static final int RENDER = 0x040000;
		public static final int TELEPHONY = 0x400000;
	}

	private final int mClass;

	// Fields
	public static final Creator<BluetoothClass> CREATOR = new Creator<BluetoothClass>() {

		@Override
		public BluetoothClass createFromParcel(Parcel source) {

			return new BluetoothClass(source);
		}

		@Override
		public BluetoothClass[] newArray(int size) {

			return new BluetoothClass[size];
		}
	};

	public static final int ERROR = 0xff000000;

	public static final int PROFILE_A2DP = 0x01;

	public static final int PROFILE_HEADSET = 0x00;
	public static final int PROFILE_OPP = 0x02;

	// Constructors
	public BluetoothClass(int deviceClass) {

		mClass = deviceClass;
	}

	private BluetoothClass(Parcel source) {

		mClass = source.readInt();
	}

	// Methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {

		return 0;
	}

	public boolean doesClassMatch(int deviceClass) {

		return mClass == deviceClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {

		if (this == o) {

			return true;
		}

		if (!(o instanceof BluetoothClass)) {

			return false;
		}

		BluetoothClass c = (BluetoothClass) o;

		return mClass == c.mClass;
	}

	/**
	 * Return the (major and minor) device class component of this
	 * BluetoothClass.
	 * 
	 * Values returned from this function can be compared with the public
	 * constants in BluetoothClass.Device to determine which device class is
	 * encoded in this Bluetooth class.
	 * 
	 * @return device class component
	 */
	public int getDeviceClass() {

		if (mClass == ERROR) {
			return ERROR;
		}

		return (mClass & Device.BITMASK);
	}

	/**
	 * Return the major device class component of this Bluetooth class.
	 * 
	 * Values returned from this function can be compared with the public
	 * constants in BluetoothClass.Device.Major to determine which major class
	 * is encoded in this Bluetooth class.
	 * 
	 * @return major device class component
	 */
	public int getMajorDeviceClass() {

		if (mClass == ERROR) {
			return ERROR;
		}

		return (mClass & Device.Major.BITMASK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {

		return mClass;
	}

	/**
	 * Return true if the specified service class is supported by this class.
	 * 
	 * Valid service classes are the public constants in BluetoothClass.Service.
	 * For example, AUDIO.
	 * 
	 * @param service
	 *            valid service class
	 * @return true if the service class is supported
	 */
	public boolean hasService(int service) {

		if (mClass == ERROR) {
			return false;
		}

		return ((mClass & Service.BITMASK & service) != 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return Integer.toString(mClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel out, int flags) {

		out.writeInt(mClass);
	}

}
