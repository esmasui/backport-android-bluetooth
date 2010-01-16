package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import backport.android.bluetooth.R;

public class MainActivity extends Activity {

	public void onBluetoothChatButtonClick(View view) {

		Intent chatIntent = new Intent(this, BluetoothChat.class);
		startActivity(chatIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button = (Button) findViewById(R.id.button_chat);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onBluetoothChatButtonClick(v);
			}
		});
	}
}
