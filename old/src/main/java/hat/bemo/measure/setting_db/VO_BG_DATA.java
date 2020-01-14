package hat.bemo.measure.setting_db;

public class VO_BG_DATA {
	private String account;
	private String ITEMNO;	
	private String unit;	
	private String bsTime;
	private String glu;
	private String hemoglobin;
	private String bloodFlowVelocity;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getItemno() {
		return ITEMNO;
	}
	public void setItemno(String itemno) {
		ITEMNO = itemno;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getBsTime() {
		return bsTime;
	}
	public void setBsTime(String bsTime) {
		this.bsTime = bsTime;
	}
	public String getGlu() {
		return glu;
	}
	public void setGlu(String glu) {
		this.glu = glu;
	}
	public String getHemoglobin() {
		return hemoglobin;
	}
	public void setHemoglobin(String hemoglobin) {
		this.hemoglobin = hemoglobin;
	}
	public String getBloodFlowVelocity() {
		return bloodFlowVelocity;
	}
	public void setBloodFlowVelocity(String bloodFlowVelocity) {
		this.bloodFlowVelocity = bloodFlowVelocity;
	}
}