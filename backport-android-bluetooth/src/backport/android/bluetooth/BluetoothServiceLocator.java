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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.IBinder;

final class BluetoothServiceLocator {

	private static final String BLUETOOTH_SERVICE = "bluetooth";

	private static final String IBLUETOOTH_DEVICE = "android.bluetooth.IBluetoothDevice";

	private static final String IBLUETOOTH_DEVICE_STUB = IBLUETOOTH_DEVICE
			+ "$Stub";


	private static/* IBluetoothDevice */Object CACHED_INSTANCE;

	public static final synchronized/* IBluetoothDevice */Object getBluetoothService() {

		if (CACHED_INSTANCE != null) {

			return CACHED_INSTANCE;
		}

		try {

			CACHED_INSTANCE = ServiceLocator.getServiceStub(BLUETOOTH_SERVICE, IBLUETOOTH_DEVICE_STUB);

			return CACHED_INSTANCE;
		} catch (Exception e) {

			throw new IllegalStateException(e);
		}
	}
}
