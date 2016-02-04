package com.balloonoffice.balloonapp;

/**
 * Created by bluenightz on 6/9/15 AD.
 */
public class APPCONFIG {

    public static String PACKAGENAME = "com.balloonoffice.balloonapp";

    public static String PREFERENCE_FILENAME = "";
    // log in
//    public static String LOGINPROCESS = "http://service.balloonoffice.com/signin";
    public static String LOGINPROCESS = "http://service.balloonoffice.com/api/login";

//    สำหรับส่งค่าไปบันทึก
    public static String SAVE_DATA = "http://service.balloonoffice.com/api/save";

    // รายงานการขายสินค้ารายวัน
//    public static String PRODUCTLIST_JSON = "http://www.balloonoffice.com/BalloonApp/reportMaterialPerday.php";
    public static String PRODUCTLIST_JSON = "http://service.balloonoffice.com/api/movement";

    // สำหรับดาวน์โหลด
    public static String INSTALL_PATH = "http://www.balloonoffice.com/BalloonApp/BalloonApp.apk";

    // สำหรับรายละเอียด application ไว้เช็คการ update
    public static String CHECKVERSION_PATH = "http://www.balloonoffice.com/BalloonApp/appVersion.php";

    // เลขเวอร์ชั่น ณ ขณะนี้
    public static String APPVERSION = "1.15";


    // Sample
    public static String PRODUCT_CATEGORY = "http://service.balloonoffice.com/api/category";
    public static String PRODUCT_CATEGORY2 = "http://service.balloonoffice.com/api/category";
    public static String PRODUCT_CATEGORY3 = "http://service.balloonoffice.com/api/list";
    public static String PRODUCT_VIEW_DETAIL = "http://service.balloonoffice.com/api/detail";
}
