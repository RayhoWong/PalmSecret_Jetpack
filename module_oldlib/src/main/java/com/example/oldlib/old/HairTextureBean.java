package com.example.oldlib.old;

import android.graphics.Bitmap;
import android.graphics.PointF;


import com.example.oldlib.hair.GPUImageFilterGroup;

import java.util.ArrayList;

public class HairTextureBean extends HairBaseBean {
    String pkgName;
    Bitmap texture;
    ArrayList<PointF> mTexturePoints;
    ArrayList<Integer> mFaceKeyPointIndex;
    GPUImageFilterGroup mFilterGroup;
    boolean hasConfig;


    public HairTextureBean(String pkgName, Bitmap texture) {
        this.pkgName = pkgName;
        this.texture = texture;
    }

    public HairTextureBean(String pkgName, Bitmap texture, ArrayList<PointF> texturePoints,
                           ArrayList<Integer> faceKeyPointIndex, GPUImageFilterGroup filterGroup) {
        this(pkgName, texture);
        mTexturePoints = texturePoints;
        mFaceKeyPointIndex = faceKeyPointIndex;
        mFilterGroup = filterGroup;
        hasConfig = true;
    }

    @Override
    public GPUImageFilter createSoftLightFilter(float[] textureCoor, float[] vertexCoor) {
        GPUImageHairTextureFilter filter = new GPUImageHairTextureFilter(textureCoor, vertexCoor);
        filter.setBitmap(texture);
        return filter;
    }

    public boolean isHasConfig() {
        return hasConfig;
    }

    public ArrayList<PointF> getTexturePoints() {
        return mTexturePoints;
    }

    public ArrayList<Integer> getFaceKeyPointIndex() {
        return mFaceKeyPointIndex;
    }

    public String getPkgName() {
        return pkgName;
    }

    public GPUImageFilterGroup getFilterGroup() {
        return mFilterGroup;
    }

    public void setFilterGroup(GPUImageFilterGroup filterGroup) {
        mFilterGroup = filterGroup;
    }
}
