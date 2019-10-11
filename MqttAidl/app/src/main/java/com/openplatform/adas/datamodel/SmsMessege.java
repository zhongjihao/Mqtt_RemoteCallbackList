package com.openplatform.adas.datamodel;


import android.util.Log;

import com.openplatform.adas.constant.SmsConstant;
import com.openplatform.adas.interfacemanager.IOnCmdMessageProc;
import com.openplatform.adas.util.Assert;
import com.openplatform.aidl.CmdMesage;

import java.util.ArrayList;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/12 10:21
 * Description :
 */
public class SmsMessege {
    private static final String TAG = "SmsMessege";
    private String cmd;
    private String value;


    public SmsMessege(IOnCmdMessageProc proc, String[] content) {
        Assert.isTrue(content!=null && (content.length>=1));
        Log.d(TAG,"count: "+content.length);
        ArrayList<CmdMesage> cmdList = new ArrayList<>();
        for(int i=0;i<content.length;i++){
            Log.d(TAG,"content["+i+"]: "+content[i]);
            String[] data = content[i].split(";");
            if(data != null && data.length > 0){
                switch (data[0]){
                    case SmsConstant.COMMON_TTS:{
                        if(SmsConstant.COMMON_STADAS.equalsIgnoreCase(data[1])){
                            if(SmsConstant.FCWCAB.equalsIgnoreCase(data[2])){
                                cmd = SmsConstant.TTS_STADAS_FCWCAB;
                                value = data[3];
                                CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                cmdList.add(cmdMesage);
                            }
                        }else if(SmsConstant.COMMON_RDADAS.equalsIgnoreCase(data[1])){
                            if(SmsConstant.FCWCAB.equalsIgnoreCase(data[2])){
                                cmd = SmsConstant.TTS_RDADAS_FCWCAB;
                                CmdMesage cmdMesage = new CmdMesage();
                                cmdMesage.setCmd(cmd);
                                if(data.length >3){
                                    value = data[3];
                                    cmdMesage.setValue(value);
                                }
                                cmdList.add(cmdMesage);
                            }
                        }
                        break;
                    }
                    case SmsConstant.COMMON:{
                        if(SmsConstant.STPF.equalsIgnoreCase(data[1])){
                            switch (data[2]){
                                case SmsConstant.SVRM:{
                                    cmd = SmsConstant.ST_MAINIP;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.SVR1:{
                                    cmd = SmsConstant.ST_SLAVEIP;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.FCW:{
                                    cmd = SmsConstant.ST_FCW;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.HWL:{
                                    cmd = SmsConstant.ST_HWL;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.LDW:{
                                    cmd = SmsConstant.ST_LDW;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.PCW:{
                                    cmd = SmsConstant.ST_PCW;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.SMOKE:{
                                    cmd = SmsConstant.ST_SMOKE;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.PHONE:{
                                    cmd = SmsConstant.ST_PHONE;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.UNUSUAL:{
                                    cmd = SmsConstant.ST_UNUSUAL;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.NODRIVER:{
                                    cmd = SmsConstant.ST_NODRIVER;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.EYE:{
                                    cmd = SmsConstant.ST_EYE;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.YAWN:{
                                    cmd = SmsConstant.ST_YAWN;
                                    value = data[3];
                                    CmdMesage cmdMesage = new CmdMesage(cmd,value);
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                            }
                        }else if(SmsConstant.RDPF.equalsIgnoreCase(data[1])){
                            switch (data[2]){
                                case SmsConstant.SVRM:{
                                    cmd = SmsConstant.RD_MAINIP;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.SVR1:{
                                    cmd = SmsConstant.RD_SLAVEIP;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.FCW:{
                                    cmd = SmsConstant.RD_FCW;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.HWL:{
                                    cmd = SmsConstant.RD_HWL;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.LDW:{
                                    cmd = SmsConstant.RD_LDW;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.PCW:{
                                    cmd = SmsConstant.RD_PCW;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.SMOKE:{
                                    cmd = SmsConstant.RD_SMOKE;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.PHONE:{
                                    cmd = SmsConstant.RD_PHONE;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.UNUSUAL:{
                                    cmd = SmsConstant.RD_UNUSUAL;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.NODRIVER:{
                                    cmd = SmsConstant.RD_NODRIVER;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.EYE:{
                                    cmd = SmsConstant.RD_EYE;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                                case SmsConstant.YAWN:{
                                    cmd = SmsConstant.RD_YAWN;
                                    CmdMesage cmdMesage = new CmdMesage();
                                    cmdMesage.setCmd(cmd);
                                    if(data.length >3){
                                        value = data[3];
                                        cmdMesage.setValue(value);
                                    }
                                    cmdList.add(cmdMesage);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        Log.d(TAG,"result cmdList count: "+cmdList.size());
        proc.onSmsMessageProc(cmdList);
    }
}
