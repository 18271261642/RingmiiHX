package hat.bemo.BlueTooth.blegatt.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public class HttpPostData {
	private static final int REQUEST_TIMEOUT = 20*1000;//设置请求超时10秒钟  
	private static final int SO_TIMEOUT = 20*1000;  //设置等待数据超时时间10秒钟  
	private Connection connection;	 
	private String httpParams;

	public HttpPostData(String httpParams){		
		this.httpParams = httpParams;
	    connection = new Connection();	 
		connection.execute();
	} 
	
	class Connection extends AsyncTask<String, String, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {	        	     	       
			try {	   
				BasicHttpParams mBasicHttpParams = new BasicHttpParams();
				//連線逾時  
			    HttpConnectionParams.setConnectionTimeout(mBasicHttpParams, REQUEST_TIMEOUT);
			    HttpConnectionParams.setSoTimeout(mBasicHttpParams, SO_TIMEOUT);  
				HttpClient client = new DefaultHttpClient(mBasicHttpParams);				
				 
//				String URL = "http://122.146.113.23/foratest/deliverdata/GatewayAPI.aspx";
				//URIGHT
				String URL = "http://122.146.113.23/demo/deliverdata/GatewayAPI.aspx";
				//Fora
				//String URL = "http://122.146.113.23/forav216/DeliverData/GatewayAPI.aspx";
				//NEW
				//String URL = "http://telehealth.foracare.com.tw/DeliverData/GatewayAPI.aspx";
//				String URL = "mark test";
						 
				System.out.println("length:"+httpParams.length());
				System.out.println(URL);
				System.out.println(httpParams);
				
				HttpPost post = new HttpPost(URL);	
//				post.addHeader("x-encryption", "aes-128");
//				StringEntity stringArrayEntity = new StringEntity(AES128.encrypt(httpParams.toString()));
				  
	            StringEntity stringArrayEntity = new StringEntity(httpParams, HTTP.UTF_8);
	            
	            stringArrayEntity.setContentType("text/plain;charset=US-ASCII");
				try {			 
					post.setEntity(stringArrayEntity);
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
//					String d = AES128.decrypt(entity);
//					entity = d;
				}		
				else{
					HttpEntity responseEntity = response.getEntity();
					entity = EntityUtils.toString(responseEntity);
//					String d = AES128.decrypt(entity);
//					entity = d;
				}
				try {
					Log.i("entity", entity);
				} catch (Exception e) {
					e.printStackTrace();
				}				 				 
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}	
			return null;
		}		
	} 
}