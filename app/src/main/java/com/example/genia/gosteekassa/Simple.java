package com.example.genia.gosteekassa;

import android.util.Log;

public class Simple {

    private static String TAG = "Simple";

    public static void main(String[] args) {
       // getWorkerDays("ПН, СР, ПТ, ВС");
        System.out.println("main: " + getWorkerDays());

    }



    private static String getWorkerDays(){
        String workerDays = "", firsDay = "", lastDay = "";
        Boolean fDay = false, lDay = false, status = true, isLast = false, isAllNotisCheck = false;
        Boolean [] week = {false, false, false, false, false, false, false};

        week[0] = true;
        week[1] = true;
        week[2] = true;
        week[3] = true;
        week[4] = false;
        week[5] = false;
        week[6] = true;



        for (int i = 0; i < 7; i++){
            if (week[i]){
                isAllNotisCheck = true;
                break;
            }
        }

        if (!isAllNotisCheck){
            System.out.println("getWorkerDays: Не выбраны дни недели");
            return "";
        }

        for (int i = 0; i < 7; i++){
            if (!week[i]){
                status = false;
                break;
            }
        }

        if (status) return "Ежедневно";

        for (int i = 0; i <= 6; i++){
            System.out.println("getWorkerDays: [" + i + "] = " + week[i]);
            if (!fDay && week[i]){
                firsDay = getDay(i);
                if (isLast) workerDays += ", ";
                isLast = false;
                fDay = true;
                if (i < 6 && !week[i+1]){
                    if (i+1 == 6){
                        workerDays +=firsDay;
                        fDay = false;
                    }else {
                        workerDays +=firsDay + ", ";
                        fDay = false;
                    }

                }
            }
            if (fDay && week[i]){
                if (i < 6 && week[i+1]) continue;
                lastDay += getDay(i);
                lDay = true;
            }
            if (fDay && lDay) {
                workerDays += firsDay + "-" + lastDay;
                fDay = false;
                lDay = false;
                firsDay = "";
                lastDay = "";
                isLast = true;
            }

        }

        return workerDays;
    }

    private static String getDay(int i){
        switch (i){
            case 0:
                return "ПН";
            case 1:
                return "ВТ";
            case 2:
                return "СР";
            case 3:
                return "ЧТ";
            case 4:
                return "ПТ";
            case 5:
                return "СБ";
            case 6:
                return "ВС";

        }
        return "";
    }

    private static void getWorkerDays(String days){
        Boolean [] week = {false, false, false, false, false, false, false};
        if (days.equals("Ежедневно")){
            for (int i = 0; i < 7; i++){
                week[i] = true;
            }
        }else {
            String [] str = days.split(",");
            String [] str2;
            for (String str1:str) {
                str2 = str1.split("-");
                if (str2.length == 2){
                    for (int i = getNumberOfDay(str2[0].trim());
                         i <= getNumberOfDay(str2[1].trim()); i++) {
                        week[i] = true;
                    }
                }
                if (str2.length == 1)
                    week[getNumberOfDay(str2[0].trim())] = true;
            }
        }
                for (Boolean boll:week) System.out.println("week = " + boll);
    }

    private static int getNumberOfDay(String str){
        switch (str){
            case "ПН":
                return 0;
            case "ВТ":
                return 1;
            case "СР":
                return 2;
            case "ЧТ":
                return 3;
            case "ПТ":
                return 4;
            case "СБ":
                return 5;
            case "ВС":
                return 6;

        }
        return 7;
    }
}
