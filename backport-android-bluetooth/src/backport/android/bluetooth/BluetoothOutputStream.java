/**
 * 
 */
package backport.android.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.RfcommSocket;
import android.util.Log;

class BluetoothOutputStream extends OutputStream {

	private RfcommSocket mSocket;

	private OutputStream mOutputStream;

	public BluetoothOutputStream(RfcommSocket socket) {

		mSocket = socket;

		try {

			mOutputStream = socket.getOutputStream();
		} catch (IOException e) {

			Log.e(BluetoothSocket.TAG, BluetoothSocket.EMPTY, e);
		}
	}

	
	
	public boolean equals(Object o) {
		return mOutputStream.equals(o);
	}



	public void flush() throws IOException {
		mOutputStream.flush();
	}



	public int hashCode() {
		return mOutputStream.hashCode();
	}



	public String toString() {
		return mOutputStream.toString();
	}



	public void write(byte[] buffer, int offset, int count) throws IOException {
		mOutputStream.write(buffer, offset, count);
	}



	public void write(byte[] buffer) throws IOException {
		mOutputStream.write(buffer);
	}



	public void write(int oneByte) throws IOException {
		mOutputStream.write(oneByte);
	}



	@Override
	protected void finalize() throws Throwable {
	
		super.finalize();
		close();
	}
	
	@Override
	public void close() throws IOException {

		try {

			super.close();
			mSocket.shutdownOutput();
		} catch (IOException e) {

			Log.e(BluetoothSocket.TAG, BluetoothSocket.EMPTY, e);
		}

	}
}