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