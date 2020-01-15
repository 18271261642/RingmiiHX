package hat.bemo.measure.set;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MeasureHttpPostData {
	private static final int REQUEST_TIMEOUT = 20*1000;//设置请求超时10秒钟  
	private static final int SO_TIMEOUT = 20*1000;  //设置等待数据超时时间10秒钟  
	private List<NameValuePair> reqParam;
	private String URL;
	private Connection connection;
	private Context context;
	private MeasureController controller;
	
	public MeasureHttpPostData(String URL, String type, List<NameValuePair> reqParam, Context context){
		this.URL = URL;
		this.reqParam = reqParam;
		this.context = context;
		System.out.println("Mark ALLJSON = " + URL);
		controller = new MeasureController();
	    connection = new Connection();	 
		connection.execute();
	}
	
	@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
	class Connection extends AsyncTask<String, String, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {	        	     	       
			try {	   
				BasicHttpParams httpParams = new BasicHttpParams();
				//連線逾時  
			    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
				HttpClient client = new DefaultHttpClient(httpParams);
				HttpPost post = new HttpPost(URL);								 		
				try {			 
					post.setEntity(new UrlEncodedFormEntity(reqParam, HTTP.UTF_8));
					Log.e("reqParam", reqParam.toString());	
				} catch (Exception e) {
					e.printStackTrace();
				}
				String entity = "";
	 
				HttpResponse response = client.execute(post);
				int statusCode = response.getStatusLine().getStatusCode();
				Log.i("statusCode", statusCode+"");
				if (statusCode == 200) {
					HttpEntity responseEntity = response.getEntity();
					entity = EntityUtils.toString(responseEntity);
				}
				
				try {
					 jsonObject = new JSONObject();
					 jsonArray = new JSONArray(entity);
					 jsonObject = jsonArray.getJSONObject(0);
					 Data =  jsonObject.getString("msg");
					 Log.i("TYPE", Data); 
					} catch (Exception e) {
						e.printStackTrace();
					}	
				
				Log.i("entity", "entity:"+entity);			
				Log.i("URL", "URL: "+ URL.toString() + "?" + httpParams.toString());		
				Update_data(Data, entity);					 
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}	
			return null;
		}		
	}
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private String Data;

	private void Update_data(String Type, String entity){		
//		Update up = new Update();
//		String[] ITEMNO = {"1"};
				
		ESettings_type mSetting_type = ESettings_type.ByStr(Type);
		
		switch(mSetting_type){	
			case type_801203:					
				MeasureController.delete0x36();
				Log.e(MeasureController.type_0x36, "封包:0x36完成!!!!!!!!!!");								 
			break;
			case type_801204:					
				MeasureController.delete0x37();
				Log.e(MeasureController.type_0x37, "封包:0x37完成!!!!!!!!!!");								 
			break;
			case type_801205:					
				MeasureController.delete0x38();
				Log.e(MeasureController.type_0x38, "封包:0x38完成!!!!!!!!!!");								 
			break;
			default:
				try{
					Log.e("上傳失敗", "封包:上傳失敗!!");				
					Log.e("上傳失敗", "錯誤代碼:"+Type);
				}catch(Exception e){
					e.printStackTrace();
				}			
			break;
		} 
	}
	
	public enum ESettings_type {
		type_801203("80120300"), 
		type_801204("80120400"),
		type_801205("80120500"),
		type_err("");

		private String type;

		ESettings_type(String str) {
			this.type = str;
		}

		public static ESettings_type ByStr(final String val) {
			for (ESettings_type type : ESettings_type.values()) {
				if (type.type.equals(val)) {
					return type;
				}
			}
			return type_err; // Never return null
		}
	}
}