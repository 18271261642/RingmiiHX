package hat.bemo.setting;

/**
 * Created by apple on 2017/11/10.
 */

public class EnumSettings {


    public enum Http_PostData_Enum {
        type80010100("80010100"),
        type80060100("80060100"),
        type80150200("80150200"),
        type80150300("80150300"),
        type80130700("80130700"),
        type80090200("80090200"),
        type80110100("80110100"),
        type80100500("80100500"),
        type80090300("80090300"),
        type80090400("80090400"),
        type80090500("80090500"),
        type4c000("4c000"),
        type4d000("4d000"),
        type80130100("80130100"),
        type80130200("80130200"),
        type80150100("80150100"),
        type80130600("80130600"),
        type80072100("80072100"),
        type80131000("80131000"),
        type80100200("80100200"),
        type80130500("80130500"),
        type80090800("80090800"),
        type80161000("80161000"),
        type80161100("80161100"),
        type10("10"),
        type11("11"),
        type4B("4B"),
        type4C("4C"),
        type4D("4D"),
        type_err ("");

        private String type;

        Http_PostData_Enum(String str) {
            this.type = str;
        }

        public static Http_PostData_Enum ByStr(final String val) {
            for (Http_PostData_Enum type : Http_PostData_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum CustomShortcutsFragment_Enum {
        CUSTOM_SHORTCUTS_TYPE("custom_shortcuts_type"),
        CUSTOM_SHORTCUTS_ONE("custom_shortcuts_one"),
        CUSTOM_SHORTCUTS_TWO("custom_shortcuts_two"),
        CUSTOM_SHORTCUTS_THREE("custom_shortcuts_three"),
        type_err("");
        private String type;

        CustomShortcutsFragment_Enum(String str) {
            this.type = str;
        }

        public static CustomShortcutsFragment_Enum ByStr(final String val) {
            for (CustomShortcutsFragment_Enum type : CustomShortcutsFragment_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum MedicineFragment2_Enum {
        type1_1("1_1"),
        type2_1("2_1"),
        type3_1("3_1"),
        type4_1("4_1"),
        type5_1("5_1"),
        type6_1("6_1"),
        type7_1("7_1"),

        type1_2("1_2"),
        type2_2("2_2"),
        type3_2("3_2"),
        type4_2("4_2"),
        type5_2("5_2"),
        type6_2("6_2"),
        type7_2("7_2"),

        type1_3("1_3"),
        type2_3("2_3"),
        type3_3("3_3"),
        type4_3("4_3"),
        type5_3("5_3"),
        type6_3("6_3"),
        type7_3("7_3"),

        type1_4("1_4"),
        type2_4("2_4"),
        type3_4("3_4"),
        type4_4("4_4"),
        type5_4("5_4"),
        type6_4("6_4"),
        type7_4("7_4"),

        type1_5("1_5"),
        type2_5("2_5"),
        type3_5("3_5"),
        type4_5("4_5"),
        type5_5("5_5"),
        type6_5("6_5"),
        type7_5("7_5"),
        type_err("");
        private String type;

        MedicineFragment2_Enum(String str) {
            this.type = str;
        }

        public static MedicineFragment2_Enum ByStr(final String val) {
            for (MedicineFragment2_Enum type : MedicineFragment2_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum MarkMedi_Enum {
        type1_1("1_1"),
        type2_1("2_1"),
        type3_1("3_1"),
        type4_1("4_1"),
        type5_1("5_1"),
        type6_1("6_1"),
        type7_1("7_1"),

        type1_2("1_2"),
        type2_2("2_2"),
        type3_2("3_2"),
        type4_2("4_2"),
        type5_2("5_2"),
        type6_2("6_2"),
        type7_2("7_2"),

        type1_3("1_3"),
        type2_3("2_3"),
        type3_3("3_3"),
        type4_3("4_3"),
        type5_3("5_3"),
        type6_3("6_3"),
        type7_3("7_3"),

        type1_4("1_4"),
        type2_4("2_4"),
        type3_4("3_4"),
        type4_4("4_4"),
        type5_4("5_4"),
        type6_4("6_4"),
        type7_4("7_4"),

        type1_5("1_5"),
        type2_5("2_5"),
        type3_5("3_5"),
        type4_5("4_5"),
        type5_5("5_5"),
        type6_5("6_5"),
        type7_5("7_5"),
        type_err("");
        private String type;

        MarkMedi_Enum(String str) {
            this.type = str;
        }

        public static MarkMedi_Enum ByStr(final String val) {
            for (MarkMedi_Enum type : MarkMedi_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum FenceMessage_x29_AlarmManager_Enum {
        type1_1("1_1"),
        type2_1("2_1"),
        type3_1("3_1"),
        type4_1("4_1"),
        type5_1("5_1"),
        type6_1("6_1"),
        type7_1("7_1"),

        type1_2("1_2"),
        type2_2("2_2"),
        type3_2("3_2"),
        type4_2("4_2"),
        type5_2("5_2"),
        type6_2("6_2"),
        type7_2("7_2"),
        type_err("");

        private String type;

        FenceMessage_x29_AlarmManager_Enum(String str) {
            this.type = str;
        }

        public static FenceMessage_x29_AlarmManager_Enum ByStr(final String val) {
            for (FenceMessage_x29_AlarmManager_Enum type : FenceMessage_x29_AlarmManager_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum Frequency_x01_AlarmManager_Enum {
        type_1("1"),
        type_33("33"),
        type_49("49"),
        type_65("65"),
        type_97("97"),
        type_177("177"),

        type_01("1"),
        type_02("2"),
        type_03("3"),
        type_04("4"),
        type_05("5"),
        Disable("0"),
        type_err("");

        private String type;

        Frequency_x01_AlarmManager_Enum(String str) {
            this.type = str;
        }

        public static Frequency_x01_AlarmManager_Enum ByStr(final String val) {
            for (Frequency_x01_AlarmManager_Enum type : Frequency_x01_AlarmManager_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum RemindEvent_x11_Enum {
        type1_1("1_1"),
        type2_1("2_1"),
        type3_1("3_1"),
        type4_1("4_1"),
        type5_1("5_1"),
        type6_1("6_1"),
        type7_1("7_1"),

        type1_2("1_2"),
        type2_2("2_2"),
        type3_2("3_2"),
        type4_2("4_2"),
        type5_2("5_2"),
        type6_2("6_2"),
        type7_2("7_2"),

        type1_3("1_3"),
        type2_3("2_3"),
        type3_3("3_3"),
        type4_3("4_3"),
        type5_3("5_3"),
        type6_3("6_3"),
        type7_3("7_3"),

        type1_4("1_4"),
        type2_4("2_4"),
        type3_4("3_4"),
        type4_4("4_4"),
        type5_4("5_4"),
        type6_4("6_4"),
        type7_4("7_4"),

        type1_5("1_5"),
        type2_5("2_5"),
        type3_5("3_5"),
        type4_5("4_5"),
        type5_5("5_5"),
        type6_5("6_5"),
        type7_5("7_5"),
        type_err("");

        private String type;

        RemindEvent_x11_Enum(String str) {
            this.type = str;
        }

        public static RemindEvent_x11_Enum ByStr(final String val) {
            for (RemindEvent_x11_Enum type : RemindEvent_x11_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum MissingProgram_fragment_Enum{
        DELALL("DELALL"),
        ONE_CHECKTYPE("ONE_CHECKTYPE"),
        TWO_CHECKTYPE("TWO_CHECKTYPE"),
        type_1("1"),
        type_2("2"),

        type_801601("801601"),
        type_801602("801602"),
        type_801604("801604"),
        type_801605("801605"),
        type_801606("801606"),

        type_80160300("80160300"),
        type_80160500("80160500"),
        type_80160600("80160600"),

        type_err("");

        private String type;

        MissingProgram_fragment_Enum(String str) {
            this.type = str;
        }

        public static MissingProgram_fragment_Enum ByStr(final String val) {
            for (MissingProgram_fragment_Enum type : MissingProgram_fragment_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum PreWarningReceiver_Enum{
        AmberAlarmTimer_fragment("AmberAlarmTimer_fragment"),
        PreWarning("PreWarning"),
        VoiceMessage("VoiceMessage"),
        LauncherActivity("LauncherActivity"),
        type_err("");

        private String type;

        PreWarningReceiver_Enum(String str) {
            this.type = str;
        }

        public static PreWarningReceiver_Enum ByStr(final String val) {
            for (PreWarningReceiver_Enum type : PreWarningReceiver_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum GCMIntentService_Enum{
        types("type"),
        title("title"),
        content("content"),
        B12("B12"),
        B13("B13"),
        type_err("");

        private String type;

        GCMIntentService_Enum(String str) {
            this.type = str;
        }

        public static GCMIntentService_Enum ByStr(final String val) {
            for (GCMIntentService_Enum type : GCMIntentService_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum DataSync_Enum{
        get_no_data("get_no_data"),
        not_data("not_data"),
        type_err("");

        private String type;

        DataSync_Enum(String str) {
            this.type = str;
        }

        public static DataSync_Enum ByStr(final String val) {
            for (DataSync_Enum type : DataSync_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

    public enum Fall_Enum{
        APItype_851001("851001"),
        APItype_851002("851002"),
        APItype_851301("851301"),
        APItype_851302("851302"),
        APItype_851303("851303"),

        Return_API_type_851001("85100100"),
        Return_API_type_851002("85100200"),
        Return_API_type_851301("85130100"),
        Return_API_type_851302("85130200"),
        Return_API_type_851303("85130300"),
        type_err("");

        private String type;

        Fall_Enum(String str) {
            this.type = str;
        }

        public static Fall_Enum ByStr(final String val) {
            for (Fall_Enum type : Fall_Enum.values()) {
                if (type.type.equals(val)) {
                    return type;
                }
            }
            return type_err; // Never return null
        }
    }

}
