package backport.android.bluetooth.samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.R;
import backport.android.bluetooth.chat.BluetoothChat;

public class MainActivity extends Activity {

	public void onBluetoothChatButtonClick(View view) {

		Intent chatIntent = new Intent(this, BluetoothChat.class);
		startActivity(chatIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}
}
