package com.gengyun.statistical.test;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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

public class ReadExcel {
	public static void main(String[] args) throws Exception {
		readXml("/test.xlsx");
	}

	public static void readXml(String fileName) throws Exception {
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (fileName.endsWith("xlsx"))
			isE2007 = true;
		try {
			InputStream input = ReadExcel.class.getResourceAsStream(fileName);
			Workbook wb = null;
			System.out.println(input);
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				System.out.println("Row #" + row.getRowNum()); // 获得行号从0开始
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
				while (cells.hasNext()) {
					Cell cell = cells.next();
					System.out.println("Cell #" + cell.getColumnIndex());
					System.out.println(readCellValues(cell));
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String readCellValues(Cell cell) throws Exception {
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
						System.out.println(cell.getCellStyle().getDataFormat());
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
						System.out.println(date);
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
					result = "";
					// 4.布尔值单元格类型
				case HSSFCell.CELL_TYPE_BOOLEAN:
					System.out.println(cell.getBooleanCellValue());
					break;
				// 5.公式单元格类型
				case HSSFCell.CELL_TYPE_FORMULA:
					System.out.println(cell.getCellFormula());
					break;
				// 6.不支持的其它类型
				default:
					System.out.println("unsuported cell type");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}