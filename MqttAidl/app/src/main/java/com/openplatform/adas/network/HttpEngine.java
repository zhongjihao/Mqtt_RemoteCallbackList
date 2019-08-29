package com.openplatform.adas.network;

import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.util.FileUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/16 10:21
 * Description :
 */
public class HttpEngine implements IHttpEngine {
    private final static String TAG = "HttpEngine";

    public HttpEngine(){

    }

    @Override
    public void OnPostRequest(String postUrl,String token,String jsonRequest,ISuccessCallback iSuccessCallback,IFailCallback iFailCallback){
        HashMap<String, String> result = new HashMap<>();

        URL url = null;
        OutputStreamWriter writer = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(postUrl);
            Log.d(TAG,"OnPostRequest------>postUrl: "+postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept", "application/json");
            if(!TextUtils.isEmpty(token)){
                conn.setRequestProperty("token", token);
            }
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.connect();

            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonRequest);
            writer.flush();

            int responseCode = conn.getResponseCode();
            Log.d(TAG,"OnPostRequest-------->responseCode: "+responseCode);
            if(responseCode == 200){
                byte[] bytes = FileUtil.readInputStream(conn.getInputStream());
                result.put(KEY_BODY, new String(bytes,"utf-8"));
                if(iSuccessCallback != null){
                    iSuccessCallback.onSuccess(result);
                }
            }else{
                byte[] bytes = FileUtil.readInputStream(conn.getErrorStream());
                if(iFailCallback != null){
                    iFailCallback.onFail(responseCode,new String(bytes,"utf-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(iFailCallback != null){
                iFailCallback.onFail(-1,e.getMessage());
            }
        } finally{
            try {
                if(writer!=null){
                    writer.close();
                }
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnUploadFile(String postUrl, String token,String filePath, ISuccessCallback iSuccessCallback, IFailCallback iFailCallback){
        HashMap<String, String> result = new HashMap<>();

        URL url = null;
        OutputStream os = null;
        HttpURLConnection conn = null;
        FileInputStream file = null;
        DataOutputStream dos = null;
        try {
            String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; // 内容类型

            url = new URL(postUrl);
            Log.d(TAG,"OnUploadFile------>postUrl: "+postUrl);
            conn = (HttpURLConnection) url.openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP请求正文的流。
            conn.setChunkedStreamingMode(8*1024); //8K
            // 不使用缓存
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            // 设置编码格式
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if(!TextUtils.isEmpty(token)){
                conn.setRequestProperty("token", token);
            }

            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            //conn.setRequestProperty("Content-Length", "1024");
            conn.connect();

            // 上传文件
            file = new FileInputStream(filePath);
            os = conn.getOutputStream();

            dos = new DataOutputStream(os);
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);

            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            Log.d(TAG,"OnUploadFile------>fileName: "+fileName+"   filePath: "+filePath);
            sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + fileName + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset="
                    + "utf-8" + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());

            byte[] b = new byte[1024];
            int count = 0;
            while((count = file.read(b)) != -1){
                dos.write(b, 0, count);
            }
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
            dos.write(end_data);
            dos.flush();

            int responseCode = conn.getResponseCode();
            Log.d(TAG,"OnUploadFile-------->responseCode: "+responseCode);
            if(responseCode == 200){
                byte[] bytes = FileUtil.readInputStream(conn.getInputStream());
                result.put(KEY_BODY, new String(bytes,"utf-8"));
                if(iSuccessCallback != null){
                    iSuccessCallback.onSuccess(result);
                }
            }else{
                byte[] bytes = FileUtil.readInputStream(conn.getErrorStream());
                if(iFailCallback != null){
                    iFailCallback.onFail(responseCode,new String(bytes,"utf-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(iFailCallback != null){
                iFailCallback.onFail(-1,e.getMessage());
            }
        } finally{
            try {
                if(os!=null){
                    os.close();
                }
                if(dos != null){
                    dos.close();
                }
                if(file != null){
                    file.close();
                }
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnUploadFile(String postUrl, String token,String batchNum,String filePath, ISuccessCallback iSuccessCallback, IFailCallback iFailCallback){
        HashMap<String, String> result = new HashMap<>();

        URL url = null;
        OutputStream os = null;
        HttpURLConnection conn = null;
        FileInputStream file = null;
        DataOutputStream dos = null;
        try {
            String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; // 内容类型

            url = new URL(postUrl);
            Log.d(TAG,"OnUploadFile------>postUrl: "+postUrl);
            conn = (HttpURLConnection) url.openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP请求正文的流。
            conn.setChunkedStreamingMode(8*1024); //8K
            // 不使用缓存
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            // 设置编码格式
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if(!TextUtils.isEmpty(token)){
                conn.setRequestProperty("token", token);
            }
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            //conn.setRequestProperty("Content-Length", "1024");
            conn.connect();

            // 上传文件
            file = new FileInputStream(filePath);
            os = conn.getOutputStream();

            dos = new DataOutputStream(os);
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);

            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            Log.d(TAG,"OnUploadFile------>fileName: "+fileName+"   filePath: "+filePath);

            sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + fileName + "\"" + LINE_END);
            sb.append("Content-Type: image/jpeg;"+ LINE_END);

//          sb.append("Content-Type: application/octet-stream; charset="
//                    + "\"utf-8\"" + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());


            byte[] b = new byte[1024];
            int count = 0;
            while((count = file.read(b)) != -1){
                dos.write(b, 0, count);
            }
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + LINE_END)
                    .getBytes();
            dos.write(end_data);

            /**
             * 这里重点注意： name里面的值为服务器端需要key
             */
            Log.d(TAG,"OnUploadFile------>batch: "+batchNum);
            StringBuffer batchBuild = new StringBuffer();
            batchBuild.append("Content-Disposition: form-data; name=\"" + "batch" + "\"" + LINE_END);

            batchBuild.append(LINE_END);
            batchBuild.append(batchNum);
            batchBuild.append(LINE_END);

            batchBuild.append(PREFIX);
            batchBuild.append(BOUNDARY);
            batchBuild.append(PREFIX);
            batchBuild.append(LINE_END);
            dos.write(batchBuild.toString().getBytes());

            dos.flush();

            int responseCode = conn.getResponseCode();
            Log.d(TAG,"OnUploadFile-------->responseCode: "+responseCode);
            if(responseCode == 200){
                byte[] bytes = FileUtil.readInputStream(conn.getInputStream());
                result.put(KEY_BODY, new String(bytes,"utf-8"));
                if(iSuccessCallback != null){
                    iSuccessCallback.onSuccess(result);
                }
            }else{
                byte[] bytes = FileUtil.readInputStream(conn.getErrorStream());
                if(iFailCallback != null){
                    iFailCallback.onFail(responseCode,new String(bytes,"utf-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(iFailCallback != null){
                iFailCallback.onFail(-1,e.getMessage());
            }
        } finally{
            try {
                if(os!=null){
                    os.close();
                }
                if(dos != null){
                    dos.close();
                }
                if(file != null){
                    file.close();
                }
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnGetRequest(String urlStr,String token,String jsonRequest,ISuccessCallback iSuccessCallback,IFailCallback iFailCallback){
        HashMap<String, String> result = new HashMap<>();

        URL url = null;
        OutputStreamWriter writer = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlStr);
            Log.d(TAG,"OnGetRequest------>urlStr: "+urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept", "application/json");
            if(!TextUtils.isEmpty(token)){
                conn.setRequestProperty("token", token);
            }
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.connect();
            if(!TextUtils.isEmpty(jsonRequest)){
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonRequest);
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            Log.d(TAG,"OnGetRequest-------->responseCode: "+responseCode);
            if(responseCode == 200){
                byte[] bytes = FileUtil.readInputStream(conn.getInputStream());
                result.put(KEY_BODY, new String(bytes,"utf-8"));
                if(iSuccessCallback != null){
                    iSuccessCallback.onSuccess(result);
                }
            }else{
                byte[] bytes = FileUtil.readInputStream(conn.getErrorStream());
                if(iFailCallback != null){
                    iFailCallback.onFail(responseCode,new String(bytes,"utf-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(iFailCallback != null){
                iFailCallback.onFail(-1,e.getMessage());
            }
        } finally{
            try {
                if(writer!=null){
                    writer.close();
                }
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
