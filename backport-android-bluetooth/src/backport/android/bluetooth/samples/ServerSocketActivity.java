package backport.android.bluetooth.samples;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothServerSocket;
import backport.android.bluetooth.BluetoothSocket;
import backport.android.bluetooth.R;
import backport.android.bluetooth.protocols.BluetoothProtocols;

public class ServerSocketActivity extends Activity {

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
					PROTOCOL_SCHEME_BT_OBEX,
					BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);

			BluetoothSocket socket = _serverSocket.accept();

			if (socket != null) {

				InputStream inputStream = socket.getInputStream();
				inputStream.read();
			}
		} catch (IOException e) {

			Log.e(TAG, "", e);
		} finally {

		}
	}

}
