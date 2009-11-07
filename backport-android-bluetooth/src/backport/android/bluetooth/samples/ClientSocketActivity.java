package backport.android.bluetooth.samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothDevice;
import backport.android.bluetooth.R;

public class ClientSocketActivity extends Activity {

	private static final int REQUEST_DISCOVERY = 0x1;;

	private Handler _handler = new Handler();

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private Runnable _connectionWorker = new Runnable() {

		@Override
		public void run() {

			connect();
		}
	};

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

		BluetoothDevice device = data
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		Toast.makeText(this, device.getName(), Toast.LENGTH_SHORT).show();
	}

	protected void connect() {

	}
}
