package hat.bemo.measure.setting_db;

public class VO_BO_DATA {
	private String ITEMNO;
	private String account;
	private String OXY_TIME;
	private String OXY_COUNT;
	private String PLUS;
	private String SPO2;
	private String CREATE_DATE;
	
	public String getItemno() {
		return ITEMNO;
	}
	public void setItemno(String itemno) {
		ITEMNO = itemno;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getOXY_TIME() {
		return OXY_TIME;
	}
	public void setOXY_TIME(String oXY_TIME) {
		OXY_TIME = oXY_TIME;
	}
	public String getOXY_COUNT() {
		return OXY_COUNT;
	}
	public void setOXY_COUNT(String oXY_COUNT) {
		OXY_COUNT = oXY_COUNT;
	}
	public String getPLUS() {
		return PLUS;
	}
	public void setPLUS(String pLUS) {
		PLUS = pLUS;
	}
	public String getSPO2() {
		return SPO2;
	}
	public void setSPO2(String sPO2) {
		SPO2 = sPO2;
	}
	public String getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
}