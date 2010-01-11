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

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothIntentRedirector extends BroadcastReceiver {

	private static final String TAG = BluetoothIntentRedirector.class
			.getSimpleName();

	public static final int BLUETOOTH_STATE_OFF = 0;
	public static final int BLUETOOTH_STATE_ON = 2;
	public static final int BLUETOOTH_STATE_TURNING_OFF = 3;
	public static final int BLUETOOTH_STATE_TURNING_ON = 1;
	public static final int BOND_BONDED = 1;
	public static final int BOND_BONDING = 2;
	public static final int BOND_NOT_BONDED = 0;
	public static final int RESULT_FAILURE = -1;
	public static final int RESULT_SUCCESS = 0;
	public static final int SCAN_MODE_CONNECTABLE = 1;
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 3;
	public static final int SCAN_MODE_NONE = 0;
	// private static final String BLUETOOTH_ADMIN_PERM =
	// android.Manifest.permission.BLUETOOTH_ADMIN;
	private static final String BLUETOOTH_PERM = android.Manifest.permission.BLUETOOTH;

	private static interface Converter {
		boolean convertIntent(Intent src, Intent dest);
	}

	private static abstract class ConverterTemplate implements Converter {

		public final boolean convertIntent(Intent src, Intent dest) {
			String action = src.getAction();

			if (!hasResponsibility(action)) {
				return false;
			}

			convertIntentInternal(src, dest);
			return true;
		}

		protected abstract boolean hasResponsibility(String action);

		protected abstract String getAction();

		protected void convertIntentInternal(Intent src, Intent dest) {
			String action = getAction();
			dest.setAction(action);
		}
	}

	private static final class DiscoveryFinishedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.DISCOVERY_COMPLETED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
		}
	}

	private static final class DiscoveryStartedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.DISCOVERY_STARTED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothAdapter.ACTION_DISCOVERY_STARTED;
		}
	}

	private static final class LocalNameChangedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.NAME_CHANGED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String name = src.getStringExtra(BluetoothIntent.NAME);
			dest.putExtra(BluetoothAdapter.EXTRA_LOCAL_NAME, name);
		}
	}

	private static final class ScanModeChangedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.SCAN_MODE_CHANGED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			int scanMode = src.getIntExtra(BluetoothIntent.SCAN_MODE,
					BluetoothAdapter.ERROR);
			scanMode = convertScanMode(scanMode);
			dest.putExtra(BluetoothAdapter.EXTRA_SCAN_MODE, scanMode);
			// previous scan mode supported since eclair.
			dest.putExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE,
					BluetoothAdapter.ERROR);
		}

		private int convertScanMode(int scanMode) {
			switch (scanMode) {
			case SCAN_MODE_NONE:
				return BluetoothAdapter.SCAN_MODE_NONE;
			case SCAN_MODE_CONNECTABLE:
				return BluetoothAdapter.SCAN_MODE_CONNECTABLE;
			case SCAN_MODE_CONNECTABLE_DISCOVERABLE:
				return BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
			}
			return BluetoothAdapter.ERROR;
		}
	}

	private static final class StateChangedConverter extends ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action
					.equals(BluetoothIntent.BLUETOOTH_STATE_CHANGED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothAdapter.ACTION_STATE_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			int state = src.getIntExtra(BluetoothIntent.BLUETOOTH_STATE,
					BluetoothAdapter.ERROR);
			state = convertState(state);
			dest.putExtra(BluetoothAdapter.EXTRA_STATE, state);
			int previousState = src.getIntExtra(
					BluetoothIntent.BLUETOOTH_PREVIOUS_STATE,
					BluetoothAdapter.ERROR);
			previousState = convertState(previousState);
			dest.putExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, previousState);
		}

		private int convertState(int state) {
			switch (state) {
			case BLUETOOTH_STATE_TURNING_OFF:
				return BluetoothAdapter.STATE_TURNING_OFF;
			case BLUETOOTH_STATE_OFF:
				return BluetoothAdapter.STATE_OFF;
			case BLUETOOTH_STATE_TURNING_ON:
				return BluetoothAdapter.STATE_TURNING_ON;
			case BLUETOOTH_STATE_ON:
				return BluetoothAdapter.STATE_ON;
			}
			return BluetoothAdapter.ERROR;
		}
	}

	private static final class AclConnectedConverter extends ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action
					.equals(BluetoothIntent.REMOTE_DEVICE_CONNECTED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_ACL_CONNECTED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
		}
	}

	private static final class AclDisconnectedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action
					.equals(BluetoothIntent.REMOTE_DEVICE_DISCONNECTED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_ACL_DISCONNECTED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
		}
	}

	private static final class AclDisconnectRequestedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action
					.equals(BluetoothIntent.REMOTE_DEVICE_DISCONNECT_REQUESTED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
		}
	}

	private static final class BondStateChangedConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.BOND_STATE_CHANGED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_BOND_STATE_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			int bondState = src.getIntExtra(BluetoothIntent.BOND_STATE,
					BluetoothDevice.ERROR);
			bondState = convertBondState(bondState);
			dest.putExtra(BluetoothDevice.EXTRA_BOND_STATE, bondState);
			int previousBondState = src.getIntExtra(
					BluetoothIntent.BOND_PREVIOUS_STATE, BluetoothDevice.ERROR);
			previousBondState = convertBondState(previousBondState);
			dest.putExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
					previousBondState);
		}

		private int convertBondState(int bondState) {
			switch (bondState) {
			case BOND_NOT_BONDED:
				return BluetoothDevice.BOND_NONE;
			case BOND_BONDING:
				return BluetoothDevice.BOND_BONDING;
			case BOND_BONDED:
				return BluetoothDevice.BOND_BONDED;
			}
			return BluetoothDevice.ERROR;
		}
	}

	private static final class BondStateChangedBondingConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.PAIRING_REQUEST_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_BOND_STATE_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			dest.putExtra(BluetoothDevice.EXTRA_BOND_STATE,
					BluetoothDevice.BOND_BONDING);
			dest.putExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
					BluetoothDevice.BOND_NONE);
		}
	}

	private static final class BondStateChangedBondNoneConverter extends
			ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.PAIRING_CANCEL_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_BOND_STATE_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			dest.putExtra(BluetoothDevice.EXTRA_BOND_STATE,
					BluetoothDevice.BOND_NONE);
			dest.putExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
					BluetoothDevice.BOND_BONDING);
		}
	}

	private static final class ClassChangedConverter extends ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action
					.equals(BluetoothIntent.REMOTE_DEVICE_CLASS_UPDATED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_CLASS_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			int deviceClass = src.getIntExtra(BluetoothIntent.CLASS,
					BluetoothDevice.ERROR);
			dest.putExtra(BluetoothDevice.EXTRA_CLASS, deviceClass);
		}
	}

	private static final class FoundConverter extends ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.REMOTE_DEVICE_FOUND_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_FOUND;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			String name = src.getStringExtra(BluetoothIntent.NAME);
			dest.putExtra(BluetoothDevice.EXTRA_NAME, name);
			short rssi = src.getShortExtra(BluetoothIntent.RSSI,
					Short.MIN_VALUE);
			dest.putExtra(BluetoothDevice.EXTRA_RSSI, rssi);
		}
	}

	private static final class NameChangedConverter extends ConverterTemplate {

		@Override
		protected boolean hasResponsibility(String action) {
			return action.equals(BluetoothIntent.REMOTE_NAME_UPDATED_ACTION);
		}

		@Override
		protected String getAction() {
			return BluetoothDevice.ACTION_NAME_CHANGED;
		}

		@Override
		protected void convertIntentInternal(Intent src, Intent dest) {
			super.convertIntentInternal(src, dest);
			String address = src.getStringExtra(BluetoothIntent.ADDRESS);
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
					.getRemoteDevice(address);
			dest.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			String name = src.getStringExtra(BluetoothIntent.NAME);
			dest.putExtra(BluetoothDevice.EXTRA_NAME, name);
		}
	}

	private static final Converter[] CONVERTERS;

	static {

		List<Converter> temp = new ArrayList<Converter>();

		// defined in BluetoothAdapter
		temp.add(new DiscoveryFinishedConverter());
		temp.add(new DiscoveryStartedConverter());
		temp.add(new LocalNameChangedConverter());
		temp.add(new ScanModeChangedConverter());
		temp.add(new StateChangedConverter());

		// defined in BluetoothDevice
		temp.add(new AclConnectedConverter());
		temp.add(new AclDisconnectedConverter());
		temp.add(new AclDisconnectRequestedConverter());
		temp.add(new BondStateChangedConverter());
		temp.add(new BondStateChangedBondingConverter());
		temp.add(new BondStateChangedBondNoneConverter());
		temp.add(new ClassChangedConverter());
		temp.add(new FoundConverter());
		temp.add(new NameChangedConverter());

		CONVERTERS = temp.toArray(new Converter[temp.size()]);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "receive:" + intent.toString());
		Intent convertedIntent = new Intent();

		for (int i = 0, size = CONVERTERS.length; i < size; ++i) {
			Converter converter = CONVERTERS[i];
			boolean converted = converter
					.convertIntent(intent, convertedIntent);

			if (converted) {

				String pkg = BackportProperties.getPackageName();
				
				if (pkg != null) {
					convertedIntent.setPackage(pkg);
				}
				
				// context.sendBroadcast(convertedIntent, BLUETOOTH_PERM);
				context.sendBroadcast(convertedIntent);
				Log.d(TAG, "redirect:" + convertedIntent.toString());
				return;
			}
		}
	}
}
