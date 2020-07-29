package cn.wuweikang;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StringUtil {


	/**将点的集合改为数组**/
	public static int[] getEcgDataINTs(List<int[]> d){
		long start = System.currentTimeMillis();
		int[] data = new int[d.size()*12];
		for (int i = 0; i < d.size(); i++) {
			for (int j = 0; j < 12; j++) {
				data[i*12+j] = d.get(i)[j];
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("重新赋值时间："+(end-start));
		return data;
	}


	public static Map<String, StringBuffer> praseIntDataToString(List<int[]> bufferDrawSnap) {
		Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sb4 = new StringBuffer();
		StringBuffer sb5 = new StringBuffer();
		StringBuffer sb6 = new StringBuffer();
		StringBuffer sb7 = new StringBuffer();
		StringBuffer sb8 = new StringBuffer();
		StringBuffer sb9 = new StringBuffer();
		StringBuffer sb10 = new StringBuffer();
		StringBuffer sb11 = new StringBuffer();
		StringBuffer sb12 = new StringBuffer();
		for (int j = 0; j<bufferDrawSnap.size(); j++) {
			for (int i = 0; i < 12; i++) {
				if (i==0) {
					sb1.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==1) {
					sb2.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==2) {
					sb3.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==3) {
					sb4.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==4) {
					sb5.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==5) {
					sb6.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==6) {
					sb7.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==7) {
					sb8.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==8) {
					sb9.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==9) {
					sb10.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==10) {
					sb11.append(bufferDrawSnap.get(j)[i]+" ");
				}
				if (i==11) {
					sb12.append(bufferDrawSnap.get(j)[i]+" ");
				}
			}
		}
		map.put("Ch0", sb1);
		map.put("Ch1", sb2);
		map.put("Ch2", sb3);
		map.put("Ch3", sb4);
		map.put("Ch4", sb5);
		map.put("Ch5", sb6);
		map.put("Ch6", sb7);
		map.put("Ch7", sb8);
		map.put("Ch8", sb9);
		map.put("Ch9", sb10);
		map.put("Ch10", sb11);
		map.put("Ch11", sb12);
		return map;
	}

	public static String parseJson(String string){
		String result = "";
		if (TextUtils.isEmpty(string)) {
			return result;
		}
		try {
			JSONObject jsonObject = new JSONObject(string);
			result = jsonObject.getString("userName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**将点的集合改为导的集合**/
	public static List<int[]> getEcgData(List<int[]> data) {
		List<int[]> des = new ArrayList<int[]>();
		int[] d1 = new int[data.size()];
		int[] d2 = new int[data.size()];
		int[] d3 = new int[data.size()];
		int[] d4 = new int[data.size()];
		int[] d5 = new int[data.size()];
		int[] d6 = new int[data.size()];
		int[] d7 = new int[data.size()];
		int[] d8 = new int[data.size()];
		int[] d9 = new int[data.size()];
		int[] d10 = new int[data.size()];
		int[] d11 = new int[data.size()];
		int[] d12 = new int[data.size()];
		for (int j = 0; j<data.size(); j++) {
			for (int i = 0; i < 12; i++) {
				if (i==0) {
					d1[j] = data.get(j)[i];
				}
				if (i==1) {
					d2[j] = data.get(j)[i];
				}
				if (i==2) {
					d3[j] = data.get(j)[i];
				}
				if (i==3) {
					d4[j] = data.get(j)[i];
				}
				if (i==4) {
					d5[j] = data.get(j)[i];
				}
				if (i==5) {
					d6[j] = data.get(j)[i];
				}
				if (i==6) {
					d7[j] = data.get(j)[i];
				}
				if (i==7) {
					d8[j] = data.get(j)[i];
				}
				if (i==8) {
					d9[j] = data.get(j)[i];
				}
				if (i==9) {
					d10[j] = data.get(j)[i];
				}
				if (i==10) {
					d11[j] = data.get(j)[i];
				}
				if (i==11) {
					d12[j] = data.get(j)[i];
				}
			}
		}
		des.add(d1);
		des.add(d2);
		des.add(d3);
		des.add(d4);
		des.add(d5);
		des.add(d6);
		des.add(d7);
		des.add(d8);
		des.add(d9);
		des.add(d10);
		des.add(d11);
		des.add(d12);
		return des;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}


}
