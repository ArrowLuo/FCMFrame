/**
 * 
 */
package com.ccit.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Huaishao Luo
 * @create 2016年8月18日下午3:54:13
 */
public class CommUtils {
	
	/** Object转换为Int类型方法 出错返回0 */
	public static int null2Int(Object s) {
		int v = 0;
		if (s != null)
			try {
				v = Integer.parseInt(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	/** Object转换为Float类型方法 出错返回0.0F */
	public static float null2Float(Object s) {
		float v = 0.0F;
		if (s != null)
			try {
				v = Float.parseFloat(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	/** Object转换为Double类型方法 出错返回0.0D */
	public static double null2Double(Object s) {
		double v = 0.0D;
		if (s != null)
			try {
				v = Double.parseDouble(null2String(s));
			} catch (Exception localException) {
			}
		return v;
	}

	/** Object转换为Boolean类型方法 出错返回false */
	public static boolean null2Boolean(Object s) {
		boolean v = false;
		if (s != null)
			try {
				v = Boolean.parseBoolean(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	/** Object转换为String类型方法 出错返回"" */
	public static String null2String(Object s) {
		return s == null ? "" : s.toString().trim();
	}

	/** Object转换为Long类型方法 出错返回-1 */
	public static Long null2Long(Object s) {
		Long v = Long.valueOf(-1L);
		if (s != null)
			try {
				v = Long.valueOf(Long.parseLong(s.toString()));
			} catch (Exception localException) {
			}
		return v;
	}
	
	/** md5加密算法 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/** md5加密算法 */
    public static String md5(String text) {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        String md5Str = new String(encodeHex(bytes));
        return md5Str.toLowerCase();
    }
    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;
        for (int j = 0; i < l; ++i) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }
        return out;
    }
    
    /** 产生随机字符*/
	public static char randomChar() {
		char[] chars = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f',
				'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l',
				'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r',
				'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x',
				'X', 'y', 'Y', 'z', 'Z' };
		int index = (int) (Math.random() * 52.0D) - 1;
		if (index < 0) {
			index = 0;
		}
		return chars[index];
	}
	
	/** 
	 * 产生随机数
	 */
	public static int randomBetweenInt(int begin, int end) {
		int rInt = begin;
		Random random = new Random();
		while (begin <= end) {
			rInt = random.nextInt(end + 1);
			if (rInt >= begin && rInt <= end) {
				return rInt;
			}
		}
		return 0;
	}

	/** 
	 * 分割字符串
	 */
	public static String[] splitByChar(String s, String c) {
		String[] list = s.split(c);
		return list;
	}
	
	/** 产生一定长度的随机数 */
	public static final String randomString(int length) {
		char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/** 产生一定长度的数字字符串*/
	public static final String randomInt(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] numbersAndLetters = "0123456789".toCharArray();

		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}

	/**
	 * 计算两个时间相差天数
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getDateDistance(String time1, String time2) {
		long quot = 0L;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000L / 60L / 60L / 24L;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}
	
	/**保留两位小数*/
	public static double formatMoney(Object str) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(str)).doubleValue();
	}
	
	
	/** URLDecoder UTF-8解码*/
	public static String decode(String s) {
		String ret = s;
		try {
			ret = URLDecoder.decode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	/** URLDecoder UTF-8编码*/
	public static String encode(String s) {
		String ret = s;
		try {
			ret = URLEncoder.encode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	/** 转化字符编码为coding*/
	public static String convert(String str, String coding) {
		String newStr = "";
		if (str != null)
			try {
				newStr = new String(str.getBytes("ISO-8859-1"), coding);
			} catch (Exception e) {
				return newStr;
			}
		return newStr;
	}
	
}
