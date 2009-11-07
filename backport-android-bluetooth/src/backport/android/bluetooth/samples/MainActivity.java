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

package backport.android.bluetooth.samples;

import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.R;
import backport.android.bluetooth.R.layout;
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

	public void onDiscoverableButtonClicked(View view) {

		Intent enabler = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(enabler, REQUEST_DISCOVERABLE);
	}

	public void onStartDiscoveryButtonClicked(View view) {

		Intent enabler = new Intent(this, DiscoveryActivity.class);
		startActivity(enabler);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_ENABLE) {

			if (resultCode == RESULT_OK) {

			}
		}
	}
}
