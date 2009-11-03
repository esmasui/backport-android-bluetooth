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