package com.balloonoffice.balloonapp;

/**
 * Created by bluenightz on 6/11/15 AD.
 */
public class ActiveProduct {
    public static String CODE;
    public static String TITLE;
    public static String SRC;
    public static String QUANTITY;
    public static ProductList.CodeObj.Code CODEOBJ;
    //public static boolean isFirst;

    public static void setValue( ProductList.CodeObj.Code obj ){
        CODEOBJ = obj;
        CODE = obj.code;
        TITLE = obj.title;
        SRC = obj.src;
        QUANTITY = obj.quantity;

    }

}
