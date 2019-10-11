package com.openplatform.adas.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.datamodel.SmsMessege;
import com.openplatform.adas.interfacemanager.IOnCmdMessageProc;
import com.openplatform.adas.manager.NotifyManager;
import com.openplatform.aidl.CmdMesage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/25 10:21
 * Description :
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSBroadcastReceiver";


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "recv sms");
        SmsMessage msg = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg = SmsMessage.createFromPdu((byte[]) p);

                Date date = new Date(msg.getTimestampMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);

                final String senderNumber = msg.getOriginatingAddress();
                final String body = msg.getMessageBody();

                final String[] msgTxt = new String[]{body};

                Log.d(TAG, "from:" + senderNumber + " \n content:" + body + "\n receverTime:"
                        + receiveTime);

                new SmsMessege(new IOnCmdMessageProc() {
                    @Override
                    public void onSmsMessageProc(List<CmdMesage> list) {
                        NotifyManager.getInstance().OnSmsCmdNotify(senderNumber,list);
                    }
                }, msgTxt);
            }
        }
    }

    public static void sendMessage(String content, String phone) {
        Log.d(TAG, "messege send :" + content+"  to "+phone);
        try {
            if (TextUtils.isEmpty(content)) {
                Log.e(TAG, "messege null------------------");
                return;
            }

            SmsManager smsManager = SmsManager.getDefault();
            if (content.length() > 160) {
                ArrayList<String> phoneList = smsManager.divideMessage(content);
                smsManager.sendMultipartTextMessage(phone, null, phoneList, null, null);
            } else {
                Log.d(TAG,"sendTextMessage");
                smsManager.sendTextMessage(phone, null, content, null, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
            e.printStackTrace();
        }

    }
}
