package cn.wuweikang;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wehealth.ecg.EcgDataParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BTConnectStreamThread extends Thread {
	private final String TAG = "BTCONNTHREAD";
	Handler handler;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	EcgDataParser ecgParser;

	private BluetoothSocket socket;
	private BluetoothDevice btDevice;
	private InputStream mmInStream;
	private OutputStream mmOutStream;

	public static final int DRAW_ECG_WAVE = 1000;
	public static final int BT_CONNECT_FAILED = 997;
	public static final int BT_CONNECTED = 996;
	public static final int BT_SENDMSG_FAILED = 993;
	public static final int BLUETOOTH_DETACHED = 991;
	public boolean isReceiveBTData = false;
	public boolean isFirstReceData = false;
	private static final int BUFSIZ = 15000;

	private int socketConnectCount = 0;

	private Context mContext;

	public BTConnectStreamThread(Context context, BluetoothDevice device, EcgDataParser.EcgDataGetListener ecgDataGetListener) {
		btDevice = device;
		mContext = context;
		ecgParser = new EcgDataParser(ecgDataGetListener);
		ecgParser.EcgDataParserInit();//解析心电数据包初始化
	}

	public void setHandler(Handler h) {
		handler = h;
	}

	/**
	 * 给设备发指令
	 *
	 * @param f
	 */
	private void btCThreadwrite(byte[] f) {
		if (mmOutStream != null) {
			try {
				mmOutStream.write(f);
				mmOutStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
				handler.sendEmptyMessage(BT_SENDMSG_FAILED);
			}
		} else {
			handler.sendEmptyMessage(BT_SENDMSG_FAILED);
		}
	}

	public boolean btCThreadisConnected() {
		if (socket != null) {
			return true;
		} else {
			return false;
		}
	}

	//测量结束
	public void stopBlueTooth() {
		try {
			isReceiveBTData = false;
			btCThreadwrite(EcgDataParser.PackEcgDeivceStop());//让设备停止
			Thread.sleep(4);
			boolean readState = true;
			int count = 0;
			while (readState) {//确保蓝牙关闭，多次发送结束命令
				if (count > 6) {
					readState = false;
				}
				if (mmInStream == null) {
					readState = false;
				} else {
					count++;
					int available = mmInStream.available();
					byte[] cmds = new byte[available];
					int numBytesRead = mmInStream.read(cmds);
					if (numBytesRead <= 0) {
						readState = false;
					}
					btCThreadwrite(EcgDataParser.PackEcgDeivceStop());
				}
			}
			btCThreadstop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͣ停止连接
	 */
	private void btCThreadstop() {
		ecgParser.stopInit();//数据解析包初始化
		closeSocket();
	}

	/**
	 * 关闭蓝牙Socket
	 **/
	public void closeSocket() {
		if (socket != null) {
			try {
				mmInStream.close();
				mmOutStream.close();
				socket.close();
				mmInStream = null;
				mmOutStream = null;
				socket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		super.run();
		createSocket();//创建Socket
		boolean connectState = false;
		int connectCount = 0;
		while (!connectState) {//确保连接成功
			connectState = createSocketConnect();
			if (!connectState) {
				connectCount++;
			}
			if (connectCount == 20) {
				connectState = true;
			}
		}
		if (connectCount == 20) {
			Message msg = handler.obtainMessage(BT_CONNECT_FAILED);
			msg.obj = mContext.getResources().getString(R.string.wwk_bt_device_conn_fail); // "设备蓝牙没有连接成功";
			handler.sendMessage(msg);
			return;
		}
		//获取输入输出流，确定设备信息，准备接收数据
		getIOStream();
		byte[] buffer = new byte[BUFSIZ];
		while (isReceiveBTData) {//接收数据
			try {
				int length = mmInStream.read(buffer);
				ecgParser.EcgParserPacket(buffer, length);//解析数据包，解析完了会通过接口EcgDataParser.EcgDataGetListener把数据返回
				try {
					Thread.sleep(8);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建Socket连接
	 *
	 * @return true为连接成功，否则连接失败
	 */
	private boolean createSocketConnect() {
		boolean connectState = false;
		if (socket != null && !socket.isConnected()) {
			try {
				socket.connect();
				Thread.sleep(50);
				connectState = true;
			} catch (IOException e) {
				e.printStackTrace();
				connectState = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				connectState = false;
			}
		}
		return connectState;
	}

	/**
	 * 供初始化调用 获取socket的IOS流
	 */
	private void getIOStream() {
		try {
			mmInStream = socket.getInputStream();
			mmOutStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			if (socket != null && mmInStream != null) {
				btCThreadwrite(EcgDataParser.PackEcgDeivceInfoCmd());//发送控制命令，会得到心电电极的设备序列号
				sleep(300);
				int available = mmInStream.available();
				byte[] cmds = new byte[available];
				mmInStream.read(cmds);

				if (ecgParser.EcgParserCMDInfo(cmds)) {//判断得到的返回数据是否正确，如果正确，则向设备发送检查命令，这个命令时确定用户可以点击设备上开始按钮
					handler.sendEmptyMessage(DRAW_ECG_WAVE);
					btCThreadwrite(EcgDataParser.PackEcgDeivceStart());//PackEcgDeivceStart()
					ecgParser.setModle();//
					sleep(50);
					if (!isReceiveBTData) {//设置可以接受数据
						isReceiveBTData = true;
					}
//					PreferUtils.getIntance().setECGDeviceBTMAC(btDevice.getAddress());
				} else {
					Message msg = handler.obtainMessage(BT_CONNECT_FAILED);
					msg.obj = mContext.getResources().getString(R.string.wwk_device_connect_error); // "连接设备失败，请确认电池电量是否充足或者重新打开设备";
					handler.sendMessage(msg);
					isReceiveBTData = false;
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = handler.obtainMessage(BT_CONNECT_FAILED);
			msg.obj = mContext.getResources().getString(R.string.wwk_device_connect_error); // "连接设备失败，请确认电池电量是否充足或者重新打开设备";
			handler.sendMessage(msg);
			return;
		}
	}

	/**
	 * 创建Socket
	 */
	private void createSocket() {
		try {
			socket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
