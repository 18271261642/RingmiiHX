package com.guider.baselib.device.standard;

public class Constant {


    // todo 发包的时候全是false就对了
    public static final boolean isDebugOfUser = false;  // 这个开关是账号相关的,true之后就自动填写医生账号了
    public static final boolean isCreazyDebug = false;  // 这个是跳转开关 , 打开后就会在MainActivity直接走测试代码了
    public static final boolean notInitBugly = false;    // 这个是Bugly的测试开关 , 为true的时候不会上报异常
    public static final boolean isDebug = false;       // 是轻量级的Debug开关 , 不会影响正常流程 , 打开他之后有些地方就可以更快速但不合理的执行了.

    public static final String FPG = "FPG";         // 空腹
    public static final String TWOHPPG = "TWOHPPG"; // 饭后两小时

    public static final String XUEYA = "BLOODPRESSURE";   // 血压
    public static final String XUETANG = "BLOODSUGAR";    // 血糖
    public static final String XUEYANG = "BLOODOXYGEN";   // 血氧
    public static final String TEMP = "TEMP";              // 体温
    public static final String XUELIUSU = "BARTERY";      // 血流速度
    public static final String XUEHONGDANBAI = "HEMOGLOBIN";// 血红蛋白

    public static final String ASI = "ARTASI";            // 动脉硬化
    public static final String AVI = "ARTAVI";            // AVI
    public static final String C = "CVALUE";              // 动脉流畅性
    public static final String BMI = "BMI";               // 身体质量指数

    public static final String XINLV = "HEARTBEAT";       // 心率
    public static final String PILAOZHISHU = "LFHF";      // 疲劳指数
    public static final String YALIZHISHU = "SDNN";       // 压力指数
    public static final String XINLVJIANKANG = "PNN50";   // 心率健康




    /**
     * 通用
     */
    public static final String HEALTHRANGE_NORMAL = "正常";
    public static final String HEALTHRANGE_HIGH = "偏高";
    public static final String HEALTHRANGE_LOW = "偏低";

    /**
     * 血压
     */
    public static final String HEALTHRANGE_BP_LOW = "正常血压偏低";
    public static final String HEALTHRANGE_BP_HIGH = "正常血压偏高";
    public static final String HEALTHRANGE_BP_HYP = "疑似高血压";
    public static final String HEALTHRANGE_BP_NORMAL = "理想血压";
    /**
     * 血糖
     */
    public static final String HEALTHRANGE_BS_FBS_LOW = "空腹血糖偏低";
    public static final String HEALTHRANGE_BS_FBS_HIGH = "空腹血糖偏高";
    public static final String HEALTHRANGE_BS_FBS_NORMAL = "空腹血糖正常";
    public static final String HEALTHRANGE_BS_PBS_LOW = "餐后两小时血糖偏低";
    public static final String HEALTHRANGE_BS_PBS_HIGH = "餐后两小时血糖偏高";
    public static final String HEALTHRANGE_BS_PBS_NORMAL = "餐后两小时血糖正常";
    /**
     * 动脉硬化
     */
    public static final String HEALTHRANGE_ART_CRITICAL = "临界";
    public static final String HEALTHRANGE_ART_MILD = "轻度偏高";
    public static final String HEALTHRANGE_ART_MEDIUM = "中度偏高";
    public static final String HEALTHRANGE_ART_SEVERE = "重度偏高";
    /**
     * 六导心电
     */
    // 疲劳指数
    public static final String HEALTHRANGE_LFHF_MEDIUM = "过劳";
    public static final String HEALTHRANGE_LFHF_MILD = "轻松";
    public static final String HEALTHRANGE_LFHF_NORMAL = "正常";

    // 压力指数
    public static final String HEALTHRANGE_SDNN_MEDIUM = "不佳";
    public static final String HEALTHRANGE_SDNN_MILD = "尚可";
    public static final String HEALTHRANGE_SDNN_NORMAL = "正常";

    // 心率健康
    public static final String HEALTHRANGE_PNN50_MEDIUM = "注意";
    public static final String HEALTHRANGE_PNN50_MILD = "不佳";
    public static final String HEALTHRANGE_PNN50_NORMAL = "良好";


    /* --------------------- App升级用到的字段 ------------------------*/

    public static final String CLIENT_TYPE_M500 = "ANDROID_M500";       // m500
    public static final String CLIENT_TYPE_M100 = "ANDROID_M100";       // m500
    public static final String CLIENT_TYPE_M1000 = "ANDROID_M1000";     // m1000
    public static final String CLIENT_TYPE_NOM = "ANDROID_USER";        // 安卓个人app
    public static final String CLIENT_TYPE_DOCTOR = "ANDROID_DOCTOR";   // 安卓医生app
    public static final String CLIENT_TYPE_GLU = "ANDROID_SUGAR";       // 安卓无创血糖App
}
