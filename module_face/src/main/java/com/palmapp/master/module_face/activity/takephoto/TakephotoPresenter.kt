package com.palmapp.master.module_face.activity.takephoto;

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.cameraview.CameraView
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_face.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.*


/**
 * Created by zhangliang on 15-8-19.
 * 变老拍照页面
 */
class TakephotoPresenter : BasePresenter<TakephotoView>() {
    private val TAG = "TakephotoPresenter"
    val REQUEST_CAMERA_PERMISSION   = 1
    val REQUEST_MEDIA_PERMISSION    = 2
    var fileUri:String? = ""

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    fun scaleBitmp(context:Context?, cameraView: CameraView, data: ByteArray) {

        Observable.create<Boolean>{

            var bitmap: Bitmap? = null
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            } catch (e: OutOfMemoryError) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 2
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)
            }

            var finalBitmap: Bitmap? = null
            bitmap?.let {
                if (it.width > it.height) {
                    // 缩放
                    val aspectRadio = it.height * 1.0f / it.width
                    val screenH = AppUtil.getScreenH(context)
                    val scaledBitmap = ThumbnailUtils.extractThumbnail(bitmap, screenH, (aspectRadio * screenH).toInt())

                    // 旋转镜像
                    val matrix = Matrix()

                    matrix.setRotate(if (cameraView?.facing == CameraView.FACING_FRONT) 270f else 90f, (scaledBitmap.width shr 1).toFloat(), (scaledBitmap.height shl 1).toFloat())

                    if (cameraView?.facing == CameraView.FACING_FRONT) {
                        matrix.postScale(-1f, 1f)
                    }

                    finalBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true)

                    if (finalBitmap !== scaledBitmap) {
                        scaledBitmap.recycle()
                    }
                } else {
                    if (cameraView?.facing == CameraView.FACING_FRONT) {

                        val radio = bitmap.getHeight() * 1.0f / bitmap.getWidth()
                        val scaledBitmap = ThumbnailUtils.extractThumbnail(bitmap, AppUtil.getScreenW(context), (AppUtil.getScreenW(context) * radio).toInt())
                        val matrix = Matrix()
                        matrix.postScale(-1f, 1f)   //镜像水平翻转
                        finalBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true)
                        if (finalBitmap != scaledBitmap) {
                            scaledBitmap.recycle()
                        }
                    } else {
                        val radio = bitmap.getHeight() * 1.0f / bitmap.getWidth()
                        val screenWidth = AppUtil.getScreenW(context)
                        finalBitmap = ThumbnailUtils.extractThumbnail(bitmap, AppUtil.getScreenW(context), (screenWidth * radio).toInt())
                        if (finalBitmap !==bitmap) {
                            bitmap.recycle()
                        }
                    }
                }
            }

            try {
                val baos = ByteArrayOutputStream()
                finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                outputBitmap(cameraView.context, baos.toByteArray())
            } catch (e:Exception) {

            }
            it.onComplete()

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Boolean) {

                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
                    getView()?.stopCamera()
                    getView()?.startTakephotoedFragment()
                }
            })
    }

    // 输出图片, 该方法请放入子线程内执行
    private fun outputBitmap(context:Context, data: ByteArray) {
        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture")
        var os: OutputStream? = null
        try {
            os = FileOutputStream(file)
            os!!.write(data)
            os.close()
        } catch (e: IOException) {
            Log.w(TAG, "Cannot write to $file", e)
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                // Ignore
            }
        }
        fileUri = file?.absolutePath
    }

    fun checkPermission(context:Context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            getView()?.isCameraPermissionGranted = true
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getView()?.isStroagePermissionGranted = true
        }
    }

    // 处理相机权限
    fun permissionCamera(context:Context) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                getView()?.startCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA) -> {
                //            if (Build.MANUFACTURER == "samsung"){
                //                LogUtil.d(TAG,"三星手机权限")
                //            }
                getView()?.showFragmentDialog(Manifest.permission.CAMERA)
            }
            else -> {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf<String>(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    // 处理相册权限
    fun permissionMedia(context:Context) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                getView()?.startPicture()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                getView()?.showFragmentDialog(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_MEDIA_PERMISSION
                )
            }
        }
    }

    // 处理权限回调
    fun permissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_MEDIA_PERMISSION -> {
                if (permissions.size != 2 || grantResults.size != 2) {
                    getView()?.permissionDenide()
                    Toast.makeText(getView()?.getContext() as Activity, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT).show()
                }
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.storage_a000,"","2")
                        getView()?.permissionDenide()
                        Toast.makeText(getView()?.getContext() as Activity, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT).show()
                    } else {
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.storage_a000,"","1")
                        getView()?.isStroagePermissionGranted = true
                        getView()?.startPicture()
                    }
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (permissions.size != 1 || grantResults.size != 1) {
                    Toast.makeText(getView()?.getContext() as Activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show()
                }
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_a000,"","2")
                        getView()?.permissionDenide()
                        Toast.makeText(getView()?.getContext() as Activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show()
                    }else{
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_a000,"","1")
                        getView()?.isCameraPermissionGranted = true
                        getView()?.startCamera()
                    }
                }
            }

        } // No need to start camera here; it is handled by onResume
    }


    fun pictureResult(dataIntent: Intent?, context:Context) {
        dataIntent?.let {
            fileUri = getRealPathFromUri(context, it.getData());
            getView()?.startTakephotoedFragment()
        }
    }


    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUri(context:Context, uri: Uri):String? {
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
    private fun getRealPathFromUriBelowAPI19(context:Context, uri:Uri):String? {
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
    private fun getRealPathFromUriAboveApi19(context:Context, uri:Uri):String? {
        var filePath:String? = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) {
                // 使用':'分割
                val id = documentId.split(":")[1]

                val selection = MediaStore.Images.Media._ID + "=?";
                val selectionArgs:Array<String> = arrayOf(id)
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                val contentUri:Uri  = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), documentId.toLong());
                filePath = getDataColumn(context, contentUri, null, null);
            } else if (isExternalStorageDocument(uri)) {
                val id = documentId.split(":")[0]
                if ("primary".equals(id)) {
                    val sp = documentId.split(":")[1]
                    return Environment.getExternalStorageDirectory().toString() + "/" + sp
                }
            }
        }  else if ("content".equals(uri.getScheme())) {
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
    private fun getImageUrlWithAuthority(context:Context, uri:Uri):Uri? {
        var inputStream:InputStream? = null
        if (uri.getAuthority() != null) {
            try {
                LogUtil.d(uri.toString())
                inputStream = context.getContentResolver().openInputStream(uri)
                val bmp:Bitmap = BitmapFactory.decodeStream(inputStream)
                val bitmapUri = writeToTempImageAndGetPathUri(context, bmp)
                bmp?.let {
                    try {
                        if (!it.isRecycled) {
                            it.recycle()
                        }
                    } catch (e:java.lang.Exception) {

                    }
                }
                return bitmapUri
            } catch (e:FileNotFoundException) {
                e.printStackTrace()
            }finally {
                try {
                    inputStream?.let {
                        it.close()
                    }
                } catch (e:IOException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }
    /**
     * 将图片流读取出来保存到手机本地相册中
     **/
    private fun writeToTempImageAndGetPathUri(context: Context, inImage:Bitmap):Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "" + System.currentTimeMillis(), null)
        return Uri.parse(if (path == null) "" else path)
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     */
    private fun getDataColumn(context:Context, uri:Uri, selection:String?, selectionArgs:Array<String>? ):String? {
        var path:String? = null

        val projection:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e:Exception) {
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
    private fun isMediaDocument(uri:Uri):Boolean{
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri:Uri):Boolean {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority())
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri:Uri):Boolean {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
     **/
    private fun isGooglePhotosUri(uri:Uri):Boolean {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.contentprovider/0/1/mediakey:/local%3A821abd2f-9f8c-4931-bbe9-a975d1f5fabc/ORIGINAL/NONE/1075342619
     **/
    private fun isGooglePlayPhotosUri(uri:Uri):Boolean {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

}
