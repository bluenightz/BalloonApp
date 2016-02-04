package com.balloonoffice.balloonapp;

import java.util.ArrayList;

/**
 * Created by bluenightz on 6/22/15 AD.
 */
public class ChangedList {
//    public static ArrayList<ProductList.CodeObj.Code> products = null;
    public static ProductList.CodeObj codeObj = null;


    public static Object check( String code ){
        // check string
        // if true
        // return ProductList.CodeObj.Code
        // if false
        // return false

        Result r = new ChangedList.Result();


//        if( products != null ){
//            int max = products.size();
//            for(int i = 0 ; i < max ; ++i){
//                if( products.get(i).code.equals( code ) ){
//                    r.codeobj = products.get(i);
//                    r.b = true;
//                    return r;
//                }
//            }
//            r.b = false;
//        }

        if( codeObj != null ){
            int max = codeObj.codes.length;
            for(int i = 0 ; i < max ; ++i){
                ProductList.CodeObj.Code _code = (ProductList.CodeObj.Code) codeObj.codes[i];
                if( _code.code.equals( code ) ){
                    r.codeobj = _code;
                    r.b = true;
                    return r;
                }
            }
            r.b = false;
        }

//        ((CodeObj.Code) ChangedList.codeObj.codes[0]).code
        return r;
    }

    public static class Result{
        public ProductList.CodeObj.Code codeobj = null;
        public boolean b;
    }



}
