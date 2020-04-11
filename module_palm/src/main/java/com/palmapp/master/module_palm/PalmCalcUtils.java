package com.palmapp.master.module_palm;


import android.graphics.PointF;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * @author :     xiemingrui
 * @since :      2020/3/13
 */
public class PalmCalcUtils {
    private static final int MAX_Y = 192;
    private static final int MIN_Y = 0;

    public static PointF[] getVerticalLineByDistance(PointF point1, float distance) {
        PointF temp = new PointF();
        temp.x = point1.x;
        temp.y = point1.y + distance;
        return new PointF[]{point1, temp};
    }

    public static float getDistance(PointF point1, PointF point2) {
        return (float) Math.abs(Math.hypot(point1.x - point2.x, point1.y - point2.y));
    }

    public static PointF[] getVerticalLineByO(PointF point1, PointF o) {
        float distance = getDistance(point1, o);
        return getVerticalLineByDistance(o, distance);
    }

    public static boolean isContain(PointF point1, PointF point2, PointF key) {
        return point1.x < key.x && key.x < point2.x;
    }

    public static PointF getCenterPoint(PointF point1, PointF point2) {
        return new PointF((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
    }

    public static PointF[] getVerticalLine(PointF point1, PointF point2) {
        float k = calcK(point1, point2);
        k = -1 / k;
        PointF center = getCenterPoint(point1, point2);
        PointF[] points = new PointF[]{new PointF(), new PointF()};
        points[0].y = MAX_Y;
        points[1].y = 0;
        if (k == 0f) {
            points[0].x = point1.x;
            points[1].x = point1.x;
        } else {
            points[0].x = (int) (MAX_Y / k - center.y / k + center.x);
            points[1].x = (int) (MIN_Y / k - center.y / k + center.x);
        }
        return points;
    }

    private static boolean linesIntersect(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14) {
        return relativeCCW(var0, var2, var4, var6, var8, var10) * relativeCCW(var0, var2, var4, var6, var12, var14) <= 0 && relativeCCW(var8, var10, var12, var14, var0, var2) * relativeCCW(var8, var10, var12, var14, var4, var6) <= 0;
    }

    private static int relativeCCW(double var0, double var2, double var4, double var6, double var8, double var10) {
        var4 -= var0;
        var6 -= var2;
        var8 -= var0;
        var10 -= var2;
        double var12 = var8 * var6 - var10 * var4;
        if (var12 == 0.0D) {
            var12 = var8 * var4 + var10 * var6;
            if (var12 > 0.0D) {
                var8 -= var4;
                var10 -= var6;
                var12 = var8 * var4 + var10 * var6;
                if (var12 < 0.0D) {
                    var12 = 0.0D;
                }
            }
        }

        return Double.compare(var12, 0.0D);
    }

    public static boolean isIntersection(PointF p11, PointF p12,
                                         PointF p21, PointF p22) {
        // 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
        double l1x1, l1x2, l2x1, l2x2, l1y1, l1y2, l2y1, l2y2;
        l1x1 = p11.x;
        l1x2 = p12.x;
        l1y1 = p11.y;
        l1y2 = p12.y;
        l2x1 = p21.x;
        l2x2 = p22.x;
        l2y1 = p21.y;
        l2y2 = p22.y;
        return linesIntersect(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2);
    }

    public static float calcK(PointF point1, PointF point2) {
        if (point1.x - point2.x == 0) {
            return 0f;
        }
        return (point1.y - point2.y) / (point1.x - point2.x);
    }
}
