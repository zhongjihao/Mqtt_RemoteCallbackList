package com.openplatform.adas.util;

import java.io.IOException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;


public class NetUtil {
	private static final String TAG = "NetUtil";
	public static final int NETWORN_NONE = 0;
	public static final int NETWORN_WIFI = 1;
	public static final int NETWORN_MOBILE = 2;

	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_WIFI;
		}

		// 3G
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_MOBILE;
		}
		return NETWORN_NONE;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isNetworkMobileAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getType() == ConnectivityManager.TYPE_MOBILE && networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/** 
	 * 返回手机移动数据的状态 
	 * 
	 * @param pContext 
	 * @param arg 
	 *            默认填null 
	 * @return true 连接 false 未连接 
	 */  
	public static boolean getMobileDataState(Context pContext, Object[] arg) {  
	  
	    try {  
	  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	  
	        Class ownerClass = mConnectivityManager.getClass();  
	  
	        Class[] argsClass = null;  
	        if (arg != null) {  
	            argsClass = new Class[1];  
	            argsClass[0] = arg.getClass();  
	        }  
	  
	        Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
	  
	        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);  
	  
	        return isOpen;  
	  
	    } catch (Exception e) {  
	    	e.printStackTrace();
	        return false;  
	    }  
	  
	}
	
	public static void enableMobileNetwork(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ShellUtils.execCommand("svc data enable", true);
			}
		}).start();
	}
	
	public static boolean pingHost(String str) {
		try {
			Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + str);
			int status = p.waitFor();
			if (status == 0) {
				return true;
			}
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
		return false;
	}
}
