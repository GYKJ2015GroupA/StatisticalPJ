package com.gengyun.statistical.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.gengyun.statistical.main.CommonUtils;
import com.gengyun.statistical.main.ExcelParserUtil;
import com.gengyun.statistical.main.Record;

public class TestUitls {

	@Test
	public void testExcelPareserUtil() throws Exception {
		ExcelParserUtil.readXml("/test.xlsx");
	}

	@Test
	public void testStringUtil() throws Exception {
		System.out.println(CommonUtils.getTimeFromString("2015/06/30 08:22:10")
				.toLocaleString());
	}

	@Test
	public void testString() throws Exception {
		String value = "2015/06/30 08:22:10";
		System.out.println(value.substring(value.lastIndexOf(" ") + 1));
	}

	@Test
	public void testExcelPareserUtil_2() throws Exception {
		List<Record> recordList = ExcelParserUtil.readXml("/test.xlsx");
		System.out.println(recordList);
	}

	@Test
	public void testCollectionUtils() throws Exception {
		List<Record> recordList = ExcelParserUtil.readXml("/test.xlsx");
		System.out.println(CommonUtils.initOptRecMap(recordList));
	}

	@Test
	public void testTime() throws Exception {
		Calendar calendar = new GregorianCalendar(2007, 11, 25, 0, 0, 0);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		System.out.println("2007 Christmas is:" + sdf.format(date));
	}

	@Test
	public void testTime_2() throws Exception {
		// Calendar calendar = Calendar.getInstance();
		Calendar calendar = new GregorianCalendar(2007, 3, 25, 0, 0, 0);
		int dayNumber_2 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		System.out.println(dayNumber_2);
	}

	@Test
	public void testTime_3() throws Exception {
		// Calendar calendar = Calendar.getInstance();
		Calendar c1 = new GregorianCalendar(2007, 3, 25, 20, 05, 25);
		Calendar c2 = new GregorianCalendar(2007, 3, 25, 0, 0, 0);
		Calendar c = Calendar.getInstance();
		System.out.println(c1.get(Calendar.MINUTE));
		System.out.println(c1.get(Calendar.SECOND));
		System.out.println(c1.get(Calendar.HOUR_OF_DAY));
	}

	@Test
	public void testArray() throws Exception {
		double[] ds = new double[3];
		for (double d : ds) {
			System.out.println(d);
		}
	}

	@Test
	public void testArrayList() throws Exception {
		List<Integer> temp = new ArrayList<Integer>();
		temp.add(1);
		temp.add(11);
		temp.add(15);
		temp.add(8);
		temp.add(5);
		temp.add(3);
		Collections.reverse(temp);
		System.out.println(temp);
	}

	@Test
	public void testMap() throws Exception {
		Map<String, String> a = new HashMap<String, String>();
		a.put("1", "a");
		a.put("2", "b");
		a.put("3", "c");
		Map<String, String> b = new HashMap<String, String>();
		for (Entry<String, String> entry : a.entrySet()) {
			b.put(entry.getKey(), entry.getValue());
		}
		b.put("1", "aaaaa");
		b.put("3", "cccccc");
		System.out.println(a);
		System.out.println(b);
	}

	@Test
	public void testReg() throws Exception {
		Pattern p = Pattern
				.compile(
						"(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = p.matcher("2015/8/25 05:05:09");
		System.out.println(matcher.find());
	}

	@Test
	public void testFile() throws Exception {
		String dir =System.getProperty("user.dir");
	//	System.out.println(System.getProperty("user.dir"));
		//this.getClass().getClassLoader().getResource("/").getPath();System.out.println(Class.class.getClassLoader().getResourceAsStream(dir + "resource\test2.xls"));
		//System.out.println(Class.class.getClassLoader().getResourceAsStream("../resource/com/gengyun/statistical/test/test2.xls"));
		
		System.out.println(new File(dir + "/resource/test2.xls").getAbsolutePath());
		System.out.println(new File(dir + "/resource/test2.xls"));
	}

}
