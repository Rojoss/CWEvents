package com.clashwars.cwevents;

import com.clashwars.cwcore.utils.CWUtil;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4CW Events&8] &6" + msg);
    }

}
