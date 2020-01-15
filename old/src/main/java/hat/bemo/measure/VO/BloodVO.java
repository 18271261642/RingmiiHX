package hat.bemo.measure.VO;

public class BloodVO {	
	public String H_PRESSURE="0";
	public String L_PRESSURE="0";
	public String PLUS="0";
	public String BS_TIME="0";
	public String CREATE_DATE="0";
	public String UPDATE_DATE="0";
	public String IMEI="0";
	public String DATETIME="0";
	public String BLOOD_NO="0";
	
	 
	public String getBLOOD_NO() {
		return BLOOD_NO;
	}
	public void setBLOOD_NO(String bLOOD_NO) {
		BLOOD_NO = bLOOD_NO;
	}
	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}
	public String getDATETIME() {
		return DATETIME;
	}
	public void setDATETIME(String dATETIME) {
		DATETIME = dATETIME;
	}
	public String getH_PRESSURE() {
		return H_PRESSURE;
	}
	public void setH_PRESSURE(String h_PRESSURE) {
		H_PRESSURE = h_PRESSURE;
	}
	public String getL_PRESSURE() {
		return L_PRESSURE;
	}
	public void setL_PRESSURE(String l_PRESSURE) {
		L_PRESSURE = l_PRESSURE;
	}
	public String getPLUS() {
		return PLUS;
	}
	public void setPLUS(String pLUS) {
		PLUS = pLUS;
	}
	public String getBS_TIME() {
		return BS_TIME;
	}
	public void setBS_TIME(String bS_TIME) {
		BS_TIME = bS_TIME;
	}
	public String getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
}