package com.stanny.nearpal.util;

import java.util.UUID;

public class UUIDUtil {

    public static String initUUid(){
        UUID uu = UUID.randomUUID();
        return uu.toString().replaceAll("-","");
    }

    public static String randomUUID(){
        UUID uu = UUID.randomUUID();
        return uu.toString();
    }
}
