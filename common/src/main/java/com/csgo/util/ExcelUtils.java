package com.csgo.util;

import com.csgo.config.ColumnValue;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class ExcelUtils {

    private static final String DEFAULT_SHEET = "Sheet1";

    public static <T> HSSFWorkbook getHSSFWorkWithHeaders(List<T> list, Class<T> tClass, Boolean hidden) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, DEFAULT_SHEET);
        short titleForegroundColorIndex = ExcelUtils.getHSSFColorIndex(workbook);
        HSSFCellStyle titleCellStyle = ExcelUtils.setCellStyleWithAllBorderAndForegroundColor(workbook, titleForegroundColorIndex);
        int statusIndex = 0;
        for (Field field : tClass.getDeclaredFields()) {
            ColumnValue columnValue = field.getAnnotation(ColumnValue.class);
            if (null != columnValue) {
                if (hidden && columnValue.hidden()) continue;
                ExcelUtils.setValue(sheet, 0, statusIndex, columnValue.value(), titleCellStyle);
                statusIndex++;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Class c = list.get(i).getClass();
            Field[] fs = c.getDeclaredFields(); //fs为c的所有属性
            for (int k = 0; k < fs.length; k++) {
                String fieldName = fs[k].getName();
                ExcelUtils.setValue(sheet, i + 1, k, getFieldValueByName(fieldName, list.get(i)), titleCellStyle);
            }
        }
        return workbook;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            return method.invoke(o, new Object[]{});
        } catch (Exception e) {
        }
        return null;
    }

    public static void downFileWithFileName(HttpServletResponse response, Workbook workbook, HttpServletRequest request, String fileName) throws Exception {
        try (java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(response.getOutputStream())) {
            response.reset();
            response.setContentType("application/ms-excel");
            response.setCharacterEncoding("UTF-8");
            //火狐浏览器下载文件
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.replaceAll(" ", "").getBytes(StandardCharsets.UTF_8), "iso-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + encodeUrl(fileName));
            }
            workbook.write(bos);
            bos.flush();
        }
    }

    private static String encodeUrl(String text) {
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 设置Cell的值及样式
     *
     * @param st          Excel的sheet
     * @param rowIndex    行坐标
     * @param columnIndex 列坐标
     * @param value       值
     */
    private static void setValue(HSSFSheet st, int rowIndex, int columnIndex, Object value, HSSFCellStyle cellStyle) {
        Row row = st.getRow(rowIndex);
        if (row == null) {
            row = st.createRow(rowIndex);
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        int hssfCell;
        if (value instanceof Number) {
            hssfCell = HSSFCell.CELL_TYPE_NUMERIC;
        } else {
            hssfCell = HSSFCell.CELL_TYPE_STRING;
        }
        cell.setCellType(hssfCell);
        if (hssfCell == HSSFCell.CELL_TYPE_NUMERIC) {
            try {
                double cellValue = Double.parseDouble(String.valueOf(value));
                cell.setCellValue(cellValue);
            } catch (Exception ignored) {
            }
        } else {
            cell.setCellValue(String.valueOf(value));
        }
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        st.autoSizeColumn(columnIndex);
    }

    private static HSSFCellStyle setCellStyleWithAllBorderAndForegroundColor(HSSFWorkbook workbook, short foregroundColorIndex) {
        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        HSSFFont hssfFont = workbook.createFont();
        hssfFont.setFontName("宋体");                         //字体
        hssfFont.setFontHeightInPoints((short) 14);         // 字体大小，默认14
        //      hssfFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        hssfCellStyle.setFont(hssfFont);
        //       hssfCellStyle.setAlignment(org.apache.poi.ss.usermodel.CellStyle.ALIGN_LEFT);         // 左右位置
        //       hssfCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);              // 上下位置
        hssfCellStyle.setWrapText(true);
        //       hssfCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        hssfCellStyle.setFillForegroundColor(foregroundColorIndex);
//        hssfCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        hssfCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        hssfCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        hssfCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        return hssfCellStyle;
    }

    //通过修改已经存在的颜色对象的RGB值，来达到设置新的颜色
    private static short getHSSFColorIndex(HSSFWorkbook workbook) {
        HSSFPalette customPalette = workbook.getCustomPalette();
        customPalette.setColorAtIndex(HSSFColor.RED.index, (byte) -11, (byte) -11, (byte) -10);
        return HSSFColor.RED.index;
    }

    /**
     * 描述：根据文件名获取文件内容
     *
     * @param in ,fileName
     * @return 返回Excel除表头外的内容数据
     */
    public static List<List<Object>> getListByExcel(InputStream in) throws Exception {
        List<List<Object>> list = new ArrayList<>();
        //创建Excel工作薄
        Workbook work = getWorkbook(in);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            loopGetValue(sheet, list);
        }
        return list;
    }


    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param in
     * @return Workbook
     * @throws IOException InvalidFormatException
     */
    private static Workbook getWorkbook(InputStream in) throws Exception {
        Workbook wb = null;
        try {
            PushbackInputStream stream = null;
            if (!in.markSupported()) {
                stream = new PushbackInputStream(in, 8);
            }
            if (null != stream && POIFSFileSystem.hasPOIFSHeader(stream)) { //2003版
                wb = new HSSFWorkbook(stream);
            }
        } catch (IOException e) {
//            if (POIXMLDocument.hasOOXMLHeader(in)) { //2007版
//                wb = new XSSFWorkbook(OPCPackage.open(in)); //OPCPackage.open(is)取得一个文件的读写权限
//            }
        }
        return wb;
    }

    // 循环获取每个单元格的值
    private static void loopGetValue(Sheet sheet, List<List<Object>> list) {
        int first = 0;
        boolean isEmpty = false;
        //遍历当前sheet中的所有行
        for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null || row.getFirstCellNum() == j) {
                first = null != row ? row.getLastCellNum() : 0;
                continue;
            }
            //遍历所有的列
            List<Object> li = new ArrayList<>();
            for (int y = row.getFirstCellNum(); y < first; y++) {
                if (null == row.getCell(row.getFirstCellNum()) || "".equals(row.getCell(row.getFirstCellNum()).toString())) {
                    isEmpty = true;
                    break;
                }
                isEmpty = false;
                Cell cell = row.getCell(y);
                li.add(null == cell ? "" : getCellValue(cell));
            }
            if (!isEmpty) {
                list.add(li);
            }
        }
    }

    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = DateUtils.dateToString(cell.getDateCellValue());
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                try {
                    value = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);
                    CellValue cellValue = evaluator.evaluate(cell);
                    double va = cellValue.getNumberValue();
                    DecimalFormat format = new DecimalFormat("#0");
                    value = format.format(va);
                }
                break;
            default:
                break;
        }
        return value;
    }

}
