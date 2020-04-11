package com.google.android.cameraview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.manager.PERMISSION_WRITE
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_imageloader.DataHolder
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageConfigImpl
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/12
 */
class AlbumPresenter(private val activity: Activity, private val permission: PermissionPresenter) : BaseMultiplePresenter<IPhotoSelectorView>(), IPermissionView {
    private val REQUEST_CODE_ALBUM: Int = 100

    override fun onAttach(pView: IPhotoSelectorView, context: Context) {
        super.onAttach(pView, context)
        permission.registerCallback(PERMISSION_WRITE, this)
    }

    fun choosePhoto() {
        if (permission.checkPermission(PERMISSION_WRITE)) {
            val intentToPickPic = Intent(Intent.ACTION_PICK, null)
            intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            activity.startActivityForResult(intentToPickPic, REQUEST_CODE_ALBUM)
        } else {
            permission.requestPermission(PERMISSION_WRITE)
        }
    }

    fun cancelPicture() {
        getView()?.showNormalView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM) {
            val path = getPhotoPath(data, activity)
            path?.let {
                val h = AppUtil.getScreenH(activity)
                val w = AppUtil.getScreenW(activity)
                val config = ImageConfigImpl.builder().data(DataHolder.create(it)).skipMemoryCache(true).overrideSize(w, h).cacheStrategy(1)
                    .imageLoadingListener(object : ImageLoadingListener() {
                        override fun onLoadingComplete(bitmap: Bitmap?) {
                            super.onLoadingComplete(bitmap)
                            if (bitmap != null) {
                                getView()?.showPictureView(bitmap)
                            }
                        }
                    })
                    .build()
                ImageLoader.getInstance().loadImage(activity, config)

            }
        }
    }

    override fun onPermissionGranted(code: Int) {
        choosePhoto()
    }

    override fun onPermissionDenied(code: Int) {

    }

    override fun onPermissionEveryDenied(code: Int) {
        permission.showEveryDeniedPermissionDialog(PERMISSION_WRITE)
    }


    private fun getPhotoPath(dataIntent: Intent?, context: Context): String? {
        dataIntent?.let {
            return getRealPathFromUri(context, it.data)
        }
        return null
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= 19) {
            return getRealPathFromUriAboveApi19(context, uri)
        } else {
            return getRealPathFromUriBelowAPI19(context, uri)
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) {
                // 使用':'分割
                val id = documentId.split(":")[1]

                val selection = MediaStore.Images.Media._ID + "=?";
                val selectionArgs: Array<String> = arrayOf(id)
                filePath = getDataColumn(
                    context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                );
            } else if (isDownloadsDocument(uri)) {
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    documentId.toLong()
                );
                filePath = getDataColumn(context, contentUri, null, null);
            } else if (isExternalStorageDocument(uri)) {
                val id = documentId.split(":")[0]
                if ("primary".equals(id)) {
                    val sp = documentId.split(":")[1]
                    return Environment.getExternalStorageDirectory().toString() + "/" + sp
                }
            }
        } else if ("content".equals(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            if (isGooglePhotosUri(uri)) {//判断是否是google相册图片
                filePath = uri.getLastPathSegment()
            } else if (isGooglePlayPhotosUri(uri)) {//判断是否是Google相册图片
                val googleUri = getImageUrlWithAuthority(context, uri)
                googleUri?.let {
                    filePath = getDataColumn(context, it, null, null)
                }
            } else {//其他类似于media这样的图片，和android4.4以下获取图片path方法类似
                filePath = getDataColumn(context, uri, null, null)
            }

        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath
    }


    /**
     * Google相册图片获取路径
     **/
    private fun getImageUrlWithAuthority(context: Context, uri: Uri): Uri? {
        var inputStream: InputStream? = null
        if (uri.getAuthority() != null) {
            try {
                LogUtil.d(uri.toString())
                inputStream = context.getContentResolver().openInputStream(uri)
                val bmp: Bitmap = BitmapFactory.decodeStream(inputStream)
                val bitmapUri = writeToTempImageAndGetPathUri(context, bmp)
                bmp?.let {
                    try {
                        if (!it.isRecycled) {
                            it.recycle()
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                return bitmapUri
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream?.let {
                        it.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }

    /**
     * 将图片流读取出来保存到手机本地相册中
     **/
    private fun writeToTempImageAndGetPathUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            "" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(if (path == null) "" else path)
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null

        val projection: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor =
                context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority())
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
     **/
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.contentprovider/0/1/mediakey:/local%3A821abd2f-9f8c-4931-bbe9-a975d1f5fabc/ORIGINAL/NONE/1075342619
     **/
    private fun isGooglePlayPhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
}