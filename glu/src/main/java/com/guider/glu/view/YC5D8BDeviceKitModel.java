//package com.guider.glu.view;
//
//import com.jhbs.bluetooth.R;
//import com.jhbs.bluetooth.bluetooth2.BTDeviceKit.BTDeviceKitModel;
//import com.jhbs.bluetooth.bluetooth2.BTDeviceKit.BTDeviceKitModel_V1;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IGetCmd;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IGetOnePacket;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IOnDoOther;
//import com.jhbs.bluetooth.bluetooth2.ipacket.IPackStrategy_V1;
//import com.jhbs.bluetooth.bluetooth2.packet.PacketModel;
//import com.jhbs.bluetooth.entity.TBluetoothDeviceEntity;
//import com.jhbs.bluetooth.util.BinaryUtil;
//import com.jhbs.bluetooth.util.BluetoothConsts;
//import com.jhbs.bluetooth.util.PacketUtil;
//import com.tiancheng.util.ByteUtil;
//import com.tiancheng.util.Log;
//
//import java.io.IOException;
//import java.util.LinkedList;
//
///**
// * 01怡成5D-8B血糖仪
// *
// * @author donggang
// */
//public class YC5D8BDeviceKitModel extends BTDeviceKitModel_V1 {
//    protected static final String TAG = "YC5D8BDeviceKitModel";
//    public static int lastByteLength = 0;
//
//    public YC5D8BDeviceKitModel() {
//        m_intResourceId = R.mipmap.yc_5d_8b;
//        m_intGrayResourceId = R.mipmap.yc_5d_8b_gray;
//        m_stringManufacturerName = "怡成";
//        m_stringDeviceName = "血糖仪";
//        m_stringCommParam = "--";
//        m_stringBTPin = "1234";
//        m_intCmdDataMinLength = 5;
//        // 2016-01-25 Modify by Cuism : 异常检测标志置为false,使用自己的心跳包
//        m_boolDefaultExceptionCheck = false;
//
//        m_BTSocket = null;
//        m_BTDevice = null;
//
//
//        Log.d(TAG, "YC5D8BDeviceKitModel");
//        //获取命令字的相关信息
//        this.m_iGetOnePacket = new IGetOnePacket() {
//
//            @Override
//            public int getCmdIndex() {
//                // 找包头 0xYA 0x06，找不到返回
//                return BinaryUtil.indexOf3(m_byteRecvBuffer,
//                        m_intRecvBufferLength, (byte) 0x06, (byte) 0x40,
//                        (byte) 0x5F);
//            }
//
//            @Override
//            public int getDataLength(int index) {
//                return ByteUtil.byteToInt(m_byteRecvBuffer[index + 4]) + 6;
//            }
//
//            @Override
//            public boolean isCheckSumOK(int index, int dataLength) {
//                return checkSum(m_byteRecvBuffer, m_intRecvBufferLength,
//                        m_byteRecvBuffer[dataLength - 1], index, dataLength - 1);
//            }
//
//            @Override
//            public byte[] getCopyByte(int index, int dataLength) {
//                byte[] byteData = new byte[dataLength];
//                System.arraycopy(m_byteRecvBuffer, index, byteData, 0,
//                        dataLength);
//                System.arraycopy(m_byteRecvBuffer,
//                        index + dataLength,
//                        m_byteRecvBuffer,
//                        0,
//                        m_intRecvBufferLength - dataLength - index);
//                m_intRecvBufferLength -= (dataLength + index);
//                return byteData;
//            }
//        };
//
//        // 组包接口
//        this.m_iPackStrategy = new IPackStrategy_V1() {
//            @Override
//            public Object pack(Object object) {
//                // Log.d(TAG, "pack");
//                if (null == object)
//                    return null;
//
//                LinkedList<byte[]> listByteData = (LinkedList<byte[]>) object;
//                for (int i = 0; i < listByteData.size(); i++) {
//                    byte[] byteData = (byte[]) listByteData.get(i);
//                    byte[] bytePacket = PacketUtil.copyPacket(byteData, 0, 0,
//                            byteData.length, 0);
//                    if (null == bytePacket)
//                        return null;
//                }
//                return object;
//            }
//        };
//        // 获取命令字接口
//        this.m_iGetCmd = new IGetCmd() {
//            @Override
//            public int getCmd(Object object) {
//                return BTDeviceKitModel.CMD_DEFAULT;
//            }
//        };
//
//        // socket连接成功后的操作,
//        // 这里需不需要在,更复杂，比如socket连接成功后，在第一次进入
//        // 生理数据显示界面时才执行
//        this.m_iOnResume = new IOnDoOther() {
//            @Override
//            public int onDoOther(Object object) {
//                // socket连接
//                if (0 > BTDeviceKitModel.m_iPort
//                        .openPort(YC5D8BDeviceKitModel.this)) // 连接失败
//                {
//                    return -1;
//                }
//
//                return 0;
//            }
//        };
//
//        // 停止测量
//        m_iOnPause = new IOnDoOther() {
//            @Override
//            public int onDoOther(Object object) {
//                if (null == m_BTSocket)
//                    return -1;
//
//                try {
//                    m_BTSocket.close();
//                    m_BTSocket = null;
//                    Log.d(TAG, "pause send cmd.");
//
//                } catch (IOException e) {
//                    Log.d(TAG, "m_iOnPause " + e.toString());
//                    return -1;
//                }
//
//                m_intRecvBufferLength = 0;
//                return 0;
//            }
//        };
//        // 蓝牙出问题需要重新打开
//        this.m_iOnInterrupt = new IOnDoOther() {
//            @Override
//            public int onDoOther(Object object) {
//                return 0;
//            }
//        };
//
//        // 添加包处理接口
//        PacketModel packetModel = new YC5D8BPacketModel(this);
//        this.m_sarrayPacketModels.put(CMD_DEFAULT, packetModel);
//
//    }
//
//    /**
//     * 判断校验和是否
//     *
//     * @param ab      缓冲区
//     * @param length  缓冲区长度
//     * @param realSum
//     * @param len     校验字节长度
//     * @return
//     */
//    private boolean checkSum(byte[] ab, int length, byte realSum, int start,
//                             int len) {
//        if (null == ab)
//            return false;
//        int tempLength = length > ab.length ? ab.length : length;
//        if (tempLength < (start + len)) {
//            return false;
//        }
//        byte tempSum = ab[start];
//        for (int i = start + 1; i < len; i++) {
//            tempSum ^= ab[i];
//        }
//        return realSum == tempSum;
//    }
//
//    @Override
//    public void initBleDevice(TBluetoothDeviceEntity tBluetoothDeviceEntity) {
//        tBluetoothDeviceEntity.setBluetoothDeviceName(m_stringDeviceName);
//        tBluetoothDeviceEntity.setBluetoothVersion(m_stringBluetoothVersion);
//        tBluetoothDeviceEntity.setAutoConnect(m_booleanAutoConnect);
//        tBluetoothDeviceEntity.setBTPin(m_stringBTPin);
//        tBluetoothDeviceEntity.setBluetoothDeviceSort(BluetoothConsts.BLUETOOTH_SORT_BLOOD_SUGAR);
//        tBluetoothDeviceEntity.setResourceId(R.mipmap.yc_5d_8b);
//        tBluetoothDeviceEntity.setGrayResourceId(R.mipmap.yc_5d_8b_gray);
//    }
//}
