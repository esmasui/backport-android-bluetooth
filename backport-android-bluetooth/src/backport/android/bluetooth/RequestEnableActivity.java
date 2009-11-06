package backport.android.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

public class RequestEnableActivity extends RequestPermissionActivity {

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private Handler _handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.enable);

		if (_bluetooth.isEnabled()) {

			setResult(RESULT_OK);
			finish();
		}
	}

	public void onButtonClicked(View view) {

		ActivityUtils.indeterminate(this, _handler, "Turning on Bluetooth...",
				new Runnable() {

					public void run() {

						for (int i = 0; i < 100; ++i) {

							if (_bluetooth.isEnabled()) {

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

		_bluetooth.enable();
	}
}
