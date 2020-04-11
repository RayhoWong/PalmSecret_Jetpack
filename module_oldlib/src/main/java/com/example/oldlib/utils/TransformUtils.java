package com.example.oldlib.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by liweibing on 17-11-2.
 */

public class TransformUtils {

    protected static RectF transform(RectF srcRect, Matrix matrixA, Matrix cacheMatrix) {
        RectF dstRect = new RectF();
        RectF a = new RectF(srcRect);
        matrixA.mapRect(dstRect, a);//还原

        float scale = dstRect.width() / a.width();
        float dx = dstRect.centerX() - a.centerX();
        float dy = dstRect.centerY() - a.centerY();

        cacheMatrix.reset();
        cacheMatrix.setScale(scale, scale, a.centerX(), a.centerY());
        cacheMatrix.mapRect(dstRect, a);
        dstRect.offset(dx, dy);

        return dstRect;
    }

    public static float getPointLenght(float[] start , float[] end){
        float x = start[0] - end[0];
        float y = start[1] - end[1];
        return (float) Math.sqrt(x * x + y * y);
    }

    public static  float[] rotate(float[] src ,float[] center ,float degrees){
        float[] dis = new float[src.length] ;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees,center[0],center[1]);
        matrix.mapPoints(dis,src);
        return dis;
    }

    public static float[] transform(double[] start , boolean isleft , float intensity ) {

        double hudu = (2*Math.PI / 360) * 35;
        double x1 = start[0] + Math.cos(hudu) * intensity;
        if(isleft){
            x1 = start[0] - Math.cos(hudu) * intensity;
        }
        double y1 = start[1]  + Math.sin(hudu) * intensity;
        float[] result = new float[start.length];
        result[0] = (float) x1;
        result[1] = (float) y1;
        return result;
    }

    public static  float[] transform(double[] start ,double[] end ,double intensity ){
        RealVector startRealVector = new ArrayRealVector(start);
        RealVector endRealVector = new ArrayRealVector(end);
        RealVector startEndRealVector = endRealVector.subtract(startRealVector);
//        double factor = startEndRealVector.getNorm()  * intensity;
        double factor =  intensity;
        RealVector delta = startEndRealVector.mapMultiply(factor);
        RealVector result = startRealVector.add(delta);
        float[] newStart = new float[result.getDimension()];
        double[]  doubles = result.toArray();
        for(int i=0;i<doubles.length;i++){
            newStart[i] = (float)doubles[i];
        }
        return newStart;
    }

    public static  float[] transform(double[] start ,double[] end , double[] realstart , double intensity ){
        RealVector startRealVectorr = new ArrayRealVector(realstart);
        RealVector startRealVector = new ArrayRealVector(start);
        RealVector endRealVector = new ArrayRealVector(end);
        RealVector startEndRealVector = endRealVector.subtract(startRealVector);
//        double factor = startEndRealVector.getNorm()  * intensity;
        double factor =  intensity;
        RealVector delta = startEndRealVector.mapMultiply(factor);
        RealVector result = startRealVectorr.add(delta);
        float[] newStart = new float[result.getDimension()];
        double[]  doubles = result.toArray();
        for(int i=0;i<doubles.length;i++){
            newStart[i] = (float)doubles[i];
        }
        return newStart;
    }


    public static float[] getCheckLandMark(){

        return  null;
    }

    /***
     * 求已知直线linePointStart--linePointEnd 和 直线外一点outsideLinePoint，求垂足点
     * @param linePointStart
     * @param linePointEnd
     * @param outsideLinePoint
     * @return
     */
//    public static float[] perpendicular(float[] linePointStart, float[] linePointEnd ,float[] outsideLinePoint){
//
//        float x1 = linePointStart[0];
//        float y1 = linePointStart[1];
//
//        float x7 = linePointEnd[0];
//        float y7 = linePointEnd[1];
//
//        float x0 = outsideLinePoint[0];
//        float y0 = outsideLinePoint[1];
//
//        float x = (x1 * (y7 - y1) -y1 * (x7 - x1)) / (  x0/y1 * (x7 - x1) - (y7- y1));
//        float y = - x0 /y0 * x;
//
//        float[] perpendicularPoint = new float[]{x,y};
//
//        return perpendicularPoint;
//    }


    public static float[] perpendicular(
            float[] linePointStart,  // 直线开始点
            float[] linePointEnd , // 直线结束点
            float[] outsideLinePoint     // 直线外一点
    )
    {
        float[] retVal = new float[2];

        float dx = linePointStart[0] - linePointEnd[0];
        float dy =  linePointStart[1] - linePointEnd[1];
        if(Math.abs(dx) < 0.00000001 && Math.abs(dy) < 0.00000001 )
        {
            retVal = linePointStart;
            return retVal;
        }

        float u = (outsideLinePoint[0]- linePointStart[0])*( linePointStart[0] - linePointEnd[1]) +
                (outsideLinePoint[1] -  linePointStart[1])*( linePointStart[1] -linePointEnd[1]);
        u = u/((dx*dx)+(dy*dy));

        retVal[0] =  linePointStart[0] + u*dx;
        retVal[1] =  linePointStart[1] + u*dy;

        return retVal;
    }





    /****
     * 求已知和向量（起点为vectorPointStart，终点为vectorPointEnd)，向量外一点作为起点outsideVectorStartPoint等长向量的终点
     * @param vectorPointStart
     * @param vectorPointEnd
     * @param outsideVectorStartPoint
     * @return
     */
    public  static float[] parallelAndEquilongEndPoint(float[] vectorPointStart, float[] vectorPointEnd ,float[] outsideVectorStartPoint){
        float deltaX = vectorPointEnd[0] - vectorPointStart[0];
        float deltaY = vectorPointEnd[1] - vectorPointStart[1];
        float x = outsideVectorStartPoint[0] +deltaX;
        float y = outsideVectorStartPoint[1] +deltaY;
        return  new float[]{x,y};
    }

}
