package hat.bemo;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.guider.healthring.R;
/**
 * Created by apple on 2017/11/23.
 */

public class FORASetupFragment extends BaseFragment {

    //private ImageView imageViewdemo;
    private RelativeLayout relativeLayoutSetup;
    //private LinearLayout lin1;
    //private LinearLayout lin2;
    //private LinearLayout lin3;
    //private LinearLayout lin4;
    ImageView mdelist, mdepair;
    private BluetoothAdapter mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
    int havedevice = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fora_setup, container, false);
        relativeLayoutSetup  = (RelativeLayout) mView.findViewById(R.id.relativeLayoutSetup);
        //lin1 = (LinearLayout) mView.findViewById(R.id.lin1);
        //lin2 = (LinearLayout) mView.findViewById(R.id.lin2);
        //lin3 = (LinearLayout) mView.findViewById(R.id.lin3);
        //lin4 = (LinearLayout) mView.findViewById(R.id.lin4);
        //imageViewdemo = (ImageView) mView.findViewById(R.id.depair);
        //imageViewdemo.setImageResource(R.drawable.setup_main);

        mdepair = (ImageView) mView.findViewById(R.id.depair);
        mdelist = (ImageView) mView.findViewById(R.id.delist);

        onClick();

        if (!mBluetoothAdapter.isEnabled()){

            mBluetoothAdapter.enable();
            System.out.println("Mark 開藍芽");

        }

        return mView;
    }

    private void onClick() {

        mdepair.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startDevicePairingFragment();
            }
        });

        mdelist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startDeviceListFragment();
            }
        });

		/*
		lin3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				ComponentName com = new ComponentName("com.adups.fota", "com.adups.fota.GoogleOtaClient");
//				startActivity(new Intent().setComponent(com));
				((BaseActivity)getActivity()).changeFragment(new CommunicationSetting());
			}
		});

		lin4.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
			//((BaseActivity)getActivity()).changeFragment(new DeviceSettingsFragment());
				//Mark 刪除紀錄
				setAskDeleteDialog();
			}
		});
		*/
    }

    //Mark 刪除記錄 Jim寫的
//    private void setAskDeleteDialog() {
//        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
//        dialog.setContentView(R.layout.fora_history_del);
//        RelativeLayout relativeLayoutDialog = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutDialog);
//        View deleteYes = (View) dialog.findViewById(R.id.deleteYes);
//        View deleteNo = (View) dialog.findViewById(R.id.deleteNo);
//
//        /**
//         * 硬體設備型號
//         * prams 800 800H
//         */
//        if(UserInfoFragment.HARDWARE_VERSION.equals("800")){
//
//        }
//        else{
//            relativeLayoutDialog.setPadding(10, 10, 10, 10);
//        }
//
//        deleteYes.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                delMeasureRecord();
//                delMeasureDevice();
//                dialog.cancel();
//            }
//        });
//
//        deleteNo.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//        dialog.show();
//    }

//    private void delMeasureRecord() {
//        Delete mDelete = new Delete();
//        MeasureBLEDAO dao = new  MeasureBLEDAO();
//        ArrayList<VOMeasureRecord> getmeasure = dao.getMeasureRecord(getActivity());
//        if(getmeasure != null) {
//            for(int i=0; i<getmeasure.size(); i++) {
//                VOMeasureRecord data = getmeasure.get(i);
//                Log.e("Table_name","Table_name="+ MeasureRecord.TABLE_NAME);
//                System.out.println("getItemno="+data.getItemno());
//                System.out.println("getMeasureType="+data.getMeasureType());
//                System.out.println("getValue="+data.getValue());
//                System.out.println("getCreateDate="+data.getCreateDate());
//                System.out.println("---------------------------");
//                mDelete.deleteMeasureRecord(getActivity(), data.getItemno());
//            }
//        }
//    }

//    private void delMeasureDevice() {
//        Delete mDelete = new Delete();
//        MeasureBLEDAO dao = new  MeasureBLEDAO();
//        ArrayList<VOMeasureDevice> getmeasure = dao.getMeasureDevice(getActivity(), null);
//        if(getmeasure != null) {
//            for(int i=0; i<getmeasure.size(); i++) {
//                VOMeasureDevice data = getmeasure.get(i);
//                Log.e("Table_name","Table_name="+MeasureRecord.TABLE_NAME);
//                System.out.println("getItemno="+data.getItemno());
//                System.out.println("getDevice="+data.getDevice());
//                System.out.println("getCreateDate="+data.getCreateDate());
//                System.out.println("---------------------------");
//                mDelete.deleteMeasureDevice(getActivity(), data.getItemno());
//            }
//        }
//    }

    private void startDevicePairingFragment() {
        ((BaseActivity)BaseActivity.mContext).changeFragment(new FORADevicePairingFragment());
    }


    private void startDeviceListFragment() {
        ((BaseActivity)BaseActivity.mContext).changeFragment(new FORADeviceListFragment());
    }

    @Override
    public void setTextSize() {
        /**
         * 硬體設備型號
         * prams 800 800H
         */
//        if(UserInfoFragment.HARDWARE_VERSION.equals("800")){
//
//        }
//        else{
            relativeLayoutSetup.setPadding(10, 10, 10, 10);
//        }
    }

    @Override
    protected void LoadData() {
        // TODO Auto-generated method stub

    }


}
