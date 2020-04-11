package com.picstudio.photoeditorplus.stasm;

import android.graphics.PointF;

import org.opencv.core.Rect;

public class RotatedRectF {

    private float centerX;
    private float centerY;
    private float angle;
    private float width;
    private float height;

    public RotatedRectF(float centerX, float centerY, float angle, float width, float height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
        this.width = width;
        this.height = height;
    }

    public RotatedRectF() {

    }

    public static RotatedRectF from(float[] rawData) {
        RotatedRectF rectF = new RotatedRectF();
        rectF.centerX = rawData[0];
        rectF.centerY = rawData[1];
        rectF.angle = rawData[2];
        rectF.width = rawData[3];
        rectF.height = rawData[4];
        return rectF;
    }

    public void points(PointF pt[])
    {
        double _angle = angle * Math.PI / 180.0;
        double b = Math.cos(_angle) * 0.5f;
        double a = Math.sin(_angle) * 0.5f;

        pt[0] = new PointF(
                (float) (centerX - a * height - b * width),
                (float) (centerY + b * height - a * width));

        pt[1] = new PointF(
                (float) (centerX + a * height - b * width),
                (float) (centerY - b * height - a * width));

        pt[2] = new PointF(
                2 * centerX - pt[0].x,
                2 * centerY - pt[0].y);

        pt[3] = new PointF(
                2 * centerX - pt[1].x,
                2 * centerY - pt[1].y);
    }

    public android.graphics.Rect boundingRect()
    {
        PointF[] pt = new PointF[4];
        points(pt);
        Rect r = new Rect((int) Math.floor(Math.min(Math.min(Math.min(pt[0].x, pt[1].x), pt[2].x), pt[3].x)),
                (int) Math.floor(Math.min(Math.min(Math.min(pt[0].y, pt[1].y), pt[2].y), pt[3].y)),
                (int) Math.ceil(Math.max(Math.max(Math.max(pt[0].x, pt[1].x), pt[2].x), pt[3].x)),
                (int) Math.ceil(Math.max(Math.max(Math.max(pt[0].y, pt[1].y), pt[2].y), pt[3].y)));
        r.width -= r.x - 1;
        r.height -= r.y - 1;
        return new android.graphics.Rect(r.x, r.y, r.x + r.width, r.y + r.height);
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "RotatedRectF{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", angle=" + angle +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
