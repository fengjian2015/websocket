package com.in.livechat.ui.common.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.in.livechat.ui.R;
import com.in.livechat.ui.album.permission.Permission;
import com.in.livechat.ui.album.permission.PermissionCallback;
import com.in.livechat.ui.album.permission.Rigger;
import com.in.livechat.ui.album.utils.FileUtil;
import com.in.livechat.ui.chat.util.TimeUtil;
import com.in.livechat.ui.common.Chat;
import com.in.livechat.ui.common.callback.LubanCallBack;

import java.io.File;
import java.util.HashMap;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;

import static com.in.livechat.ui.album.utils.AlbumCons.IMG_CROP_SIZE_1;
import static com.in.livechat.ui.album.utils.AlbumCons.IMG_CROP_SIZE_2;
import static com.in.livechat.ui.album.utils.AlbumCons.REQUEST_CAMERA;
import static com.in.livechat.ui.album.utils.AlbumCons.REQUEST_CROP;

/**
 * description ： TODO:类的作用
 * author : Fickle
 * date : 2021/3/10 12:25
 */
public class ImgUtil {
    public static String cameraOutputPath;
    public static String cropOutputPath;

    public static void load(Context context, String imgUrl, ImageView imageView) {
        Glide.with(context).load(imgUrl).apply(getRequestOptions(R.drawable.live_img_avatar_default)).into(imageView);
    }

    public static void load(Context context, int resourceId, ImageView imageView) {
        load(context, resourceId, imageView, 0);
    }

    public static void loadEmoticon(Context context, int resourceId, ImageView imageView) {
        load(context, resourceId, imageView, R.drawable.live_emoticon_00);
    }

    public static void load(Context context, Integer resourceId, ImageView imageView, int defaultId) {
        Glide.with(context).load(resourceId).apply(getRequestOptions(defaultId)).into(imageView);
    }

    public static void load(Context context, String imgUrl, ImageView imageView, int defaultId) {
        Glide.with(context).load(imgUrl).apply(getRequestOptions(defaultId)).into(imageView);
    }

    //圆形
    public static void loadCircle(Context context, String imgUrl, ImageView imageView) {
        Glide.with(context).load(imgUrl)
                .apply(circleOptions)
                .into(imageView);
    }


    private static RequestOptions circleOptions = new RequestOptions()
            .placeholder(R.drawable.live_img_avatar_default)
            .error(R.drawable.live_img_avatar_default)
            .circleCrop()
            .fallback(R.drawable.live_img_avatar_default);

    private static RequestOptions getRequestOptions(int defaultId) {
        return new RequestOptions()
                .placeholder(defaultId)
                .error(defaultId)
                .centerCrop()
                .fallback(defaultId);
    }


    /**
     * 打开相册
     * @param activity
     */
    public static void openCamera(final Activity activity) {
        Rigger.on(activity).permissions(Permission.CAMERA)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        takePhoto(activity);
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });
    }

    /**
     * 拍照
     */
    private static void takePhoto(Activity activity) {
        try {
            FileUtil.createDir(FileUtil.getSDCachePath(activity));
            cameraOutputPath = FileUtil.getSDCachePath(activity)
                    + TimeUtil.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg";
            File file = new File(cameraOutputPath);
            Uri imgUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imgUri = Uri.fromFile(file);
            } else {
                imgUri = FileProvider.getUriForFile(activity,
                        Chat.getFileProvider(), file);
            }
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(intent, REQUEST_CAMERA);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compress(Context context, String filePath, LubanCallBack lubanCallBack) {
        try {
            Luban.with(context)
                    .load(filePath)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(lubanCallBack)
                    .launch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 剪裁图片
     */
    public static void cropPic(Activity activity, String inputPath, int type) {
        try {
            Uri inputUri;
            Uri outputUri;
            FileUtil.createDir(FileUtil.getSDCachePath(activity));
            cropOutputPath = FileUtil.getSDCachePath(activity)
                    + TimeUtil.getCurrentTime("yyyyMMdd_HHmmss") + ".jpg";
            FileUtil.createFile(cropOutputPath);
            Intent intent = new Intent("com.android.camera.action.CROP");

            if (Build.VERSION.SDK_INT >= 24) {
                inputUri = FileProvider.getUriForFile(activity,
                        Chat.getFileProvider(), new File(inputPath));
                outputUri = FileProvider.getUriForFile(activity,
                        Chat.getFileProvider(), new File(cropOutputPath));
                //开启临时权限
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //重点:针对7.0以上的操作
                intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, outputUri));
            } else {
                inputUri = Uri.fromFile(new File(inputPath));
                outputUri = Uri.fromFile(new File(cropOutputPath));
            }
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("crop", "true");
            // 裁剪框的比例（根据需要显示的图片比例进行设置）
            if (type == IMG_CROP_SIZE_1) {
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 2);
                // 裁剪后图片的宽高（注意和上面的裁剪比例保持一致)
                intent.putExtra("outputX", 600);
                intent.putExtra("outputY", 600);
            } else if (type == IMG_CROP_SIZE_2) {
                intent.putExtra("aspectX", 26);
                intent.putExtra("aspectY", 15);
                intent.putExtra("outputX", 1040);
                intent.putExtra("outputY", 600);
            } else {
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 500);
                intent.putExtra("outputY", 500);
            }
            activity.startActivityForResult(intent, REQUEST_CROP);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
