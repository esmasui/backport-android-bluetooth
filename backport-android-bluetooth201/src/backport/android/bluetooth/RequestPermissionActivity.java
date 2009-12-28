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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;

/**
 * RequestDiscoverablityActivity、RequestEnableActivityの基底クラス. <br />
 */
abstract class RequestPermissionActivity extends Activity {

	public void indeterminate(Context context, Handler handler, String message,
			final Runnable runnable, OnDismissListener dismissListener) {

		try {

			indeterminateInternal(context, handler, message, runnable,
					dismissListener, true);
		} catch (Exception e) {

			; // nop.
		}
	}

	public void indeterminate(Context context, Handler handler, String message,
			final Runnable runnable, OnDismissListener dismissListener,
			boolean cancelable) {

		try {

			indeterminateInternal(context, handler, message, runnable,
					dismissListener, cancelable);
		} catch (Exception e) {

			; // nop.
		}
	}

	/**
	 * Progressダイアログを構成する.
	 * 
	 * @param cancelListener
	 * @return
	 */
	private ProgressDialog createProgressDialog(Context context, String message) {

		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(false);
		dialog.setMessage(message);

		return dialog;
	}

	private void indeterminateInternal(Context context, final Handler handler,
			String message, final Runnable runnable,
			OnDismissListener dismissListener, boolean cancelable) {

		final ProgressDialog dialog = createProgressDialog(context, message);
		dialog.setCancelable(cancelable);

		if (dismissListener != null) {

			dialog.setOnDismissListener(dismissListener);
		}

		dialog.show();

		new Thread() {

			@Override
			public void run() {

				// 関数オブジェクトつくるのめんどーだった.
				runnable.run();

				handler.post(new Runnable() {

					public void run() {

						try {

							dialog.dismiss();
						} catch (Exception e) {

							; // nop.
						}

					}
				});
			};
		}.start();
	}

}
