package backport.android.bluetooth;

import java.util.UUID;

import android.bluetooth.IBluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothDevice implements Parcelable {

	private static final String TAG = "BluetoothDevice";

	public String ACTION_ACL_CONNECTED;
	public String ACTION_ACL_DISCONNECTED;
	public String ACTION_ACL_DISCONNECT_REQUESTED;
	public String ACTION_BOND_STATE_CHANGED;
	public String ACTION_CLASS_CHANGED;
	public String ACTION_FOUND;
	public String ACTION_NAME_CHANGED;
	public int BOND_BONDED;
	public int BOND_BONDING;
	public int BOND_NONE;
	public int ERROR;
	public String EXTRA_BOND_STATE;
	public String EXTRA_CLASS;
	public String EXTRA_DEVICE;
	public String EXTRA_NAME;
	public String EXTRA_PREVIOUS_BOND_STATE;
	public String EXTRA_RSSI;

	private static IBluetoothDevice sService;

	private final String mAddress;

	static IBluetoothDevice getService() {

		synchronized (BluetoothDevice.class) {
			if (sService == null) {

				sService = IBluetoothDeviceLocator.get();
			}
		}
		return sService;
	}

	BluetoothDevice(String address) {

		getService(); // ensures sService is initialized

		if (!BluetoothAdapter.checkBluetoothAddress(address)) {

			throw new IllegalArgumentException(address
					+ " is not a valid Bluetooth address");
		}

		mAddress = address;
	}

	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) {

		return null;
	}

	public boolean equals(Object o) {

		if (o instanceof BluetoothDevice) {

			return mAddress.equals(((BluetoothDevice) o).getAddress());
		}

		return false;
	}

	public String getAddress() {

		return mAddress;
	}

	public BluetoothClass getBluetoothClass() {

		try {

			int classInt = sService.getRemoteClass(mAddress);

			if (classInt == BluetoothClass.ERROR)
				return null;

			return new BluetoothClass(classInt);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return null;
	}

	public int getBondState() {

		try {

			return sService.getBondState(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return BOND_NONE;
	}

	public String getName() {

		try {

			return sService.getRemoteName(mAddress);
		} catch (RemoteException e) {

			Log.e(TAG, "", e);
		}

		return null;
	}

	@Override
	public int hashCode() {

		return mAddress.hashCode();
	}

	@Override
	public String toString() {

		return mAddress;
	}
	
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BluetoothDevice> CREATOR =
            new Parcelable.Creator<BluetoothDevice>() {
        public BluetoothDevice createFromParcel(Parcel in) {
            return new BluetoothDevice(in.readString());
        }
        public BluetoothDevice[] newArray(int size) {
            return new BluetoothDevice[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAddress);
    }

}
