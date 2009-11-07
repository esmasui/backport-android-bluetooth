package backport.android.bluetooth.samples;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothDevice;
import backport.android.bluetooth.BluetoothSocket;
import backport.android.bluetooth.R;
import backport.android.bluetooth.protocols.BluetoothProtocols;

public class ClientSocketActivity extends Activity {

	private static final String TAG = ClientSocketActivity.class
			.getSimpleName();

	private static final int REQUEST_DISCOVERY = 0x1;;

	private Handler _handler = new Handler();

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.client_socket);

		if (!_bluetooth.isEnabled()) {

			finish();

			return;
		}

		Intent intent = new Intent(this, DiscoveryActivity.class);
		Toast.makeText(this, "select device to connect", Toast.LENGTH_SHORT)
				.show();
		startActivityForResult(intent, REQUEST_DISCOVERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode != REQUEST_DISCOVERY) {

			return;
		}

		if (resultCode != RESULT_OK) {

			return;
		}

		final BluetoothDevice device = data
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		new Thread() {

			public void run() {

				connect(device);
			};
		}.start();
	}

	protected void connect(BluetoothDevice device) {

		BluetoothSocket socket = null;

		try {

			// socket = device
			// .createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
			socket = device
					.createRfcommSocketToServiceRecord(BluetoothProtocols.RFCOMM_PROTOCOL_UUID);
			socket.connect();
			//socket.

		} catch (IOException e) {

			Log.e(TAG, "", e);
		} finally {

			if (socket != null) {

				try {

					socket.close();
				} catch (IOException e) {

					Log.e(TAG, "", e);
				}
			}
		}
	}
}
