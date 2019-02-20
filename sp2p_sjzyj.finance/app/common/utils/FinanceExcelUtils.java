package common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import play.Play;

public class FinanceExcelUtils {
	
	private static void setCellGBKValue(HSSFCell cell, String value) {
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		// 设置CELL的编码信息
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(value);
	}
	
	public static void setCellValue(HSSFCell cell, Object object){
		if(object == null){
			cell.setCellValue("");
		}else{
			if (object instanceof String) {
				cell.setCellValue(String.valueOf(object));
			}else if(object instanceof Long){
				Long temp = (Long)object;
				String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
				cell.setCellValue(cellvalue);
			}else if(object instanceof Double){
				Double temp = (Double)object;
				String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
				cell.setCellValue(cellvalue);
			}else if(object instanceof Float){
				Float temp = (Float)object;
				String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
				cell.setCellValue(cellvalue);
			}else if(object instanceof Integer){
				Integer temp = (Integer)object;
				cell.setCellValue(temp.intValue());
			}else if(object instanceof BigDecimal){
				BigDecimal temp = (BigDecimal)object;
				String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
				cell.setCellValue(cellvalue);
			}else{
				cell.setCellValue("");
			}
		}
	}
	
	/**
	 * 生成Excel文件
	 * 
	 * @param sheetName sheet名称
	 * @param row 行数
	 * @param column 列数
	 * @param list 内容属性
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.25
	 * */
	public static File createExcel (String sheetName , int row , int column , List<String[]> list ) {
		HSSFWorkbook wb = new HSSFWorkbook();
		
		HSSFSheet sheet = null;
		
		// 对每个表生成一个新的sheet,并以表名命名
		if(sheetName == null){
			sheetName = "sheet1";
		}
		
		sheet = wb.createSheet(sheetName);
		
		//表格样式
		HSSFFont font2 = wb.createFont();
		font2.setFontName("宋体");
		font2.setFontHeightInPoints((short) 10);
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		style.setFont(font2);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		//style.setWrapText(true);
		//style.setLocked(true);
		//添加样式
		for(int i = 0 ; i < row ; i++){
			HSSFRow roww = sheet.createRow(i);
			for (short j = 0 ; j < column ; j++) {
				
				HSSFCell celll = roww.createCell(j);
				if (sheet.getRow(0) == roww ) {
					roww.setHeight((short)600);
				}else {
					celll.setCellStyle(style);
				}
			}
		}
		
		
		if (list != null && list.size() > 0) {
			
			for (int i = 0 ; i < list.size() ; i++) {
				String [] aaa = list.get(i);
				
				/**合并行列*/
				String a1 = aaa[0];
				if (!a1.equals("")) {
					
					String [] a11 = a1.split(",");
					Integer startrow = Integer.parseInt(a11[0]);
					Integer overrow = Integer.parseInt(a11[1]);
					Short startcol = Short.parseShort(a11[2]);
					Short overcol = Short.parseShort(a11[3]);
					Region region = new Region();
					region.setRowFrom(startrow);
					region.setRowTo(overrow);
					region.setColumnFrom(startcol);
					region.setColumnTo(overcol);
					sheet.addMergedRegion(region);
				}
				
				/**写入的值*/
				String a2 = aaa[1];
				
				/**单元格的位置行列*/
				String a3 = aaa[2];
				if (!a3.equals("")) {
					String [] a33 = a3.split(",");
					
					Integer r = Integer.parseInt(a33[0]);
					Short c = Short.parseShort(a33[1]);
					HSSFRow getRow = sheet.getRow(r);
					
					String a6 = aaa[5];
					if (a6!= null && !a6.equals("")) {
						String [] a66 = a6.split(",");
						Short height = Short.parseShort(a66[0]);
						Short width = Short.parseShort(a66[1]);
						getRow.setHeight(height);
						sheet.setColumnWidth(c, width);
					}
					
					HSSFCell getCell = getRow.getCell(c);
					setCellGBKValue(getCell,a2);
					
					String a5 = aaa[4];
					if (!a5.equals("")) {
						HSSFFont font = wb.createFont();
						font.setFontName("宋体");
						font.setFontHeightInPoints(Short.parseShort(a5));
						
						String a8 = aaa[7];
						if (!a8.equals("")) {
							font.setColor(Short.parseShort(a8));
						}
						
						String a4 = aaa[3];
						
						HSSFCellStyle contentStyle = wb.createCellStyle();
						
						contentStyle.setFont(font);
						
						contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
						contentStyle.setWrapText(true);
						//contentStyle.setLocked(true);
						
						
						if (a4.equals("m")) {
							contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
						}else if (a4.equals("l")) {
							contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 左右居中
						}else if (a4.equals("r")) {
							contentStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);// 左右居中
						}
						
						String a7 = aaa[6];
						if (!a7.equals("")) {
							if (a7.equals("border")) {
								contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
								contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
								contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
								contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
							}
							if (a7.equals("noBorder")) {
								
								contentStyle.setBorderBottom(HSSFCellStyle.BORDER_NONE); //下边框
								contentStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);//左边框
								contentStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);//上边框
								contentStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);//右边框
							}
						}
						
						getCell.setCellStyle(contentStyle);
					}
					
					
				}
				
			}
			
		}
		
		String path = Play.getFile("/tmp/").getAbsolutePath();
    	String filename = UUID.randomUUID().toString() + ".xls";
    	File file = new File(path + "/" + filename);
    	
    	OutputStream os = null; 
    	try {
    		os = new FileOutputStream(file);
    		wb.write(os);
    		os.flush();
    		os.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
		
	}
}
