package backport.android.bluetooth;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.Database;
import android.bluetooth.IBluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothAdapter {

	private static final String TAG = "BluetoothAdapter";


	/**
	 * Sentinel error value for this class. Guaranteed to not equal any other
	 * integer constant in this class. Provided as a convenience for functions
	 * that require a sentinel error value, for example:
	 * <p>
	 * <code>Intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
	 * BluetoothAdapter.ERROR)</code>
	 */
	public static final int ERROR = Integer.MIN_VALUE;

	/**
	 * Broadcast Action: The state of the local Bluetooth adapter has been
	 * changed.
	 * <p>
	 * For example, Bluetooth has been turned on or off.
	 * <p>
	 * Always contains the extra fields {@link #EXTRA_STATE} and
	 * {@link #EXTRA_PREVIOUS_STATE} containing the new and old states
	 * respectively.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH} to receive.
	 */
	// TODO @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
	public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";

	/**
	 * Used as an int extra field in {@link #ACTION_STATE_CHANGED} intents to
	 * request the current power state. Possible values are: {@link #STATE_OFF},
	 * {@link #STATE_TURNING_ON}, {@link #STATE_ON}, {@link #STATE_TURNING_OFF},
	 */
	public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";
	/**
	 * Used as an int extra field in {@link #ACTION_STATE_CHANGED} intents to
	 * request the previous power state. Possible values are: {@link #STATE_OFF}
	 * , {@link #STATE_TURNING_ON}, {@link #STATE_ON},
	 * {@link #STATE_TURNING_OFF},
	 */
	public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";

	/**
	 * Indicates the local Bluetooth adapter is off.
	 */
	public static final int STATE_OFF = 10;
	/**
	 * Indicates the local Bluetooth adapter is turning on. However local
	 * clients should wait for {@link #STATE_ON} before attempting to use the
	 * adapter.
	 */
	public static final int STATE_TURNING_ON = 11;
	/**
	 * Indicates the local Bluetooth adapter is on, and ready for use.
	 */
	public static final int STATE_ON = 12;
	/**
	 * Indicates the local Bluetooth adapter is turning off. Local clients
	 * should immediately attempt graceful disconnection of any remote links.
	 */
	public static final int STATE_TURNING_OFF = 13;

	/**
	 * Activity Action: Show a system activity that requests discoverable mode.
	 * <p>
	 * This activity will also request the user to turn on Bluetooth if it is
	 * not currently enabled.
	 * <p>
	 * Discoverable mode is equivalent to
	 * {@link #SCAN_MODE_CONNECTABLE_DISCOVERABLE}. It allows remote devices to
	 * see this Bluetooth adapter when they perform a discovery.
	 * <p>
	 * For privacy, Android is not by default discoverable.
	 * <p>
	 * The sender can optionally use extra field
	 * {@link #EXTRA_DISCOVERABLE_DURATION} to request the duration of
	 * discoverability. Currently the default duration is 120 seconds, and
	 * maximum duration is capped at 300 seconds for each request.
	 * <p>
	 * Notification of the result of this activity is posted using the
	 * {@link android.app.Activity#onActivityResult} callback. The
	 * <code>resultCode</code> will be the duration (in seconds) of
	 * discoverability or {@link android.app.Activity#RESULT_CANCELED} if the
	 * user rejected discoverability or an error has occurred.
	 * <p>
	 * Applications can also listen for {@link #ACTION_SCAN_MODE_CHANGED} for
	 * global notification whenever the scan mode changes.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH}
	 */
	// TODO @SdkConstant(SdkConstantType.ACTIVITY_INTENT_ACTION)
	//public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
	public static final String ACTION_REQUEST_DISCOVERABLE = BackportProperties.getRequestDiscoverable();

	/**
	 * Used as an optional int extra field in
	 * {@link #ACTION_REQUEST_DISCOVERABLE} intents to request a specific
	 * duration for discoverability in seconds. The current default is 120
	 * seconds, and requests over 300 seconds will be capped. These values could
	 * change.
	 */
	public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";

	/**
	 * Activity Action: Show a system activity that allows the user to turn on
	 * Bluetooth.
	 * <p>
	 * This system activity will return once Bluetooth has completed turning on,
	 * or the user has decided not to turn Bluetooth on.
	 * <p>
	 * Notification of the result of this activity is posted using the
	 * {@link android.app.Activity#onActivityResult} callback. The
	 * <code>resultCode</code> will be {@link android.app.Activity#RESULT_OK} if
	 * Bluetooth has been turned on or
	 * {@link android.app.Activity#RESULT_CANCELED} if the user has rejected the
	 * request or an error has occurred.
	 * <p>
	 * Applications can also listen for {@link #ACTION_STATE_CHANGED} for global
	 * notification whenever Bluetooth is turned on or off.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH}
	 */
	// TODO @SdkConstant(SdkConstantType.ACTIVITY_INTENT_ACTION)
	//public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";
	public static final String ACTION_REQUEST_ENABLE = BackportProperties.getRequestEnable();

	/**
	 * Broadcast Action: Indicates the Bluetooth scan mode of the local Adapter
	 * has changed.
	 * <p>
	 * Always contains the extra fields {@link #EXTRA_SCAN_MODE} and
	 * {@link #EXTRA_PREVIOUS_SCAN_MODE} containing the new and old scan modes
	 * respectively.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH}
	 */
	// TODO @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
	public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";

	/**
	 * Used as an int extra field in {@link #ACTION_SCAN_MODE_CHANGED} intents
	 * to request the current scan mode. Possible values are:
	 * {@link #SCAN_MODE_NONE}, {@link #SCAN_MODE_CONNECTABLE},
	 * {@link #SCAN_MODE_CONNECTABLE_DISCOVERABLE},
	 */
	public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";
	/**
	 * Used as an int extra field in {@link #ACTION_SCAN_MODE_CHANGED} intents
	 * to request the previous scan mode. Possible values are:
	 * {@link #SCAN_MODE_NONE}, {@link #SCAN_MODE_CONNECTABLE},
	 * {@link #SCAN_MODE_CONNECTABLE_DISCOVERABLE},
	 */
	public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";

	/**
	 * Indicates that both inquiry scan and page scan are disabled on the local
	 * Bluetooth adapter. Therefore this device is neither discoverable nor
	 * connectable from remote Bluetooth devices.
	 */
	public static final int SCAN_MODE_NONE = 20;
	/**
	 * Indicates that inquiry scan is disabled, but page scan is enabled on the
	 * local Bluetooth adapter. Therefore this device is not discoverable from
	 * remote Bluetooth devices, but is connectable from remote devices that
	 * have previously discovered this device.
	 */
	public static final int SCAN_MODE_CONNECTABLE = 21;
	/**
	 * Indicates that both inquiry scan and page scan are enabled on the local
	 * Bluetooth adapter. Therefore this device is both discoverable and
	 * connectable from remote Bluetooth devices.
	 */
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;

	/**
	 * Broadcast Action: The local Bluetooth adapter has started the remote
	 * device discovery process.
	 * <p>
	 * This usually involves an inquiry scan of about 12 seconds, followed by a
	 * page scan of each new device to retrieve its Bluetooth name.
	 * <p>
	 * Register for {@link BluetoothDevice#ACTION_FOUND} to be notified as
	 * remote Bluetooth devices are found.
	 * <p>
	 * Device discovery is a heavyweight procedure. New connections to remote
	 * Bluetooth devices should not be attempted while discovery is in progress,
	 * and existing connections will experience limited bandwidth and high
	 * latency. Use {@link #cancelDiscovery()} to cancel an ongoing discovery.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH} to receive.
	 */
	// TODO @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
	public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";
	/**
	 * Broadcast Action: The local Bluetooth adapter has finished the device
	 * discovery process.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH} to receive.
	 */
	// TODO @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
	public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";

	/**
	 * Broadcast Action: The local Bluetooth adapter has changed its friendly
	 * Bluetooth name.
	 * <p>
	 * This name is visible to remote Bluetooth devices.
	 * <p>
	 * Always contains the extra field {@link #EXTRA_LOCAL_NAME} containing the
	 * name.
	 * <p>
	 * Requires {@link android.Manifest.permission#BLUETOOTH} to receive.
	 */
	// TODO @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
	public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
	/**
	 * Used as a String extra field in {@link #ACTION_LOCAL_NAME_CHANGED}
	 * intents to request the local Bluetooth name.
	 */
	public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";

	/** @hide */
	public static final String BLUETOOTH_SERVICE = "bluetooth";

	private static final int ADDRESS_LENGTH = 17;

	private static BluetoothAdapter sAdapter;

	private final IBluetoothDevice mService;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			/* handle socket closing */
			int handle = msg.what;
			try {
				Log.d(TAG, "Removing service record "
						+ Integer.toHexString(handle));
				Database.getInstance().removeServiceRecord(handle);
				// mService.removeServiceRecord(handle);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
		}
	};

	public boolean cancelDiscovery() {

		try {

			return mService.cancelDiscovery();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public static boolean checkBluetoothAddress(String address) {

		if (address == null || address.length() != ADDRESS_LENGTH) {
			return false;
		}
		for (int i = 0; i < ADDRESS_LENGTH; i++) {
			char c = address.charAt(i);
			switch (i % 3) {
			case 0:
			case 1:
				if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')) {
					// hex character, OK
					break;
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

	public boolean disable() {

		try {

			return mService.disable(true);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public boolean enable() {

		try {

			return mService.enable();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public String getAddress() {

		try {

			return mService.getAddress();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return null;
	}

	public Set<BluetoothDevice> getBondedDevices() {

		try {

			String[] bonds = mService.listBonds();
			int size = bonds.length;
			Set<BluetoothDevice> devices = new LinkedHashSet<BluetoothDevice>(
					size);

			for (int i = 0; i < size; ++i) {

				String address = bonds[i];
				devices.add(new BluetoothDevice(address));
			}

			return devices;
		} catch (RemoteException e) {

			Log.e(TAG, "", e);

			return null;
		}
	}

	public BluetoothAdapter(IBluetoothDevice service) {

		if (service == null) {

			throw new IllegalArgumentException("service is null");
		}

		mService = service;
	}

	public static synchronized BluetoothAdapter getDefaultAdapter() {

		if (sAdapter == null) {

			IBluetoothDevice service = IBluetoothDeviceLocator.get();

			if (service != null) {

				sAdapter = new BluetoothAdapter(service);
			}
		}

		return sAdapter;
	}

	public String getName() {

		try {

			return mService.getName();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return null;
	}

	public BluetoothDevice getRemoteDevice(String address) {

		return new BluetoothDevice(address);
	}

	public int getScanMode() {

		try {

			int scanMode = mService.getScanMode();
			
			switch (scanMode) {
			case BluetoothIntentRedirector.SCAN_MODE_CONNECTABLE:

				return BluetoothAdapter.SCAN_MODE_CONNECTABLE;

			case BluetoothIntentRedirector.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
				
				return BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;

			}
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return SCAN_MODE_NONE;
	}

	public int getState() {

		try {

			return mService.getBluetoothState();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return STATE_OFF;
	}

	public boolean isDiscovering() {

		try {

			return mService.isDiscovering();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public boolean isEnabled() {

		try {

			return mService.isEnabled();
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public BluetoothServerSocket listenUsingRfcommWithServiceRecord(
			String name, UUID uuid) throws IOException {

		BluetoothServerSocket socket;
		int channel;
		int errno;
		while (true) {
			// channel = picker.nextChannel();

			// if (channel == -1) {
			// throw new IOException("No available channels");
			// }

			socket = new BluetoothServerSocket(uuid);
			// errno = socket.mSocket.bindListen();

			// サービスレコードの登録ができないため、UUIDからチャンネルを決定します.
			//channel = UUIDHelper.toUUID16(uuid) & BluetoothSocket.DEFAULT_CHANNEL;
			channel = 1;
			
			//channel = BluetoothSocket.DEFAULT_CHANNEL;

			boolean bind = socket.mSocket.mRfcommSocket.bind(null, channel);
			errno = bind ? 0 : BluetoothSocket.EADDRINUSE;
			// EADDRINUSE

			if (errno == 0) {

				break; // success
			} else if (errno == BluetoothSocket.EADDRINUSE) {

				Log.d(TAG, "RFCOMM channel " + channel + " in use");
				// Log.d(TAG, "any RFCOMM channel unavailable");
				try {
					socket.close();
				} catch (IOException e) {
				}
				break; // try another channel
			} else {
				try {
					socket.close();
				} catch (IOException e) {
				}
				// socket.mSocket.throwErrnoNative(errno); // Exception as a
				// result
				throw new IOException(Integer.toString(errno));
				// of bindListen()
			}

		}

		socket.mSocket.mRfcommSocket.listen(-1);
		//
		// ERROR/bluetooth_Database.cpp(505): Could not get onto the system bus!
		// ERROR/libdbus(505): arguments to
		// dbus_connection_set_exit_on_disconnect() were incorrect, assertion
		// "connection != NULL" failed in file
		// external/dbus/dbus/dbus-connection.c line 2830.
		// ERROR/libdbus(505): This is normally a bug in some application using
		// the D-Bus library.
		//
		// int handle = -1;
		//
		// try {
		//
		// Database database = Database.getInstance();
		// handle = database.advertiseRfcommService(
		// socket.mSocket.mRfcommSocket, name, uuid);
		// } catch (IOException e) {
		// Log.e(TAG, "", e);
		// }
		//
		// if (handle == -1) {
		// try {
		// socket.close();
		// } catch (IOException e) {
		// }
		// throw new IOException("Not able to register SDP record for " + name);
		// }
		//
		// socket.setCloseHandler(mHandler, handle);

		// channel = socket.mSocket.mRfcommSocket.getPort();
		Log.d(TAG, "listening on RFCOMM channel " + channel);

		return socket;
	}

	public boolean setName(String name) {

		try {

			return mService.setName(name);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

	public boolean startDiscovery() {

		try {

			return mService.startDiscovery(true);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return false;
	}

}
