package com.openplatform.adas.constant;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/12 10:21
 * Description :
 */
public class SmsConstant {
    public static final String COMMON_TTS = "#_ZTTtsSet";
    public static final String COMMON_STADAS = "STADAS";
    public static final String COMMON_RDADAS = "RDADAS";
    public static final String FCWCAB = "FCWCAB";
    public static final String TTS_STADAS_FCWCAB = COMMON_TTS +";"+COMMON_STADAS+";"+FCWCAB+ ";";
    public static final String TTS_RDADAS_FCWCAB = COMMON_TTS +";"+COMMON_RDADAS+";"+FCWCAB+ ";";

    public static final String COMMON = "#AITE";
    public static final String RDPF = "RDPF";
    public static final String STPF = "STPF";

    public static final String SVRM = "SVRM";
    public static final String ST_MAINIP = COMMON +";" +STPF+";"+SVRM+";";
    public static final String RD_MAINIP = COMMON +";"+RDPF+";"+SVRM+";";

    public static final String SVR1 = "SVR1";
    public static final String ST_SLAVEIP = COMMON +";"+ STPF+";"+SVR1+";";
    public static final String RD_SLAVEIP = COMMON + ";"+RDPF+";"+SVR1+";";

    public static final String FCW = "FCW";
    public static final String RD_FCW = COMMON +";"+ RDPF+";"+FCW+";";
    public static final String ST_FCW = COMMON +";"+ STPF+";"+FCW+";";

    public static final String HWL = "HWL";
    public static final String RD_HWL = COMMON +";"+ RDPF+";"+HWL+";";
    public static final String ST_HWL = COMMON +";"+ STPF+";"+HWL+";";

    public static final String LDW = "LDW";
    public static final String RD_LDW = COMMON +";"+ RDPF+";"+LDW+";";
    public static final String ST_LDW = COMMON +";"+ STPF+";"+LDW+";";

    public static final String PCW = "PCW";
    public static final String RD_PCW = COMMON +";"+ RDPF+";"+PCW+";";
    public static final String ST_PCW = COMMON +";"+ STPF+";"+PCW+";";

    public static final String SMOKE = "SMOKE";
    public static final String RD_SMOKE = COMMON +";"+ RDPF+";"+SMOKE+";";
    public static final String ST_SMOKE = COMMON +";"+ STPF+";"+SMOKE+";";

    public static final String PHONE = "PHONE";
    public static final String RD_PHONE = COMMON +";"+ RDPF+";"+PHONE+";";
    public static final String ST_PHONE = COMMON +";"+ STPF+";"+PHONE+";";

    public static final String UNUSUAL = "UNUSUAL";
    public static final String RD_UNUSUAL = COMMON +";"+ RDPF+";"+UNUSUAL+";";
    public static final String ST_UNUSUAL = COMMON +";"+ STPF+";"+UNUSUAL+";";

    public static final String NODRIVER = "NODRIVER";
    public static final String RD_NODRIVER = COMMON +";"+ RDPF+";"+NODRIVER+";";
    public static final String ST_NODRIVER = COMMON +";"+ STPF+";"+NODRIVER+";";

    public static final String EYE = "EYE";
    public static final String RD_EYE = COMMON +";"+ RDPF+";"+EYE+";";
    public static final String ST_EYE = COMMON +";"+ STPF+";"+EYE+";";

    public static final String YAWN = "YAWN";
    public static final String RD_YAWN = COMMON +";"+ RDPF+";"+YAWN+";";
    public static final String ST_YAWN = COMMON +";"+ STPF+";"+YAWN+";";

}
