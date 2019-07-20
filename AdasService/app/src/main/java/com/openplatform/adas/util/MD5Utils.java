package com.openplatform.adas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.res.AssetManager;

public class MD5Utils {

	public static String generate(byte[] data) {
		byte[] md5 = getMD5(data);
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(36);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5sum(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest()).toLowerCase();
		} catch (Exception e) {
			System.out.println("error");
			return "";
		}
	}

	public static byte[] md5sumByteArrays(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return md5.digest();
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}

	public static String md5sumAssetFile(Context ctx, String assetFilePath) {
		AssetManager am = ctx.getAssets();
		InputStream inputStream = null;
		MessageDigest md5;
		try {
			byte buffer[] = new byte[1024];
			int length = 0;
			md5 = MessageDigest.getInstance("MD5");

			inputStream = am.open(assetFilePath);

			while ((length = inputStream.read(buffer)) > 0) {
				md5.update(buffer, 0, length);
			}

			inputStream.close();
			return toHexString(md5.digest()).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String md5sum(byte[] data) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(data, 0, data.length);
			return toHexString(md5.digest()).toLowerCase();
		} catch (Exception e) {
			System.out.println("error");
			return "";
		}
	}
	
	// public static void main(String[] args) {
	// InputStream fis;
	// byte[] buffer = new byte[1024];
	// int numRead = 0;
	// MessageDigest md5;
	// try {
	// fis = new FileInputStream("/data/opt/xgcom-0.04.2.tgz");
	// md5 = MessageDigest.getInstance("MD5");
	// while ((numRead = fis.read(buffer)) > 0) {
	// md5.update(buffer, 0, numRead);
	// }
	// fis.close();
	// long start = System.currentTimeMillis();
	// String temp = toHexString(md5.digest()).toLowerCase();
	// System.out.println(String.format("temp=%s,time=%d",
	// temp,(System.currentTimeMillis() - start)));
	// start = System.currentTimeMillis();
	// BigInteger bi = new BigInteger(md5.digest()).abs();
	// temp = bi.toString(36);
	// System.out.println(String.format("temp=%s,time=%d",
	// temp,(System.currentTimeMillis() - start)));
	//
	// } catch (Exception e) {
	// System.out.println("error:"+e.toString());
	// }
	// }

	public static boolean verifyInstallPackage(String packagePath, String crc) {
		try {
			MessageDigest sig = MessageDigest.getInstance("MD5");
			File packageFile = new File(packagePath);
			InputStream signedData = new FileInputStream(packageFile);
			byte[] buffer = new byte[4096];// 每次检验的文件区大小
			long toRead = packageFile.length();
			long soFar = 0;
			boolean interrupted = false;
			while (soFar < toRead) {
				interrupted = Thread.interrupted();
				if (interrupted)
					break;
				int read = signedData.read(buffer);
				soFar += read;
				sig.update(buffer, 0, read);
			}
			byte[] digest = sig.digest();
			String digestStr = bytesToHexString(digest);// 将得到的MD5值进行移位转换
			digestStr = digestStr.toLowerCase();
			crc = crc.toLowerCase();
			if (digestStr.equals(crc)) {// 比较两个文件的MD5值，如果一样则返回true
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		int i = 0;
		while (i < src.length) {
			int v;
			String hv;
			v = (src[i] >> 4) & 0x0F;
			hv = Integer.toHexString(v);
			stringBuilder.append(hv);
			v = src[i] & 0x0F;
			hv = Integer.toHexString(v);
			stringBuilder.append(hv);
			i++;
		}
		return stringBuilder.toString();
	}
}
