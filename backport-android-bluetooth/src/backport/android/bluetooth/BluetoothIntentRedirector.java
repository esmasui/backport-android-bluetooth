package backport.android.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothIntentRedirector extends BroadcastReceiver {

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

	private static final Converter[] CONVERTERS;

	static {

		List<Converter> temp = new ArrayList<Converter>();
		temp.add(new DiscoveryFinishedConverter());
		temp.add(new DiscoveryStartedConverter());
		temp.add(new LocalNameChangedConverter());
		temp.add(new ScanModeChangedConverter());
		temp.add(new StateChangedConverter());
		CONVERTERS = temp.toArray(new Converter[temp.size()]);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent convertedIntent = new Intent();

		for (int i = 0, size = CONVERTERS.length; i < size; ++i) {

			Converter converter = CONVERTERS[i];
			boolean converted = converter
					.convertIntent(intent, convertedIntent);

			if (converted) {

				context.sendBroadcast(convertedIntent);

				return;
			}
		}
	}
}
