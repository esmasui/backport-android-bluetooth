package backport.com.android.settings.bluetooth;

import android.os.Bundle;
import android.view.View;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.R;

public class RequestEnableActivity extends RequestPermissionActivity {

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable);
	}

	public void onButtonClicked(View view) {

		_bluetooth.enable();
	}
}
