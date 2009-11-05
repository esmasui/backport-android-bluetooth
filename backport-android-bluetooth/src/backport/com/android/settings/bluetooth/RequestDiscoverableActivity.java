package backport.com.android.settings.bluetooth;

import android.os.Bundle;
import backport.android.bluetooth.R;


public class RequestDiscoverableActivity extends RequestPermissionActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.discoverable);
	}
}
