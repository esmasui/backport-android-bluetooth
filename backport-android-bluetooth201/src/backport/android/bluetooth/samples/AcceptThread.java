package backport.android.bluetooth.samples;

import java.io.IOException;
import java.util.UUID;

import android.util.Log;
import backport.android.bluetooth.BluetoothAdapter;
import backport.android.bluetooth.BluetoothServerSocket;
import backport.android.bluetooth.BluetoothSocket;

/**
 * サーバーサイドのスレッドです. クライアントからの接続要求を待ち受けます.
 * 実際に使用する場合は、manageConnectedSocket()メソッドを実装します。
 */
public abstract class AcceptThread extends Thread {

	/**
	 * デバッグの際にスレッドを識別しやすいように設定する名前.
	 */
	private static final String TAG = "AcceptThread";

	/**
	 * ローカルデバイス.
	 */
	private final BluetoothAdapter mLocalDevice;

	/**
	 * サーバーソケット.
	 */
	private final BluetoothServerSocket mServerSocket;

	/**
	 * スレッドを構成します.処理のためにBluetoothAdapter、サービスの名前とUUIDを指定します.
	 * 
	 * @param locDev
	 * @param name
	 * @param uuid
	 */
	public AcceptThread(BluetoothAdapter locDev, String name, UUID uuid) {

		// デバッグのために、スレッドの名前を設定します.
		setName(TAG);

		// 後ほど使用するのでローカルデバイスの参照を保持します.
		mLocalDevice = locDev;

		BluetoothServerSocket tmp = null;

		try {

			// サーバーソケットを開きます.
			// 指定したサービス名とUUIDがシステムのサービスレコードに登録されます.
			tmp = mLocalDevice.listenUsingRfcommWithServiceRecord(name, uuid);
		} catch (IOException e) {
			
			Log.e(TAG, "", e);
		}

		mServerSocket = tmp;
	}

	/**
	 * 待ち受け中のサーバーソケットを閉じます.
	 */
	public void cancel() {

		if (mServerSocket == null) {

			return;
		}

		try {

			mServerSocket.close();
		} catch (IOException e) {
		}
	}

	public void run() {

		if (mServerSocket == null) {

			return;
		}

		BluetoothSocket socket = null;

		for (;;) {

			try {

				// クライアントからの接続要求を待ち受けます.
				socket = mServerSocket.accept();
			} catch (IOException e) {

				break;
			}

			// 接続が確立された場合、接続済みのBluetoothSocketオブジェクトが返されます.
			if (socket != null) {

				// データ転送を行います.
				// メソッドには、新たにスレッドを生成し、その中でデータ転送を行う処理が実装されている想定です.
				manageConnectedSocket(socket);

				try {

					// 接続が確立された後はサーバーソケットを閉じて、これ以上の接続要求を待ち受けないようにします.
					mServerSocket.close();
				} catch (IOException e) {
				}

				break;
			}
		}
	}

	/**
	 * メソッドには、新たにスレッドを生成し、その中でデータ転送を行う処理が実装されている想定です
	 * 
	 * @param socket
	 */
	protected abstract void manageConnectedSocket(BluetoothSocket socket);

}
