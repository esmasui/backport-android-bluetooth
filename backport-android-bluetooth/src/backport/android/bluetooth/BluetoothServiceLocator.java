package backport.android.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.IBinder;

final class BluetoothServiceLocator {

	private static final String BLUETOOTH_SERVICE = "bluetooth";

	private static final String SERVICE_MANAGER = "android.os.ServiceManager";

	private static final String GET_SERVICE_METHOD = "getService";

	private static final String IBLUETOOTH_DEVICE = "android.bluetooth.IBluetoothDevice";

	private static final String IBLUETOOTH_DEVICE_STUB = IBLUETOOTH_DEVICE
			+ "$Stub";

	private static final String AS_INTERFACE = "asInterface";

	private static/* IBluetoothDevice */Object CACHED_INSTANCE;

	public static final synchronized/* IBluetoothDevice */Object getBluetoothService() {

		if (CACHED_INSTANCE != null) {

			return CACHED_INSTANCE;
		}

		try {

			CACHED_INSTANCE = prepareBluetoothDevice();

			return CACHED_INSTANCE;
		} catch (Exception e) {

			throw new IllegalStateException(e);
		}
	}

	private static final ClassLoader getClassLoader() {

		return BluetoothServiceLocator.class.getClassLoader();
	}

	private static final Method getDeclaredMethod(Class<?> owner,
			String methodName, Class<?>... parameterTypes)
			throws SecurityException, NoSuchMethodException {

		Method m = owner.getDeclaredMethod(methodName, parameterTypes);

		if (!m.isAccessible()) {

			m.setAccessible(true);
		}

		return m;
	}

	private static final Method getGetServiceMethod(Class<?> serviceManager)
			throws SecurityException, NoSuchMethodException {

		return getDeclaredMethod(serviceManager, GET_SERVICE_METHOD,
				String.class);
	}

	private static final Object getIBluetoothDevice(Object binder, Class<?> stub)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException,
			NoSuchMethodException, ClassNotFoundException {

		Method method = getIBluetoothDeviceStubAsInterfaceMethod(stub);

		return method.invoke(null, binder);
	}

	private static final Class<?> getIBluetoothDeviceStub()
			throws ClassNotFoundException {

		return getClassLoader().loadClass(IBLUETOOTH_DEVICE_STUB);
	}

	private static final Method getIBluetoothDeviceStubAsInterfaceMethod(
			Class<?> stub) throws SecurityException, NoSuchMethodException {

		return getDeclaredMethod(stub, AS_INTERFACE, IBinder.class);
	}

	private static final Class<?> getServiceManager()
			throws ClassNotFoundException {

		return getClassLoader().loadClass(SERVICE_MANAGER);
	}

	private static final/* IBluetoothDevice */Object prepareBluetoothDevice()
			throws Exception {

		Class<?> serviceManager = getServiceManager();
		Method method = getGetServiceMethod(serviceManager);
		Object binder = method.invoke(null, BLUETOOTH_SERVICE);
		Class<?> stub = getIBluetoothDeviceStub();
		Object service = getIBluetoothDevice(binder, stub);

		return /* (IBluetoothDevice) */service;
	}
}
