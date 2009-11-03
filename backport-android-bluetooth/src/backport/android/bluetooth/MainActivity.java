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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.util.Log;

public class MainActivity extends Activity {

	private static final String TAG = "@MainActivity";

	private Handler _handler = new Handler();

	private BluetoothServerSocket _server;

	private BluetoothSocket _socket;

	private static final int OBEX_CONNECT = 0x80;

	private static final int OBEX_DISCONNECT = 0x81;

	private static final int OBEX_PUT = 0x02;

	private static final int OBEX_PUT_END = 0x82;

	private static final int OBEX_RESPONSE_OK = 0xa0;

	private static final int OBEX_RESPONSE_CONTINUE = 0x90;

	private static final int BIT_MASK = 0x000000ff;

	// "OBEX File Transfer" (0x1106)
	// "L2CAP" (0x0100)
	// "RFCOMM" (0x0003)
	// "OBEX" (0x0008)

	// private static final UUID RFCOMM = UUID.

	Thread t = new Thread() {

		@Override
		public void run() {

			try {

				_server = BluetoothAdapter.getDefaultAdapter()
						.listenUsingRfcommWithServiceRecord("OBEX", null);
				_socket = _server.accept();

				reader.start();

				Log.d(TAG, "shutdown thread");
			} catch (IOException e) {

				e.printStackTrace();
			}
		};
	};

	Thread reader = new Thread() {

		@Override
		public void run() {

			try {

				Log.d(TAG, "getting inputstream");

				InputStream inputStream = _socket.getInputStream();
				OutputStream outputStream = _socket.getOutputStream();

				Log.d(TAG, "got inputstream");

				int read = -1;
				byte[] bytes = new byte[2048];
				ByteArrayOutputStream baos = new ByteArrayOutputStream(
						bytes.length);
				while ((read = inputStream.read(bytes)) != -1) {

					baos.write(bytes, 0, read);
					byte[] req = baos.toByteArray();

					int op = req[0] & BIT_MASK;

					Log.d(TAG, "read:" + Arrays.toString(req));
					Log.d(TAG, "op:" + Integer.toHexString(op));

					switch (op) {
					case OBEX_CONNECT:

						outputStream.write(new byte[] {
								(byte) OBEX_RESPONSE_OK, 0, 7, 16, 0, 4, 0 });

						break;

					case OBEX_DISCONNECT:

						outputStream.write(new byte[] {
								(byte) OBEX_RESPONSE_OK, 0, 3, 0 });

						break;

					case OBEX_PUT:

						outputStream.write(new byte[] {
								(byte) OBEX_RESPONSE_CONTINUE, 0, 3, 0 });

						break;

					case OBEX_PUT_END:

						outputStream.write(new byte[] {
								(byte) OBEX_RESPONSE_OK, 0, 3, 0 });

						break;

					default:

						outputStream.write(new byte[] {
								(byte) OBEX_RESPONSE_OK, 0, 3, 0 });
					}

					Log.d(TAG, new String(baos.toByteArray(), "utf-8"));

					baos = new ByteArrayOutputStream(bytes.length);
				}

				Log.d(TAG, new String(baos.toByteArray(), "utf-8"));
			} catch (IOException e) {

				e.printStackTrace();
			}
		};
	};

	private Thread put = new Thread() {

		public void run() {

		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// t.start();

		Intent i = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
		// Intent i = new Intent(Intent.ACTION_PICK);
		// i.setType("vnd.android.cursor.item/phone");
		// i.setType("vnd.android.cursor.dir/phone");

		// i.setType("image/*");
		startActivityForResult(i, 1);

		// try {
		//
		// BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		// // Log.d(TAG, o.toString());
		// Toast.makeText(this, bluetooth.getName(), Toast.LENGTH_LONG).show();
		// } catch (Exception e) {
		//
		// Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, data.getData().toString());

		switch (requestCode) {
		case (1):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);

				for (; c.moveToNext();) {

					Log.d(TAG, "c1---------------------------------------");
					dump(c);

					Uri uri = Uri.withAppendedPath(data.getData(),
							Contacts.People.ContactMethods.CONTENT_DIRECTORY);

					Cursor c2 = managedQuery(uri, null, null, null, null);

					for (; c2.moveToNext();) {

						Log.d(TAG, "c2---------------------------------------");
						dump(c2);
					}
					// String name = c.getString(c
					// .getColumnIndexOrThrow(People.NUMBER));

				}
				// c.close();
			}
			break;
		}

		// try {
		// InputStream is = getContentResolver().openInputStream(
		// data.getData());
		// int read = -1;
		// byte[] b = new byte[4096];
		// ByteArrayOutputStream baos = new ByteArrayOutputStream(b.length);
		//
		// try {
		// for (; (read = is.read(b)) > -1;) {
		//
		// baos.write(b, 0, read);
		// }
		//
		// Log.d(TAG, new String(baos.toByteArray(), "utf-8"));
		// is.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } catch (FileNotFoundException e) {
		//
		// e.printStackTrace();
		// } finally {
		//
		// }
	}

	private void dump(Cursor c) {

		for (int i = 0, size = c.getColumnCount(); i < size; ++i) {

			String col = c.getColumnName(i);

			String s = c.getString(i);
			Log.d(TAG, col + "=" + s);
		}
	}

}