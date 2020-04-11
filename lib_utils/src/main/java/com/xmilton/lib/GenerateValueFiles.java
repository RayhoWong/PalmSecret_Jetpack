package com.xmilton.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
/**
 * 屏幕适配生成类
 * @author :     xiemingrui
 * @since :      2019/7/31
 */
public class GenerateValueFiles {

    private int baseW;
    private String dirStr = "D:\\Android\\Workspace\\palm_truck\\baselib\\src\\main\\res";

    private final static String WTemplate = "<dimen name=\"change_{0}px\">{1}dp</dimen>\n";


    private final static String VALUE_TEMPLATE = "values-sw{0}dp";

    private static final int MIN_SUPPORT = 300;
    private static final int MAX_SUPPORT = 460;

    public GenerateValueFiles(int baseW) {
        this.baseW = baseW;
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();

        }
    }


    public void generate() {
        String[] vals = new String[(MAX_SUPPORT - MIN_SUPPORT) / 10+1];
        int index = 0;
        for (int i = MIN_SUPPORT; i <= MAX_SUPPORT; ) {
            vals[index++] = String.valueOf(i);
            i += 10;
        }
        for (String val : vals) {
            generateXmlFile(Integer.parseInt(val));
        }

    }

    private void generateXmlFile(int w) {

        StringBuffer sbForWidth = new StringBuffer();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForWidth.append("<resources>");
        float cellw = w * 3.0f / baseW;//2表示屏幕密度xhdpi

        for (int i = 0; i < baseW; i++) {
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}",
                    change(cellw * i / 3) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", baseW + "").replace("{1}",
                w + ""));
        sbForWidth.append(WTemplate.replace("{0}", 1104 + "").replace("{1}",
                change(cellw * 1104 / 3) + ""));
        sbForWidth.append(WTemplate.replace("{0}", 1312 + "").replace("{1}",
                change(cellw * 1312 / 3) + ""));
        sbForWidth.append("</resources>");

        File fileDir = new File(dirStr + File.separator
                + VALUE_TEMPLATE.replace("{0}", w + ""));
        fileDir.mkdir();

        File layxFile = new File(fileDir.getAbsolutePath(), "dimens.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(sbForWidth.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }

    public static void main(String[] args) {
        //基准的屏幕最小宽度，可以根据实际情况制定。
        int baseW = 1080;//720*1280(xhdpi)屏幕宽度为360dp
        new GenerateValueFiles(baseW).generate();
    }

}