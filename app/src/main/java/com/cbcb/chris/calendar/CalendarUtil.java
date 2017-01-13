package com.cbcb.chris.calendar;

/**
 * Created by chris on 1/13/17.
 */

public class CalendarUtil {

    public static boolean[] convertIntToBool(int d){
        boolean[] b=new boolean[7];
        int count=0;
        while(d>0){
            b[count]= (d& 1)>0;
            d=d>>1;
            count+=1;
        }
        return b;
    }
    public static int convertBoolToInt(boolean[] b){
        int result=0;
        for(int i =0; i<7;i++){
            if(b[i]){
                result+=Math.pow(2,i);
            }
        }
        return result;
    }
}
