package hat.bemo.measure.setting_db;

public class VO_BP {
	private String DEVICE_NAME;
	private String CREATE_DATE;
	private String SWITCH;
	private String ITEMNO;
	
	public String getSWITCH() {
		return SWITCH;
	}
	public void setSWITCH(String sWITCH) {
		SWITCH = sWITCH;
	}
	public String getDEVICE_NAME() {
		return DEVICE_NAME;
	}
	public void setDEVICE_NAME(String dEVICE_NAME) {
		DEVICE_NAME = dEVICE_NAME;
	}
	public String getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	public String getITEMNO() {
		return ITEMNO;
	}
	public void setITEMNO(String iTEMNO) {
		ITEMNO = iTEMNO;
	}
}