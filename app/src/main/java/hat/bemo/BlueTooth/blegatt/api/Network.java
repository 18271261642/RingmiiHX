package hat.bemo.BlueTooth.blegatt.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mobilogics on 2017/12/11.
 */

public class Network {

    private static final String TAG = "Network";
    private static String PostOrGet;
    private static String PostParam;
    private static String PostType;

    private static String ServerId = "47.92.218.150";
//    private static String ServerId = "210.242.50.123";
    private static Context mContext;
    private static Calendar mCalendar;
    private static SimpleDateFormat mSimpleDateFormat;
    private static Cursor mCursor;
    private static boolean mBooleanNew;
    private static int mTheFirstFew;
    private static String mType;
    private static String[] mArg;

    public static void UploadNetwork(Context Context, String... arg) {
//        arg 類型 號碼 時間 各種數值

        Log.d(TAG, "UploadNetwork");

        mContext = Context;
        mArg = arg;
        mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.CHINA);


        switch (mArg[0]) {
            case "IMBI":
                new MakeNetworkCall().execute("http://" + ServerId + "/GuiderAPI/api/getmsg/SendCardId?CARDID="+mArg[1], "Get", "IMBI");
                break;
            case "血壓":
                new MakeNetworkCall().execute("http://" + ServerId + "/GuiderAPI/api/getmsg/SendBPInfo", "Post", "血壓");
                break;
            case "體溫":
                new MakeNetworkCall().execute("http://" + ServerId + "/GuiderAPI/api/getmsg/SendBTInfo", "Post", "體溫");
                break;
            case "血氧":
                new MakeNetworkCall().execute("http://" + ServerId + "/GuiderAPI/api/getmsg/SendBOInfo", "Post", "血氧");
                break;
            case "血糖":
                new MakeNetworkCall().execute("http://" + ServerId + "/GuiderAPI/api/getmsg/SendBSInfo", "Post", "血糖");
                break;


        }

    }

    //這個要複製一堆
    static class MakeNetworkCall extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            DisplayMessage("Please Wait ...");
        }

        @Override
        protected String doInBackground(String... arg) {
            InputStream is = null;
            String URL = arg[0];
            Log.d(TAG, "URL: " + URL);
            String res = "";
            PostOrGet = arg[1];
            PostType = arg[2];
            if (arg[1].equals("Post")) {
                //Post要分開但是Get不用
                is = ByPostMethod(URL);
            } else {
                is = ByGetMethod(URL);
            }
            if (is != null) {
                res = ConvertStreamToString(is);
            } else {
                res = "Something went wrong";
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "Result: " + result);
            JSONObject obj = null;
            result = result.replace("\\", "");
//            result = result.substring(1, result.length() - 1);
//            DisplayMessage(result);
            try {

                try {
                    obj = new JSONObject(result);
                    Log.d("My App", obj.toString());
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                }
                assert obj != null;
                ContentValues cv = new ContentValues();
                if (!obj.getString("STATUS").equals("200")) {
                    switch (mArg[0]) {
                        case "悠遊卡":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1]);
                            break;
                        case "健保卡":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3],
//                                    mArg[4],
//                                    mArg[5],
//                                    mArg[6],
//                                    mArg[7]);

                            break;
                        case "血壓":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3],
//                                    mArg[4],
//                                    mArg[5]);
                            break;
                        case "體溫":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3]);
                            break;
                        case "血氧":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3],
//                                    mArg[4],
//                                    mArg[5],
//                                    mArg[6],
//                                    mArg[7]);
                            break;
                        case "血糖":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3],
//                                    mArg[4]);
                            break;
                        case "BMI":
//                            Network.UploadNetwork(mContext,
//                                    mArg[0],
//                                    mArg[1],
//                                    mArg[2],
//                                    mArg[3],
//                                    mArg[4]);
                            break;

                    }
                } else {


                }
            } catch (Exception e) {
                e.printStackTrace();
                ContentValues cv = new ContentValues();

                switch (mArg[0]) {
                    case "悠遊卡":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                             break;
                    case "健保卡":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                                mArg[1],
//                                mArg[2],
//                                mArg[3],
//                                mArg[4],
//                                mArg[5],
//                                mArg[6],
//                                mArg[7]);

                        break;
                    case "血壓":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                                mArg[1],
//                                mArg[2],
//                                mArg[3],
//                                mArg[4],
//                                mArg[5]);
                        break;
                    case "體溫":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                                mArg[1],
//                                mArg[2],
//                                mArg[3]);
                        break;
                    case "血氧":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                                mArg[1],
//                                mArg[2],
//                                mArg[3],
//                                mArg[4],
//                                mArg[5],
//                                mArg[6],
//                                mArg[7]);
                        break;
                    case "BMI":
//                        Network.UploadNetwork(mContext,
//                                mArg[0],
//                                mArg[1],
//                                mArg[2],
//                                mArg[3],
//                                mArg[4],
//                                mArg[5]);
                        break;

                }
            }


            Log.e(TAG, "Result2: " + result);
        }
    }

    static InputStream ByGetMethod(String ServerURL) {
        InputStream DataInputStream = null;
        try {
            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
//set timeout for reading InputStream
            cc.setReadTimeout(10 * 1000);
// set timeout for connection
            cc.setConnectTimeout(10 * 1000);
//set HTTP method to GET
            cc.setRequestMethod("GET");
//set it to true as we are connecting for input
            cc.setDoInput(true);
//reading HTTP response code
            int response = cc.getResponseCode();
//if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in GetData", e);
        }
        return DataInputStream;
    }

    static InputStream ByPostMethod(String ServerURL) {
        InputStream DataInputStream = null;
        try {
//Post parameters
//            System.out.println(PostParam);


            switch (mArg[0]) {

                case "血壓":
                    PostParam = "";
                    PostParam = "{" + "\"" + "CARDID" + "\"" + ":" + "\"" + mArg[1] + "\"" + "," +
                            "\"" + "DBP" + "\"" + ":" + "\"" + mArg[3] + "\"" + "," +
                            "\"" + "SBP" + "\"" + ":" + "\"" + mArg[4] + "\"" + "," +
                            "\"" + "HB" + "\"" + ":" + "\"" + mArg[5] + "\"" + "," +
                            "\"" + "CDATE" + "\"" + ":" + "\"" + mArg[2] + "\"" +
                            "}";
                    break;
                case "體溫":
                    PostParam = "";
                    PostParam = "{" + "\"" + "CARDID" + "\"" + ":" + "\"" + mArg[1] + "\"" + "," +
                            "\"" + "BT" + "\"" + ":" + "\"" + mArg[3] + "\"" + "," +
                            "\"" + "CDATE" + "\"" + ":" + "\"" + mArg[2] + "\"" +
                            "}";
                    break;

                case "血氧":
                    PostParam = "";
                    PostParam = "{" + "\"" + "CARDID" + "\"" + ":" + "\"" + mArg[1] + "\"" + "," +
                            "\"" + "BO" + "\"" + ":" + "\"" + mArg[3] + "\"" + "," +
                            "\"" + "CDATE" + "\"" + ":" + "\"" + mArg[2] + "\"" +
                            "}";
                    break;
                case "血糖":
                    PostParam = "";
                    PostParam = "{" + "\"" + "CARDID" + "\"" + ":" + "\"" + mArg[1] + "\"" + "," +
                            "\"" + "BS" + "\"" + ":" + "\"" + mArg[3] + "\"" + "," +
                            "\"" + "CDATE" + "\"" + ":" + "\"" + mArg[2] + "\"" +
                            "}";
                    break;

            }

            System.out.println(PostParam);

//Preparing
            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
//set timeout for reading InputStream
            cc.setReadTimeout(5000);
// set timeout for connection
            cc.setConnectTimeout(5000);
//set HTTP method to POST
            cc.setRequestMethod("POST");
//set it to true as we are connecting for input
            cc.setDoInput(true);
            cc.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//opens the communication link
            cc.connect();
//Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.write(PostParam.getBytes());
//            dos.writeBytes(PostParam);
//flushes data output stream.
            dos.flush();
            dos.close();
//Getting HTTP response code
            int response = cc.getResponseCode();
//if response code is 200 / OK then read Inputstream
//HttpURLConnection.HTTP_OK is equal to 200
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in GetData", e);
        }
        return DataInputStream;
    }

    static String ConvertStreamToString(InputStream stream) {
        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error in ConvertStreamToString", e);
        } catch (Exception e) {
            Log.e(TAG, "Error in ConvertStreamToString", e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error in ConvertStreamToString", e);
            } catch (Exception e) {
                Log.e(TAG, "Error in ConvertStreamToString", e);
            }
        }
        return response.toString();
    }






}

