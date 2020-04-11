package com.palmapp.master.module_palm.scan;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * @author :     xiemingrui
 * @since :      2020/3/13
 */
public class PalmResult implements Serializable {
    public static final String KEY_LIFE_SHORT = "0000";
    public static final String KEY_LIFE_LONG = "0001";
    public static final String KEY_BUSINESS_SHORT = "0100";
    public static final String KEY_BUSINESS_LONG = "0101";
    public static final String KEY_LOVE_SHIZHI = "0200";
    public static final String KEY_LOVE_FUCK = "0201";
    public static final String KEY_LOVE_WUMING = "0202";
    public static final String KEY_MONEY_EQULE = "0300";
    public static final String KEY_MONEY_HIGH = "0301";
    public static final String KEY_MONEY_LESS = "0302";
    public static final String KEY_MARRY_HAS = "0400";
    public static final String KEY_MARRY_NONE = "0401";
    public static final String KEY_THUMB_LONG = "1000";
    public static final String KEY_THUMB_SHORT = "1001";
    public static final String KEY_SHIZHI_MAX_LONG = "1100";
    public static final String KEY_SHIZHI_LONG = "1101";
    public static final String KEY_SHIZHI_SHORT = "1102";
    public static final String KEY_SHIZHI_MAX_SHORT = "1103";
    public static final String KEY_FUCK_EQULE = "1200";
    public static final String KEY_FUCK_LONG = "1201";
    public static final String KEY_FUCK_SHORT = "1202";
    public static final String KEY_WUMING_EQULE = " 1300";
    public static final String KEY_WUMING_LONG = "1301";
    public static final String KEY_WUMING_SHORT = "1302";
    public static final String KEY_SMALL_EQULE = "1400";
    public static final String KEY_SMALL_LONG = "1401";
    public static final String KEY_SMALL_SHORT = "1402";
    public static final String KEY_PALM_EQULE = "2000";
    public static final String KEY_PALM_BIG = "2001";
    public static final String KEY_PALM_SMALL = "2002";
    public static final String KEY_PALM_LONG = "2003";
    public static final String KEY_PALM_SHORT = "2004";

    public String life_length = "-1", business_length = "-1", love_pos = "-1", money_pos = "-1", marry_type = "-1", thumb_length = "-1", shizhi_length = "-1", fuck_length = "-1", wuming_length = "-1", small_length = "-1", palm_type = "-1";
    public int finger_length = -1;
    public int palm_width = -1;
    public int palm_length = -1;

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("生命线长度：");
        if (life_length.equals(KEY_LIFE_LONG)) {
            sb.append("长");
        } else if (life_length.equals(KEY_LIFE_SHORT)) {
            sb.append("短");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("智慧线长度：");
        if (business_length.equals(KEY_BUSINESS_LONG)) {
            sb.append("长");
        } else if (business_length.equals(KEY_BUSINESS_SHORT)) {
            sb.append("短");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("感情线位置：");
        if (love_pos.equals(KEY_LOVE_SHIZHI)) {
            sb.append("食指区域");
        } else if (love_pos.equals(KEY_LOVE_FUCK)) {
            sb.append("中指区域");
        } else if (love_pos.equals(KEY_LOVE_WUMING)) {
            sb.append("无名指区域");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("事业线位置：");
        if (money_pos.equals(KEY_MONEY_EQULE)) {
            sb.append("止于智慧线");
        } else if (money_pos.equals(KEY_MONEY_HIGH)) {
            sb.append("高于智慧线");
        } else if (money_pos.equals(KEY_MONEY_LESS)) {
            sb.append("低于智慧线");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("婚姻线条数：");
        if (marry_type.equals(KEY_MARRY_HAS)) {
            sb.append("1条");
        } else if (marry_type.equals(KEY_MARRY_NONE)) {
            sb.append("没有");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("大拇指长度：");
        if (thumb_length.equals(KEY_THUMB_LONG)) {
            sb.append("长形拇指");
        } else if (thumb_length.equals(KEY_THUMB_SHORT)) {
            sb.append("短形拇指");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("食指长度：");
        if (shizhi_length.equals(KEY_SHIZHI_MAX_LONG)) {
            sb.append("过长食指");
        } else if (shizhi_length.equals(KEY_SHIZHI_LONG)) {
            sb.append("长形食指");
        } else if (shizhi_length.equals(KEY_SHIZHI_SHORT)) {
            sb.append("短形食指");
        } else if (shizhi_length.equals(KEY_SHIZHI_MAX_SHORT)) {
            sb.append("过短食指");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("中指长度：");
        if (fuck_length.equals(KEY_FUCK_EQULE)) {
            sb.append("标准中指");
        } else if (fuck_length.equals(KEY_FUCK_LONG)) {
            sb.append("长形中指");
        } else if (fuck_length.equals(KEY_FUCK_SHORT)) {
            sb.append("短形中指");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("无名指长度：");
        if (wuming_length.equals(KEY_WUMING_EQULE)) {
            sb.append("标准无名指");
        } else if (wuming_length.equals(KEY_WUMING_LONG)) {
            sb.append("长形无名指");
        } else if (wuming_length.equals(KEY_WUMING_SHORT)) {
            sb.append("短形无名指");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("尾指长度：");
        if (small_length.equals(KEY_SMALL_EQULE)) {
            sb.append("标准尾指");
        } else if (small_length.equals(KEY_SMALL_LONG)) {
            sb.append("长形尾指");
        } else if (small_length.equals(KEY_SMALL_SHORT)) {
            sb.append("短形尾指");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("手掌形状：");
        if (palm_type.equals(KEY_PALM_EQULE)) {
            sb.append("标准形");
        } else if (palm_type.equals(KEY_PALM_BIG)) {
            sb.append("宽掌形");
        } else if (palm_type.equals(KEY_PALM_SMALL)) {
            sb.append("狭掌形");
        } else if (palm_type.equals(KEY_PALM_LONG)) {
            sb.append("长指形");
        } else if (palm_type.equals(KEY_PALM_SHORT)) {
            sb.append("短指形");
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("食指长度值：");
        if (finger_length != -1) {
            sb.append(finger_length);
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("手掌长度值：");
        if (palm_length != -1) {
            sb.append(palm_length);
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        sb.append("手掌宽度值：");
        if (palm_width != -1) {
            sb.append(palm_width);
        } else {
            sb.append("未知");
        }
        sb.append("\n");
        return sb.toString();

    }
}
