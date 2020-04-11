package com.palmapp.master.baselib.view;

import android.graphics.Bitmap;

public interface IPhotoEditView {
    void setSource(Bitmap source);
    void rotatePhoto();
    Bitmap getCropBitmap(boolean round);
    Bitmap getCropBitmap(boolean round, boolean isScale);
    void upDateCropDescription(CharSequence bottom);

}
