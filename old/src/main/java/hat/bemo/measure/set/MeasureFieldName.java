package hat.bemo.measure.set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MeasureFieldName {
	
	public List<NameValuePair> NameValuePair(String... params){
		List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
		reqParam.add(new BasicNameValuePair("DataHash", params[0]));
		reqParam.add(new BasicNameValuePair("GKeyId", params[1]));
		reqParam.add(new BasicNameValuePair("TimeStamp", params[2]));
		reqParam.add(new BasicNameValuePair("AppDataJson", params[3]));	
		
//		reqParam.add(new BasicNameValuePair("userAccount", params[0]));
//		reqParam.add(new BasicNameValuePair("account", params[1]));
//		reqParam.add(new BasicNameValuePair("timeStamp", params[2]));
//		reqParam.add(new BasicNameValuePair("data", params[3]));	
//		reqParam.add(new BasicNameValuePair("role", params[4]));
//		reqParam.add(new BasicNameValuePair("AppDataJson", params[5]));
		return reqParam;		
	}
}