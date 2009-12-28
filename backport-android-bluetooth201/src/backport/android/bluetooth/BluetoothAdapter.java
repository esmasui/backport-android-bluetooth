package backport.android.bluetooth;

import java.io.IOException;
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

	static final int START_CHANNEL = 12;

	public String ACTION_DISCOVERY_FINISHED;
	public String ACTION_DISCOVERY_STARTED;
	public String ACTION_LOCAL_NAME_CHANGED;
	public String ACTION_REQUEST_DISCOVERABLE;
	public String ACTION_REQUEST_ENABLE;
	public String ACTION_SCAN_MODE_CHANGED;
	public String ACTION_STATE_CHANGED;
	public int ERROR;
	public String EXTRA_DISCOVERABLE_DURATION;
	public String EXTRA_LOCAL_NAME;
	public String EXTRA_PREVIOUS_SCAN_MODE;
	public String EXTRA_PREVIOUS_STATE;
	public String EXTRA_SCAN_MODE;
	public String EXTRA_STATE;
	public int SCAN_MODE_CONNECTABLE;
	public int SCAN_MODE_CONNECTABLE_DISCOVERABLE;
	public int SCAN_MODE_NONE;
	public int STATE_OFF;
	public int STATE_ON;
	public int STATE_TURNING_OFF;
	public int STATE_TURNING_ON;

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

		return null;
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

			return mService.getScanMode();
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
			channel = UUIDHelper.toUUID16(uuid) + START_CHANNEL;

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
