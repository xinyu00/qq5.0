package com.example.effects.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringUtils {

	/** 将汉语的string转换为拼音返回 */
	public static String getPinyin(String string) {
		
		StringBuilder sb = new StringBuilder();
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			System.out.println("要转换的字符为："+c);
			
			// 不处理空白字符
			if (Character.isWhitespace(c)) {
				continue;
			}
			
			// 英文字符不做转换
			if (c < 127 && c > - 128) {
				sb.append(c);
				continue;
			}
			
			try {
				String[] hanyuPinyin = PinyinHelper.toHanyuPinyinStringArray(c,format);
//				for (String string2 : hanyuPinyin) {
//					System.out.println("转换出来的拼音为："+string2);
//				}
				sb.append(hanyuPinyin[0]);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
}
