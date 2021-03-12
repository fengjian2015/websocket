package com.in.livechat.ui.album.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import com.in.livechat.socket.util.LogUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    private static final int FLAG_SUCCESS = 1;//创建成功
    private static final int FLAG_EXISTS = 2;//已存在
    private static final int FLAG_FAILED = 3;//创建失败
    //文件夹名
    private static final String DIR_NAME_MAIN = "LiveChat/";
    private static final String DIR_NAME_IMG = "images/";
    private static final String DIR_NAME_DOWNLOAD = "download/";
    private static final String DIR_NAME_TAPE_RECORD = "tapeRecord/";
    private static final String DIR_NAME_VIDEO = "video/";
    private static final String DIR_NAME_DOC = "doc/";
    private static final String DIR_NAME_DEBUG_LOG = "debugLog/";
    private static final String DIR_NAME_APK = "apk/";
    //声明各种类型文件的dataType
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";
    //未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    private static final String DATA_TYPE_ALL = "*/*";


    /**
     * 内部存储中的files路径
     */
    private static String getInnerFilePath(Context context) {
        String internalFilePath = context.getFilesDir().getAbsolutePath();
        if (!internalFilePath.endsWith(File.separator)) {
            return internalFilePath + File.separator;
        }
        return internalFilePath;
    }

    /**
     * 内部存储中的cache路径
     */
    private static String getInnerCachePath(Context context) {
        String innerCachePath = context.getCacheDir().getAbsolutePath();
        if (!innerCachePath.endsWith(File.separator)) {
            return innerCachePath + File.separator;
        }
        return innerCachePath;
    }

    /**
     * SD卡中的files路径,无SD卡则返回对应内部路径
     */
    public static String getSDFilePath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalFilesDir(DIR_NAME_MAIN);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(File.separator)) {
                    return filePath + File.separator;
                }
                return filePath;
            }
        }
        return getInnerFilePath(context);
    }

    /**
     * SD卡中的cache路径,无SD卡则返回对应内部路径
     */
    public static String getSDCachePath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(File.separator)) {
                    return filePath + File.separator;
                }
                return filePath;
            }
        }
        return getInnerCachePath(context);
    }

    /**
     * SD卡的根路径,无SD卡则返回对应内部路径
     */
    public static String getSDDirPath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!TextUtils.isEmpty(path)) {
                if (!path.endsWith(File.separator)) {
                    return path + File.separator + DIR_NAME_MAIN;
                }
                return path + DIR_NAME_MAIN;
            } else {
                File file = Environment.getExternalStoragePublicDirectory(DIR_NAME_MAIN);
                if (file != null) {
                    String externalPath = file.getAbsolutePath();
                    if (!externalPath.endsWith(File.separator)) {
                        return externalPath + File.separator + DIR_NAME_MAIN;
                    }
                    return externalPath + DIR_NAME_MAIN;
                }
            }
        }
        return getInnerFilePath(context);
    }

    /**
     * SD卡下载路径
     */
    public static String getSDDownloadPath(Context context) {
        return getSDDirPath(context) + DIR_NAME_DOWNLOAD;
    }



    /**
     * 删除指定目录下文件及目录
     */
    public static boolean deleteDir(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                // 处理目录
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        deleteDir(file1.getAbsolutePath());
                    }
                }
                // 如果是文件，删除
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    // 目录下没有文件或者目录，删除
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path)){
            return false;
        }
        try {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 创建 单个 文件
     *
     * @param filePath 待创建的文件路径
     * @return 结果码
     */
    public static int createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            LogUtil.i("The directory [ " + filePath + " ] has already exists");
            return FLAG_EXISTS;
        }
        if (filePath.endsWith(File.separator)) {// 以 路径分隔符 结束，说明是文件夹
            LogUtil.i("The file [ \" + filePath + \" ] can not be a directory");
            return FLAG_FAILED;
        }

        //判断父目录是否存在
        if (!file.getParentFile().exists()) {
            //父目录不存在 创建父目录
            LogUtil.i("creating parent directory...");
            if (!file.getParentFile().mkdirs()) {
                LogUtil.i("created parent directory failed...");
                return FLAG_FAILED;
            }
        }

        //创建目标文件
        try {
            if (file.createNewFile()) {//创建文件成功
                LogUtil.i("create file [ " + filePath + " ] success");
                return FLAG_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i("create file [ " + filePath + " ] failed");
            return FLAG_FAILED;
        }

        return FLAG_FAILED;
    }


    /**
     * 创建文件夹
     */
    public static void createDir(String dirPath) {
        File dir = new File(dirPath);
        //文件夹是否已经存在
        if (dir.exists()) {
            LogUtil.i("The directory [ " + dirPath + " ] has already exists");
        }
        //不是以路径分隔符"/"结束，则添加路径分隔符"/"
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }

        //判断父目录是否存在
        if (!dir.getParentFile().exists()) {
            //父目录不存在 创建父目录
            LogUtil.i("creating parent directory...");
            if (!dir.getParentFile().mkdirs()) {
                LogUtil.i("created parent directory failed...");
            }
        }
        //创建文件夹
        if (dir.mkdirs()) {
            LogUtil.i("create directory [ " + dirPath + " ] success");
        }
        LogUtil.i("create directory [ " + dirPath + " ] failed");
    }
    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

}
