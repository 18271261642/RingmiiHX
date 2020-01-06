//package com.guider.health.bluetooth.test;
//
//import android.bluetooth.BluetoothDevice;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.guider.health.bluetooth.R;
//import com.guider.health.bluetooth.core.Params;
//
//
//public class DataShowFragment extends Fragment {
//
//    TextView connectNameTv;
//    ListView showDataLv;
//    EditText inputEt;
//    Button sendBt;
//    ArrayAdapter<String> dataListAdapter;
//
//    MainActivity mainActivity;
//    Handler uiHandler;
//
//    BluetoothDevice remoteDevice;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.layout_data_trans, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        connectNameTv = view.findViewById(R.id.device_name_tv);
//        showDataLv = view.findViewById(R.id.show_data_lv);
//        inputEt =  view.findViewById(R.id.input_et);
//        sendBt = view.findViewById(R.id.send_bt);
//        sendBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msgSend = "";
//
//                Message message = new Message();
//                message.what = Params.MSG_WRITE_DATA;
//                message.obj = "    先不处理";
//                uiHandler.sendMessage(message);
//
//                inputEt.setText("");
//            }
//        });
//
//        dataListAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_item_new_data);
//        showDataLv.setAdapter(dataListAdapter);
//
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mainActivity = (MainActivity) getActivity();
//        uiHandler = mainActivity.getUiHandler();
//    }
//
//
//    public void receiveClient(BluetoothDevice clientDevice) {
//        this.remoteDevice = clientDevice;
//        connectNameTv.setText("连接设备: " + remoteDevice.getName());
//    }
//
//
//    public void updateDataView(String newMsg,int role) {
//
//
//        //-------------暂时模拟----------------
//
//
//        int resulta = 0;
//        int resultb = 0;
//        int resultc = 0;
//        String sta = " ";
//
//        String[] strs = newMsg.split(" ");
//
//        for (int i = 0; i < strs.length; i++){
//            if (strs[i].equals("FD")){
//                if (strs[i+1].equals("FD")){
//                    String resultaS = strs[i+3];
//                    String resultaH = strs[i+4];
//                    String resultbS = strs[i+5];
//                    String resultbH = strs[i+6];
//                    String resultcS = strs[i+7];
//                    String resultcH = strs[i+8];
//                    if (strs[i+9].equals("55")){
//                        sta = "心率正常";
//                    }else{
//                        sta = "心率不齐";
//                    }
//
//                    resulta = Integer.valueOf(convertHexToString(resultaH+resultaS),16);
//                    resultb = Integer.valueOf(convertHexToString(resultbH+resultbS),16);
//                    resultc = Integer.valueOf(convertHexToString(resultcH+resultcS),16);
//
//                    dataListAdapter.clear();
////        int i = (int)(-10+Math.random()*(10-1+1));
//
//                    dataListAdapter.add("收缩压" + " : " +resulta);
//                    dataListAdapter.add("舒张压" + " : " +resultb);
//                    dataListAdapter.add("心率" + " : " +resultc);
//                    dataListAdapter.add("心率状态" + " : " +sta);
//
//                }
//
//            }
//        }
//
//
//
//
//
//
//    }
//
//
//    public void connectServer(BluetoothDevice serverDevice) {
//        this.remoteDevice = serverDevice;
//        connectNameTv.setText("连接到设备: " + remoteDevice.getName());
//    }
//
//    public String convertHexToString(String hex){
//
//        StringBuilder sb = new StringBuilder();
//        StringBuilder temp = new StringBuilder();
//
//        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
//        for( int i=0; i<hex.length()-1; i+=2 ){
//
//            //grab the hex in pairs
//            String output = hex.substring(i, (i + 2));
//            //convert hex to decimal
//            int decimal = Integer.parseInt(output, 16);
//            //convert the decimal to character
//            sb.append((char)decimal);
//
//            temp.append(decimal);
//        }
//
//        return sb.toString();
//    }
//
//
//
//}
