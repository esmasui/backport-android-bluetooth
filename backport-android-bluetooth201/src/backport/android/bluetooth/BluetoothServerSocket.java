package backport.android.bluetooth;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import android.os.Handler;

public class BluetoothServerSocket implements Closeable {

	final BluetoothSocket mSocket;
	private Handler mHandler;
	private int mMessage;

	BluetoothServerSocket(UUID uuid) throws IOException {
		mSocket = new BluetoothSocket(null, uuid);
	}

	public BluetoothSocket accept() throws IOException {

		return accept(-1);
	}

	public BluetoothSocket accept(int timeout) throws IOException {

		return mSocket.accept(timeout);
	}

	public void close() throws IOException {

		synchronized (this) {
			if (mHandler != null) {
				mHandler.obtainMessage(mMessage).sendToTarget();
			}
		}
		mSocket.close();
	}

	synchronized void setCloseHandler(Handler handler, int message) {
		mHandler = handler;
		mMessage = message;
	}

}
