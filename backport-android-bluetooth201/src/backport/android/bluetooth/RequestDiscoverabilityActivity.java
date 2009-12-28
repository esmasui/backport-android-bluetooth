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

import android.bluetooth.IBluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.WindowManager;

public class RequestDiscoverabilityActivity extends RequestPermissionActivity {

	private IBluetoothDevice mLocalDevice = (IBluetoothDevice) IBluetoothDeviceLocator
			.get();

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.discoverability);

		setResult(RESULT_CANCELED);
	}

	public void onButtonClicked(View view) {

		indeterminate(this, mHandler, "Making device Discoverable...",
				new Runnable() {

					public void run() {

						for (int i = 0; i < 100; ++i) {

							try {
								
								if (!mLocalDevice.isEnabled()) {

									mLocalDevice.enable();
								}
							} catch (RemoteException e2) {
							}

							int scanMode;

							try {

								scanMode = mLocalDevice.getScanMode();
							} catch (RemoteException e1) {

								scanMode = BluetoothAdapter.ERROR;
							}

							if (scanMode == BluetoothIntentRedirector.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

								mHandler.post(new Runnable() {

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

			mLocalDevice
					.setScanMode(BluetoothIntentRedirector.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
		} catch (RemoteException e) {

			finish();
		}
	}
}
