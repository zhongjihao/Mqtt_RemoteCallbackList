package com.openplatform.adas;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.openplatform.adas.util.FileUtil;

import java.lang.Thread.UncaughtExceptionHandler;

import darks.log.Logger;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/16 10:21
 * Description :
 */
public class AdasServiceApplication extends Application implements UncaughtExceptionHandler {
    private static final String TAG = "AdasServiceApplication";
    private UncaughtExceptionHandler sSystemUncaughtExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.Android.setApplication(this);
        Log.d(TAG,"E: onCreate");

        FactoryImpl.register(getApplicationContext(), this);

        sSystemUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        Log.d(TAG,"X: onCreate");
    }


    public void initializeSync(final Factory factory) {
        Log.d(TAG,"initializeSync");
    }

    public void initializeAsync(final Factory factory){
        Log.d(TAG,"initializeAsync");

    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        final boolean background = getMainLooper().getThread() != thread;
        if (!handleException(ex) && sSystemUncaughtExceptionHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            if (background) {
                Log.e(TAG, "Uncaught exception in background thread " + thread, ex);

                final Handler handler = new Handler(getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);
                    }
                });
            } else {
                sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            Log.e(TAG, "Exit App------>Uncaught exception in background thread " + thread, ex);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        Log.d(TAG, "E: handleException----->ex: "+ex.toString());
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(Factory.get().getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        FileUtil.collectDeviceInfo(Factory.get().getApplicationContext());
        //保存日志文件
        FileUtil.saveCrashInfo2File(ex);

        Log.d(TAG, "X: handleException----->ex: "+ex.toString());
        return true;
    }
}
