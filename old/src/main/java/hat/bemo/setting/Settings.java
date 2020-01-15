package hat.bemo.setting;

/**
 * Created by apple on 2017/12/11.
 */

public class Settings {

//    public final static int[] week_array = {R.string.Sunday,
//            R.string.Monday,
//            R.string.Tuesday,
//            R.string.Wednesday,
//            R.string.Thursday,
//            R.string.Friday,
//            R.string.Saturday
//    };

    public final static String[] Month_array_eng = {"January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    public final static String[] Month_array_eng2 = {"Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    };

    public final static String[] Month_array  = {"一月",
            "二月",
            "三月",
            "四月",
            "五月",
            "六月",
            "七月",
            "八月",
            "九月",
            "十月",
            "十一月",
            "十二月"
    };

    public final static String[] week = {"星期日",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六",
    };

    public final static String[] week_1 = {"7_1",
            "1_1",
            "2_1",
            "3_1",
            "4_1",
            "5_1",
            "6_1",
    };

    public final static String[] week_2 = {"7_2",
            "1_2",
            "2_2",
            "3_2",
            "4_2",
            "5_2",
            "6_2",
    };

    public final static String[] week_eng = {"Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
    };
    public class DeviceType{
        public final static int BP = 0 ;
        public final static int BG = 1 ;
        public final static int BO = 2 ;
        public final static int WT = 3 ;
        public final static int BF = 4 ;
    }

    // Bundle Key
    public final static String Bundle_Device_Type = "DEVICE_TYPE";
    public final static String Bundle_Settings_Page = "SETTINGS_PAGE";
    public final static String Bundle_Non_Settings_Page = "NON_SETTINGS_PAGE";
    //快撥電話
    public final static String Bundle_Dial_Stage = "DIAL_STAGE";
    public final static String Bundle_Dial_Type = "DIAL_TYPE";
    //通話設定
    public final static String Bundle_Contact_Stage = "CONTACT_STAGE";
    public final static String Bundle_Contact_Type = "CONTACT_TYPE";
    //事件提醒
    public final static String Bundle_Remind_Type = "REMIND_TYPE";
    public final static String Bundle_Device_Command = "DEVICE_COMMAND";
    //最新量測記錄
    public final static String Bundle_records_Type = "LOG_TYPE";

}
