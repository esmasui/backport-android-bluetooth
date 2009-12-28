package backport.android.bluetooth.samples;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothSocket;
import backport.android.bluetooth.R;
import backport.android.bluetooth.UUIDHelper;
import backport.android.bluetooth.chat.BluetoothChat;

public class MainActivity extends Activity {

	private BluetoothAdapter mLocalDevice;

	private Handler mHandler = new Handler();

	private class MyAcceptThread extends AcceptThread {

		public MyAcceptThread(BluetoothAdapter locDev, String name, UUID uuid) {

			super(locDev, name, uuid);
		}

		@Override
		protected void manageConnectedSocket(BluetoothSocket socket) {

			cancel();
			showToast(socket.toString());
		}

	}

	private AcceptThread mAcceptThread;

	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (mAcceptThread != null) {

			mAcceptThread.cancel();
			mAcceptThread = null;
		}
	}

	public void onBluetoothChatButtonClick(View view) {

		Intent chatIntent = new Intent(this, BluetoothChat.class);
		startActivity(chatIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// mLocalDevice = BluetoothAdapter.getDefaultAdapter();

		// mHandler.post(new Runnable() {
		//
		// public void run() {
		//
		// doTest();
		// }
		// });

	}

	private void showToast(final String text) {

		final Context self = this;

		mHandler.post(new Runnable() {

			public void run() {

				Toast toast = Toast.makeText(self, text, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	private void doTest() {

		doTestName();

		try {

			doTestDatabase();

			doTestServerSocket();
		} catch (Exception e) {

			showToast(e.toString());
		}
	}

	private void doTestServerSocket() {

		// mAcceptThread = new MyAcceptThread(mLocalDevice, "MyService",
		// UUIDHelper.RFCOMM_PROTOCOL_UUID);
		mAcceptThread = new MyAcceptThread(mLocalDevice, "MyService",
				UUIDHelper.fromUUID16(0x9999));
		mAcceptThread.start();
	}

	private void doTestName() {

		String name = mLocalDevice.getName();

		showToast(name);
	}

	private void doTestDatabase() throws Exception {

		Class<?> clazz = getClassLoader().loadClass(
				"android.bluetooth.Database");

		showToast(clazz.toString());
	}
}
