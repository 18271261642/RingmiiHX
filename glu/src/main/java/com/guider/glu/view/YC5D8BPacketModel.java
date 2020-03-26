//package com.guider.glu.view;
//
//import java.util.Date;
//import java.util.LinkedList;
//
//import org.simple.eventbus.EventBus;
//
//import android.text.format.Time;
//
//import com.jhbs.bluetooth.bluetooth2.BTDeviceKit.BTDeviceKitModel;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IOnCmdReceiveListener;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IPackStrategy_V1;
//import com.jhbs.bluetooth.bluetooth2.packet.PacketModel;
//import com.jhbs.bluetooth.bluetooth2.port.IPort;
//import com.jhbs.bluetooth.util.Event;
//import com.jhbs.bluetooth.util.MyBaseConsts;
//import com.jhbs.bluetooth.util.SplitUtil;
//import com.tiancheng.util.ByteUtil;
//import com.tiancheng.util.DateUtil;
//import com.tiancheng.util.Log;
//import com.tiancheng.util.WaitUtil;
//
///**
// * 怡成血糖仪数据包类
// *
// * @author donggang
// *         2016-03-08 Modify by Cuism 修改内容：修改获取数据机制，改用获取最新1条数据命令字，解决了原来机制先测试后开App不能获取数据问题。
// *         同时增加搜索延时和过程控制状态判断和处理，有效解决了改用命令字导致的，每次设备开机都会获取上一条测试数据的问题。
// */
//public class YC5D8BPacketModel extends PacketModel {
//    /**
//     * 获取设备序列号
//     */
//    public static final int CMD_GET_DEVICE_NO = 1;
//    /**
//     * 获取血糖数据
//     */
//    public static final int CMD_GET_BLOOD_SUGAR_DATA = 2;
//    /**
//     * 设备处于过程控制状态
//     */
//    public static final int CMD_DEVICE_CONTROL = 3;
//    /**
//     * 设备关机
//     */
//    public static final int CMD_DEVICE_POWEROFF = 4;
//
//    /**
//     * 初始状态
//     */
//    public static int m_intState = 0;
//    /**
//     * 搜索设备
//     */
//    public static final int STATE_SEARCH_DEVICE = 0;
//    /**
//     * 建立连接
//     */
//    public static final int STATE_ESTABLISH_CONNECTION = 1;
//    /**
//     * 开始测量
//     */
//    public static final int STATE_START_MEASURE = 2;
//    /**
//     * 校正时间
//     */
//    public static final int STATE_CHECK_TIME = 3;
//    /**
//     * 读取第1条数据
//     */
//    public static final int STATE_CHECK_NEW_DATA = 4;
//
//    protected static final String TAG = "YC5D8BPacketModel";
//    private boolean m_boolFlag = true;
//    /**
//     * 搜索设备超时时间
//     */
//    private final int m_intSearchDeviceTimeout = 4000;
//    private long lastSearchTimeMills = 0;
//    /**
//     * 建立连接超时时间
//     */
//    private final int m_intConnectTimeout = 6000;
//    private long lastTimeMills = 0;
//    /**
//     * 搜索设类命令字
//     */
//    private byte[] m_byteArraySearchDevice = new byte[]
//            {0x4F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x02, (byte) 0xFF, (byte) 0xFF,
//                    (byte) (0x4F ^ 0xFF ^ 0xFF ^ 0xFF ^ 0x02 ^ 0xFF ^ 0xFF)};
//    /**
//     * 是否校验过时间
//     */
//    private boolean m_boolCheckTime = false;
//
//    /**
//     * 设备信息
//     */
//    private String[] ma_deviceInfo = {"怡成5D-8B血糖仪", MyBaseConsts.DATA_TYPE_BLOOD_SUGAR, MyBaseConsts.DATA_PROCESS_NORMAL + ""};
//
//    /**
//     * 设备序列号
//     */
//    private byte[] deviceSerialNumber = new byte[4];
//    /**
//     * 设备类
//     */
//    private BTDeviceKitModel m_btDeviceKitModel = null;
//
//    public YC5D8BPacketModel(BTDeviceKitModel btDeviceKitModel) {
//        m_btDeviceKitModel = btDeviceKitModel;
//        m_intStatus = -1; // 超时控制，由packet类本身控制
//        m_longTimeOut = -1; // 永不超时
//
//        // 解包
//        this.m_sarrayCmdPair.put(PacketModel.CMD_DEFAULT, new IOnCmdReceiveListener() {
//            @Override
//            public int onCmdReceive(IPort port, Object object) {
//                if (null == object)
//                    return -1;
//                byte[] bytePacket = (byte[]) object;
//
//                int data = handle(bytePacket, 0);
//                switch (data) // 获取数据类型
//                {
//                    case CMD_GET_DEVICE_NO: // 获取设备序列号
//                        if (System.currentTimeMillis() - lastTimeMills > m_intConnectTimeout) {    // 超时处理，是为了避免设备未关机前，多次获取到相同数据
//                            deviceSerialNumber[0] = bytePacket[2];
//                            deviceSerialNumber[1] = bytePacket[3];
//                            deviceSerialNumber[2] = bytePacket[5];
//                            deviceSerialNumber[3] = bytePacket[6];
//                            port.write(m_btDeviceKitModel.m_BTSocket, getSendByte(STATE_ESTABLISH_CONNECTION, 9)); // 终端向设备发送建立连接指令
//
//                            m_intState = STATE_CHECK_NEW_DATA; // 切换到获取最新数据状态
//                        }
//                        break;
//                    case CMD_DEVICE_CONTROL: //设备处于过程控制状态
//                        m_intState = STATE_START_MEASURE; //切换到测量状态，等待血糖数据
//                        break;
//                    case CMD_GET_BLOOD_SUGAR_DATA: // 获取血糖数据
//                        if (bytePacket.length >= 15) {
//                            short Sugar = (short) ((bytePacket[10] & 0xFF) | ((bytePacket[9] << 8) & 0xFF00));
//                            float bloodSugar = Sugar / 10f;
//                            Log.d(TAG, "5D8B血糖值:" + bloodSugar + ":" + String.format("%02X%02X", bytePacket[9], bytePacket[10]));
//                            if (0.0f < bloodSugar && 99.0f > bloodSugar) {
//                                String uploadDate = DateUtil.dateToString(DateUtil.localToUtc(new Date()), "yyyy-MM-dd HH:mm:ss");
//                                String[] tmpData = {uploadDate, String.valueOf(bloodSugar)};
//                                String msg = SplitUtil.combin(ma_deviceInfo, tmpData);
//                                String event = Event.EVENT_BLOOD_GLUCOSE;
//                                EventBus.getDefault().post(msg, event);
//                            }
//                            m_intState = STATE_SEARCH_DEVICE;
//                        }
//                        break;
//                    case CMD_DEVICE_POWEROFF: // 设备关机
//                        m_intState = STATE_SEARCH_DEVICE;
//                        break;
//                    default:
//                        break;
//                }
//                lastTimeMills = System.currentTimeMillis(); //更新连接超时时间戳
//                lastSearchTimeMills = lastTimeMills;
//                return 0;
//            }
//        });
//
//        this.m_iPackStrategy = new IPackStrategy_V1() {
//
//            @Override
//            public Object pack(Object object) {
//                LinkedList<byte[]> listByteData = new LinkedList<byte[]>();
//                if (System.currentTimeMillis() - lastSearchTimeMills > m_intSearchDeviceTimeout) {
//                    lastSearchTimeMills = System.currentTimeMillis();
//                    if (STATE_SEARCH_DEVICE == m_intState) {// 1-搜索设备
//                        listByteData.add(m_byteArraySearchDevice);
//                        Log.d(TAG, "怡成血糖仪：发搜索设备指令");
//                    } else if (STATE_CHECK_NEW_DATA == m_intState) {// 2-获取最新1条数据
//                        listByteData.add(getSendByte(STATE_CHECK_NEW_DATA, 12));
//                        Log.d(TAG, "怡成血糖仪：发获取最新1条数据指令");
//                    } else {
//                        return null;
//                    }
//                } else {
//                    Log.d(TAG, "怡成血糖仪 搜索设备 超时timeout");
//                    return null;
//                }
//                return listByteData;
//            }
//        };
//    }
//
//    // 处理读入的字节
//    public int handle(byte[] data, int start) {
//        int result = -1; //返回值 默认-1
//        if (data[0] == (byte) 0x5F && data[1] == (byte) 0x06) {// 获取设备序列号
//            result = CMD_GET_DEVICE_NO;
//        } else if (((data[0] == (byte) 0x41 || data[0] == (byte) 0x51))
//                && data[1] == (byte) 0x06 && data.length >= 15 && data[4] == (byte) 0x0C) {// 获取血糖数据
//            result = CMD_GET_BLOOD_SUGAR_DATA;
//        } else if ((data[0] == (byte) 0x4F) && data[1] == (byte) 0x06 && data[4] == (byte) 0x06
//                && data.length >= 12 && data[8] == (byte) 0x06) {// 血糖仪过程控制状态
//            result = CMD_DEVICE_CONTROL;
//        } else if ((data[0] == (byte) 0x4F) && data[1] == (byte) 0x06
//                && data[4] == (byte) 0x03 && data[7] == (byte) 0xFE) {// 血糖仪关机
//            result = CMD_DEVICE_POWEROFF;
//        }
//        return result;
//    }
//
//    /**
//     * 发送命令
//     *
//     * @param type   发送的命令类型
//     * @param length 发送的命令长度
//     * @return
//     */
//    public byte[] getSendByte(int type, int length) {
//        byte[] comm = new byte[length];
//        comm[2] = deviceSerialNumber[0];
//        comm[3] = deviceSerialNumber[1];
//        comm[5] = deviceSerialNumber[2];
//        comm[6] = deviceSerialNumber[3];
//
//        if (type == STATE_ESTABLISH_CONNECTION) {
//            comm[0] = 0x4F;
//            comm[1] = 0x06;
//            comm[4] = 0x03;
//            comm[7] = (byte) 0xFE;
//        } else if (type == STATE_CHECK_TIME) {
//            comm[0] = 0x4F;
//            comm[1] = 0x06;
//            comm[4] = 0x0A;
//            comm[7] = 0x00;
//            comm[8] = 0x00;
//
//            Time time = new Time();
//            time.setToNow();
//            comm[9] = (byte) ByteUtil.Dec2bcd(time.year - 2000); // 年低位:
//            comm[10] = (byte) ByteUtil.Dec2bcd(time.month); // 月
//            comm[11] = (byte) ByteUtil.Dec2bcd(time.monthDay); // 日
//            comm[12] = (byte) ByteUtil.Dec2bcd(time.hour); // 时
//            comm[13] = (byte) ByteUtil.Dec2bcd(time.minute); // 分
//            comm[14] = (byte) ByteUtil.Dec2bcd(time.second); // 秒
//        } else if (type == STATE_CHECK_NEW_DATA) { // 读取第1条数据
//            comm[0] = 0x4F;
//            comm[1] = 0x06;
//            comm[4] = 0x06;
//            comm[7] = 0x00;
//            comm[8] = 0x05;
//            comm[9] = 0x00;
//            comm[10] = 0x01;
//        }
//        comm[length - 1] = getCheckSum(comm);
//        return comm;
//    }
//
//    /**
//     * 得到指定byte数组的校验值
//     *
//     * @param dataBytes byte数组
//     * @return
//     */
//    public byte getCheckSum(byte[] dataBytes) {
//        int length = dataBytes.length;
//        int checkSum = 0;
//        for (int i = 0; i < length - 1; i++) {
//            checkSum ^= dataBytes[i];
//        }
//        return (byte) checkSum;
//    }
//}
