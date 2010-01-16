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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.IBluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

public class RequestDiscoverableActivity extends RequestPermissionActivity {

	private static final int DEFAULT_DURATION = 120;

	private IBluetoothDevice mLocalDevice;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discoverability);
		setResult(RESULT_CANCELED);
		mLocalDevice = (IBluetoothDevice) IBluetoothDeviceLocator.get();
		Intent intent = getIntent();
		int duration = obtainDuration(intent);
		AlertDialog dialog = createDialog(duration);
		dialog.show();
	}

	int obtainDuration(Intent i) {
		int d = i.getIntExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				DEFAULT_DURATION);
		return d;
	}

	AlertDialog createDialog(final int duration) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("Bluetooth permission request");
		StringBuilder b = new StringBuilder();
		b.append("An application on your phone");
		b.append(" is requesting permission to tun on Bluetooth");
		b.append(" and to make your phone discoverable by other devices");
		b.append(" for ");
		b.append(duration);
		b.append(" seconds.");
		b.append(" Do you want to do this?");
		String msg = b.toString();
		builder.setMessage(msg);
		builder.setPositiveButton("Yes", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				onButtonClicked(duration);
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	private void onButtonClicked(final int duration) {
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
										setResult(duration);
										finish();
									}
								});

								break;
							}

							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
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
