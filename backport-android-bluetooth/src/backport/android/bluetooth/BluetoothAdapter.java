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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothError;
import android.bluetooth.IBluetoothDevice;
import android.os.RemoteException;
import android.util.Log;

/**
 * Represents the local Bluetooth adapter.
 * 
 * Use getDefaultAdapter() to get the default local Bluetooth adapter.
 * 
 * Use the BluetoothDevice class for operations on remote Bluetooth devices.
 * 
 */
public final class BluetoothAdapter {

	private static final String TAG = BluetoothAdapter.class.getSimpleName();

	private static final String EMPTY = "";

	// private static final int PORT = 1;

	private static final int ADDRESS_LENGTH = 17;

	/**
	 * Broadcast Action: The local Bluetooth adapter has finished the device
	 * discovery process.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";

	/**
	 * Broadcast Action: The local Bluetooth adapter has started the remote
	 * device discovery process.
	 * 
	 * This usually involves an inquiry scan of about 12 seconds, followed by a
	 * page scan of each new device to retrieve its Bluetooth name.
	 * 
	 * Register for ACTION_FOUND to be notified as remote Bluetooth devices are
	 * found.
	 * 
	 * Device discovery is a heavyweight procedure. New connections to remote
	 * Bluetooth devices should not be attempted while discovery is in progress,
	 * and existing connections will experience limited bandwidth and high
	 * latency. Use cancelDiscovery() to cancel an ongoing discovery.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";

	/**
	 * Broadcast Action: The local Bluetooth adapter has changed its friendly
	 * Bluetooth name.
	 * 
	 * This name is visible to remote Bluetooth devices.
	 * 
	 * Always contains the extra field EXTRA_LOCAL_NAME containing the name.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";

	/**
	 * Activity Action: Show a system activity that requests discoverable mode.
	 * 
	 * This activity will also request the user to turn on Bluetooth if it is
	 * not currently enabled.
	 * 
	 * Discoverable mode is equivalent to SCAN_MODE_CONNECTABLE_DISCOVERABLE. It
	 * allows remote devices to see this Bluetooth adapter when they perform a
	 * discovery.
	 * 
	 * For privacy, Android is not by default discoverable.
	 * 
	 * The sender can optionally use extra field EXTRA_DISCOVERABLE_DURATION to
	 * request the duration of discoverability. Currently the default duration
	 * is 120 seconds, and maximum duration is capped at 300 seconds for each
	 * request.
	 * 
	 * Notification of the result of this activity is posted using the
	 * onActivityResult(int, int, Intent) callback. The resultCode will be the
	 * duration (in seconds) of discoverability, or a negative value if the user
	 * rejected discoverability.
	 * 
	 * Applications can also listen for ACTION_SCAN_MODE_CHANGED for global
	 * notification whenever the scan mode changes.
	 * 
	 * Requires BLUETOOTH
	 */
	public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";

	/**
	 * Activity Action: Show a system activity that allows the user to turn on
	 * getBluetoothService().
	 * 
	 * This system activity will return once Bluetooth has completed turning on,
	 * or the user has decided not to turn Bluetooth on.
	 * 
	 * Notification of the result of this activity is posted using the
	 * onActivityResult(int, int, Intent) callback. The resultCode will be
	 * negative if the user did not turn on Bluetooth, and non-negative if
	 * Bluetooth has been turned on.
	 * 
	 * Applications can also listen for ACTION_STATE_CHANGED for global
	 * notification whenever Bluetooth is turned on or off.
	 * 
	 * Requires BLUETOOTH
	 */
	public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";

	/**
	 * Broadcast Action: Indicates the Bluetooth scan mode of the local Adapter
	 * has changed.
	 * 
	 * Always contains the extra fields EXTRA_SCAN_MODE and
	 * EXTRA_PREVIOUS_SCAN_MODE containing the new and old scan modes
	 * respectively.
	 * 
	 * Requires BLUETOOTH
	 */
	public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";

	/**
	 * Broadcast Action: The state of the local Bluetooth adapter has been
	 * changed.
	 * 
	 * For example, Bluetooth has been turned on or off.
	 * 
	 * Always contains the extra fields EXTRA_STATE and EXTRA_PREVIOUS_STATE
	 * containing the new and old states respectively.
	 * 
	 * Requires BLUETOOTH to receive.
	 */
	public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";

	/**
	 * Sentinel error value for this class. Guaranteed to not equal any other
	 * integer constant in this class. Provided as a convenience for functions
	 * that require a sentinel error value, for example:
	 * 
	 * Intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
	 */
	public static final int ERROR = -2147483648; // (0x80000000)

	/**
	 * Used as an optional int extra field in ACTION_REQUEST_DISCOVERABLE
	 * intents to request a specific duration for discoverability in seconds.
	 * The current default is 120 seconds, and requests over 300 seconds will be
	 * capped. These values could change.
	 */
	public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";

	/**
	 * Used as a String extra field in ACTION_LOCAL_NAME_CHANGED intents to
	 * request the local Bluetooth name.
	 */
	public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";

	/**
	 * Used as an int extra field in ACTION_SCAN_MODE_CHANGED intents to request
	 * the previous scan mode. Possible values are: SCAN_MODE_NONE,
	 * SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE,
	 */
	public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";

	/**
	 * Used as an int extra field in ACTION_STATE_CHANGED intents to request the
	 * previous power state. Possible values are: STATE_OFF, STATE_TURNING_ON,
	 * STATE_ON, STATE_TURNING_OFF,
	 */
	public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";

	/**
	 * Used as an int extra field in ACTION_SCAN_MODE_CHANGED intents to request
	 * the current scan mode. Possible values are: SCAN_MODE_NONE,
	 * SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE,
	 */
	public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";

	/**
	 * Used as an int extra field in ACTION_STATE_CHANGED intents to request the
	 * current power state. Possible values are: STATE_OFF, STATE_TURNING_ON,
	 * STATE_ON, STATE_TURNING_OFF,
	 */
	public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";

	/**
	 * Indicates that inquiry scan is disabled, but page scan is enabled on the
	 * local Bluetooth adapter. Therefore this device is not discoverable from
	 * remote Bluetooth devices, but is connectable from remote devices that
	 * have previously discovered this device.
	 */
	public static final int SCAN_MODE_CONNECTABLE = 21; // (0x00000015)

	/**
	 * Indicates that both inquiry scan and page scan are enabled on the local
	 * Bluetooth adapter. Therefore this device is both discoverable and
	 * connectable from remote Bluetooth devices.
	 */
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23; // (0x00000017)

	/**
	 * Indicates that both inquiry scan and page scan are disabled on the local
	 * Bluetooth adapter. Therefore this device is neither discoverable nor
	 * connectable from remote Bluetooth devices.
	 */
	public static final int SCAN_MODE_NONE = 20; // (0x00000014)

	/**
	 * Indicates the local Bluetooth adapter is off.
	 */
	public static final int STATE_OFF = 10; // (0x0000000a)

	/**
	 * Indicates the local Bluetooth adapter is on, and ready for use.
	 */
	public static final int STATE_ON = 12; // (0x0000000c)

	/**
	 * Indicates the local Bluetooth adapter is turning off. Local clients
	 * should immediately attempt graceful disconnection of any remote links.
	 */
	public static final int STATE_TURNING_OFF = 13; // (0x0000000d)

	/**
	 * Indicates the local Bluetooth adapter is turning on. However local
	 * clients should wait for STATE_ON before attempting to use the adapter.
	 */
	public static final int STATE_TURNING_ON = 11; // (0x0000000b)

	private BluetoothAdapter() {

	}

	private static BluetoothAdapter DEFAULT_ADAPTER;

	/**
	 * Validate a Bluetooth address, such as "00:43:A8:23:10:F0"
	 * 
	 * Alphabetic characters must be uppercase to be valid.
	 * 
	 * @param address
	 *            Bluetooth address as string
	 * @return true if the address is valid, false otherwise
	 */
	/** Sanity check a bluetooth address, such as "00:43:A8:23:10:F0" */
	public static boolean checkBluetoothAddress(String address) {
		if (address == null || address.length() != ADDRESS_LENGTH) {
			return false;
		}
		for (int i = 0; i < ADDRESS_LENGTH; i++) {
			char c = address.charAt(i);
			switch (i % 3) {
			case 0:
			case 1:
				if (Character.digit(c, 16) != -1) {
					break; // hex character, OK
				}
				return false;
			case 2:
				if (c == ':') {
					break; // OK
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Get a handle to the default local Bluetooth adapter.
	 * 
	 * Currently Android only supports one Bluetooth adapter, but the API could
	 * be extended to support more. This will always return the default adapter.
	 * 
	 * @return the default local adapter, or null if Bluetooth is not supported
	 *         on this hardware platform
	 */
	public static synchronized BluetoothAdapter getDefaultAdapter() {

		if (DEFAULT_ADAPTER != null) {

			return DEFAULT_ADAPTER;
		}

		DEFAULT_ADAPTER = new BluetoothAdapter();

		return DEFAULT_ADAPTER;
	}

	/**
	 * Cancel the current device discovery process.
	 * 
	 * Requires BLUETOOTH_ADMIN.
	 * 
	 * @return true on success, false on error
	 */
	public boolean cancelDiscovery() {

		try {

			return getBluetoothService().cancelDiscovery();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Turn off the local Bluetooth adapter.
	 * 
	 * This gracefully shuts down all Bluetooth connections, stops Bluetooth
	 * system services, and powers down the underlying Bluetooth hardware.
	 * 
	 * This is an asynchronous call: it will return immediately, and clients
	 * should listen for ACTION_STATE_CHANGED to be notified of subsequent
	 * adapter state changes. If this call returns true, then the adapter state
	 * will immediately transition from STATE_ON to STATE_TURNING_OFF, and some
	 * time later transition to either STATE_OFF or STATE_ON. If this call
	 * returns false then there was an immediate problem that will prevent the
	 * adapter from being turned off - such as the adapter already being turned
	 * off.
	 * 
	 * Requires BLUETOOTH_ADMIN Returns
	 * 
	 * @return true to indicate adapter shutdown has begun, or false on
	 *         immediate error
	 */
	public boolean disable() {

		try {

			return getBluetoothService().disable(true);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Turn on the local Bluetooth adapter.
	 * 
	 * This powers on the underlying Bluetooth hardware, and starts all
	 * Bluetooth system services.
	 * 
	 * This is an asynchronous call: it will return immediately, and clients
	 * should listen for ACTION_STATE_CHANGED to be notified of subsequent
	 * adapter state changes. If this call returns true, then the adapter state
	 * will immediately transition from STATE_OFF to STATE_TURNING_ON, and some
	 * time later transition to either STATE_OFF or STATE_ON. If this call
	 * returns false then there was an immediate problem that will prevent the
	 * adapter from being turned on - such as Airplane mode, or the adapter is
	 * already turned on.
	 * 
	 * Requires BLUETOOTH_ADMIN
	 * 
	 * @return true to indicate adapter startup has begun, or false on immediate
	 *         error
	 */
	public boolean enable() {

		try {

			return getBluetoothService().enable();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Returns the hardware address of the local Bluetooth adapter.
	 * 
	 * For example, "00:11:22:AA:BB:CC".
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return Bluetooth hardware address as string
	 */
	public String getAddress() {

		try {

			return getBluetoothService().getAddress();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return null;
		}
	}

	/**
	 * Return the set of BluetoothDevice objects that are bonded (paired) to the
	 * local adapter.
	 * 
	 * Requires getBluetoothService().
	 * 
	 * @return unmodifiable set of BluetoothDevice, or null on error
	 */
	public Set<BluetoothDevice> getBondedDevices() {

		try {

			String[] bonds = getBluetoothService().listBonds();
			int size = bonds.length;
			Set<BluetoothDevice> devices = new LinkedHashSet<BluetoothDevice>(
					size);

			for (int i = 0; i < size; ++i) {

				String address = bonds[i];
				devices.add(new BluetoothDevice(address));
			}

			return devices;
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return null;
		}
	}

	/**
	 * Get the friendly Bluetooth name of the local Bluetooth adapter.
	 * 
	 * This name is visible to remote Bluetooth devices.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return the Bluetooth name, or null on error
	 */
	public String getName() {

		try {
			return getBluetoothService().getName();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return null;
		}
	}

	/**
	 * Get a BluetoothDevice object for the given Bluetooth hardware address.
	 * 
	 * Valid Bluetooth hardware addresses must be upper case, in a format such
	 * as "00:11:22:33:AA:BB". The helper checkBluetoothAddress(String) is
	 * available to validate a Bluetooth address.
	 * 
	 * A BluetoothDevice will always be returned for a valid hardware address,
	 * even if this adapter has never seen that device.
	 * 
	 * @param address
	 *            valid Bluetooth MAC address
	 * @return
	 * @throws IllegalArgumentException
	 *             if address is invalid
	 */
	public BluetoothDevice getRemoteDevice(String address)
			throws IllegalArgumentException {

		if (!checkBluetoothAddress(address)) {

			throw new IllegalArgumentException(address);
		}

		return new BluetoothDevice(address);
	}

	/**
	 * Get the current Bluetooth scan mode of the local Bluetooth adaper.
	 * 
	 * The Bluetooth scan mode determines if the local adapter is connectable
	 * and/or discoverable from remote Bluetooth devices.
	 * 
	 * Possible values are: SCAN_MODE_NONE, SCAN_MODE_CONNECTABLE,
	 * SCAN_MODE_CONNECTABLE_DISCOVERABLE.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return scan mode
	 */
	public int getScanMode() {

		try {

			return getBluetoothService().getScanMode();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return BluetoothError.ERROR_IPC;
		}
	}

	/**
	 * Get the current state of the local Bluetooth adapter.
	 * 
	 * Possible return values are STATE_OFF, STATE_TURNING_ON, STATE_ON,
	 * STATE_TURNING_OFF.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return current state of Bluetooth adapter
	 */
	public int getState() {

		try {

			return getBluetoothService().getBluetoothState();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return BluetoothError.ERROR_IPC;
		}
	}

	/**
	 * Return true if the local Bluetooth adapter is currently in the device
	 * discovery process.
	 * 
	 * Device discovery is a heavyweight procedure. New connections to remote
	 * Bluetooth devices should not be attempted while discovery is in progress,
	 * and existing connections will experience limited bandwidth and high
	 * latency. Use cancelDiscovery() to cancel an ongoing discovery.
	 * 
	 * Applications can also register for ACTION_DISCOVERY_STARTED or
	 * ACTION_DISCOVERY_FINISHED to be notified when discovery starts or
	 * completes.
	 * 
	 * Requires getBluetoothService().
	 * 
	 * @return true if discovering
	 */
	public boolean isDiscovering() {

		try {

			return getBluetoothService().isDiscovering();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Return true if Bluetooth is currently enabled and ready for use.
	 * 
	 * Equivalent to: getBluetoothState() == STATE_ON
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @return true if the local adapter is turned on
	 */
	public boolean isEnabled() {

		try {

			return getBluetoothService().isEnabled();
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Create a listening, secure RFCOMM Bluetooth socket with Service Record.
	 * 
	 * A remote device connecting to this socket will be authenticated and
	 * communication on this socket will be encrypted.
	 * 
	 * Use accept() to retrieve incoming connections from a listening
	 * BluetoothServerSocket.
	 * 
	 * The system will assign an unused RFCOMM channel to listen on.
	 * 
	 * The system will also register a Service Discovery Protocol (SDP) record
	 * with the local SDP server containing the specified UUID, service name,
	 * and auto-assigned channel. Remote Bluetooth devices can use the same UUID
	 * to query our SDP server and discover which channel to connect to. This
	 * SDP record will be removed when this socket is closed, or if this
	 * application closes unexpectedly.
	 * 
	 * Use createRfcommSocketToServiceRecord(UUID) to connect to this socket
	 * from another device using the same UUID.
	 * 
	 * Requires BLUETOOTH
	 * 
	 * @param name
	 *            service name for SDP record
	 * @param uuid
	 *            uuid for SDP record
	 * @return a listening RFCOMM BluetoothServerSocket
	 * @throws on
	 *             error, for example Bluetooth not available, or insufficient
	 *             permissions, or channel in use.
	 */
	public BluetoothServerSocket listenUsingRfcommWithServiceRecord(
			String name, UUID uuid) throws IOException {

//		for (int port = 12; port <= 30; port++) {
//
//			try {
//
//				BluetoothServerSocket socket = BluetoothServerSocket
//						.listenUsingRfcommOn(port);
//
//				Log.i(TAG, uuid + " listen on " + port);
//
//				return socket;
//			} catch (IOException e) {
//
//				// ignore;
//			}
//		}
//
//		throw new IOException();
		return BluetoothServerSocket.listenUsingRfcommOn(-1);
	}

	/**
	 * Set the friendly Bluetooth name of the local Bluetoth adapter.
	 * 
	 * This name is visible to remote Bluetooth devices.
	 * 
	 * Valid Bluetooth names are a maximum of 248 UTF-8 characters, however many
	 * remote devices can only display the first 40 characters, and some may be
	 * limited to just 20.
	 * 
	 * Requires BLUETOOTH_ADMIN
	 * 
	 * @param name
	 *            a valid Bluetooth name
	 * @return true if the name was set, false otherwise
	 */
	public boolean setName(String name) {

		try {

			return getBluetoothService().setName(name);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	/**
	 * Start the remote device discovery process.
	 * 
	 * The discovery process usually involves an inquiry scan of about 12
	 * seconds, followed by a page scan of each new device to retrieve its
	 * Bluetooth name.
	 * 
	 * This is an asynchronous call, it will return immediately. Register for
	 * ACTION_DISCOVERY_STARTED and ACTION_DISCOVERY_FINISHED intents to
	 * determine exactly when the discovery starts and completes. Register for
	 * ACTION_FOUND to be notified as remote Bluetooth devices are found.
	 * 
	 * Device discovery is a heavyweight procedure. New connections to remote
	 * Bluetooth devices should not be attempted while discovery is in progress,
	 * and existing connections will experience limited bandwidth and high
	 * latency. Use cancelDiscovery() to cancel an ongoing discovery.
	 * 
	 * Device discovery will only find remote devices that are currently
	 * discoverable (inquiry scan enabled). Many Bluetooth devices are not
	 * discoverable by default, and need to be entered into a special mode.
	 * 
	 * Requires BLUETOOTH_ADMIN.
	 * 
	 * @return true on success, false on error
	 */
	public boolean startDiscovery() {

		try {

			return getBluetoothService().startDiscovery(true);
		} catch (RemoteException e) {

			Log.e(TAG, EMPTY, e);

			return false;
		}
	}

	private static IBluetoothDevice getBluetoothService() {

		return (IBluetoothDevice) BluetoothServiceLocator.getBluetoothService();
	}
}
