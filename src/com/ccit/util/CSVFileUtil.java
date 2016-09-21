/**
 * 
 */
package com.ccit.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CSDN:hantiannan
 * @update Huaishao Luo
 * @create 2016年9月15日上午10:08:07
 */
public class CSVFileUtil {
	// CSV文件编码
	public static final String ENCODE = "UTF-8";

	private BufferedReader br = null;

	public CSVFileUtil(BufferedReader br) throws Exception {
		this.br = br;
	}

	/**
	 * 从CSV文件流中读取一个CSV行。
	 */
	public String readLine() throws Exception {
		StringBuffer readLine = new StringBuffer();
		boolean bReadNext = true;
		while (bReadNext) {
			if (readLine.length() > 0) {
				readLine.append("\r\n");
			}
			String strReadLine = br.readLine();
			// readLine is Null
			if (strReadLine == null) {
				return null;
			}
			readLine.append(strReadLine);
			// 如果双引号是奇数的时候继续读取。考虑有换行的是情况。
			if (countChar(readLine.toString(), '"', 0) % 2 == 1) {
				bReadNext = true;
			} else {
				bReadNext = false;
			}
		}
		return readLine.toString();
	}

	/**
	 * 把CSV文件的一行转换成字符串数组。指定数组长度，不够长度的部分设置为null。
	 */
	public String[] fromCSVLine(String source, int size) {
		List<String> tmpArray = fromCSVLinetoArray(source);
		if (size < tmpArray.size()) {
			size = tmpArray.size();
		}
		String[] rtnArray = new String[size];
		tmpArray.toArray(rtnArray);
		return rtnArray;
	}

	/**
	 * 把CSV文件的一行转换成字符串数组。不指定数组长度。
	 */
	public List<String> fromCSVLinetoArray(String source) {
		if (source == null || source.length() == 0) {
			return new ArrayList<String>();
		}
		int currentPosition = 0;
		int maxPosition = source.length();
		int nextComma = 0;
		ArrayList<String> rtnArray = new ArrayList<String>();
		while (currentPosition < maxPosition) {
			nextComma = nextComma(source, currentPosition);
			rtnArray.add(nextToken(source, currentPosition, nextComma));
			currentPosition = nextComma + 1;
			if (currentPosition == maxPosition) {
				rtnArray.add("");
			}
		}
		return rtnArray;
	}

	/**
	 * 把字符串类型的数组转换成一个CSV行。（输出CSV文件的时候用）
	 */
	public static String toCSVLine(String[] strArray) {
		if (strArray == null) {
			return "";
		}
		StringBuffer cvsLine = new StringBuffer();
		for (int idx = 0; idx < strArray.length; idx++) {
			String item = addQuote(strArray[idx]);
			cvsLine.append(item);
			if (strArray.length - 1 != idx) {
				cvsLine.append(',');
			}
		}
		return cvsLine.toString();
	}

	/**
	 * 字符串类型的List转换成一个CSV行。（输出CSV文件的时候用）
	 */
	public String toCSVLine(ArrayList<String> strArrList) {
		if (strArrList == null) {
			return "";
		}
		String[] strArray = new String[strArrList.size()];
		for (int idx = 0; idx < strArrList.size(); idx++) {
			strArray[idx] = (String) strArrList.get(idx);
		}
		return toCSVLine(strArray);
	}

	// ==========以下是内部使用的方法=============================
	/**
	 * 计算指定文字的个数。
	 */
	private int countChar(String str, char c, int start) {
		int i = 0;
		int index = str.indexOf(c, start);
		return index == -1 ? i : countChar(str, c, index + 1) + 1;
	}

	/**
	 * 查询下一个逗号的位置。
	 */
	private static int nextComma(String source, int st) {
		int maxPosition = source.length();
		boolean inquote = false;
		while (st < maxPosition) {
			char ch = source.charAt(st);
			if (!inquote && ch == ',') {
				break;
			} else if ('"' == ch) {
				inquote = !inquote;
			}
			st++;
		}
		return st;
	}

	/**
	 * 取得下一个字符串
	 */
	private static String nextToken(String source, int st, int nextComma) {
		StringBuffer strb = new StringBuffer();
		int next = st;
		while (next < nextComma) {
			char ch = source.charAt(next++);
			if (ch == '"') {
				if ((st + 1 < next && next < nextComma)
						&& (source.charAt(next) == '"')) {
					strb.append(ch);
					next++;
				}
			} else {
				strb.append(ch);
			}
		}
		return strb.toString();
	}

	/**
	 * 在字符串的外侧加双引号。如果该字符串的内部有双引号的话，把"转换成""。
	 *
	 * @param item
	 *            字符串
	 * @return 处理过的字符串
	 */
	private static String addQuote(String item) {
		if (item == null || item.length() == 0) {
			return "\"\"";
		}
		StringBuffer sb = new StringBuffer();
		sb.append('"');
		for (int idx = 0; idx < item.length(); idx++) {
			char ch = item.charAt(idx);
			if ('"' == ch) {
				sb.append("\"\"");
			} else {
				sb.append(ch);
			}
		}
		sb.append('"');
		return sb.toString();
	}
}
