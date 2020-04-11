package com.palmapp.master.baselib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.cs.bd.commerce.util.DrawUtils;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.R;
import com.palmapp.master.baselib.utils.AppUtil;
import com.palmapp.master.baselib.utils.GlideUtil;
import com.palmapp.master.baselib.utils.LogUtil;


public class PhotoEditView extends View implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener, IPhotoEditView {
    private static final int DEFAULT_WIDTH = AppUtil.getScreenW(GoCommonEnv.INSTANCE.getApplication());
    private static final int DEFAULT_HEIGHT = AppUtil.getScreenH(GoCommonEnv.INSTANCE.getApplication());
    private static final float MAX_SCALE = 5.0F;
    private static final float DEFAULT_SCALE = 1.0F;
    private final float scale;
    private float mScaleFactor = DEFAULT_SCALE;
    private Paint mCropPaint;
    private Bitmap mSource;
    private Bitmap mSourceDefault;
    private RectF mDisplayRect;
    private RectF mDisplayRectTemp;
    private RectF mCropRect;
    private int mHeight, mWidth;
    private boolean mPendingInit;
    private Matrix mScaleMatrix;
    private StaticLayout mCropDescription;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private Paint mCirclePaint;
    private Bitmap mFaceMask;
    private Bitmap mFaceMaskLine;
    private boolean mIsDrawTextEnable = true;
    private boolean mIsChange = false;
    private Matrix matrix = new Matrix();

    private int borderRes;
    private int maskRes;

    private int mask_width;
    private int mask_height;

    public PhotoEditView(Context context) {
        this(context, null);
    }

    public PhotoEditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        mGestureDetector = new GestureDetector(getContext(), this);
        mCropPaint = new Paint();
        mCropPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCropPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.parseColor("#ccffffff"));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(GlideUtil.dip2px(getContext(), 2));
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PhotoEditView);

        mask_width = ta.getDimensionPixelSize(R.styleable.PhotoEditView_mask_width, 0);
        mask_height = ta.getDimensionPixelSize(R.styleable.PhotoEditView_mask_height, 0);

        borderRes = ta.getResourceId(R.styleable.PhotoEditView_PhotoEditView_border, -1);
        maskRes = ta.getResourceId(R.styleable.PhotoEditView_PhotoEditView_mask, -1);
        scale = ta.getFloat(R.styleable.PhotoEditView_mask_scale, 0.8f);
        ta.recycle();
        calcMask();
    }

    private void calcMask() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        Bitmap border = BitmapFactory.decodeResource(getResources(), borderRes, options);
        Bitmap mask = BitmapFactory.decodeResource(getResources(), maskRes, options);
        float w =  (AppUtil.getScreenW(GoCommonEnv.INSTANCE.getApplication()) * scale);

        if (mask_width > 0 && mask_height > 0) {
            border = Bitmap.createScaledBitmap(
                    border,
                    mask_width,
                    mask_height,
                    false
            );
            mask = Bitmap.createScaledBitmap(
                    mask,
                    mask_width,
                    mask_height,
                    false
            );
        } else {
            border = Bitmap.createScaledBitmap(
                    border,
                    (int) w,
                    (int) ((w / border.getWidth()) * border.getHeight()),
                    false
            );
            mask = Bitmap.createScaledBitmap(
                    mask,
                    (int) w,
                    (int) ((w / mask.getWidth()) * mask.getHeight()),
                    false
            );
        }
        mFaceMaskLine = border;
        mFaceMask = mask;
    }

    public void setMaskAndBorder(int mask, int border) {
        borderRes = border;
        maskRes = mask;
        calcMask();
        initDisplayRect();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, DEFAULT_HEIGHT);
        }
    }

    public void setChangeSide() {
        matrix.postScale(-1f, 1f);   //镜像水平翻转
        mFaceMask = Bitmap.createBitmap(mFaceMask, 0, 0, mFaceMask.getWidth(), mFaceMask.getHeight(), matrix, true);
        mFaceMaskLine = Bitmap.createBitmap(mFaceMaskLine, 0, 0, mFaceMaskLine.getWidth(), mFaceMaskLine.getHeight(), matrix, true);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSource != null && mDisplayRect != null) {
            canvas.drawBitmap(mSource, null, mDisplayRect, null);
        }
        if (mCropRect != null) {
            int saveLayer = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawColor(Color.parseColor("#80000000"));
//            canvas.drawCircle(mCropRect.centerX(), mCropRect.centerY(), mCropRect.width() / 2, mCropPaint);
            canvas.drawBitmap(mFaceMask, null, mCropRect, mCropPaint);
            canvas.restoreToCount(saveLayer);
//            canvas.drawCircle(mCropRect.centerX(), mCropRect.centerY(), mCropRect.width() / 2, mCirclePaint);
            canvas.drawBitmap(mFaceMaskLine, null, mCropRect, mCirclePaint);
        }
        if (mIsDrawTextEnable) {
            drawText(canvas);
        }
    }

    public void setDrawTextEnable(boolean enable) {
        mIsDrawTextEnable = enable;
        invalidate();
    }

    private void drawText(Canvas canvas) {
        StaticLayout textLayout = mCropDescription;
        if (textLayout != null && mCropRect != null) {
            canvas.save();
            canvas.translate((mWidth - textLayout.getWidth()) / 2, mCropRect.bottom + GlideUtil.dip2px(getContext(), 34));
            mCropDescription.draw(canvas);
            canvas.restore();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initDisplayRect();
    }


    private void initDisplayRect() {
        float height = mSource == null ? mHeight : mSource.getHeight();
        float width = mSource == null ? mWidth : mSource.getWidth();
        float mViewRadio = mHeight * 1.0f / mWidth;
        float mBitmapRadio = height * 1.0f / width;
        float rectWidth = mWidth;
        float rectHeight = mHeight;
        if (mViewRadio > mBitmapRadio) {
            rectHeight = rectWidth * mBitmapRadio;
        } else {
            rectWidth = rectHeight / mBitmapRadio;
        }
        float left = (mWidth - rectWidth) / 2;
        float top = (mHeight - rectHeight) / 2;
        mDisplayRect = new RectF(left, top, left + rectWidth, top + rectHeight);
//        int cropHeight = (mWidth / 2 - CameraView.FACE_CIRCLE_MARGIN_LEFT_RIGHT) * 2;
        int cropHeight = mFaceMask.getHeight();
        int cropWith = mFaceMask.getWidth();
//        int cropWith = DrawUtils.dip2px(258);
        left = (mWidth - cropWith) / 2;
        top = GlideUtil.dip2px(getContext(), 96);
        mCropRect = new RectF(left, top, left + cropWith, top + cropHeight);
        if (rectWidth < cropWith) {
            float dx = (rectWidth - cropWith) / 2;
            mDisplayRect.inset(dx, mBitmapRadio * dx);
        } else if (rectHeight < cropHeight) {
            float dy = (rectHeight - cropHeight) / 2;
            mDisplayRect.inset(dy / mBitmapRadio, dy);
        }
        if (mDisplayRect.left > mCropRect.left ||
                mDisplayRect.top > mCropRect.top ||
                mDisplayRect.right < mCropRect.right ||
                mDisplayRect.bottom < mCropRect.bottom) {
            mDisplayRect.offset(mCropRect.centerX() - mDisplayRect.centerX(), mCropRect.centerY() - mDisplayRect.centerY());
        }
        mPendingInit = false;
//        Log.d("xiaowu", "sourceRadio:" + mSource.getWidth() * 1.0f / mSource.getHeight());
        Log.d("xiaowu", "photoRadio:" + mDisplayRect.width() / mDisplayRect.height());
        invalidate();
    }

    boolean mHandleScale;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || count > 1) {
            mHandleScale = true;
        }
        if (!mHandleScale) {
            mGestureDetector.onTouchEvent(event);
        } else {
            mScaleGestureDetector.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mHandleScale = false;
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        mScaleFactor *= scaleFactor;
        if (mScaleFactor < MAX_SCALE && mDisplayRect != null) {
            if (mScaleMatrix == null) {
                mScaleMatrix = new Matrix();
            }
            if (mDisplayRectTemp == null) {
                mDisplayRectTemp = new RectF(mDisplayRect);
            } else {
                mDisplayRectTemp.set(mDisplayRect);
            }
            mScaleMatrix.reset();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            if (focusX < mDisplayRectTemp.left) {
                focusX = mDisplayRectTemp.left;
            }
            if (focusX > mDisplayRectTemp.right) {
                focusX = mDisplayRectTemp.right;
            }

            if (focusY < mDisplayRectTemp.top) {
                focusY = mDisplayRectTemp.top;
            }
            if (focusY > mDisplayRectTemp.bottom) {
                focusY = mDisplayRectTemp.bottom;
            }
            mScaleMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            mScaleMatrix.mapRect(mDisplayRectTemp);
            boolean left = mDisplayRectTemp.left < mCropRect.left;
            boolean right = mDisplayRectTemp.right > mCropRect.right;
            boolean top = mDisplayRectTemp.top < mCropRect.top;
            boolean bottom = mDisplayRectTemp.bottom > mCropRect.bottom;
            if (left && right && top && bottom) {
                mDisplayRect.set(mDisplayRectTemp);
            } else {
                if (mDisplayRectTemp.width() > mCropRect.width() && mDisplayRectTemp.height() > mCropRect.height()) {
                    boolean canTranLeft = false;
                    boolean canTranRight = false;
                    boolean canTranTop = false;
                    boolean canTranBottom = false;
                    boolean canTranX = true;
                    boolean canTranY = true;
                    float dx = mDisplayRectTemp.width() - mDisplayRect.width();
                    float dy = mDisplayRectTemp.height() - mDisplayRect.height();
                    if (!left) {
                        canTranRight = mDisplayRect.right + dx > mCropRect.right;
                    } else {
                        canTranLeft = mDisplayRect.left - dx < mCropRect.left;
                    }

                    if (!right) {
                        canTranLeft = mDisplayRect.left - dx < mCropRect.left;
                    } else {
                        canTranRight = mDisplayRect.right + dx > mCropRect.right;
                    }

                    canTranX = canTranRight || canTranLeft;
                    if (!top) {
                        canTranBottom = mDisplayRect.bottom + dy > mCropRect.bottom;
                    } else {
                        canTranTop = mDisplayRect.top - dy < mCropRect.top;
                    }

                    if (!bottom) {
                        canTranTop = mDisplayRect.top - dy < mCropRect.top;
                    } else {
                        canTranBottom = mDisplayRect.bottom + dy > mCropRect.bottom;
                    }
                    canTranY = canTranTop || canTranBottom;
                    if (canTranY) {
                        if (canTranRight && canTranLeft) {
                            mDisplayRect.left -= (dx / 2);
                            mDisplayRect.right += (dx / 2);
                        } else if (canTranRight) {
                            mDisplayRect.right += dx;
                        } else if (canTranLeft) {
                            mDisplayRect.left -= dx;
                        }

                    } else {
                        mScaleFactor = DEFAULT_SCALE;
                    }
                    if (canTranX) {
                        if (canTranBottom && canTranTop) {
                            mDisplayRect.top -= (dy / 2);
                            mDisplayRect.bottom += (dy / 2);
                        } else if (canTranBottom) {
                            mDisplayRect.bottom += dy;
                        } else if (canTranTop) {
                            mDisplayRect.top -= dy;
                        }
                    } else {
                        mScaleFactor = DEFAULT_SCALE;
                    }


                } else {
                    mScaleFactor = DEFAULT_SCALE;
                }
            }
            invalidate();
        } else {
            mScaleFactor = MAX_SCALE;
        }
        Log.d("xiaowu", "scaleFactory:" + mScaleFactor);
        return true;//true 消耗该次结果，下次结果以上次结果为参考，false不消耗该次结果，下次结果以最初结果为准
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mHandleScale && mDisplayRect != null) {
            float dx = distanceX;
            if (mDisplayRectTemp == null) {
                mDisplayRectTemp = new RectF(mDisplayRect);
            } else {
                mDisplayRectTemp.set(mDisplayRect);
            }
            float tranLeft = mDisplayRectTemp.left - distanceX - mCropRect.left;
            float tranRight = mDisplayRectTemp.right - distanceX - mCropRect.right;
            if (tranLeft > 0) {
                dx = distanceX + tranLeft;
            } else if (tranRight < 0) {
                dx = distanceX + tranRight;
            }
            float dy = distanceY;
            float tranTop = mDisplayRectTemp.top - distanceY - mCropRect.top;
            float tranBottom = mDisplayRectTemp.bottom - distanceY - mCropRect.bottom;
            if (tranTop > 0) {
                dy = distanceY + tranTop;
            } else if (tranBottom < 0) {
                dy = distanceY + tranBottom;
            }
            mDisplayRectTemp.offset(-dx, -dy);
            mDisplayRect.set(mDisplayRectTemp);
            invalidate();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    Canvas mCanvasTemp = new Canvas();
    Matrix mMatrixTemp = new Matrix();
    Paint mPaintTemp = new Paint(Paint.ANTI_ALIAS_FLAG);


    @Override
    public void setSource(Bitmap source) {
        mSourceDefault = source;
        mSource = mSourceDefault;
        if (mHeight == 0 || mWidth == 0) {
            mPendingInit = true;
        } else {
            initDisplayRect();
        }
    }

    public void clearSource() {
        mSource = null;
        initDisplayRect();
    }

    @Override
    public void rotatePhoto() {
    }


    @Override
    public Bitmap getCropBitmap(boolean round) {
        if (mSource == null) {
            return null;
        }
        Log.d("xiaowu", "sourceRadio:" + mSource.getWidth() * 1.0f / mSource.getHeight());
        Log.d("xiaowu", "photoRadio:" + mDisplayRect.width() / mDisplayRect.height());
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawColor(Color.TRANSPARENT);
        paint.setColor(Color.WHITE);
        if (!round) {
            canvas.drawRect(mCropRect, paint);
        } else {
            canvas.drawCircle(mCropRect.centerX(), mCropRect.centerY(), mCropRect.width() / 2, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mSource, null, mDisplayRect, paint);
        Rect temp = new Rect((int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right,
                (int) mCropRect.bottom);
        float maxTop = Math.max(0, mDisplayRect.top);
        if (temp.top < maxTop) {
            temp.top = (int) maxTop;
        }
        float maxLeft = Math.max(0, mDisplayRect.left);
        if (temp.left < maxLeft) {
            temp.left = (int) maxLeft;
        }
        float minRight = Math.min(bitmap.getWidth(), mDisplayRect.right);
        if (temp.left + temp.width() > minRight) {
            temp.right -= (temp.left + temp.width() - minRight);
        }
        float minBottom = Math.min(bitmap.getHeight(), mDisplayRect.bottom);
        if (temp.top + temp.height() > minBottom) {
            temp.bottom -= (temp.top + temp.height() - minBottom);
        }
        return Bitmap.createBitmap(bitmap, temp.left, temp.top, temp.width(), temp.height());
    }

    @Override
    public Bitmap getCropBitmap(boolean round, boolean isScale) {
        if (mSource == null) {
            return null;
        }
        Log.d("xiaowu", "sourceRadio:" + mSource.getWidth() * 1.0f / mSource.getHeight());
        Log.d("xiaowu", "photoRadio:" + mDisplayRect.width() / mDisplayRect.height());
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawColor(Color.TRANSPARENT);
        paint.setColor(Color.WHITE);
        if (!round) {
            canvas.drawRect(mCropRect, paint);
        } else {
            canvas.drawCircle(mCropRect.centerX(), mCropRect.centerY(), mCropRect.width() / 2, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mSource, null, mDisplayRect, paint);
        Rect temp = new Rect((int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right,
                (int) mCropRect.bottom);
//        temp.inset(0, -temp.height() / 10);
        float maxTop = Math.max(0, mDisplayRect.top);
        if (temp.top < maxTop) {
            temp.top = (int) maxTop;
        }
        float maxLeft = Math.max(0, mDisplayRect.left);
        if (temp.left < maxLeft) {
            temp.left = (int) maxLeft;
        }
        float minRight = Math.min(bitmap.getWidth(), mDisplayRect.right);
        if (temp.left + temp.width() > minRight) {
            temp.right -= (temp.left + temp.width() - minRight);
        }
        float minBottom = Math.min(bitmap.getHeight(), mDisplayRect.bottom);
        if (temp.top + temp.height() > minBottom) {
            temp.bottom -= (temp.top + temp.height() - minBottom);
        }
        Log.d("xiaowu", "sourceRadio:" + mSource.getWidth() * 1.0f / mSource.getHeight());
        return Bitmap.createBitmap(bitmap, temp.left, temp.top, temp.width(), temp.height());
    }

    @Override
    public void upDateCropDescription(CharSequence bottom) {
        if (!TextUtils.isEmpty(bottom)) {
            TextPaint tp = new TextPaint();
            tp.setColor(Color.WHITE);
            tp.setStyle(Paint.Style.FILL);
            tp.setTypeface(Typeface.SANS_SERIF);
            tp.setFakeBoldText(true);
            tp.setTextSize(DrawUtils.sp2px(16));
            tp.setAntiAlias(true);
            mCropDescription = new StaticLayout(bottom, tp, AppUtil.getScreenW(GoCommonEnv.INSTANCE.getApplication()) - GlideUtil.dip2px(getContext(), 60), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        } else {
            mCropDescription = null;
        }

    }

    /* public Bitmap getCropBitmap() {
        if (mSource == null) {
            return null;
        }
        float scaleX = mSource.getWidth() / mDisplayRect.width();
        float scaleY = mSource.getHeight() / mDisplayRect.height();
        int x = (int) ((mCropRect.left - mDisplayRect.left) * scaleX);
        int y = (int) ((mCropRect.top - mDisplayRect.top) * scaleY);
        int width = (int) (mCropRect.width() * scaleX);
        int height = (int) (mCropRect.height() * scaleY);
        Bitmap src = Bitmap.createBitmap(mSource, x, y, width, height);
        return Bitmap.createScaledBitmap(
                src,
                (int) mCropRect.width(),
                (int) mCropRect.height(),
                true);
    }*/
}
