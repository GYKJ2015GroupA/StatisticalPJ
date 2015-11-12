package com.gengyun.statistical.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作集合的一些工具类
 * 
 * @author qqzeng
 * 
 */
public class CommonUtils {

	public static Map<String, Record> initOptRecMap(List<Record> recList){
		// 1.确定初始目标时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(recList.get(0).getTime());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		Calendar cal = new GregorianCalendar(year, month, 1,0,0,0);    
		Date initDate = cal.getTime();  
		
		// 2.存放不同种机器的初始状态记录
		Map<String, Record> initRec = new HashMap<String, Record>();
		for(Record r : recList){
			Record rec = new Record();
			rec.setName(r.getName());
			rec.setTime(initDate);
			rec.setOpt("启动");
			initRec.put(rec.getName(), rec);
		}
		return initRec;
	}
	
	
	public static Date getTimeFromString(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			return sdf.parse(timeStr.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}
}

