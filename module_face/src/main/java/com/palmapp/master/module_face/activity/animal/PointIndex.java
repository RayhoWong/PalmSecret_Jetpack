package com.palmapp.master.module_face.activity.animal;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/25
 * @tips 这个类是Object的子类
 * @fuction
 */
public class PointIndex {

    public int indexX;
    public int indexY;

    public PointIndex(int indexX) {
        this.indexX = indexX;
        this.indexY = indexX + 1;
    }
}
