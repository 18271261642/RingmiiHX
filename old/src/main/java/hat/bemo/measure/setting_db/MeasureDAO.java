package hat.bemo.measure.setting_db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MeasureDAO {
	private Measure_Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public ArrayList<VO_BP> getdata_BP(Context context, String sql_type, String value){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;
		
		//判斷裝置是否存在
		if(sql_type.equals("exist")){
			sql = "SELECT * FROM "+TABLE_BP.TABLE_NAME + " WHERE DEVICE_NAME = '" + value+ "'";
		}
		//設備已連接
		else if(sql_type.equals("connected")){
			sql = "SELECT * FROM "+TABLE_BP.TABLE_NAME + " WHERE SWITCH = '" + value+ "'";
		}
		//預設
		else if(sql_type.equals("")){
			sql = "SELECT * FROM "+TABLE_BP.TABLE_NAME;
		}
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){
				
				ArrayList<VO_BP> list = new ArrayList<VO_BP>();
				do{
					VO_BP data = new VO_BP();
					data.setDEVICE_NAME(c.getString(c.getColumnIndex(TABLE_BP.DEVICE_NAME)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BP.CREATE_DATE)));	
					data.setSWITCH(c.getString(c.getColumnIndex(TABLE_BP.SWITCH)));
					data.setITEMNO(c.getString(c.getColumnIndex(TABLE_BP.ITEMNO)));	
					list.add(data);

				}while(c.moveToNext());
				return list;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_BO> getdata_BO(Context context, String sql_type, String value){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;
		
		//判斷裝置是否存在
		if(sql_type.equals("exist")){
			sql = "SELECT * FROM "+TABLE_BO.TABLE_NAME + " WHERE DEVICE_NAME = '" + value+ "'";
		}
		//設備已連接
		else if(sql_type.equals("connected")){
			sql = "SELECT * FROM "+TABLE_BO.TABLE_NAME + " WHERE SWITCH = '" + value+ "'";
		}
		//預設
		else if(sql_type.equals("")){
			sql = "SELECT * FROM "+TABLE_BO.TABLE_NAME;
		}
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){
				
				ArrayList<VO_BO> list = new ArrayList<VO_BO>();
				do{
					VO_BO data = new VO_BO();
					data.setDEVICE_NAME(c.getString(c.getColumnIndex(TABLE_BO.DEVICE_NAME)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BO.CREATE_DATE)));	
					data.setSWITCH(c.getString(c.getColumnIndex(TABLE_BO.SWITCH)));
					data.setITEMNO(c.getString(c.getColumnIndex(TABLE_BO.ITEMNO)));	
					list.add(data);

				}while(c.moveToNext());
				return list;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_BG> getdata_BG(Context context, String sql_type, String value){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;
		System.out.println("Mark 藍芽這邊7?");
		//判斷裝置是否存在
		if(sql_type.equals("exist")){
			System.out.println("Mark 藍芽這邊77?");
			sql = "SELECT * FROM "+TABLE_BG.TABLE_NAME + " WHERE DEVICE_NAME = '" + value+ "'";
		}
		//設備已連接
		else if(sql_type.equals("connected")){
			System.out.println("Mark 藍芽這邊777?");
			sql = "SELECT * FROM "+TABLE_BG.TABLE_NAME + " WHERE SWITCH = '" + value+ "'";
		}
		//預設
		else if(sql_type.equals("")){
			System.out.println("Mark 藍芽這邊7777?");
			sql = "SELECT * FROM "+TABLE_BG.TABLE_NAME;
		}
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){
				
				ArrayList<VO_BG> list = new ArrayList<VO_BG>();
				do{
					VO_BG data = new VO_BG();
					data.setDEVICE_NAME(c.getString(c.getColumnIndex(TABLE_BG.DEVICE_NAME)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BG.CREATE_DATE)));	
					data.setSWITCH(c.getString(c.getColumnIndex(TABLE_BG.SWITCH)));
					data.setITEMNO(c.getString(c.getColumnIndex(TABLE_BG.ITEMNO)));	
					list.add(data);

				}while(c.moveToNext());
				return list;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_WT> getdata_WT(Context context, String sql_type, String value){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;
		
		//判斷裝置是否存在
		if(sql_type.equals("exist")){
			sql = "SELECT * FROM "+TABLE_WT.TABLE_NAME + " WHERE DEVICE_NAME = '" + value+ "'";
		}
		//設備已連接
		else if(sql_type.equals("connected")){
			sql = "SELECT * FROM "+TABLE_WT.TABLE_NAME + " WHERE SWITCH = '" + value+ "'";
		}
		//預設
		else if(sql_type.equals("")){
			sql = "SELECT * FROM "+TABLE_WT.TABLE_NAME;
		}
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){
				
				ArrayList<VO_WT> list = new ArrayList<VO_WT>();
				do{
					VO_WT data = new VO_WT();
					data.setDEVICE_NAME(c.getString(c.getColumnIndex(TABLE_WT.DEVICE_NAME)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_WT.CREATE_DATE)));	
					data.setSWITCH(c.getString(c.getColumnIndex(TABLE_WT.SWITCH)));
					data.setITEMNO(c.getString(c.getColumnIndex(TABLE_WT.ITEMNO)));	
					list.add(data);

				}while(c.moveToNext());
				return list;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_BP_DATA> Get_BP_MeasurementRecord(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+TABLE_BP_DATA.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<VO_BP_DATA> list = new ArrayList<VO_BP_DATA>();
				do{
					VO_BP_DATA data = new VO_BP_DATA();
					data.setAccount(c.getString(c.getColumnIndex(TABLE_BP_DATA.account)));
					data.setITEMNO(c.getString(c.getColumnIndex(TABLE_BP_DATA.ITEMNO)));	
					data.setBS_TIME(c.getString(c.getColumnIndex(TABLE_BP_DATA.BS_TIME)));
					data.setHIGH_PRESSURE(c.getString(c.getColumnIndex(TABLE_BP_DATA.HIGH_PRESSURE)));					
					data.setLOW_PRESSURE(c.getString(c.getColumnIndex(TABLE_BP_DATA.LOW_PRESSURE)));	
					data.setPLUS(c.getString(c.getColumnIndex(TABLE_BP_DATA.PLUS)));
					data.setMODEL(c.getString(c.getColumnIndex(TABLE_BP_DATA.MODEL)));	
					data.setDATA_TYPE(c.getString(c.getColumnIndex(TABLE_BP_DATA.DATA_TYPE)));	
					data.setYEAR(c.getString(c.getColumnIndex(TABLE_BP_DATA.YEAR)));
					data.setARR(c.getString(c.getColumnIndex(TABLE_BP_DATA.ARR)));	
					data.setUSUAL_MODE(c.getString(c.getColumnIndex(TABLE_BP_DATA.USUAL_MODE)));	
					data.setBS_TIME(c.getString(c.getColumnIndex(TABLE_BP_DATA.BS_TIME)));
					data.setDIAGNOSTIC_MODE(c.getString(c.getColumnIndex(TABLE_BP_DATA.DIAGNOSTIC_MODE)));	
					data.setRES(c.getString(c.getColumnIndex(TABLE_BP_DATA.RES)));	
					data.setAM(c.getString(c.getColumnIndex(TABLE_BP_DATA.AM)));
					data.setPM(c.getString(c.getColumnIndex(TABLE_BP_DATA.PM)));
					data.setAFIB(c.getString(c.getColumnIndex(TABLE_BP_DATA.AFIB)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BP_DATA.CREATE_DATE)));
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}

	public ArrayList<VO_BG_DATA> Get_BG_MeasurementRecord(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+TABLE_BG_DATA.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<VO_BG_DATA> list = new ArrayList<VO_BG_DATA>();
				do{
					VO_BG_DATA data = new VO_BG_DATA();
					data.setAccount(c.getString(c.getColumnIndex(TABLE_BG_DATA.account)));
					data.setItemno(c.getString(c.getColumnIndex(TABLE_BG_DATA.ITEMNO)));	
					data.setBloodFlowVelocity(c.getString(c.getColumnIndex(TABLE_BG_DATA.bloodFlowVelocity)));
					data.setBsTime(c.getString(c.getColumnIndex(TABLE_BG_DATA.bsTime)));					
					data.setGlu(c.getString(c.getColumnIndex(TABLE_BG_DATA.glu)));	
					data.setHemoglobin(c.getString(c.getColumnIndex(TABLE_BG_DATA.hemoglobin)));
					data.setUnit(c.getString(c.getColumnIndex(TABLE_BG_DATA.unit)));				
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_Temp_DATA> Get_TEMP_MeasurementRecord(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+TABLE_TEMP_DATA.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<VO_Temp_DATA> list = new ArrayList<VO_Temp_DATA>();
				do{
					VO_Temp_DATA data = new VO_Temp_DATA();
					data.setAccount(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.account)));
					data.setBS_TIME(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.BS_TIME)));	
					data.setTYPE_T(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.TYPE_T)));
					data.setUINT(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.UINT)));					
					data.setTEMP(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.TEMP)));	
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_TEMP_DATA.CREATE_DATE)));	
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_BW_DATA> Get_BW_MeasurementRecord(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+TABLE_BW_DATA.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<VO_BW_DATA> list = new ArrayList<VO_BW_DATA>();
				do{
					VO_BW_DATA data = new VO_BW_DATA();
					data.setAccount(c.getString(c.getColumnIndex(TABLE_BW_DATA.account)));
					data.setBFTime(c.getString(c.getColumnIndex(TABLE_BW_DATA.BF_TIME)));	
					data.setHeight(c.getString(c.getColumnIndex(TABLE_BW_DATA.HEIGHT)));
					data.setSex(c.getString(c.getColumnIndex(TABLE_BW_DATA.SEX)));					
					data.setUnit(c.getString(c.getColumnIndex(TABLE_BW_DATA.UNIT)));	
					data.setBw(c.getString(c.getColumnIndex(TABLE_BW_DATA.BW)));
					data.setRfRate(c.getString(c.getColumnIndex(TABLE_BW_DATA.BF_RATE)));
					data.setBmi(c.getString(c.getColumnIndex(TABLE_BW_DATA.BMI)));
					data.setRecycle(c.getString(c.getColumnIndex(TABLE_BW_DATA.RECYCLE)));
					data.setOrganFat(c.getString(c.getColumnIndex(TABLE_BW_DATA.ORGAN_FAT)));
					data.setBone(c.getString(c.getColumnIndex(TABLE_BW_DATA.BONE)));
					data.setBirthYear(c.getString(c.getColumnIndex(TABLE_BW_DATA.BIRTH_YEAR)));
					data.setBirthMonth(c.getString(c.getColumnIndex(TABLE_BW_DATA.BIRTH_MONTH)));
					data.setBirthDay(c.getString(c.getColumnIndex(TABLE_BW_DATA.BIRTH_DAY)));
					data.setAge(c.getString(c.getColumnIndex(TABLE_BW_DATA.AGE)));
					data.setMuscle(c.getString(c.getColumnIndex(TABLE_BW_DATA.MUSCLE)));
					data.setResister(c.getString(c.getColumnIndex(TABLE_BW_DATA.RESISTER)));
					data.setAqua(c.getString(c.getColumnIndex(TABLE_BW_DATA.AQUA)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BW_DATA.CREATE_DATE)));				
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<VO_BO_DATA> Get_BO_MeasurementRecord(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+TABLE_BO_DATA.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<VO_BO_DATA> list = new ArrayList<VO_BO_DATA>();
				do{
					VO_BO_DATA data = new VO_BO_DATA();
					data.setAccount(c.getString(c.getColumnIndex(TABLE_BO_DATA.account)));
					data.setItemno(c.getString(c.getColumnIndex(TABLE_BO_DATA.ITEMNO)));	
					data.setOXY_TIME(c.getString(c.getColumnIndex(TABLE_BO_DATA.OXY_TIME)));
					data.setOXY_COUNT(c.getString(c.getColumnIndex(TABLE_BO_DATA.OXY_COUNT)));					
					data.setPLUS(c.getString(c.getColumnIndex(TABLE_BO_DATA.PLUS)));	
					data.setSPO2(c.getString(c.getColumnIndex(TABLE_BO_DATA.SPO2)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_BO_DATA.CREATE_DATE)));				
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
	
	public ArrayList<HRDataInfoVO> getHRDataSheet(Context context){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c = null;
		String sql = null;

		sql = "SELECT * FROM "+HRDataSheet.TABLE_NAME;
				
		try {
			c = db.rawQuery(sql, null);
			
			if(c.moveToFirst()){				
				ArrayList<HRDataInfoVO> list = new ArrayList<HRDataInfoVO>();
				do{
					HRDataInfoVO data = new HRDataInfoVO();
					data.setHRDataNumber(c.getString(c.getColumnIndex(HRDataSheet.HRDataNumber)));
					data.setHeartRate(c.getString(c.getColumnIndex(HRDataSheet.HeartRate)));	
					data.setPrValue(c.getString(c.getColumnIndex(HRDataSheet.PrValue)));
					data.setRelaxDegree(c.getString(c.getColumnIndex(HRDataSheet.RelaxDegree)));					
					data.setBreaThing(c.getString(c.getColumnIndex(HRDataSheet.BreaThing)));	
					data.setHeartage(c.getString(c.getColumnIndex(HRDataSheet.Heartage)));
					data.setFiveHa(c.getString(c.getColumnIndex(HRDataSheet.FiveHa)));							
					data.setBsTime(c.getString(c.getColumnIndex(HRDataSheet.BsTime)));
					data.setCreateDate(c.getString(c.getColumnIndex(HRDataSheet.CreateDate)));					
					data.setRowData(c.getString(c.getColumnIndex(HRDataSheet.RowData)));	
					data.setCreateDateDetails(c.getString(c.getColumnIndex(HRDataSheet.CreateDateDetails)));
					data.setDetailNo(c.getString(c.getColumnIndex(HRDataSheet.DetailNo)));	
					list.add(data);
				}while(c.moveToNext());
				return list;
			}		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
}