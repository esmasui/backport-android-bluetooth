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
		int id = getDialogInfoIconId();
		builder.setIcon(id);
		builder.setTitle(getString(R.string.dialog_title_permission_request));
		builder.setMessage(getString(R.string.dialog_message_enabling_bluetooth));
		
		builder.setPositiveButton(android.R.string.yes, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				onButtonClicked();
			}
		});
		builder.setNegativeButton(android.R.string.no, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	private void onButtonClicked() {

		indeterminate(this, mHandler, getString(R.string.dialog_message_enabling_bluetooth_progress),
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
