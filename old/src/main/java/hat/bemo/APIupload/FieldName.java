package hat.bemo.APIupload;

/**
 * Created by apple on 2017/11/10.
 */
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class FieldName {
    public List<NameValuePair> NameValuePair(String... params){
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        reqParam.add(new BasicNameValuePair("DataHash", params[0]));
        reqParam.add(new BasicNameValuePair("GKeyId", params[1]));
        reqParam.add(new BasicNameValuePair("TimeStamp", params[2]));
        reqParam.add(new BasicNameValuePair("AppDataJson", params[3]));
        return reqParam;
    }
}
