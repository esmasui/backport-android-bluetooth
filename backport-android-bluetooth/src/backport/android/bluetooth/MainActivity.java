package backport.android.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private static final int REQUEST_ENABLE = 0x1;

	private static final int REQUEST_DISCOVERABLE = 0x2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onEnableButtonClicked(View view) {

		Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enabler, REQUEST_ENABLE);
	}

	public void onDisableButtonClicked(View view) {

		_bluetooth.disable();
	}

}
