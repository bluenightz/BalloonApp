package com.balloonoffice.balloonapp.Model;

import java.util.ArrayList;

/**
 * Created by bluenightz on 2/8/16 AD.
 */
public class CodeObj<Code>{
    public Code[] codes;
    public ArrayList<CodeObj.Code> pl = null;

    public void addtofirst(){
        CodeObj.Code c = (CodeObj.Code) codes[0];
        c.code = "8858667045108";
    }

    public ArrayList<CodeObj.Code> getArrayList(){
        ArrayList<CodeObj.Code> list = new ArrayList<CodeObj.Code>();


        for(int i = 0; i < codes.length; ++i){
            CodeObj.Code _c = (CodeObj.Code) codes[i];
            list.add(_c);
        }


        return list;
    }

    public class Time{
        public String time;
        public String date;
        public String quantity; // qunatity from admin count at that time
        public boolean isCorrect;
    }

    public class Code{
        public String code;
        public String title;
        public String quantity; // quantity from yesterday
        public String src;

        public Time[] checkschedule;




        public boolean checkStock(String num, String date, String time){
            Time t = (CodeObj.Time) checkschedule[0];
            t.quantity = num;
            t.time = time;
            t.date = date;

            if( quantity.equals(t.quantity) ){
                t.isCorrect = true;
            }else{
                t.isCorrect = false;
            }

            return t.isCorrect;
        }

    }

}
