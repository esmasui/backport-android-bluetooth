package backport.android.bluetooth;

import android.os.RemoteException;

public interface IBluetoothDeviceDelegate {

	boolean disable(boolean persistSetting) throws RemoteException;
	
	/** Droid Erisのdisable()に引数がないという謎の実装用.  */
	boolean disable() throws RemoteException;
}
