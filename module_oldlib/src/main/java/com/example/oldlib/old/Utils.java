package com.example.oldlib.old;


import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by chenfangyi on 17-5-12.
 * 用于计算顶点坐标和纹理坐标的工具类
 */

public class Utils {

    public enum VERTEX_MODE{
        DCAB, ABDC
    }

    public static final float VERTEX_COORD_ABCD[] = {
            -1.0f, -1.0f,   // a
            1.0f, -1.0f,   // b
            1.0f, 1.0f, // c
            -1.0f, 1.0f  // d
    };

    private static final float TEXTURE_COORD_ABCD[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f, };

    private final static int A_INDEX = 0;

    private final static int B_INDEX = 2;

    private final static int C_INDEX = 4;

    private final static int D_INDEX = 6;

    /**
     * 这个函数作用是将target标准化成顶点坐标
     * @param target  实际显示的Rect的点
     * @param displayWidth
     * @param displayHeight
     */
    public static void countStandardVertex(float[] target, float displayWidth, float displayHeight){
        int length = target.length;
        float divideW = displayWidth / 2;
        float divideH = displayHeight / 2;
        for(int i = 0 ; i < length ; i+=2){
            target[i] = (target[i] - divideW) / divideW;
            target[i+1] = -((target[i+1] - divideH) / divideH);
        }
    }

    /**
     * countStandardVertex的逆过程
     * @param target
     * @param displayWidth
     * @param displayHeight
     */
    public static void countActionRect(RectF target, float displayWidth, float displayHeight){
        float divideW = displayWidth / 2;
        float divideH = displayHeight / 2;
        target.set(target.left * divideW + divideW, -target.top * divideH + divideH, target.right * divideW + divideW, -target.bottom * divideH + divideH);
    }

    /**
     * 按照d->c->a->b 的顺序获取标准化后的顶点坐标
     * @param srcRect
     * @param displayWidth
     * @param displayHeight
     * @return
     */
    public static float[] getStandardVertex(final RectF srcRect, float displayWidth, float displayHeight, VERTEX_MODE mode){
        float[] result = new float[8];
        if(mode == VERTEX_MODE.DCAB) {
            result[0] = srcRect.left;
            result[1] = srcRect.top;
            result[2] = srcRect.right;
            result[3] = srcRect.top;
            result[4] = srcRect.left;
            result[5] = srcRect.bottom;
            result[6] = srcRect.right;
            result[7] = srcRect.bottom;
        } else if(mode == VERTEX_MODE.ABDC){
            result[0] = srcRect.left;
            result[1] = srcRect.bottom;
            result[2] = srcRect.right;
            result[3] = srcRect.bottom;
            result[4] = srcRect.left;
            result[5] = srcRect.top;
            result[6] = srcRect.right;
            result[7] = srcRect.top;
        }
        countStandardVertex(result, displayWidth, displayHeight);
        return result;
    }

    /**
     * getStandardVertex的逆过程
     * @param vertexCoord
     * @param displayWidth
     * @param displayHeight
     * @param mode
     * @return
     */
    public static RectF vertexToActionRect(float[] vertexCoord, float displayWidth, float displayHeight, VERTEX_MODE mode){
        RectF result = new RectF();
        if(mode == VERTEX_MODE.DCAB) {
            result.set(vertexCoord[A_INDEX], vertexCoord[A_INDEX + 1], vertexCoord[B_INDEX], vertexCoord[C_INDEX + 1]);
        } else if(mode == VERTEX_MODE.ABDC){
            result.set(vertexCoord[A_INDEX], vertexCoord[C_INDEX + 1], vertexCoord[B_INDEX], vertexCoord[A_INDEX + 1]);
        }
        countActionRect(result, displayWidth, displayHeight);
        return result;
    }

    /**
     *
     * 这里需要注意的是：
     * 旋转必须在翻转的处理之前， 因为旋转不会影响翻转， 而翻转是会影响旋转的
     * 比如水平翻转后再旋转90效果结果是旋转了-90， 而反过来，旋转90后水平翻转却还是水平翻转
     * @param flipH
     * @param flipV
     * @param rotation
     * @return
     */
    public static float[] getTextureCoord(boolean flipH, boolean flipV, int rotation, VERTEX_MODE mode){
        float[] result = new float[8];
        rotation = (rotation % 360 + 360) % 360;//保证是正的
        float[] buffer = new float[8];
        System.arraycopy(TEXTURE_COORD_ABCD, 0, buffer, 0, TEXTURE_COORD_ABCD.length);

        if(rotation == 90){//d -> a  &  c -> d  &  b -> c  &  a -> b
            replace(buffer, B_INDEX, C_INDEX, D_INDEX, A_INDEX);
        } else if(rotation == 180){//d <-> b  &  c <-> a
            swap(buffer, A_INDEX, C_INDEX);
            swap(buffer, B_INDEX, D_INDEX);
        } else if(rotation == 270){//d -> c  &  c -> b  &  b -> a  &  a -> d
            replace(buffer, D_INDEX, A_INDEX, B_INDEX, C_INDEX);
        }

        if(flipH){//a <->b  &  c <-> d
            swap(buffer, A_INDEX, B_INDEX);
            swap(buffer, C_INDEX, D_INDEX);
        }

        if(flipV){//a <->d  &  b <-> c
            swap(buffer, A_INDEX, D_INDEX);
            swap(buffer, B_INDEX, C_INDEX);
        }

        if(mode == VERTEX_MODE.DCAB) {
            replace(result, D_INDEX, C_INDEX, A_INDEX, B_INDEX, buffer);
        } else if(mode == VERTEX_MODE.ABDC){
            replace(result, A_INDEX, B_INDEX, D_INDEX, C_INDEX, buffer);
        }
        return result;
    }

    /**
     * 获取旋转后的纹理坐标
     * @param target
     * @param flipH
     * @param flipV
     * @param rotation
     * @param mode
     * @return
     */
    public static void getRotationTextureCoord(float []target, boolean flipH, boolean flipV, int rotation, VERTEX_MODE mode){
        float[] abcdBuffer = new float[8];//转化为标准的 abcd旋转后再转化回来
        if(mode == VERTEX_MODE.ABDC){
            replace(abcdBuffer, A_INDEX, B_INDEX, D_INDEX, C_INDEX, target);
        } else if(mode == VERTEX_MODE.DCAB){
            replace(abcdBuffer, C_INDEX, D_INDEX, A_INDEX, B_INDEX, target);
        }

        if(rotation == 90){//d -> a  &  c -> d  &  b -> c  &  a -> b
            replace(abcdBuffer, B_INDEX, C_INDEX, D_INDEX, A_INDEX);
        } else if(rotation == 180){//d <-> b  &  c <-> a
            swap(abcdBuffer, A_INDEX, C_INDEX);
            swap(abcdBuffer, B_INDEX, D_INDEX);
        } else if(rotation == 270){//d -> c  &  c -> b  &  b -> a  &  a -> d
            replace(abcdBuffer, D_INDEX, A_INDEX, B_INDEX, C_INDEX);
        }

        if(flipH){//a <->b  &  c <-> d
            swap(abcdBuffer, A_INDEX, B_INDEX);
            swap(abcdBuffer, C_INDEX, D_INDEX);
        }

        if(flipV){//a <->d  &  b <-> c
            swap(abcdBuffer, A_INDEX, D_INDEX);
            swap(abcdBuffer, B_INDEX, C_INDEX);
        }

        if(mode == VERTEX_MODE.ABDC){//将生成的最终状态的数据赋值到target中
            replace(target, A_INDEX, B_INDEX, D_INDEX, C_INDEX, abcdBuffer);
        } else if(mode == VERTEX_MODE.DCAB) {
            replace(target, D_INDEX, C_INDEX, A_INDEX, B_INDEX, abcdBuffer);
        }
    }


    /**
     *
     * @param mode
     * @return
     */
    public static float[] getVertexCoord(VERTEX_MODE mode){
        float[] result = new float[8];
        if(mode == VERTEX_MODE.DCAB) {
            replace(result, D_INDEX, C_INDEX, A_INDEX, B_INDEX, VERTEX_COORD_ABCD);
        } else if(mode == VERTEX_MODE.ABDC){
            replace(result, A_INDEX, B_INDEX, D_INDEX, C_INDEX, VERTEX_COORD_ABCD);
        }
        return result;
    }

    /**
     * 用于交换 swapIndex1 <-> swapIndex2
     * @param array
     * @param swapIndex1
     * @param swapIndex2
     */
    private static void swap(float[] array, int swapIndex1, int swapIndex2){
        float cacheX = array[swapIndex1];
        float cacheY = array[swapIndex1 + 1];
        array[swapIndex1] = array[swapIndex2];
        array[swapIndex1 + 1] = array[swapIndex2 + 1];
        array[swapIndex2] = cacheX;
        array[swapIndex2 + 1] = cacheY;
    }

    /**
     * replaceArray  ->  array
     * aReplaceIndex 替换 A_INDEX
     * bReplaceIndex 替换 B_INDEX
     * cReplaceIndex 替换 C_INDEX
     * dReplaceIndex 替换 D_INDEX
     * @param array
     * @param aReplaceIndex
     * @param bReplaceIndex
     * @param cReplaceIndex
     * @param dReplaceIndex
     * @param replaceArray
     */
    private static void replace(float[] array, int aReplaceIndex, int bReplaceIndex, int cReplaceIndex, int dReplaceIndex, float[] replaceArray){
        array[A_INDEX] = replaceArray[aReplaceIndex];
        array[A_INDEX + 1] = replaceArray[aReplaceIndex + 1];
        array[B_INDEX] = replaceArray[bReplaceIndex];
        array[B_INDEX + 1] = replaceArray[bReplaceIndex + 1];
        array[C_INDEX] = replaceArray[cReplaceIndex];
        array[C_INDEX + 1] = replaceArray[cReplaceIndex + 1];
        array[D_INDEX] = replaceArray[dReplaceIndex];
        array[D_INDEX + 1] = replaceArray[dReplaceIndex + 1];
    }

    /**
     * array  ->  array
     * aReplaceIndex 替换 A_INDEX
     * bReplaceIndex 替换 B_INDEX
     * cReplaceIndex 替换 C_INDEX
     * dReplaceIndex 替换 D_INDEX
     * @param array
     * @param aReplaceIndex
     * @param bReplaceIndex
     * @param cReplaceIndex
     * @param dReplaceIndex
     */
    private static void replace(float[] array, int aReplaceIndex, int bReplaceIndex, int cReplaceIndex, int dReplaceIndex){
        float []replaceArray = new float[array.length];
        System.arraycopy(array, 0, replaceArray, 0, array.length);
        replace(array, aReplaceIndex, bReplaceIndex, cReplaceIndex, dReplaceIndex, replaceArray);
    }

    public static void countReadPixelRect(Rect target, int addSize, boolean hasAddPaddingX, boolean hasAddPaddingY, float[] textureCoordNotFlip, VERTEX_MODE mode){
        int length = textureCoordNotFlip.length;
        int a_Index = A_INDEX, b_Index = B_INDEX, c_Index = C_INDEX, d_Index = D_INDEX;
        for(int i = 0 ; i < length ; i+=2){
            if(textureCoordNotFlip[i] == TEXTURE_COORD_ABCD[A_INDEX] && textureCoordNotFlip[i + 1] == TEXTURE_COORD_ABCD[A_INDEX + 1]){
                a_Index = i;
            }

            if(textureCoordNotFlip[i] == TEXTURE_COORD_ABCD[B_INDEX] && textureCoordNotFlip[i + 1] == TEXTURE_COORD_ABCD[B_INDEX + 1]){
                b_Index = i;
            }

            if(textureCoordNotFlip[i] == TEXTURE_COORD_ABCD[C_INDEX] && textureCoordNotFlip[i + 1] == TEXTURE_COORD_ABCD[C_INDEX + 1]){
                c_Index = i;
            }

            if(textureCoordNotFlip[i] == TEXTURE_COORD_ABCD[D_INDEX] && textureCoordNotFlip[i + 1] == TEXTURE_COORD_ABCD[D_INDEX + 1]){
                d_Index = i;
            }
        }
        if(mode == VERTEX_MODE.DCAB) {
            if(a_Index == A_INDEX){//说明原来的A现在在D的位置， 就是左上角
                if(b_Index == B_INDEX){//因为A在左上则B只能在 A/C位置    C
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                        target.offset(0, addSize);
                    }
                } else if(b_Index == C_INDEX){//A
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                        target.offset(addSize, 0);
                    }
                }
            } else if(a_Index == B_INDEX){//说明原来的A现在在C的位置， 就是右上角
                if(b_Index == A_INDEX){//因为A在右上则B只能在 B/D位置    D
                    if(hasAddPaddingX){
                        target.offset(addSize, 0);
                    }
                    if(hasAddPaddingY){
                        target.offset(0, addSize);
                    }
                } else if(b_Index == D_INDEX){//B
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                    }
                }
            } else if(a_Index == C_INDEX){//说明原来的A现在在A的位置， 就是左下角
                if(b_Index == A_INDEX){//因为A在左下则B只能在 B/D位置    D
                    if(hasAddPaddingX){
                        target.offset(0, addSize);
                    }
                    if(hasAddPaddingY){
                        target.offset(addSize, 0);
                    }
                } else if(b_Index == D_INDEX){//B
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                    }
                }
            } else{//D_INDEX  //说明原来的A现在在B的位置， 就是右下角
                if(b_Index == B_INDEX){//因为A在右下则B只能在 A/C位置    C
                    if(hasAddPaddingX){
                        target.offset(0, addSize);
                    }
                    if(hasAddPaddingY){
                    }
                } else if(b_Index == C_INDEX){//A
                    if(hasAddPaddingX){
                        target.offset(addSize, 0);
                    }
                    if(hasAddPaddingY){
                    }
                }
            }
        } else if(mode == VERTEX_MODE.ABDC){
            if(a_Index == A_INDEX){//说明原来的A现在在A的位置， 就是左下角
                if(b_Index == B_INDEX){//因为A在左下则B只能在 B/D位置    B
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                    }
                } else if(b_Index == C_INDEX){//D
                    if(hasAddPaddingX){
                        target.offset(0, addSize);
                    }
                    if(hasAddPaddingY){
                        target.offset(addSize, 0);
                    }
                }
            } else if(a_Index == B_INDEX){//说明原来的A现在在B的位置， 就是右下角
                if(b_Index == A_INDEX){//因为A在右下则B只能在 A/C位置    A
                    if(hasAddPaddingX){
                        target.offset(addSize, 0);
                    }
                    if(hasAddPaddingY){
                    }
                } else if(b_Index == D_INDEX){//C
                    if(hasAddPaddingX){
                        target.offset(0, addSize);
                    }
                    if(hasAddPaddingY){
                    }
                }
            } else if(a_Index == C_INDEX){//说明原来的A现在在D的位置， 就是左上角
                if(b_Index == A_INDEX){//因为A在左上则B只能在 A/C位置    A
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                        target.offset(addSize, 0);
                    }
                } else if(b_Index == D_INDEX){//C
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                        target.offset(0, addSize);
                    }
                }
            } else{//D_INDEX  //说明原来的A现在在C的位置， 就是右上角
                if(b_Index == B_INDEX){//因为A在右上则B只能在 B/D位置    B
                    if(hasAddPaddingX){
                    }
                    if(hasAddPaddingY){
                    }
                } else if(b_Index == C_INDEX){//D
                    if(hasAddPaddingX){
                        target.offset(addSize, 0);
                    }
                    if(hasAddPaddingY){
                        target.offset(0, addSize);
                    }
                }
            }
        }
    }


    public static float[] screenCoorToGLCoor(RectF rect, int width, int height) {
        float[] result = new float[8];
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.left, rect.top}, width, height, 0, false), 0, result, 0, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.right, rect.top}, width, height, 0, false), 0, result, 2, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.left, rect.bottom}, width, height, 0, false), 0, result, 4, 2);
        System.arraycopy(screenCoorToGLCoor(new float[]{rect.right, rect.bottom}, width, height, 0, false), 0, result, 6, 2);
        return result;
    }

    private static float[] screenCoorToGLCoor(float[] point, int width, int height, int orientation, boolean isBackCamera) {
        float x = 2.0F * point[0] / width - 1.0f; // (point[0] / width) * 2 - 1;
        float y = 2.0F * point[1] / height - 1.0F ; // 1 - (point[1] / height) * 2;

        if (isBackCamera) {
            x = -x;
        }

        float[] pointf;
        if (orientation == 1) {
            pointf = new float[]{-y, x};
        } else if (orientation == 2) {
            pointf = new float[]{y, -x};
        } else if (orientation == 3) {
            pointf = new float[]{-x, -y};
        } else {
            pointf = new float[] { x, y};
        }
        return pointf;
    }
}

