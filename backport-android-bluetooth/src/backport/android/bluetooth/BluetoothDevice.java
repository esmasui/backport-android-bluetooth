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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.bluetooth.BluetoothError;
import android.bluetooth.IBluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

/**
 * Represents a remote Bluetooth device.
 * 
 * Use getRemoteDevice(String) to create a BluetoothDevice.
 * 
 * This class is really just a thin wrapper for a Bluetooth hardware address.
 * Objects of this class are immutable. Operations on this class are performed
 * on the remote Bluetooth hardware address, using the BluetoothAdapter that was
 * used to create this BluetoothDevice.
 * 
 */
public final class BluetoothDevice implements Parcelable {

	private static final String TAG = BluetoothDevice.class.getSimpleName();

	private static final String EMPTY = "";

	private static final int PORT = 12;

	/**
	 * Broadcast Action: Indicates a low level (ACL) connection has been
	 * established with a remote device.
	 * 
	 * Always contains the extra field EXTRA_DEVICE.
	 * 
	 * ACL connections are managed automatically by the Android Bluetooth stack.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";

	/**
	 * Broadcast Action: Indicates a low level (ACL) disconnection from a remote
	 * device.
	 * 
	 * Always contains the extra field EXTRA_DEVICE.
	 * 
	 * ACL connections are managed automatically by the Android Bluetooth stack.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_ACL_DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED";

	/**
	 * Broadcast Action: Indicates that a low level (ACL) disconnection has been
	 * requested for a remote device, and it will soon be disconnected.
	 * 
	 * This is useful for graceful disconnection. Applications should use this
	 * intent as a hint to immediately terminate higher level connections
	 * (RFCOMM, L2CAP, or profile connections) to the remote device.
	 * 
	 * Always contains the extra field EXTRA_DEVICE.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_ACL_DISCONNECT_REQUESTED = "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED";

	/**
	 * Broadcast Action: Indicates a change in the bond state of a remote
	 * device. For example, if a device is bonded (paired).
	 * 
	 * Always contains the extra fields EXTRA_DEVICE, EXTRA_BOND_STATE and
	 * EXTRA_PREVIOUS_BOND_STATE.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";

	/**
	 * Broadcast Action: Bluetooth class of a remote device has changed.
	 * 
	 * Always contains the extra fields EXTRA_DEVICE and EXTRA_CLASS.
	 * 
	 * Requires BLUETOOTH to receive. See Also
	 * 
	 * ERROR(BluetoothClass} /{@link DonutBluetoothClass})
	 */
	public static final String ACTION_CLASS_CHANGED = "android.bluetooth.device.action.CLASS_CHANGED";

	/**
	 * Broadcast Action: Remote device discovered.
	 * 
	 * Sent when a remote device is found during discovery.
	 * 
	 * Always contains the extra fields EXTRA_DEVICE and EXTRA_CLASS. Can
	 * contain the extra fields EXTRA_NAME and/or EXTRA_RSSI if they are
	 * available.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND";

	/**
	 * Broadcast Action: Indicates the friendly name of a remote device has been
	 * retrieved for the first time, or changed since the last retrieval.
	 * 
	 * Always contains the extra fields EXTRA_DEVICE and EXTRA_NAME.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_NAME_CHANGED = "android.bluetooth.device.action.NAME_CHANGED";

	/**
	 * Indicates the remote device is bonded (paired).
	 * 
	 * A shared link keys exists locally for the remote device, so communication
	 * can be authenticated and encrypted.
	 * 
	 * Being bonded (paired) with a remote device does not necessarily mean the
	 * device is currently connected. It just means that the ponding procedure
	 * was compeleted at some earlier time, and the link key is still stored
	 * locally, ready to use on the next connection.
	 */
	public static final int BOND_BONDED = 12; // (0x0000000c)

	/**
	 * Indicates bonding (pairing) is in progress with the remote device.
	 */
	public static final int BOND_BONDING = 11; // (0x0000000b)

	/**
	 * Indicates the remote device is not bonded (paired).
	 * 
	 * There is no shared link key with the remote device, so communication (if
	 * it is allowed at all) will be unauthenticated and unencrypted.
	 */
	public static final int BOND_NONE = 10; // (0x0000000a)

	public static final Creator<BluetoothDevice> CREATOR = new Creator<BluetoothDevice>() {

		public BluetoothDevice createFromParcel(Parcel source) {

			return new BluetoothDevice(source);
		}

		public BluetoothDevice[] newArray(int size) {

			return new BluetoothDevice[size];
		}
	};

	/**
	 * Sentinel error value for this class. Guaranteed to not equal any other
	 * integer constant in this class. Provided as a convenience for functions
	 * that require a sentinel error value, for example:
	 * 
	 * Intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
	 * BluetoothDevice.ERROR)
	 */
	public static final int ERROR = -2147483648; // (0x80000000)

	/**
	 * Used as an int extra field in ACTION_BOND_STATE_CHANGED intents. Contains
	 * the bond state of the remote device.
	 * 
	 * Possible values are: BOND_NONE, BOND_BONDING, BOND_BONDED.
	 */
	public static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE";

	/**
	 * Used as an Parcelable BluetoothClass extra field in ACTION_FOUND and
	 * ACTION_CLASS_CHANGED intents.
	 */
	public static final String EXTRA_CLASS = "android.bluetooth.device.extra.CLASS";

	/**
	 * Used as a Parcelable BluetoothDevice extra field in every intent
	 * broadcast by this class. It contains the BluetoothDevice that the intent
	 * applies to.
	 */
	public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";

	/**
	 * Used as a String extra field in ACTION_NAME_CHANGED and ACTION_FOUND
	 * intents. It contains the friendly Bluetooth name.
	 */
	public static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME";

	/**
	 * Used as an int extra field in ACTION_BOND_STATE_CHANGED intents. Contains
	 * the previous bond state of the remote device.
	 * 
	 * Possible values are: BOND_NONE, BOND_BONDING, BOND_BONDED.
	 */
	public static final String EXTRA_PREVIOUS_BOND_STATE = "android.bluetooth.device.extra.PREVIOUS_BOND_STATE";

	/**
	 * Used as an optional short extra field in ACTION_FOUND intents. Contains
	 * the RSSI value of the remote device as reported by the Bluetooth
	 * hardware.
	 */
	public static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI";

	public static byte[] convertPinToBytes(String pin) {

		if (pin == null) {

			return null;
		}

		byte[] pinBytes;

		try {

			pinBytes = pin.getBytes("UTF8");
		} catch (UnsupportedEncodingException uee) {

			Log.e(TAG, "UTF8 not supported?!?"); // this should not happen

			return null;
		}

		if (pinBytes.length <= 0 || pinBytes.length > 16) {

			return null;
		}

		return pinBytes;
	}

	private final String mAddress;

	BluetoothDevice(String address) {

		mAddress = address;
	}

	private BluetoothDevice(Parcel in) {

		mAddress = in.readString();
	}

	/**
	 * Create an RFCOMM BluetoothSocket ready to start a secure outgoing
	 * connection to this remote device using SDP lookup of uuid.
	 * 
	 * This is designed to be used with
	 * listenUsingRfcommWithServiceRecord(String, UUID) for peer-peer Bluetooth
	 * applications.
	 * 
	 * Use connect() to intiate the outgoing connection. This will also perform
	 * an SDP lookup of the given uuid to determine which channel to connect to.
	 * 
	 * The remote device will be authenticated and communication on this socket
	 * will be encrypted.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @param uuid
	 *            service record uuid to lookup RFCOMM channel
	 * @return a RFCOMM BluetoothServerSocket ready for an outgoing connection
	 * @throws on
	 *             error, for example Bluetooth not available, or insufficient
	 *             permissions
	 */
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
			throws IOException {

		return BluetoothSocket.createRfcommSocket(mAddress, PORT);
	}

	/**
	 * Describe the kinds of special objects contained in this Parcelable's
	 * marshalled representation.
	 * 
	 * @return a bitmask indicating the set of special object types marshalled
	 *         by the Parcelable.
	 */
	public int describeContents() {

		return 0;
	}

	/**
	 * Compares this instance with the specified object and indicates if they
	 * are equal. In order to be equal, o must represent the same object as this
	 * instance using a class-specific comparison. The general contract is that
	 * this comparison should be both transitive and reflexive.
	 * 
	 * The implementation in Object returns true only if o is the exact same
	 * object as the receiver (using the == operator for comparison). Subclasses
	 * often implement equals(Object) so that it takes into account the two
	 * object's types and states.
	 * 
	 * The general contract for the equals(Object) and hashCode() methods is
	 * that if equals returns true for any two objects, then hashCode() must
	 * return the same value for these objects. This means that subclasses of
	 * Object usually override either both methods or none of them.
	 * 
	 * @param o
	 *            the object to compare this instance with.
	 * @return true if the specified object is equal to this Object; false
	 *         otherwise.
	 */
	public boolean equals(Object o) {

		if (this == o) {

			return true;
		}

		if (!(o instanceof BluetoothDevice)) {

			return false;
		}

		BluetoothDevice c = (BluetoothDevice) o;

		return mAddress.equals(c.mAddress);
	}

	/**
	 * Returns the hardware address of this BluetoothDevice.
	 * 
	 * For example, "00:11:22:AA:BB:CC".
	 * 
	 * @return Bluetooth hardware address as string
	 */
	public String getAddress() {

		return mAddress;
	}

	/**
	 * Get the Bluetooth class of the remote device.
	 * 
	 * Requires BLUETOOTH.
	 * 
	 * @return Bluetooth class object, or null on error
	 */
	public BluetoothClass getBluetoothClass() {

		try {

			return new BluetoothClass(getBluetoothService().getRemoteClass(
					mAddress));
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return null;
		}
	}

	/**
	 * Get the bond state of the remote device.
	 * 
	 * Possible values for the bond state are: BOND_NONE, BOND_BONDING,
	 * BOND_BONDED.
	 * 
	 * Requires BLUETOOTH.
	 * 
	 * @return the bond state
	 */
	public int getBondState() {

		try {

			return getBluetoothService().getBondState(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return BluetoothError.ERROR_IPC;
		}
	}

	/**
	 * Get the friendly Bluetooth name of the remote device.
	 * 
	 * The local adapter will automatically retrieve remote names when
	 * performing a device scan, and will cache them. This method just returns
	 * the name for this device from the cache.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return the Bluetooth name, or null if there was a problem.
	 */
	public String getName() {

		try {

			return getBluetoothService().getRemoteName(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return null;
		}
	}

	/**
	 * Returns an integer hash code for this object. By contract, any two
	 * objects for which equals(Object) returns true must return the same hash
	 * code value. This means that subclasses of Object usually override both
	 * methods or neither method.
	 * 
	 * @return this object's hash code.
	 */
	public int hashCode() {

		return mAddress.hashCode();
	}

	/**
	 * Returns a string representation of this BluetoothDevice.
	 * 
	 * Currently this is the Bluetooth hardware address, for example
	 * "00:11:22:AA:BB:CC". However, you should always use getAddress() if you
	 * explicitly require the Bluetooth hardware address in case the toString()
	 * representation changes in the future.
	 * 
	 * @return string representation of this BluetoothDevice
	 */
	public String toString() {

		return mAddress;
	}

	/**
	 * Flatten this object in to a Parcel.
	 * 
	 * @param out
	 *            The Parcel in which the object should be written.
	 * @param flags
	 *            Additional flags about how the object should be written. May
	 *            be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 */
	public void writeToParcel(Parcel out, int flags) {

		out.writeString(mAddress);
	}

	// Methods No documentation.

	public boolean cancelBondProcess() {

		try {

			return getBluetoothService().cancelBondProcess(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	public boolean cancelPairingUserInput() {

		throw new UnsupportedOperationException();
	}

	public boolean createBond() {

		try {
			return getBluetoothService().createBond(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	public BluetoothSocket createInsecureRfcommSocket(int port)
			throws IOException {

		return BluetoothSocket.createInsecureRfcommSocket(mAddress, port);
	}

	public BluetoothSocket createRfcommSocket(int port) throws IOException {

		return BluetoothSocket.createRfcommSocket(mAddress, port);
	}

	public BluetoothSocket createScoSocket() throws IOException {

		throw new UnsupportedOperationException();
	}

	public boolean fetchUuidsWithSdp() {

		throw new UnsupportedOperationException();
	}

	// public int getServiceChannel(ParcelUuid parcel) {
	//
	// throw new UnsupportedOperationException();
	// }

	public boolean getTrustState() {

		throw new UnsupportedOperationException();
	}

	public boolean removeBond() {

		try {

			return getBluetoothService().removeBond(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	// public ParcelUuid[] getUuids() {
	//
	// throw new UnsupportedOperationException();
	// }

	public boolean setPairingConfirmation(boolean pairingConfirmation) {

		throw new UnsupportedOperationException();
	}

	public boolean setPasskey(int passKey) {

		throw new UnsupportedOperationException();
	}

	public boolean setPin(byte[] pin) {

		try {

			return getBluetoothService().setPin(mAddress, pin);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	public boolean setTrust(boolean trust) {

		throw new UnsupportedOperationException();
	}

	private static IBluetoothDevice getBluetoothService() {

		return (IBluetoothDevice) BluetoothServiceLocator.getBluetoothService();
	}

	// static IBluetooth getService(){
	//		
	// }

}
