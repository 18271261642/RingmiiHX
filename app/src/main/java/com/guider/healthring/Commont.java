package com.guider.healthring;

/**
 * sp---key
 */
public class Commont {

    //国内的用这个微信登录
//    public static final String WX_APP_ID = "wx0144beedb6c2eed7";
//    public static final String WX_APP_SECRET = "edd86a83749ba0bf9f8f96e9e26af708";
    //GooglePlay用这个
    public static final String WX_APP_ID = "wx7e5e9e90ae4d8f51";
    public static final String WX_APP_SECRET = "8864cc82ce546270207e5ba1f3ef0054";




    /**
     * 判断B31是否带有血压功能
     */
    public static final String IS_B31_HAS_BP_KEY = "is_b31_bp";
    public static final boolean isDebug = true;//日志
    public static boolean DevicesSport = false;
    public static int COUNTNUMBER = 4;
    public static int GPSCOUNT = 2;
    //    public static boolean isGPSed = false; //一轮的短信数量已经发送完成
    public static boolean isSosOpen = false;
    //public static final String BLEMAC = "mylanmac";//蓝牙MAC

    public static boolean H9_SyncTrue = false;//由于H9读取数据需是串行所以添加同步开关标识
    public static final String BLEMAC = "mylanmac";//蓝牙MAC
    public static final String BLENAME = "mylanya"; //蓝牙名字

    public static final String USER_HEIGHT = "userheight";    //用户身高
    public static final String USER_SEX = "user_sex";       //用户性别
    public static final String USER_ID_DATA = "userId";     //用户ID
    //用户信息，userInfo对象json格式保存
    public static final String USER_INFO_DATA = "saveuserinfodata";


    public static final String DEVICESCODE = "b30_devices_code";//设备校验码
    public static final String BATTERNUMBER = "batter";//电量

    /***
     * 个性化设置 -------   SP - key
     */

    /**
     * 设备类的
     */

    public static final String ISSystem = "isSystem";//是否为公制

    public static final String IS24Hour = "is24Hour";//是否为24小时制

    public static final String ISAutoHeart = "isAutoHeart";//是否自动测量心率

    public static final String ISAutoBp = "isAutoBp";//是否自动测量血压

    public static final String ISSecondwatch = "isSecondwatch";//是否开启秒表

    public static final String ISWearcheck = "isWearcheck";//肤色 --- -- 是否开启佩戴检测

    public static final String ISCheckWear = "isCheckWear";//是否开启佩戴检测

    public static final String ISFindPhone = "isFindPhone";//是否开启查找手机

    public static final String ISDevices = "isDevices";//是否开启查找设备

    public static final String ISCallPhone = "isPhone";//是否开启来电提醒

    public static final String ISDisAlert = "isDisAlert";//是否开启断开提醒

    public static final String ISHelpe = "isHelpe";//是否开启sos

    public static final String ISDisturb = "isDisturb";//是否开启勿扰模式

    public static final String ISSedentary = "isSedentary";//是否开启久坐提醒

    public static final String ISDrink = "isDrink";//是否开启喝水提醒

    public static final String ISMedicine = "isMedicine";//是否开启吃药提醒

    public static final String ISMeeting = "isMeeting";//是否开启会议提醒

    public static final String ISWrists = "isWrists";//是否开启翻腕亮屏


    //B31的HRV功能
    public static final String B31_HRV = "isB31HRV";
    //B31的血压夜间检测
    public static final String B31_Night_BPOxy = "B31_bp_oxy";

    /**
     * 关于社交以及短信手机类的提醒 Key
     */
    public static final String ISSkype = "isSkype";//是否开启 Skype 提醒
    public static final String ISWhatsApp = "isWhatsApp";//是否开启 WhatsApp 提醒
    public static final String ISFacebook = "isFacebook";//是否开启 Facebook 提醒
    public static final String ISLinkendln = "isLinkendln";//是否开启 Linkendln 提醒
    public static final String ISTwitter = "isDisturb";//是否开启 Twitter 提醒
    public static final String ISViber = "isViber";//是否开启 Viber 提醒
    public static final String ISLINE = "isLINE";//是否开启 LINE 提醒
    public static final String ISSINA = "isSINA";   //新浪
    public static final String ISWechart = "isWechart";//是否开启 微信 提醒
    public static final String ISQQ = "isQQ";//是否开启 QQ 提醒
    public static final String ISMsm = "isMsm";//是否开启 短息 提醒
    public static final String ISPhone = "isPhone";//是否开启 电话 提醒

    /**
     * b30InstagramTogg
     * b30GmailTogg
     * b30SnapchartTogg
     * b30OhterTogg
     */
    public static final String ISInstagram = "isInstagram";//是否开启 Instagram 提醒
    public static final String ISGmail = "isGmail";//是否开启 Gmail 提醒
    public static final String ISSnapchart = "isSnapchart";//是否开启 Snapchart 提醒
    public static final String ISOhter = "isOhter";//是否开启 Ohter 提醒


    //H8 公制或英制
    public static final String H8_UNIT = "h8Unit";
    public static final String H8_KM = "km";
    public static final String H8_MI = "mi";


    //好友 URL
    public static final String FRIEND_BASE_URL = "http://47.90.83.197:9070/Watch";

    public static final String Findlist = "/friend/myFriends";//我的好友列表
    public static final String FindFrend = "/addFriend/findByPhone";//查找好友
    public static final String ApplyAddFind = "/addFriend/applyFriend";//申请添加好友
    public static final String FindNewFrend = "/addFriend/findApplyList";//查看我的新的好友
    public static final String FindReturnApply = "/addFriend/agreeApply";//好友通过或者驳回
    public static final String FindApplyHistory = "/addFriend/findMyApply";//我的申请记录
    public static final String DeleteFrendItem = "/friend/delFriend";//删除好友
    public static final String FrendAwesome = "/thumbs/clickThumbs";//好友点赞接口（好友之间一天只能点赞一次，不能取消赞）
    public static final String TodayRank = "/worldranking/getTodayRank";//所有用户的排名


    //public static final String FrendDetailedIsVis = "/friend/changeSeeSet";//详细资料是否对好友可见------可单独设置接口
    //public static final String GETFrendDetailedIsVis = "/friend/getSeeSet";//获取详细资料是否对好友可见------可单独获取设置接口
    public static final String FrendDetailedIsVis = "/friend/changeInfoShow";//详细资料是否对好友可见------可单独设置接口
    public static final String GETFrendDetailedIsVis = "/friend/getInfoShow";//获取详细资料是否对好友可见------可单独获取设置接口
    public static final String FrendStepData = "/friend/friendStepNumber";//好友步数详细
    public static final String FrendSleepData = "/friend/friendSleepData";//好友睡眠详细
    public static final String FrendHeartData = "/friend/friendHeartRate";//好友心率详细

    public static final String FrendSleepUpToDayData = "/sleepSlot/upSleepSlot";//好友日睡眠上传

    public static final String FrendStepToDayData = "/friend/friendStepNumber";//好友日步数详细
    public static final String FrendSLeepToDayData = "/friend/friendSleepData";//好友日睡眠详细
    public static final String FrendHeartToDayData = "/friend/friendHeartRate";//好友日心率详细
    public static final String FrendBpToDayData = "/friend/friendBloodPerssure";//好友日血压详细
    //8c4c511a45374bb595e6fdf30bb878b7  d3546a77d5bb44d2805c6bf40508ad2e
    public static final String FrendLastData = "/friend/friendInfo";//好友首页：昨日的睡眠，心率，步数
    public static final String FrendLoveMine = "/friend/ThumbsTodayFriends";//返回今日已赞我的好友
    public static final String PhoneIsRegister = "/user/checkExitRegister";//通过电话号码识别是否为RacefitPro 应用的已注册用户
    public static final String ChageDevicesName = "/user/changeEquipment";//更改设备号
    public static final String TodayLoveMe = "/friend/ThumbsTodayFriends";//回今日已赞我的好友
    public static final String DeleteApplyFrend = "/addFriend/delMyApply";//删除我的申请记录






    //盖德数据接口

    //public static final String GAI_DE_BASE_URL = "http://api.guiderhealth.com/api/v1/";
    public static final String GAI_DE_BASE_URL = "http://apihd.guiderhealth.com/api/v1/";


    //public static final String GAI_DE_BASE_URL = "http://210b2a63.nat123.cc/api/v1/";

    //血氧呼吸暂停
    public static final String BREATH_PAUSE_URL = GAI_DE_BASE_URL + "breathpause";

    //心脏负荷
    public static final String HEART_LOAD_URL = GAI_DE_BASE_URL + "heartload";

    //睡眠活动
    public static final String SLEEP_ACTIVITY_URL = GAI_DE_BASE_URL + "sleepactivity";

    //呼吸率
    public static final String BREATH_URL = GAI_DE_BASE_URL + "breathe";

    //低氧时间
    public static final String LOW_O2_URL = GAI_DE_BASE_URL + "hypoxiatime";

    //血氧
    public static final String BLOOD_OXY_URL = GAI_DE_BASE_URL + "bloodoxygen";

    //hrv
    public static final String HRV_URL = GAI_DE_BASE_URL + "hrv";



    //睡眠
    public static final String SLEEP_QUALITY_URL = GAI_DE_BASE_URL +"sleepquality";
    //心率
    public static final String HEART_URL = GAI_DE_BASE_URL + "heartbeat";
    //血压
    public static final String BLOOD_URL = GAI_DE_BASE_URL + "bloodpressure";




}
