package ling.ai.lingblekit.util;

/**
 * Created by cuiqiang on 18-1-4.
 *
 * @author cuiqiang
 */

public class ByteToStrUtil {

    /*
      * 字节数组转16进制字符串
      */
    public static String bytesToHexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexStringToBytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

    /*
    * 16进制字符串转字符串
    */
    public static String hexToString(String hex){
        String r = null;
        try {
            r = bytesToString(hexStringToBytes(hex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    /*
     * 字符串转16进制字符串
     */
    public static String stringToHexString(String s) {
        String r = bytesToHexString(string2Bytes(s));
        return r;
    }

    /*
    * 字节数组转字符串
    */
    public static String bytesToString(byte[] b) throws Exception {
        String r = new String(b,"UTF-8");
        return r;
    }

  /*
   * 字符串转字节数组
   */
    public static byte[] string2Bytes(String s){
        byte[] r = s.getBytes();
        return r;
    }

    /**
     * 把byte转化为两位十六进制数
     */
    public static String toHex(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }

    public static byte[] intToBytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    public static int bytesToInt(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) b[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
    // 十进制转化为十六进制，结果为C8。
	//  Integer.toHexString(200);

    // 十六进制转化为十进制，结果140。
    //	Integer.parseInt("8C",16);

    public static String unescapeBackslash(String escaped) {
        int backslash = escaped.indexOf('\\');
        //如果如果没有找到转义字符,直接将该字符串返回提升效率
        if (backslash < 0) {
            return escaped;
        }
        int max = escaped.length();
        StringBuilder unescaped = new StringBuilder(max - 1);
        //把转义字符串之前的字符全部加入到StringBuilder中
        unescaped.append(escaped.toCharArray(), 0, backslash);
        boolean nextIsEscaped = false;
        //转义字符的结束位置到下一个转义字符,如果有转义就加入到stringbuilder
        for (int i = backslash; i < max; i++) {
            char c = escaped.charAt(i);
            if (nextIsEscaped || c != '\\') {
                unescaped.append(c);
                //通过这个中间变量,保证遇到\就跳过,但如果遇到|的后面还是\就加入
                nextIsEscaped = false;
            } else {
                nextIsEscaped = true;
            }
        }
        return unescaped.toString();
    }
}
