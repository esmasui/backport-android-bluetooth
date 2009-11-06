package backport.android.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothIntentRedirector extends BroadcastReceiver {

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
			dest.putExtra(BluetoothAdapter.EXTRA_SCAN_MODE, scanMode);
			// previous scan mode supported since eclair.
			dest.putExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE,
					BluetoothAdapter.ERROR);
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
			dest.putExtra(BluetoothAdapter.EXTRA_STATE, state);
			int previousState = src.getIntExtra(
					BluetoothIntent.BLUETOOTH_PREVIOUS_STATE,
					BluetoothAdapter.ERROR);
			dest.putExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, previousState);
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
