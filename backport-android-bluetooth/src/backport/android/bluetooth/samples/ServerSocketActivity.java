package backport.android.bluetooth.samples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothServerSocket;
import backport.android.bluetooth.BluetoothSocket;
import backport.android.bluetooth.R;
import backport.android.bluetooth.protocols.BluetoothProtocols;

public class ServerSocketActivity extends ListActivity {

	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";

	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";

	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	private static final String TAG = ServerSocketActivity.class
			.getSimpleName();

	private Handler _handler = new Handler();

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private Thread _serverWorker = new Thread() {

		@Override
		public void run() {

			listen();
		};
	};

	private BluetoothServerSocket _serverSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.server_socket);

		if (!_bluetooth.isEnabled()) {

			finish();

			return;
		}

		// onResume();
		_serverWorker.start();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		shutdownServer();
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
	}

	private void shutdownServer() {

		new Thread() {

			@Override
			public void run() {

				_serverWorker.interrupt();

				if (_serverSocket != null) {

					try {

						_serverSocket.close();
					} catch (IOException e) {

						Log.e(TAG, "", e);
					}

					_serverSocket = null;
				}
			};
		}.start();
	}

	public void onButtonClicked(View view) {

		shutdownServer();
	}

	protected void listen() {

		try {

			_serverSocket = _bluetooth.listenUsingRfcommWithServiceRecord(
					PROTOCOL_SCHEME_RFCOMM,
					BluetoothProtocols.RFCOMM_PROTOCOL_UUID);

			final List<String> lines = new ArrayList<String>();
			lines.add("Rfcomm server started...");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					ServerSocketActivity.this,
					android.R.layout.simple_list_item_1, lines);
			setListAdapter(adapter);

			BluetoothSocket socket = _serverSocket.accept();

			if (socket != null) {

				InputStream inputStream = socket.getInputStream();
				int read = -1;
				final byte[] bytes = new byte[2048];

				for (; (read = inputStream.read(bytes)) > -1;) {

					final int count = read;
					_handler.post(new Runnable() {

						@Override
						public void run() {

							StringBuilder b = new StringBuilder();
							for (int i = 0; i < count; ++i) {

								if (i > 0) {

									b.append(' ');
								}

								String s = Integer.toHexString(bytes[i] & 0xFF);

								if (s.length() < 2) {

									b.append('0');
								}

								b.append(s);
							}

							String s = b.toString();
							lines.add(s);

							ArrayAdapter<String> adapter = new ArrayAdapter<String>(
									ServerSocketActivity.this,
									android.R.layout.simple_list_item_1, lines);
							setListAdapter(adapter);
						}
					});
				}
			}
		} catch (IOException e) {

			Log.e(TAG, "", e);
		} finally {

		}
	}
}
