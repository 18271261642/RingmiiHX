package hat.bemo.measure.set;

import android.util.Log;

import java.util.Calendar;

import hat.bemo.measure.service.NonInvasiveBloodGlucoseService;

public final class Command {

	/**
	 * 所有設備名稱
	 */
	public static final class DEVICENAME {
//		FORA血壓
		public static final String TaidocDevice = "Taidoc-Device";
		public static final String Taidoctd3128 = "TAIDOC TD3128";
		public static final String FORAP60 = "FORA P60";
		public static final String FORAD40 = "FORA D40";
//		MICROLIFE血壓
		public static final String ML103D = "ML-103D";
//		倍泰血壓計
		public static final String BP = "BP";
//		FORA血糖
		public static final String TaidocTD4279 = "TAIDOC TD4279";
//		無創血糖
		public static final String ITONDM = "ITON DM";
//		超思血氧4.0
		public static final String ichoice = "ichoice";		
//		怡成血糖
		public static final String BJYC = "BJYC-5D-8B B4"; 	
	}
	/**
	 * 歐瑟若 - 藍芽智慧體脂計 Command
	 * 
	 * @author house
	 * 
	 */
	public static final class OSERIO {
		public static final byte DIGIT0 = (byte) 0xEB;
		public static final byte DIGIT1 = (byte) 0x90;
		public static final byte DIGIT2 = (byte) 0x02;

		public static final byte[] HAND_SHAKE = new byte[] { DIGIT0, DIGIT1,
				DIGIT2, (byte) 0x30, (byte) 0x03 };

		public static final byte[] USER_INFO = new byte[] { DIGIT0, DIGIT1,
				DIGIT2, (byte) 0x33, (byte) 0x01, (byte) 0x03 };

		public static final byte[] RECEIVE_OK = new byte[] { DIGIT0, DIGIT1,
				DIGIT2, (byte) 0x50, (byte) 0x06, (byte) 0x03 };
		public static final byte[] RECEIVE_ERROR = new byte[] { DIGIT0, DIGIT1,
				DIGIT2, (byte) 0x50, (byte) 0x15, (byte) 0x03 };

		public static boolean receiveHandShake(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x40 && command.length >= 18
						&& command[17] == (byte) 0x03)
					return true;
			}
			return false;
		}
		
		public static boolean receiveTime(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x50 && (command[4] == (byte) 0x06 || command[4] == (byte) 0x15) && command[5] == (byte) 0x03)
					return true;
			}
			return false;
		}

		public static boolean receiveDeviceOFF(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x50 && command[4] == (byte) 0x1b)
					return true;
			}
			return false;

		}

		public static boolean receiveCheckUser(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x44 && command[4] == (byte) 0x0a)
					return true;
			}
			return false;

		}

		public static boolean receiveMeasureNoHistory(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x50 && command[4] == (byte) 0x18)
					return true;
			}
			return false;

		}

		public static boolean receiveMeasureHaveHistory(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x42)
					return true;
			}
			return false;

		}

		public static boolean receiveMeasureInfo(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1
					&& command[2] == DIGIT2) {
				if (command[3] == (byte) 0x41 && command[4] == (byte) 0x0a)
					return true;
			}
			return false;

		}
		
		public static byte[] syncUserCmd(int Year, int Month, int Day, int Hight, int Ideal_Weight, int Sex) {

			byte[] _year = ByteUtils.intToHexToByte_2digit(Year);
			byte _month = ByteUtils.intToHexToByte(Month);
			byte _day = ByteUtils.intToHexToByte(Day);
			byte[] _hight = ByteUtils.intToHexToByte_2digit(Hight);
			byte[] _weight = ByteUtils.intToHexToByte_2digit(Ideal_Weight);
			byte _sex = ByteUtils.intToHexToByte(Sex);

			byte[] sum = ByteUtils.intToHexToByte_2digit(
					ByteUtils.getSum((byte)0x01, _year[1], _year[0], _month, _day, _hight[1], _hight[0], _weight[1], _weight[0], _sex));

			return new byte[] { DIGIT0, DIGIT1, DIGIT2, (byte) 0x32, (byte) 0x0A, 
					(byte)0x01 , _year[1], _year[0], _month, _day, 
					_weight[1], _weight[0], _hight[1], _hight[0], _sex,
					sum[1], sum[0], (byte) 0x03 };

		}

		public static byte[] sendTimeCmd() {
			Calendar cal = Calendar.getInstance();

			byte[] yearByte = ByteUtils.intToHexToByte_2digit(cal.get(Calendar.YEAR));
			byte MM = ByteUtils.intToHexToByte(cal.get(Calendar.MONTH) + 1);
			byte dd = ByteUtils.intToHexToByte(cal.get(Calendar.DATE));
			byte hh = ByteUtils.intToHexToByte(cal.get(Calendar.HOUR));
			byte mm = ByteUtils.intToHexToByte(cal.get(Calendar.MINUTE));

			byte[] checkSum = ByteUtils.intToHexToByte_2digit(ByteUtils.getSum(yearByte[1], yearByte[0], MM, dd, hh, mm));

			return new byte[] { DIGIT0, DIGIT1, DIGIT2, (byte) 0x31,
					(byte) 0x06, yearByte[1], yearByte[0], MM, dd, hh, mm,
					checkSum[1], checkSum[0], (byte) 0x03 };
		}
	}

	/**
	 * 超思 - 指尖血氧機
	 */
	public static final class BCOXM {
		public static final byte DIGIT0 = (byte) 0x55;
		public static final byte DIGIT1 = (byte) 0xaa;
		/**
		 * HAND SHAKE
		 */
		public static final byte[] HAND_SHAKE_CMD = new byte[] { DIGIT0,
				DIGIT1, (byte) 0xfd, (byte) 0x00, (byte) 0x00, (byte) 0xfd };
		/**
		 * 設定傳輸模式
		 */
		public static final byte[] SET_TYPE_CMD = new byte[] { DIGIT0,
				DIGIT1, (byte) 0xee, (byte) 0x00, (byte) 0x00, (byte) 0xef };
		/**
		 * 刪除裝置MAC紀錄
		 */
		public static final byte[] DELETE_MAC_CMD = new byte[] { DIGIT0,
				DIGIT1, (byte) 0xf4, (byte) 0x60, (byte) 0x00, (byte) 0x54 };
		/**
		 * 發送當下測量數據指令
		 */
		public static final byte[] MEASURE_RESULT_CMD = new byte[] { DIGIT0,
				DIGIT1, (byte) 0xfa, (byte) 0xff, (byte) 0xff, (byte) 0xf8 };
		
		/**
		 * 繼續量測命令
		 */
		public static final byte[] MEASURE_KEEPALIVE_CMD = new byte[] { DIGIT0,
				DIGIT1, (byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0xf0 };

		public static boolean receiveHandShake(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == (byte) 0x00) {
				
					return true;
			}
			return false;
		}
		
		public static boolean receiveMeasure(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1 && command[2] == (byte)0xf6) {
				
					return true;
			}
			return false;
		}
		
		public static boolean receiveSetTypeOK(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1) {
				if (command[6] == (byte) 0x1e && command[3] == (byte) 0xff
						&& command[4] == (byte) 0xf8)
					return true;
			}
			return false;
		}
		
		
		
		public static boolean receiveDeleteOK(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == DIGIT1) {
				if (command[6] == (byte) 0x1e && command[3] == (byte) 0xff
						&& command[4] == (byte) 0xfa)
					return true;
			}
			return false;
		}
	}

	/**
	 * 福爾益 - 血糖血壓測試系統
	 */
	public static final class FORA {
		
		public static final byte DIGIT0 = (byte) 0x51;
		
		/**
		 * 取得最後一次量測記錄
		 */
		public static final byte[] MEASURE_RESULT_CMD = new byte[] { DIGIT0,
			(byte) 0x26, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xa3, (byte) 0x1a};
		
		/**
		 * 關閉裝置
		 */
		public static final byte[] TURN_OFF_CMD = new byte[] { DIGIT0,
			(byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xa3, (byte) 0x44};
		
		public static boolean receiveMeasure(byte[] command) {
			if (command[0] == DIGIT0 && command[1] == 0x26) {
					return true;
			}
			return false;
		}
	}

	/**
	 * 微生活 - 血壓測試系統
	 */
	public static final class MICROLIFE {
//		0x4d 0xff 0x02 0x00 0x12 0x16 0x18 0x28  4d ff 2 0 12 16 18 28
		public static final byte DIGIT0 = (byte) 0x4d;
		
		public static final byte DIGIT1 = (byte) 0xff;
		public static final byte DIGIT2 = (byte) 0x2;
		public static final byte DIGIT3 = (byte) 0x0;
		
		public static final byte DIGIT4 = (byte) 0x12;
		public static final byte DIGIT5 = (byte) 0x16;
		public static final byte DIGIT6 = (byte) 0x18;
		public static final byte DIGIT7 = (byte) 0x26;
		
		public static final byte DIGIT8 = (byte) 0x2e;
		
		public static final byte CMD_HBP16_CHK_Afib[] = {(byte)0x12,(byte)0x16, 0x18,(byte)0xf0,0x00,0x00,0x00};
		public static final byte CMD_HBP16_BP_READ[] = {(byte)0x12,(byte)0x16, 0x18, 0x28};
		/**
		 * 取得最後一次量測記錄
		 */
		public static final byte[] MEASURE_RESULT_CMD = new byte[] { DIGIT0, DIGIT1, DIGIT2, DIGIT3, DIGIT4,
																	 DIGIT5, DIGIT6, DIGIT7, DIGIT8};		
	}
	
	/**
	 * 倍泰 - 血壓測試系統
	 * BCC效驗
	 */
	public static final class BP {
		public static final byte DIGIT0 = (byte) 0xAA;	
		public static final byte DIGIT1 = (byte) 0x05;		
		//bcc check
		public static final byte DIGIT4 = (byte) 0xBB;
		public static final byte DIGIT6 = (byte) 0xFF;
		//cmd
		public static final byte DIGIT2 = (byte) 0x11;
		public static final byte DIGIT5 = (byte) 0x55;
		/**
		 * 取得最後一次量測記錄
		 */
		public static final byte[] MEASURE_RESULT_CMD = new byte[] { DIGIT0, DIGIT1, DIGIT2, DIGIT1, DIGIT4};		
		/**
		 * 關閉裝置
		 */
		public static final byte[] TURN_OFF_CMD = new byte[] {DIGIT0, DIGIT1, DIGIT5, DIGIT1, DIGIT6};
	}
	
	/**
	 * 無創血糖測試系統
	 */
	public static final class ITONDM {
		public static Commands c;
		public interface Commands{
			public void getGLUS(String command, String value);
			public void getXYBH(String command, String value);
			public void getXHDB(String command, String value);
			public void getXLSD(String command, String value);
			public void getHJWD(String value);
			public void getHJSD(String value);
			public void getTBWD(String value);
			public void getTBSD(String value);
			public void getXTMB(String command, String value);
			public void getZJJG(String value);
			public void getSZER(String value);
			public void getHJER(String value);
			public void getJSOK();
			public void getFRSZ();
			public void getERRO();
			public void getSTOP();
			public void getSJJS();
			public void getBCKS();
			public void getBCJS();
			public void getERTT();
			public void getERAD();		
			public void getERBW();
			public void getERWD();
			public void getZJWC();
			public void getWCWC();
			public void getINOK();	
			public void getCWHM();			
		}
		public void setCommandListeners(Commands ongpslisteners) {
			c = ongpslisteners;		
		}
		
		public static String DataAnalysis(String command, String value){
			Log.e("commands", "command:"+command);	
			Type_Enum type = Type_Enum.ByStr(command);			 
			switch(type){		 																		
			case GLUS:
				c.getGLUS(command, String.valueOf((Division(Double.parseDouble(value)))));
				return String.valueOf((Division(Double.parseDouble(value))));				
			case XYBH:
				c.getXYBH(command, pad(Integer.valueOf(value)));
				return pad(Integer.valueOf(value));
			case XHDB:
				c.getXHDB(command, String.valueOf((Division(Double.parseDouble(value)))));
				return String.valueOf((Division(Double.parseDouble(value))));		
			case XLSD:
				c.getXLSD(command, pad(Integer.valueOf(value)));
				return pad(Integer.valueOf(value));
			case HJWD:
				return String.valueOf((Division(Double.parseDouble(value))));
			case HJSD:
				return String.valueOf((Division(Double.parseDouble(value))));
			case TBWD:
				return String.valueOf((Division(Double.parseDouble(value))));
			case TBSD:
				return String.valueOf((Division(Double.parseDouble(value))));
			case XTMB:
				c.getXTMB(command, pad(Integer.valueOf(value)));
				return pad(Integer.valueOf(value));
			case ZJJG:
				return pad(Integer.valueOf(value));
			case SZER:
				c.getSZER(value);
				return pad(Integer.valueOf(value));
			case HJER:
				c.getHJER(pad(Integer.valueOf(value)));
				return pad(Integer.valueOf(value));				
			case JSOK:
				NonInvasiveBloodGlucoseService.responsesLog("JSOK", "數據接收OK");
				return "";
			case FRSZ:
				c.getFRSZ();
				NonInvasiveBloodGlucoseService.responsesLog("FRSZ", "已放入手指");
				return "";
			case ERRO:
				c.getERRO();
				NonInvasiveBloodGlucoseService.responsesLog("ERRO", "DSP中出错提示，手指未放入提示错误");
				return "";
			case STOP:
				NonInvasiveBloodGlucoseService.responsesLog("STOP", "測試完成");
				return "";
			case SJJS:
				NonInvasiveBloodGlucoseService.responsesLog("SJJS", "數據計算，開始處理數據");
				return "";
			case BCKS:
				NonInvasiveBloodGlucoseService.responsesLog("BCKS", "保存數據開始");
				return "";
			case BCJS:
				NonInvasiveBloodGlucoseService.responsesLog("BCJS", "保存數據結束");
				return "";
			case ERTT:
				NonInvasiveBloodGlucoseService.responsesLog("ERTT", "探頭未插入貨接觸不良");
				return "";
			case ERAD:
				c.getERAD();
				NonInvasiveBloodGlucoseService.responsesLog("ERAD", "採集數據有問題，請檢查接口");
				return "";
			case ERBW:
				c.getERBW();
				NonInvasiveBloodGlucoseService.responsesLog("ERBW", "採集數據有問題，請檢查接口");
				return "";
			case ERWD:
				NonInvasiveBloodGlucoseService.responsesLog("ERWD", "感應器之間溫度較大可以計算提示'檢查感應器特性'");
				return "";
			case ZJWC:
				NonInvasiveBloodGlucoseService.responsesLog("ZJWC", "自我檢測完成");
				return "";
			case WCWC:
				NonInvasiveBloodGlucoseService.responsesLog("ZJWC", "誤差值校正完成");
				return "";
			case INOK:
				NonInvasiveBloodGlucoseService.responsesLog("ZJWC", "電路出使化完成");
				return "";	
			case CWHM:
				c.getCWHM();
				return "";	
			case WAD1:
				NonInvasiveBloodGlucoseService.responsesLog("WAD1", "環境温度:"+String.valueOf((Division(Double.parseDouble(value)))));
				return "";
			case WAD2:
				NonInvasiveBloodGlucoseService.responsesLog("WAD2", "遠端温度:"+String.valueOf((Division(Double.parseDouble(value)))));
				return "";
			case WAD3:
				NonInvasiveBloodGlucoseService.responsesLog("WAD3", "體表温度:"+String.valueOf((Division(Double.parseDouble(value)))));
				return "";
			case WAD4:
				NonInvasiveBloodGlucoseService.responsesLog("WAD4", "近端温度:"+String.valueOf((Division(Double.parseDouble(value)))));
				return "";
			case WAD5:
				NonInvasiveBloodGlucoseService.responsesLog("WAD5", "環境湿度:"+String.valueOf((Division(Double.parseDouble(value)))));
				return "";
			default:
				return "error";
			}
		}
		
		public enum Type_Enum {
			GLUS("GLUS"), 
			XYBH("XYBH"),
			XHDB("XHDB"), 
			XLSD("XLSD"), 
			HJWD("HJWD"), 
			HJSD("HJSD"), 
			TBWD("TBWD"), 
			TBSD("TBSD"), 
			XTMB("XTMB"), 
			JSOK("JSOK"), 
			FRSZ("FRSZ"),
			HJER("HJER"), 			
			SZER("SZER"), 
			ERRO("ERRO"), 			
			STOP("STOP"), 
			SJJS("SJJS"), 
			BCKS("BCKS"), 
			BCJS("BCJS"), 
			ERTT("ERTT"),
			ERAD("ERAD"), 			
			ERBW("ERBW"), 
			ERWD("ERWD"), 			
			ZJWC("ZJWC"), 			
			WCWC("WCWC"), 
			INOK("INOK"), 			
			ZJJG("ZJJG"), 
			CWHM("CWHM"), 	
			WAD1("WAD1"),
			WAD2("WAD2"),
			WAD3("WAD3"),
			WAD4("WAD4"),
			WAD5("WAD5"),
			type_err("");
		 
			private String type;

			Type_Enum(String str) {
				this.type = str;
			}

			public static Type_Enum ByStr(final String val) {
				for (Type_Enum type : Type_Enum.values()) {
					if (type.type.equals(val)) {
						return type;
					}
				}
				return type_err; // Never return null
			}
		}
		
		private static String pad(int c){
				return String.valueOf(c);
		}
		
		private static String Division(double d){
			return String.valueOf(d / 10);
		}
	}
	
//	private static void CalculateCheckSum( byte[] bytes){
//	     short CheckSum = 0, i = 0;
//	     for( i = 0; i < bytes.length; i++ ){
//	        CheckSum = (short) ((short)CheckSum + (short)bytes[i]);
//	     }
//	     System.out.println(Integer.toHexString(CheckSum));  
//	}
	
//	public static String getBCC(byte[] data) {
//		  String ret = "";
//		  byte BCC[]= new byte[1];
//		  for(int i=0;i<data.length;i++)
//		  {
//		  BCC[0]=(byte) (BCC[0] ^ data[i]);
//		  }
//		  String hex = Integer.toHexString(BCC[0] & 0xFF);
//		  if (hex.length() == 1) {
//		  hex = '0' + hex;
//		  }
//		  ret += hex.toUpperCase();
//		  System.out.println(ret);  
//		  return ret;
//	}
	
//	private static byte[] getHexBytes(String message) {
//        int len = message.length() / 2;//AA5504B10000B5
//        char[] chars = message.toCharArray();
//        String[] hexStr = new String[len];  
//        byte[] bytes = new byte[len];
//        for (int i = 0, j = 0; j < len; i += 2, j++) {
//            hexStr[j] = "" + chars[i] + chars[i + 1];
//            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
//        }
//        for(byte b : bytes){
//        	System.out.println(b);
//        }     
//        return bytes;
//    }
}