package hat.bemo.measure.setting_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Insert {
	private Measure_Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public long insert_BP(Context context, VO_BP vo_bp){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_BP.DEVICE_NAME, vo_bp.getDEVICE_NAME());
		values.put(TABLE_BP.CREATE_DATE, vo_bp.getCREATE_DATE());
		values.put(TABLE_BP.SWITCH, vo_bp.getSWITCH());
		
		long insert = db.insert(TABLE_BP.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_BO(Context context, VO_BO vo_bo){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_BO.DEVICE_NAME, vo_bo.getDEVICE_NAME());
		values.put(TABLE_BO.CREATE_DATE, vo_bo.getCREATE_DATE());
		values.put(TABLE_BO.SWITCH, vo_bo.getSWITCH());
		
		long insert = db.insert(TABLE_BO.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_BG(Context context, VO_BG vo_bg){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_BG.DEVICE_NAME, vo_bg.getDEVICE_NAME());
		values.put(TABLE_BG.CREATE_DATE, vo_bg.getCREATE_DATE());
		values.put(TABLE_BG.SWITCH, vo_bg.getSWITCH());
		
		long insert = db.insert(TABLE_BG.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_WT(Context context, VO_WT vo_wt){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_WT.DEVICE_NAME, vo_wt.getDEVICE_NAME());
		values.put(TABLE_WT.CREATE_DATE, vo_wt.getCREATE_DATE());
		values.put(TABLE_WT.SWITCH, vo_wt.getSWITCH());
		
		long insert = db.insert(TABLE_WT.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_BP_DATA(Context context, VO_BP_DATA vo_bp){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_BP_DATA.account, vo_bp.getAccount());
		values.put(TABLE_BP_DATA.AFIB, vo_bp.getAFIB());
		values.put(TABLE_BP_DATA.AM, vo_bp.getAM());
		values.put(TABLE_BP_DATA.ARR, vo_bp.getARR());
		values.put(TABLE_BP_DATA.BS_TIME, vo_bp.getBS_TIME());
		values.put(TABLE_BP_DATA.CREATE_DATE, vo_bp.getCREATE_DATE());
		values.put(TABLE_BP_DATA.DATA_TYPE, vo_bp.getDATA_TYPE());
		values.put(TABLE_BP_DATA.DIAGNOSTIC_MODE, vo_bp.getDIAGNOSTIC_MODE());
		values.put(TABLE_BP_DATA.HIGH_PRESSURE, vo_bp.getHIGH_PRESSURE());
		values.put(TABLE_BP_DATA.LOW_PRESSURE, vo_bp.getLOW_PRESSURE());
		values.put(TABLE_BP_DATA.MODEL, vo_bp.getMODEL());
		values.put(TABLE_BP_DATA.PLUS, vo_bp.getPLUS());
		values.put(TABLE_BP_DATA.PM, vo_bp.getPM());
		values.put(TABLE_BP_DATA.RES, vo_bp.getRES());
		values.put(TABLE_BP_DATA.USUAL_MODE, vo_bp.getUSUAL_MODE());
		values.put(TABLE_BP_DATA.YEAR, vo_bp.getYEAR());
		
		long insert = db.insert(TABLE_BP_DATA.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_BO_DATA(Context context, VO_BO_DATA vo_bo){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();

		ContentValues values = new ContentValues();				
		values.put(TABLE_BO_DATA.account, vo_bo.getAccount());
		values.put(TABLE_BO_DATA.ITEMNO, vo_bo.getItemno());
		values.put(TABLE_BO_DATA.OXY_TIME, vo_bo.getOXY_TIME());
		values.put(TABLE_BO_DATA.OXY_COUNT, vo_bo.getOXY_COUNT());
		values.put(TABLE_BO_DATA.PLUS, vo_bo.getPLUS());
		values.put(TABLE_BO_DATA.SPO2, vo_bo.getSPO2());
		values.put(TABLE_BO_DATA.CREATE_DATE, vo_bo.getCREATE_DATE());
		
		long insert = db.insert(TABLE_BO_DATA.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insert_BG_DATA(Context context, VO_BG_DATA vo_bg){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(TABLE_BG_DATA.account, vo_bg.getAccount());
		values.put(TABLE_BG_DATA.ITEMNO, vo_bg.getItemno());
		values.put(TABLE_BG_DATA.unit, vo_bg.getUnit());
		values.put(TABLE_BG_DATA.bsTime, vo_bg.getBsTime());
		values.put(TABLE_BG_DATA.glu, vo_bg.getGlu());
		values.put(TABLE_BG_DATA.hemoglobin, vo_bg.getHemoglobin());
		values.put(TABLE_BG_DATA.bloodFlowVelocity, vo_bg.getBloodFlowVelocity());
		
		long insert = db.insert(TABLE_BG_DATA.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
	
	public long insertHRDataSheet(Context context, HRDataInfoVO hrVO){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();		
		values.put(HRDataSheet.HeartRate, hrVO.getHeartRate());
		values.put(HRDataSheet.PrValue, hrVO.getPrValue());
		values.put(HRDataSheet.RelaxDegree, hrVO.getRelaxDegree());
		values.put(HRDataSheet.BreaThing, hrVO.getBreaThing());
		values.put(HRDataSheet.Heartage, hrVO.getHeartage());
		values.put(HRDataSheet.FiveHa, hrVO.getFiveHa());
		values.put(HRDataSheet.BsTime, hrVO.getBsTime());
		values.put(HRDataSheet.CreateDate, hrVO.getCreateDate());
		values.put(HRDataSheet.RowData, hrVO.getRowData());
		values.put(HRDataSheet.CreateDateDetails, hrVO.getCreateDateDetails());
		values.put(HRDataSheet.DetailNo, hrVO.getDetailNo());
		long insert = db.insert(HRDataSheet.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
}