package com.picstudio.photoeditorplus.stasm.renderer;

import java.io.Serializable;

/**
 * Created by ruanjiewei on 2017/8/9
 */

public class ColorInfo implements Serializable {

    private short colorR;
    private short colorG;
    private short colorB;
    private float alpha;

    public ColorInfo(int colorR, int colorG, int colorB, float alpha) {
        this.colorR = (short) colorR;
        this.colorG = (short) colorG;
        this.colorB = (short) colorB;
        this.alpha = alpha;
    }

    public float[] getNormaliziedColor() {
        return new float[] {
                (float)colorR / 255.0f,
                (float)colorG / 255.0f,
                (float)colorB / 255.0f
        };
    }

    public short getColorR() {
        return colorR;
    }

    public short getColorG() {
        return colorG;
    }

    public short getColorB() {
        return colorB;
    }

    public float getAlpha() {
        return alpha;
    }
}
