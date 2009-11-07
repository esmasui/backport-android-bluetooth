package backport.android.bluetooth.samples;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothDevice;
import backport.android.bluetooth.R;

public class DiscoveryActivity extends ListActivity {

	private Handler _handler = new Handler();

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();

	private volatile boolean _discoveryFinished;

	private Runnable _discoveryWorkder = new Runnable() {

		@Override
		public void run() {

			_bluetooth.startDiscovery();

			for (;;) {

				if (_discoveryFinished) {

					break;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

					// nop.
				}
			}
		}
	};

	private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			_devices.add(device);
			showDevices();
		}
	};

	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			unregisterReceiver(_foundReceiver);
			unregisterReceiver(this);
			_discoveryFinished = true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.discovery);

		if (!_bluetooth.isEnabled()) {

			finish();
			
			return;
		}

		IntentFilter discoveryFilter = new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(_discoveryReceiver, discoveryFilter);
		IntentFilter foundFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		registerReceiver(_foundReceiver, foundFilter);

		ActivityUtils.indeterminate(DiscoveryActivity.this, _handler,
				"Scanning...", _discoveryWorkder, new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {

						_bluetooth.cancelDiscovery();
						_discoveryFinished = true;
					}
				}, true);
	}

	protected void showDevices() {

		List<String> list = new ArrayList<String>();

		for (int i = 0, size = _devices.size(); i < size; ++i) {

			StringBuilder b = new StringBuilder();
			BluetoothDevice d = _devices.get(i);
			b.append(d.getAddress());
			b.append('\n');
			b.append(d.getName());
			String s = b.toString();
			list.add(s);
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);

		_handler.post(new Runnable() {

			@Override
			public void run() {

				setListAdapter(adapter);
			}
		});

	}
}
