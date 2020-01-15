package hat.bemo.measure.set;


public class ByteUtils {

	public static byte integerToHexByte(int i) {
		byte[] data = hexStringToByteArray(Integer.toHexString(i));
		return data[0];
	}
	
	public static byte[] integerToHexByteArray(int i) {
		return hexStringToByteArray(Integer.toHexString(i));
	}

	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    if(len%2 != 0){
	    	s = "0"+s ;
	    	len ++ ;
	    }
	    
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String byteArrayToHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;

		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars);
	}

	public static int hexByteToInteger(byte[] bytes) {
		return Integer.parseInt(byteArrayToHexString(bytes), 16);
	}
	
	public static int getSum(byte... bytes){
		int sum = 0;
		for(byte b : bytes){
			sum += Integer.parseInt(ByteUtils.byteToHex(b), 16);
		}
		return sum;
	}
	
	public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data >>> 4) & 0x0F));
        buf.append(toHexChar(data & 0x0F));
        return buf.toString();
    }
	
	/**
     *  Convenience method to convert an int to a hex char.
     *
     * @param  i  the int to convert
     * @return char the converted char
     */
    public static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i );
        } else {
            return (char) ('a' + (i - 10));
        }
    }
	
	public static float byteConvertToFloat(byte digit_h,byte digit_l){
		String str_l =Integer.toHexString(digit_l & 0xFF);
		String str_h =Integer.toHexString(digit_h & 0xFF);
		int  result = Integer.parseInt(str_h+str_l, 16) ;
		return (float)result;
	}
	
	public static int byteConvertToInt(byte digit_h,byte digit_l){
		String str_l =Integer.toHexString(digit_l & 0xFF);
		String str_h =Integer.toHexString(digit_h & 0xFF);
		int  result = Integer.parseInt(str_h+str_l, 16) ;
		return result;
	}
	
	
	
	public static int byteConvertToInt(byte digit){
		String str =Integer.toHexString(digit & 0xFF);
		int  result = Integer.parseInt(str, 16) ;
		return result;
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static byte intToHexToByte(int value){
		System.out.println(Integer.toHexString(value));
		return hexStrToByte(Integer.toHexString(value));
	}
	
	public static byte intToHexToByte_1digit(int value){
		String hexValue = Integer.toHexString(value);
		
		if(hexValue.length()>2){
			hexValue = hexValue.substring(hexValue.length()-2);
		}
		return hexStrToByte(hexValue);
	}
	
	public static byte[] intToHexToByte_2digit(int value){
		String hexValue = Integer.toHexString(value);
		String hexValueH = "";
		String hexValueL = "";
		
		if(hexValue.length()>4){
			hexValue = hexValue.substring(hexValue.length()-4);
		}
		
		switch(hexValue.length()){
		case 4:
			hexValueH = hexValue.substring(0, 2);
			hexValueL = hexValue.substring(2);
			break;
		case 3:
			hexValueH = "0"+hexValue.substring(0, 1);
			hexValueL = hexValue.substring(1);
			break;
		case 2:
			hexValueH = "00";
			hexValueL = hexValue;
			break;
		case 1:
			hexValueH = "00";
			hexValueL = "0"+hexValue;
			break;
		}
		
		byte yH = hexStrToByte(hexValueH);
		byte yL = hexStrToByte(hexValueL);
		return new byte[]{yH, yL};
	}
	
	/**
	 * 51 → 81 </br>
	 * 2B → 43 </br>
	 * @param str	hex string
	 * */
	public static byte hexStrToByte(String str){
		if(str.length() == 1){
			return (byte)Character.digit(str.charAt(0), 16);
		}else{
			return (byte)( (Character.digit(str.charAt(str.length()-2), 16)<<4)
					+ Character.digit(str.charAt(str.length()-1), 16) );
		}
	}
}