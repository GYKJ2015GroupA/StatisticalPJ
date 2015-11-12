package com.gengyun.statistical.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Excel文件解析器类，并将每一条表格记录转化为相应的实例对象，最终存储在List集合中
 * @author qqzeng
 *
 */
public class ExcelParserUtil {

	/**
	 * 用来存储封装每一条记录的Bean对象的List集合
	 */
	private static List<Record> recordList = new ArrayList<Record>();

	public static List<Record> readXml(String fileName) throws Exception {
		// 判断是否是excel2007格式
		boolean isE2007 = false;
		if (fileName.endsWith("xlsx"))
			isE2007 = true;
		try {
			//获取Src下的文件路径
			//InputStream input = ReadExcel.class.getResourceAsStream(fileName);
			// 获取工程目录下的Resource的路径
			File file = new File(System.getProperty("user.dir") + "/resource/" + fileName);
			InputStream input = new FileInputStream(file);
			Workbook wb = null;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				// System.out.println("Row #" + row.getRowNum()); // 获得行号从0开始
				int rowNumber = row.getRowNum();
				//从第三行开始封装记录
				
				if(rowNumber >=  2){
					Record r  = new Record();
					//System.out.println();
					Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
					while (cells.hasNext()) {
						Cell cell = cells.next();
						//System.out.println("Cell #" + cell.getColumnIndex());
						//System.out.print(readCellValues(cell) + " 		 ");
						String value = readCellValues(cell);
						if(cell.getColumnIndex() == 0){
							// 设置机器名
							r.setName(value);
						}else if(cell.getColumnIndex() == 1){
							// 设置机器操作类型
							if(value != null && value.contains(" ")){
								String opt = value.substring(value.lastIndexOf(" ") + 1);
								r.setOpt(opt);
							}
						}else if(cell.getColumnIndex() == 2){
							// 设置操作的时间
							if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
						        	 r.setTime(CommonUtils.getTimeFromString(value));
							}
						}
					}
					recordList.add(r);
				}
			}
			Collections.sort(recordList);
			return recordList;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
	}

	private static String readCellValues(Cell cell) throws Exception {
		// 用于返回结果
		String result = new String();
		try {
			// 如果单元格为空，返回null
			if (cell == null) {
				result = "null";
			} else {
				// 判断单元格类型
				switch (cell.getCellType()) {
				// 1. 数字类型
				case HSSFCell.CELL_TYPE_NUMERIC:
					// 1.1 处理日期格式、时间格式
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat sdf = null;
						// System.out.println(cell.getCellStyle().getDataFormat());
						// 1.1.1 时间格式①
						if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
								.getBuiltinFormat("HH:mm:ss")) {
							sdf = new SimpleDateFormat("HH:mm:ss");
							// 1.1.2 日期格式②
						} else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
								.getBuiltinFormat("yyyy-MM-dd HH:mm:ss")) {
							sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							// 1.1.3 日期格式③
						} else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
								.getBuiltinFormat("yyyy/MM/dd hh:mm:ss")) {
							sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
							// 1.1.4 日期格式⑤
						} else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
								.getBuiltinFormat("yyyy年MM月dd日HH时mm分ss秒")) {
							sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
						} else {
							sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							// 1.1.4 日期格式④
						}
						// 1.2 强转为时间或者日期的格式
						Date date = (Date) cell.getDateCellValue();
						result = sdf.format(date);
					} else if (cell.getCellStyle().getDataFormat() == 58) {
						// 1.3 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						double value = cell.getNumericCellValue();
						Date date = (Date) org.apache.poi.ss.usermodel.DateUtil
								.getJavaDate(value);
						result = sdf.format(date);
					} else {
						// 1.4 单元格设置成常规
						double value = cell.getNumericCellValue();
						CellStyle style = cell.getCellStyle();
						DecimalFormat format = new DecimalFormat();
						String temp = style.getDataFormatString();
						if (temp.equals("General")) {
							format.applyPattern("#");
						}
						result = format.format(value);
					}
					break;
				// 2.字符串类型
				case HSSFCell.CELL_TYPE_STRING:
					result = cell.getStringCellValue();
					break;
				// 3.空白单元格类型
				case HSSFCell.CELL_TYPE_BLANK:
					result = "   ";
					// 4.布尔值单元格类型
				case HSSFCell.CELL_TYPE_BOOLEAN:
					result = cell.getBooleanCellValue() + "";
					// System.out.println(cell.getBooleanCellValue());
					break;
				// 5.公式单元格类型
				case HSSFCell.CELL_TYPE_FORMULA:
					result = cell.getCellFormula();
					// System.out.println(cell.getCellFormula());
					break;
				// 6.不支持的其它类型
				default:
					System.out.println("unsuported cell type");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
