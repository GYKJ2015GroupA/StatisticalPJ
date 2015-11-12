package com.gengyun.statistical.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestReg {
	/**
	 * (1)能匹配的年月日类型有：
	 *    2014年4月19日
	 *    2014年4月19号
	 *    2014-4-19
	 *    2014/4/19
	 *    2014.4.19
	 * (2)能匹配的时分秒类型有：
	 *    15:28:21
	 *    15:28
	 *    5:28 pm
	 *    15点28分21秒
	 *    15点28分
	 *    15点
	 * (3)能匹配的年月日时分秒类型有：
	 *    (1)和(2)的任意组合，二者中间可有任意多个空格
	 * 如果dateStr中有多个时间串存在，只会匹配第一个串，其他的串忽略
	 * @param text
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String matchDateString(String dateStr) {
        try {
            List<String> matches = null;
            Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
            Matcher matcher = p.matcher(dateStr);
            if (matcher.find() && matcher.groupCount() >= 1) {
                matches = new ArrayList<String>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String temp = matcher.group(i);
                    matches.add(temp);
                }
            } else {
                matches = Collections.EMPTY_LIST;
            }           
            if (matches.size() > 0) {
                return ((String) matches.get(0)).trim();
            } else {
            }
        } catch (Exception e) {
            return "";
        }
        
		return dateStr;
    }
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		//String iSaid = "亲爱的，2014年4月25 15时36分21秒， 我会在世贸天阶向你求婚！等到2015年6月25日，我们就结婚。";
//		String iSaid = "2015/05/20 05:20:08";
		String iSaid = "4545";
		
		// 匹配时间串
		String answer = matchDateString(iSaid);
		
		// 输出：
		// 问：请问我们什么时候结婚？
		// 答：2014年4月25 15时36分21秒
		//System.out.println("问：请问我们什么时候结婚？");
		//System.out.println("答：" + answer);
		
		new TestReg().test();
	}
	
	public void  test(){
		String path_1 = this.getClass().getClassLoader().getResource("").getPath();
		//System.out.println(path_1);
		String path_2 = System.getProperty("user.dir");
		//System.out.println(new File("/" + path_1).getAbsolutePath());
		System.out.println(new File(path_2 + "/resource/test2.xls").getAbsolutePath());
		
	}
}
