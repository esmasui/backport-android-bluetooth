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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;

public class RequestEnableActivity extends RequestPermissionActivity {

	private BluetoothAdapter mLocalDevice;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable);
		mLocalDevice = BluetoothAdapter.getDefaultAdapter();
		setResult(RESULT_CANCELED);

		if (mLocalDevice.isEnabled()) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		
		AlertDialog dialog = createDialog();
		dialog.show();
	}

	AlertDialog createDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("Bluetooth permission request");
		StringBuilder b = new StringBuilder();
		b.append("An application on your phone");
		b.append(" is requesting permission to tun on Bluetooth.");
		b.append(" Do you want to do this?");
		String msg = b.toString();
		builder.setMessage(msg);
		builder.setPositiveButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onButtonClicked();
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	private void onButtonClicked() {

		indeterminate(this, mHandler, "Turning on Bluetooth...",
				new Runnable() {

					public void run() {

						for (int i = 0; i < 100; ++i) {
							if (mLocalDevice.isEnabled()) {
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
							}
						}
					}
				}, null, false);

		mLocalDevice.enable();
	}
}
