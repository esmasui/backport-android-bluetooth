package backport.android.bluetooth;

import android.bluetooth.IBluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.WindowManager;
import backport.android.bluetooth.R;

public class RequestDiscoverableActivity extends RequestPermissionActivity {

	private static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 0x3;

	private IBluetoothDevice _bluetooth = (IBluetoothDevice) BluetoothServiceLocator
			.getBluetoothService();

	private Handler _handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.discoverable);

		try {

			if (!_bluetooth.isEnabled()) {

				finish();
			}
		} catch (RemoteException e) {

			finish();
		}
	}

	public void onButtonClicked(View view) {

		ActivityUtils.indeterminate(this, _handler,
				"Making device Discoverable...", new Runnable() {

					public void run() {

						for (int i = 0; i < 100; ++i) {

							int scanMode;

							try {

								scanMode = _bluetooth.getScanMode();
							} catch (RemoteException e1) {

								scanMode = BluetoothAdapter.ERROR;
							}

							if (scanMode == SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

								_handler.post(new Runnable() {

									public void run() {

										setResult(RESULT_OK);
										finish();
									}
								});

								break;
							}

							try {

								Thread.sleep(100);
							} catch (InterruptedException e) {

								// nop.
							}
						}
					}
				}, null, false);

		try {

			_bluetooth.setScanMode(SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		} catch (RemoteException e) {

			finish();
		}
	}
}
