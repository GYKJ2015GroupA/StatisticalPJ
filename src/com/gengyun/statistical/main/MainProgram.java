package com.gengyun.statistical.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 主程序
 * 
 * @author qqzeng
 * 
 */
public class MainProgram {

	/**
	 * 配置一些全局常量
	 * 
	 */
	private static final String MACHINE_STOP = "停止";
	private static final String MACHINE_START = "启动";
	private static final String FILE_PATH = "/test2.xls";
	
	/**
	 * 存放所有的被封装记录的Record实例的集合
	 */
	private static List<Record> recordList = new ArrayList<Record>();

	/**
	 * 存放用于临时计算的记录【启动|停止】
	 */
	private static Map<String, Record> optRec = new HashMap<String, Record>();
	
	/**
	 * 存放最后得出的每月峰平谷各时间段值
	 */
	private static Map<String, double[]> timeSegments = new HashMap<String, double[]>();
	
	/***
	 * 存放每台机器从最后一次启动到下一个月的1号0时0分0秒这个区间运行的时间段
	 */
	private static Map<String, Record> tailRec = new HashMap<String, Record>();
	
	
	public static void main(String[] args) throws Exception {
		/**
		 * 	1.初始化所有机器的操作记录封装的实例对象集合
		 */
		recordList = ExcelParserUtil.readXml(FILE_PATH);
		
		/**
		 * 	2.初始化所有机器的初始启动时间的操作类型的集合
		 */
		optRec = CommonUtils.initOptRecMap(recordList);
		
		/**
		 *  3.初始化所有机器的各个统计段运行时间和的集合
		 */
		initTimeSegments();
		
		/**
		 *  4.初始化最后所有机器的最后终止时间操作类型的集合
		 */
		initFinalStopRec();
		
		/**
		 *  5.迭代【1】中得到的记录集合进行计算时间
		 */
		optTimeSegments();
		
		/**
		 *  6.结果输出
		 */
		outRes();
	}

	


	private static void outRes() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(System.getProperty("user.dir") + "/resource/res.txt")));
			for(Entry<String, double[]> entry : timeSegments.entrySet()){
				System.out.println(entry.getKey());
				System.out.println( "峰段：" + entry.getValue()[0] + "		平段：" + entry.getValue()[1] 
							+ "		谷段："+ entry.getValue()[2]);
				String machineName = entry.getKey();
				String res = "峰段：" + entry.getValue()[0] + "		平段：" + entry.getValue()[1] 
							+ "		谷段："+ entry.getValue()[2];
				bw.write(machineName);
				bw.newLine();
				bw.flush();
				bw.write(res);
				bw.newLine();
				bw.flush();
			}
		} catch (Exception e) {
			throw new RuntimeException();
			}
		
	}




	/**
	 * 初始化所有机器的各个时间段运行的集合
	 */
	private static void initTimeSegments() {
		for(Entry<String, Record> entry : optRec.entrySet()){
			timeSegments.put(entry.getKey(), new double[3]);
		}
	}



	/**
	 *  操作记录集合、操作类型集合、时间段集合
	 */
	private static void optTimeSegments() {
		for(Record r : recordList){
			//System.out.println(r);
			// 1.计算正常顺序情况下一台机器从启动到停止的时间
			if(MACHINE_STOP.equals(r.getOpt())){
				String machineName = r.getName();
				String optType = optRec.get(machineName).getOpt();
				// 计算此台机器从 启动 ->停止  所用时间
				if(MACHINE_START.equals(optType)){
					getTimeSegments(r, optRec.get(machineName), false);
					//将其置操作类型更新为停止
					optRec.put(r.getName(), r);
				}else if(MACHINE_STOP.equals(optType)){
					//如果发生了连续两次停止的情况，这里会抛出运行时异常
					//因为要以最后一次停止时间为准，所以时间段还需加上从上一次停止到相邻的下一次停止的时间段
					getTimeSegments(r, optRec.get(machineName), false);
					//将其置操作类型更新为停止
					optRec.put(r.getName(), r);
					//throw new RuntimeException();
				}
			// 2.一台机器已经至少启动又停止过一次了，即循环中的 新一个从启动  -> 停止的轮回的情况
			}else if(MACHINE_START.equals(r.getOpt())){
				// 替换掉机器操作类型的集合的历史操作，为下一次机器的  停止操作作准备
				optRec.put(r.getName(), r);
			}
		}
		// 最后还要计算 optRec 中的每台机器的开启到下一个月的1号0时0分0秒的时间段
			//此时如果程序正确，则optRec中的所有的机器的操作类型全部为   启动 
		for(Entry<String, Record> entry : optRec.entrySet()){
			getTimeSegments(entry.getValue(), tailRec.get(entry.getKey()), true);
			//System.out.println(entry.getValue() + "------------------" + tailRec.get(entry.getKey()));
		}
	}

	/**
	 * 初始化最后所有机器的最后终止时间操作类型的集合
	 * @return	
	 * 			最后终止时间操作类型的集合
	 */
	private static void initFinalStopRec() {
		for(Entry<String, Record> entry : optRec.entrySet()){
			Record rec = entry.getValue();
			//这里还要考虑闰年与平年 以及  每月的天数的问题
			Calendar c = Calendar.getInstance();
			c.setTime(rec.getTime());
			Date finalDate = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 
					1, 0, 0, 0).getTime();
			tailRec.put(entry.getKey(), new Record(entry.getKey(), MACHINE_STOP, finalDate));
		}
	//System.out.println(tailRec);
	}
	

	/**
	 * 计算两个时间段【启动->停止】之间峰平谷的所占时间
	 * @param r	
	 * @param record
	 */
	private static void getTimeSegments(Record r, Record record, boolean isFinal) {
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(r.getTime());
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(record.getTime());
		double[] timeEveryRecord = new double[3];
		double[] time_1 = new double[3];
		double[] time_2 = new double[3];
		double [] time_3 = new double[3];
		int difMonth  = 0;
		// 1.如果不为最后一天	  2015/6/5  22:53:28  -->  2015/6/6  9:33:07
		if(!isFinal) {
			difMonth = (endTime.get(Calendar.DAY_OF_MONTH)) - (startTime.get(Calendar.DAY_OF_MONTH)) - 1;
			// 1.第一部分的时间段 2015/6/5  22:53:28  --> 2015/6/6  0:0:0
			time_1 = getTimeInterval(startTime, 0);
			// 2.第二部分的时间段   2015/6/6  0:0:0  -->  2015/6/6  9:33:07
			time_2 = getTimeInterval(endTime, 1);
		}else{
			// 2.如果为最后一天
			// 2.1计算当前处理月的总天数
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(r.getTime());
			int dayNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			difMonth = dayNumber - (startTime.get(Calendar.DAY_OF_MONTH)) - 1;
			// 1.第一部分的时间段 2015/6/5  22:53:28  --> 2015/6/6  0:0:0【如果是最后一次启动，则没有第二部分时间】
			time_1 = getTimeInterval(startTime, 0);
		}
		// 3.第三部分的时间段  2015/6/6  0:0:0  -->   2015/6/6  0:0:0	【如果有此时间段，则此次运行期间至少有相隔1天】
		if(difMonth > 0){
			time_3[0] = 9.00d * difMonth;
			time_3[1] = 7.00d * difMonth;
			time_3[2] = 8.00d * difMonth;
		}
		timeEveryRecord[0] = time_1[0] + time_2[0] + time_3[0];
		timeEveryRecord[1] = time_1[1] + time_2[1] + time_3[1];
		timeEveryRecord[2] = time_1[2] + time_2[2] + time_3[2];
		//3.将所得出来的时间加入到总时间当中
		double t1 = timeSegments.get(r.getName())[0];
		double t2 = timeSegments.get(r.getName())[1];
		double t3 = timeSegments.get(r.getName())[2];
		t1 = t1 + timeEveryRecord[0];
		t2 = t2 + timeEveryRecord[1];
		t3 = t3 + timeEveryRecord[2];
		//System.out.println(r.getName() + "峰段：" + t1 + "平段：" + t2 +  "谷段：" + t3 );
		timeSegments.put(r.getName(), new double[]{t1, t2, t3});
	}

	private static double[] getTimeInterval(Calendar time, int type) {
		int currentHour = time.get(Calendar.HOUR_OF_DAY);
		int currentMin = time.get(Calendar.MINUTE);
		int currentSec = time.get(Calendar.SECOND);
		// 峰段
		double high = 0.00d;
		// 平段
		double middle = 0.00d;
		// 谷段
		double low = 0.00d;
		//当前时间秒数
		double currentTime = (double)(currentHour * 60 * 60 + currentMin * 60 + currentSec);
			if(type == 0){
				if(currentHour <= 7 && currentHour > 0){
					high = 2.00d + 7.00d;
					middle = 3.00d + 4.00d;
					low =(1.00d * 60 * 60 +  7.00d * 60 * 60 - currentTime) / (double)3600;
				}else if(currentHour <= 10 && currentHour > 7){
					high = 2.00d + 7.00d;
					middle = (4.00d * 60 * 60 + 10.00d * 60 * 60 - currentTime) / (double)3600;
					low = 1.00d;
				}else if(currentHour <= 12 && currentHour > 10){
					high =(7.00d * 60 * 60 + 12.00d * 60 * 60 - currentTime) / (double)3600;
					middle = 4.00d ;
					low = 1.00d;
				}else if(currentHour <= 16 && currentHour > 12){
					high =7.00d;
					middle = (16.00d * 60 * 60 - currentTime) / (double)3600;
					low = 1.00d;
				}else if(currentHour <= 23 && currentHour > 16){
					high =(23.00d * 60 * 60 - currentTime) / (double)3600;
					middle =0.00d;
					low = 1.00d;
				}else if(currentHour <= 24 && currentHour > 23){
					high =0.00d;
					middle =0.00d;
					low = (24.00d * 60 * 60 - currentTime) / (double)3600;
				}
			}else if(type == 1){
				if(currentHour <= 7 && currentHour > 0){
					high = 0.00d;
					middle = 0.00d;
					low =currentTime / 3600d;
				}else if(currentHour <= 10 && currentHour > 7){
					high = 0.00d;
					middle = (currentTime - 7.00d * 60 * 60) / (double)3600;
					low = 7.00d;
				}else if(currentHour <= 12 && currentHour > 10){
					high = (currentTime - 10.00d * 60 * 60) / (double)3600;
					middle = 3.00d ;
					low = 7.00d;
				}else if(currentHour <= 16 && currentHour > 12){
					high =2.00d;
					middle = (3.00d * 60 * 60+ currentTime - 12.00d * 60 * 60) / (double)3600;
					low = 7.00d;
				}else if(currentHour <= 23 && currentHour > 16){
					high =(2.00d * 60 * 60  + currentTime - 16.00d * 60 * 60) / (double)3600;
					middle =7.00d;
					low = 7.00d;
				}else if(currentHour <= 24 && currentHour > 23){
					high =2.00d + 7.00d;
					middle =7.00d;
					low = (currentTime - 23.00d * 60 * 60 + 7.00d * 3600) / (double)3600;
				}
			}
			return new double[]{high, middle, low};
	}

}
