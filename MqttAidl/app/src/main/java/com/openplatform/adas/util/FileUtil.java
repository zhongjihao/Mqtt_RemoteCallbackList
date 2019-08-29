package com.openplatform.adas.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/10/31 10:21
 * Description :
 */
public class FileUtil {
    private final static String TAG = "FileUtil";
    public static final String ExternalStorage = "/storage/sdcard1";
    public static final String InternalStorage = "/storage/sdcard0";


    //用来存储设备信息和异常信息
    private static Map<String, String> infos = new HashMap<>();

    //用于格式化日期,作为日志文件名的一部分
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public enum Storage{
        EXTERNAL("/storage/sdcard1"),INTERNAL("/storage/sdcard0");
        private String value;
        private Storage(String value){
            this.value=value;
        }
        public String getValue(){
            return value;
        }
    }

    /**
     * 判断SD卡是否被挂载
     * @param sdcardPath
     * @return
     */
    public static boolean isMount(String sdcardPath) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(new File(sdcardPath)));
    }

    /**
     * 获取SD卡的可用字节数
     * @param sdcardPath
     * @return
     */
    public static long getSDCardAvailableSize(String sdcardPath) {
        if (isMount(sdcardPath)) {
            StatFs fs = new StatFs(sdcardPath);
            return fs.getAvailableBytes();
        }
        return 0;
    }

    /**
     * 获取指定文件夹大小
     * @param f
     * @return
     */
    public static long getDirSize(File f){
        if (f == null || !f.exists() || !f.isDirectory())
            return 0;
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            size += flist[i].length();
        }
        return size;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    public static boolean deleteFileSafely(File file){
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            Log.w("deleteFileSafely", String.format("deleteFileSafely %s", file.getAbsolutePath()));
            return tmp.delete();
        }
        return false;
    }

    public static byte[] readFileData(String path){
        byte[] buffer = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(path);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fin != null){
                try {
                    fin.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public static byte[] readStream(String fileName) {
        byte[] buffer = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        byte[] outBuffer = null;
        int len = 0;

        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        outBuffer = outStream.toByteArray();
        inStream.close();
        outStream.close();
        return outBuffer;
    }

    public static File writeFileData(String filename, byte[] content) {
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(filename);
            fos = new FileOutputStream(file);
            fos.write(content);//将byte数组写入文件
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();//关闭文件输出流
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    public static void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return	返回文件名称,便于将文件传送到服务器
     */
    public static String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()+"/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * 格式化储存卡
     */
    public static void formatSdcard(Context context,FileUtil.Storage storage) { // format SDcard
        if(storage == null) return;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {

            switch (storage){
                case INTERNAL:
                    Log.d(TAG,"format sdcard 0");
                    ShellUtils.execCommand("/system/xbin/umount -f -l /dev/block/vold/179:1",true);
                    SystemClock.sleep(1500);
                    ShellUtils.execCommand("newfs_msdos -F 32 /dev/block/mmcblk0p1",true);
                    SystemClock.sleep(1500);
                    ShellUtils.execCommand("/system/xbin/mount -t vfat -o rw /dev/block/mmcblk0p1 /storage/sdcard0",true);
                    SystemClock.sleep(1500);
                    ShellUtils.execCommand("reboot",true);
                    break;
                case EXTERNAL:
                    Log.d(TAG,"format sdcard 1 start");
                    final String formatPath = storage.getValue();
                    int formatIndex = -1;
                    Method getVolumePathsMethod = StorageManager.class.getDeclaredMethod("getVolumePaths");
                    getVolumePathsMethod.setAccessible(true);
                    String[] volumePaths = (String[]) getVolumePathsMethod.invoke(storageManager);
                    if(volumePaths!=null){
                        for (int i = 0; i < volumePaths.length; i++) {
                            if(formatPath.equals(volumePaths[i])){
                                formatIndex = i;
                                break;
                            }
                        }
                    }

                    Method getVolumeListMethod = StorageManager.class.getDeclaredMethod("getVolumeList");
                    getVolumeListMethod.setAccessible(true);
                    Parcelable[] volumeObj = (Parcelable[]) getVolumeListMethod.invoke(storageManager);

                    if(volumeObj!=null && volumeObj.length>formatIndex && formatIndex>=0){
                        Intent intent = new Intent("com.android.internal.os.storage.FORMAT_ONLY");
                        intent.setComponent(new ComponentName("android", "com.android.internal.os.storage.ExternalStorageFormatter"));
                        intent.putExtra("storage_volume", volumeObj[formatIndex]);
                        context.startService(intent);
                    }
                    Log.d(TAG,"format sdcard 1 end");
                    break;
            }
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
