/**
 * 
 */
package backport.android.bluetooth;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.RfcommSocket;
import android.util.Log;

class BluetoothInputStream extends InputStream {

	private RfcommSocket mSocket;

	private InputStream mInputStream;

	public BluetoothInputStream(RfcommSocket socket) {

		mSocket = socket;

		try {

			mInputStream = socket.getInputStream();
		} catch (IOException e) {

			Log.e(BluetoothSocket.TAG, BluetoothSocket.EMPTY, e);
		}
	}

	
	public int available() throws IOException {
		return mInputStream.available();
	}


	public boolean equals(Object o) {
		return mInputStream.equals(o);
	}


	public int hashCode() {
		return mInputStream.hashCode();
	}


	public void mark(int readlimit) {
		mInputStream.mark(readlimit);
	}


	public boolean markSupported() {
		return mInputStream.markSupported();
	}


	public int read() throws IOException {
		return mInputStream.read();
	}


	public int read(byte[] b, int offset, int length) throws IOException {
		return mInputStream.read(b, offset, length);
	}


	public int read(byte[] b) throws IOException {
		return mInputStream.read(b);
	}


	public void reset() throws IOException {
		mInputStream.reset();
	}


	public long skip(long n) throws IOException {
		return mInputStream.skip(n);
	}


	public String toString() {
		return mInputStream.toString();
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
			mSocket.shutdownInput();
		} catch (IOException e) {

			Log.e(BluetoothSocket.TAG, BluetoothSocket.EMPTY, e);
		}
	}
}