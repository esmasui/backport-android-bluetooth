package backport.com.android.settings.bluetooth;

import backport.android.bluetooth.R;
import android.os.Bundle;


public class RequestEnableActivity extends RequestPermissionActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable);
	}
}
