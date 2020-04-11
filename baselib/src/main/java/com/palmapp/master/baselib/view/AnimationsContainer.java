package com.palmapp.master.baselib.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import com.palmapp.master.baselib.R;

import java.lang.ref.SoftReference;

public class AnimationsContainer {
    public int FPS = 30;  // animation FPS

    // single instance procedures
    private static AnimationsContainer mInstance;

    private AnimationsContainer() {
    }

    ;

    public static AnimationsContainer getInstance() {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        return mInstance;
    }


    /**
     * @param imageView
     * @return progress dialog animation
     */
    public FramesSequenceAnimation createAnim(ImageView imageView, int fps, int[] anims) {
        return new FramesSequenceAnimation(imageView, anims, fps);
    }

    /**
     * AnimationPlayer. Plays animation frames sequence in loop
     */
    public class FramesSequenceAnimation {
        private int[] mFrames; // animation frames
        private int mIndex; // current frame
        private boolean mPlayComplete;
        private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
        private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
        private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
        private Handler mHandler;
        private int mDelayMillis;

        private Bitmap mBitmap = null;
        private BitmapFactory.Options mBitmapOptions;
        private OnAnimationCallback mCallback;

        public void registerCallback(OnAnimationCallback callback) {
            mCallback = callback;
        }

        public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
            mHandler = new Handler();
            mFrames = frames;
            mIndex = -1;
            mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
            mShouldRun = false;
            mIsRunning = false;
            mDelayMillis = fps;

            imageView.setImageResource(mFrames[0]);

            // use in place bitmap to save GC work (when animation images are the same size & type)
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config = bmp.getConfig();
            mBitmap = Bitmap.createBitmap(width, height, config);
            mBitmapOptions = new BitmapFactory.Options();
            // setup bitmap reuse options.
            mBitmapOptions.inBitmap = mBitmap;
            mBitmapOptions.inMutable = true;
            mBitmapOptions.inSampleSize = 1;
            mBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }

        private int getNext() {
            mIndex++;
            if (mIndex >= mFrames.length) {
                mPlayComplete = true;
                mShouldRun = false;
                mIndex = mFrames.length - 1;
            }
            return mFrames[mIndex];
        }

        /**
         * Starts the animation
         */
        public synchronized void start() {
            mPlayComplete = false;
            mShouldRun = true;
            if (mIsRunning)
                return;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = mSoftReferenceImageView.get();
                    if (mPlayComplete) {
                        if (mCallback != null) {
                            mCallback.onAnimationComplete();
                            mCallback = null;
                        }
                    }
                    if (!mShouldRun || imageView == null) {
                        mIsRunning = false;
                        return;
                    }

                    mIsRunning = true;

                    if (imageView.isShown()) {
                        int imageRes = getNext();
                        if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    }
                    mHandler.postDelayed(this, mDelayMillis);
                }
            };

            mHandler.post(runnable);
        }

        /**
         * Stops the animation
         */
        public synchronized void stop() {
            mShouldRun = false;
        }
    }

    public interface OnAnimationCallback {

        void onAnimationComplete();
    }
}