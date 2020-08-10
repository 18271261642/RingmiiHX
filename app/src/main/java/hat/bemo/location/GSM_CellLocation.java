package hat.bemo.location;

import android.annotation.SuppressLint;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.util.List;

import hat.bemo.MyApplication;

/**
 * Created by apple on 2017/10/23.
 */

public class GSM_CellLocation {

    List<CellInfo> cellInfos;
    private TelephonyManager telephonyManager;
    String MCC="0";
    String MNC="0";
    String LAC="0";
    String CELL_ID="0";
    String RSSI="-79";
    StringBuffer MCC_list;
    StringBuffer MNC_list;
    StringBuffer LAC_list;
    StringBuffer CELL_ID_list;
    StringBuffer RSSI_list;

    public interface OnGSMListeners {
        void onGsmParam(String MCC, String MNC, String LAC, String CELL_ID, String RSSI);
    }

    public OnGSMListeners mListener;

    public void setOnGSMListeners(OnGSMListeners ongsmListener) {
        mListener = ongsmListener;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("static-access")
    public GSM_CellLocation(){
        telephonyManager = (TelephonyManager) MyApplication.context.getSystemService(MyApplication.context.TELEPHONY_SERVICE);
        cellInfos = (List<CellInfo>) telephonyManager.getAllCellInfo();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void GsmLocation(){
        MCC_list = new StringBuffer();
        MNC_list = new StringBuffer();
        LAC_list = new StringBuffer();
        CELL_ID_list = new StringBuffer();
        RSSI_list = new StringBuffer();

        if(cellInfos == null){
            // 返回值MCC + MNC
            //基站資訊   http://blog.csdn.net/android_ls/article/details/8672442
            try{
                String operator = telephonyManager.getNetworkOperator();
                int mcc = Integer.parseInt(operator.substring(0, 3));
                int mnc = Integer.parseInt(operator.substring(3));

                // 中國移動和中國聯通獲取LAC、CID的方式
                GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
                int lac = location.getLac();
                int cellId = location.getCid();

                MCC = String.valueOf(mcc);
                if(MCC.equals("") | MCC == null) MCC = "0";
                MNC = String.valueOf(mnc);
                if(MNC.equals("") | MNC == null) MNC = "0";
                LAC = String.valueOf(lac);
                if(LAC.equals("") | LAC == null) LAC = "0";
                CELL_ID = String.valueOf(cellId);
                if(CELL_ID.equals("") | CELL_ID == null) CELL_ID = "0";
                RSSI = String.valueOf("-79");

                MCC_list.append(MCC).append(";");
                MNC_list.append(MNC).append(";");
                LAC_list.append(LAC).append(";");
                CELL_ID_list.append(CELL_ID).append(";");
                RSSI_list.append(RSSI).append(";");

//                Log.i("中國移動基站定位:", " MCC = " + MCC + "\t MNC = " + MNC + "\t LAC = " + LAC + "\t CID = " + CELL_ID);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(cellInfos != null) {

            for(CellInfo cellInfo : cellInfos){

                if(cellInfo instanceof CellInfoGsm){

                    CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;

                    CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                    if(cellIdentity.getCid() == Integer.MAX_VALUE)
                        continue;
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

                    MCC = String.valueOf(cellIdentity.getMcc());
                    MNC = String.valueOf(cellIdentity.getMnc());
                    LAC = String.valueOf(cellIdentity.getLac());
                    CELL_ID = String.valueOf(cellIdentity.getCid());
                    RSSI = String.valueOf(cellSignalStrengthGsm.getDbm());


                    if(MCC.equals("") | MCC == null) MCC = "0";
                    if(MNC.equals("") | MNC == null) MNC = "0";
                    if(LAC.equals("") | LAC == null) LAC = "0";
                    if(CELL_ID.equals("") | CELL_ID == null) CELL_ID = "0";

                    MCC_list.append(MCC).append(";");
                    MNC_list.append(MNC).append(";");
                    LAC_list.append(LAC).append(";");
                    CELL_ID_list.append(CELL_ID).append(";");
                    RSSI_list.append(RSSI).append(";");

//			    	Log.e("MCC", MCC);
//					Log.e("MNC", MNC);
//					Log.e("LAC", LAC);
//					Log.e("CELL_ID", CELL_ID);
//					Log.e("RSSI", RSSI);

                    // 4.3 以上版本已將 CellInfoWcdma 獨立出來
                }
                else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                        cellInfo instanceof CellInfoWcdma) {

                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;

                    CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
                    if(cellIdentity.getCid() == Integer.MAX_VALUE)
                        continue;
                    CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

                    MCC = String.valueOf(cellIdentity.getMcc());
                    MNC = String.valueOf(cellIdentity.getMnc());
                    LAC = String.valueOf(cellIdentity.getLac());
                    CELL_ID = String.valueOf(cellIdentity.getCid());
                    RSSI = String.valueOf(cellSignalStrengthWcdma.getDbm());

                    if(MCC == null || MCC.equals("") && MNC == null || MNC.equals("") && LAC == null || LAC.equals("") &&
                            CELL_ID == null || CELL_ID.equals("") && RSSI == null || RSSI.equals(""))
                    {
                        MCC = "0";
                        MNC = "0";
                        LAC = "0";
                        CELL_ID = "0";
                        RSSI = "0";
                    }

                    MCC_list.append(MCC).append(";");
                    MNC_list.append(MNC).append(";");
                    LAC_list.append(LAC).append(";");
                    CELL_ID_list.append(CELL_ID).append(";");
                    RSSI_list.append(RSSI).append(";");

//			    	Log.e("Android 4.3以上_MCC", MCC);
//					Log.e("Android 4.3以上_MNC", MNC);
//					Log.e("Android 4.3以上_LAC", LAC);
//					Log.e("Android 4.3以上_CELL_ID", CELL_ID);
//					Log.e("Android 4.3以上_RSSI", RSSI);
                }
            }
        }

        String MCCS = "0";
        String MNCS = "0";
        MCCS = MCC;
        MNCS = MNC;
        // 獲取鄰區基站信息
//        List<NeighboringCellInfo> infos = telephonyManager.getNeighboringCellInfo();
//        StringBuffer sb = new StringBuffer("總數 : " + infos.size() + "\n");
//
//        for (NeighboringCellInfo info1 : infos) {  // 根據鄰區總數進行循環
//            sb.append(" LAC : " + info1.getLac()); // 取出當前鄰區的LAC
//            sb.append(" CID : " + info1.getCid()); // 取出當前鄰區的CID
//            sb.append(" Psc : " + info1.getPsc());
//            sb.append(" Rssi : " + (-113 + 2 * info1.getRssi()) + "\n"); // 獲取鄰區基站信號強度
//
//            String LACS = "0";
//            String CELL_IDS = "0";
//            String RSSIS = "0";
//
//            LACS = String.valueOf(info1.getLac());
//            CELL_IDS = String.valueOf(info1.getCid());
//            RSSIS = String.valueOf((-113 + 2 * info1.getRssi()));
//
//            if(LACS.equals("-1") | CELL_IDS.equals("-1")){
//                LACS = "0";
//                CELL_IDS = "0";
//            }
//
//            if(LACS == null || LACS.equals("") && CELL_IDS == null || CELL_IDS.equals("") && RSSIS == null || RSSIS.equals("")){
//                LACS = "0";
//                CELL_IDS = "0";
//                RSSIS = "0";
//            }
//
//            MCC = MCC_list.append(MCCS).append(";").toString();
//            MNC = MNC_list.append(MNCS).append(";").toString();
//            LAC  = LAC_list.append(LACS).append(";").toString();
//            CELL_ID  = CELL_ID_list.append(CELL_IDS).append(";").toString();
//            RSSI  = RSSI_list.append(RSSIS).append(";").toString();
////        	Log.e("MCC_List", "花惹發花惹發花惹發花惹發花惹發花惹發花惹發花惹發");
//        }

        if(MCC == null || MCC.equals("") && MNC == null || MNC.equals("") && LAC == null || LAC.equals("") &&
                CELL_ID == null || CELL_ID.equals("") && RSSI == null || RSSI.equals(""))
        {
            MCC = "0";
            MNC = "0";
            LAC = "0";
            CELL_ID = "0";
            RSSI = "0";
        }

        MCC = MCC_list.toString();
        MNC = MNC_list.toString();
        LAC  = LAC_list.toString();
        CELL_ID  = CELL_ID_list.toString();
        RSSI  = RSSI_list.toString();

        mListener.onGsmParam(MCC, MNC, LAC, CELL_ID, RSSI);
    }


}
