package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import com.guider.healthring.R;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.Measure_Create_Table;
import hat.bemo.measure.setting_db.TABLE_BG;
import hat.bemo.measure.setting_db.TABLE_BO;
import hat.bemo.measure.setting_db.TABLE_BP;
import hat.bemo.measure.setting_db.TABLE_WT;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BO;
import hat.bemo.measure.setting_db.VO_BP;
import hat.bemo.measure.setting_db.VO_WT;

public class BluetoothLauncherActivity extends BluetoothBaseActivity{	
	protected FragmentManager mFragmentManager;
	private Measure_Create_Table dbtc = null; 
	private SQLiteDatabase db = null;
	
	@Override
	protected void onCreate(Bundle arg0) { 
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd800_activity_frame);		
		dbtc = new Measure_Create_Table(this);
		db = dbtc.getWritableDatabase();	
//		initFragment(new MeasureChoiceFragment());
		
		try{
			Intent intent = this.getIntent();
			Bundle bundle = intent.getExtras();//取得Bundle
			initFragment(new BluetoothSettingFragment(), bundle);	
		}catch(Exception e){
			e.printStackTrace();
		}
			 
//		VO_MeasuringEquipment vo = new VO_MeasuringEquipment();
//		Insert insert = new Insert();
//		vo.setDEVICE_NAME("asd");
//		vo.setCREATE_DATE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));
//		insert.insert_measure(this, vo);
		
//		Delete delete = new Delete();
//		delete.Delete_measure(this, "3");
		
		MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BP> getmeasure = dao.getdata_BP(this, "", "");
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BP data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+TABLE_BP.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
			}
		}
		ArrayList<VO_BO> getmeasure2 = dao.getdata_BO(this, "", "");
		if(getmeasure2 != null){
			for(int i=0; i<getmeasure2.size(); i++){
				VO_BO data = getmeasure2.get(i);
				Log.e("Table_name","Table_name="+TABLE_BO.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
			}
		}
		ArrayList<VO_BG> getmeasure3 = dao.getdata_BG(this, "", "");
		if(getmeasure3 != null){
			for(int i=0; i<getmeasure3.size(); i++){
				VO_BG data = getmeasure3.get(i);
				Log.e("Table_name","Table_name="+TABLE_BG.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
			}
		}
		ArrayList<VO_WT> getmeasure4 = dao.getdata_WT(this, "", "");
		if(getmeasure4 != null){
			for(int i=0; i<getmeasure4.size(); i++){
				VO_WT data = getmeasure4.get(i);
				Log.e("Table_name","Table_name="+TABLE_WT.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
			}
		}
	}
   
	// 切換Fragment
	public void changeFragment(Fragment f) {
		changeCenterFragment(f, false, null, null);
	}

	// 切換Fragment
	public void changeFragment(Fragment f, Bundle b) {
		changeCenterFragment(f, false, b, null);
	}
		
	// 切換Fragment
	public void changeFragment(Fragment f, boolean isAddBack, Bundle b, String tag) {
		changeCenterFragment(f, isAddBack, b, tag);
	}

	// 清除指定Fragment
	@SuppressWarnings("static-access")
	public void clearFragment() {
		mFragmentManager.popBackStackImmediate("text", mFragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	// 清除指定Fragment
	@SuppressWarnings("static-access")
	public void clearFragment(String tag) {
		try {
			mFragmentManager.popBackStackImmediate(tag, mFragmentManager.POP_BACK_STACK_INCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化Fragment(FragmentActivity中呼叫) 不加入返回 加入會為空白
//	public void initFragment(Fragment f) {
//		changeCenterFragment(f, false, null, "initFragment");
//	}

	// 初始化Fragment(FragmentActivity中呼叫) 不加入返回 加入會為空白
	public void initFragment(Fragment f, Bundle b) {
		changeCenterFragment(f, false, b, "initFragment");
	}
		
	private FragmentTransaction ft;

	@SuppressLint("Recycle")
	private void changeCenterFragment(Fragment f, boolean isAddBack, Bundle b, String tag) {
		try {
			mFragmentManager = getSupportFragmentManager();
			ft = mFragmentManager.beginTransaction();
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
			if (b != null) {
				f.setArguments(b);
			}
			if (tag != null && !isAddBack) {
				ft.replace(R.id.content_frame, f, tag);
			} else if(tag != null) {
				ft.replace(R.id.content_frame, f, tag);
				ft.addToBackStack(tag);
			} else {
				ft.replace(R.id.content_frame, f);
				ft.addToBackStack(tag);
			}
			ft.commit();
//			ft.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}