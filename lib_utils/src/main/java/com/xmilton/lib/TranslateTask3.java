package com.xmilton.lib;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

/**
 * Created by milton on 18/7/20.
 * 自动填写多语言工具
 */
public class TranslateTask3 {
    //多语言表
    private static final String IN_PATH = "D:\\Android\\Workspace\\palm_truck\\lib_utils\\src\\main\\java\\com\\xmilton\\lib\\109.xlsx";
    //项目地址
    private static final String OUT_PATH = "D:\\Android\\Workspace\\palm_truck\\baselib\\src\\main\\res";
    private static HashMap<String, String> mMap = new HashMap<>();
    private HashMap<String, List<Item>> mResult = new HashMap<>();
    private HashMap<Integer, String> mLanuages = new HashMap<>();
    private HashMap<Integer, String> mKeys = new HashMap<>();
    private File mTarget;

    public static void main(String args[]) throws DocumentException, IOException {
        System.out.println("三生三世");
//        TranslateTask translateTask = new TranslateTask();
//        translateTask.setTarget(new File(IN_PATH));
//        translateTask.readExcel();
//        translateTask.writeXml();
    }

    static {
        mMap.put("英语", "values");
        mMap.put("中文-简体", "values-zh-rCN");
        mMap.put("中文-繁体", "values-zh-rTW");
        mMap.put("韩语(ko)", "values-ko");
        mMap.put("日语（jp）", "values-ja");
        mMap.put("葡萄牙语(pt)", "values-pt");
        mMap.put("西班牙语(es)", "values-es");
        mMap.put("法语(fr)", "values-fr");
        mMap.put("德语(de)", "values-de");
    }

    public void writeXml() throws DocumentException, IOException {
        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setIndent(true);
        outputFormat.setNewlines(true);
        File temp = new File(OUT_PATH);
        File[] files = temp.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains("values");
            }
        });
        for (File file : files) {
            int changes = 0;
            File[] tem = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().contains("strings");
                }
            });
            File result = tem.length > 0 ? tem[0] : null;
            if (result == null)
                continue;
            if (!mResult.containsKey(file.getName()))
                continue;
            System.out.println(file.getName());
            List<Item> items = mResult.get(file.getName());
            HashMap<String, Element> mAllElements = new HashMap<>();
            SAXReader reader = new SAXReader();
            Document document = reader.read(result);
            Element root = document.getRootElement();
            Iterator<Element> iterator = root.elementIterator("string");
            XMLWriter writer = new XMLWriter(new FileOutputStream(result), outputFormat);
            while (iterator.hasNext()) {
                Element element = iterator.next();
                mAllElements.put(element.attributeValue("name"), element);
            }

            if (root.getName().equals("resources")) {
                for (Item item : items) {
                    if (!mAllElements.containsKey(item.key)) {
                        Element child = root.addElement("string");
                        child.addAttribute("name", item.key);
                        String t = item.value;
                        child.setText(t);

                    } else {
                        String t = item.value;
                        Element child = mAllElements.get(item.key);
                        child.setText(t);
                    }
                }
            }
            writer.write(document);
            writer.close();
        }
        System.out.println("完成");
    }

    public void readExcel() {
        try {
            Workbook wb = new XSSFWorkbook(new FileInputStream(mTarget));
            Sheet sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;

            for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                if (row == null)
                    break;
                for (int j = row.getFirstCellNum(); j <= row.getPhysicalNumberOfCells(); j++) {
                    if (j < 0)
                        break;
                    cell = row.getCell(j);
                    if (j == 0) {
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            mKeys.put(i, cell.getStringCellValue());
                        }
                    }
                    if (i == 0) {
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            String temp = getRealLanuages(cell.getStringCellValue());
                            if (temp != null && !temp.equals("")) {
                                mLanuages.put(j, temp);
                            }
                        }
                    }

                    if (i != 0 && j != 0) {
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && mLanuages.get(j) != null) {
                            List<Item> items = null;
                            if (mResult.containsKey(mLanuages.get(j))) {
                                items = mResult.get(mLanuages.get(j));
                            } else {
                                items = new ArrayList<>();
                            }
                            items.add(new Item(mKeys.get(i), cell.getStringCellValue()));
                            mResult.put(mLanuages.get(j), items);
                        }
                    }
                }
            }
            System.out.println("完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRealLanuages(String lanuage) {
        String result;
        Set<String> set = mMap.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            result = iterator.next();
            if (lanuage.contains(result)) {
                return mMap.get(result);
            }
        }
        System.out.println("未找到语音：" + lanuage);
        return null;
    }

    public File getTarget() {
        return mTarget;
    }

    public void setTarget(File file) {
        mTarget = file;
    }

    private class Item {
        String key;
        String value;

        public Item(String key, String value) {
            this.key = key;
            this.value = value;
            handleSpecialChar();
        }

        private void handleSpecialChar() {
            this.value = this.value.replaceAll("\"", "\\\\\"");
            this.value = this.value.replaceAll("\'", "\\\\\'");
        }
    }
}