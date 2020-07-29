package cn.wuweikang;

import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class ECGDataAnalyse {

    /**
     * 获取分析结果
     **/
    public static Set<String> getAnalyseResult(Context context, int[] index) {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < index.length; i++) {
            set.add(context.getResources().getString(analyseResultRc[index[i]]));
        }
        return set;
    }

    public static String getAnalyseResultType(Context context, int index) {
        return context.getResources().getString(analyseTypeRc[index]);
    }
    /*
    public static String analyseECGDataResult(int type, int[] index) {
        boolean isAnalyseFailed = true;//是否全是0，全是0：true，否则为false
        for (int i = 0; i < index.length; i++) {
            Log.e("TAG", "结论：" + index[i] + "");
            if (index[i] != 0) {//有不等于0的结论
                isAnalyseFailed = false;
                break;
            }
        }
        String typECG = "";
        if (type == 500) {
            typECG = analyseType[0];
        }
        if (type == 501) {
            typECG = analyseType[1];
        }
        if (type == 502) {
            typECG = analyseType[2];
        }
        if (type == 503) {
            typECG = analyseType[3];
        }
        ECGDataUtil.ECGDataResultHelper helper = new ECGDataUtil.ECGDataResultHelper();
        if (!isAnalyseFailed) {
            for (int i = 0; i < index.length; i++) {
                if (index[i] != 0) {
                    helper.getResult().put(String.valueOf(index[i]), analyseResult[index[i]]);
                }
            }
            helper.getClassfy().put(String.valueOf(type), typECG);
        } else {//分析失败
            helper.getResult().put(String.valueOf(0), "正常心电图");
            if (type == 0) {
                helper.getClassfy().put(0 + "", "分析失败");
            } else {
                helper.getClassfy().put(type + "", typECG);
            }

        }
        return ECGDataUtil.encodeResult(helper);
    }


    private static String[] analyseType = new String[]{
            "** 正常心电图 **",
            "** 非典型心电图 **",
            "** 可疑心电图 **",
            "** 异常心电图 **"
    };
    */

    private static int[] analyseTypeRc = new int[]{
            R.string.wwk_ecg_conclusion1,
            R.string.wwk_ecg_conclusion2,
            R.string.wwk_ecg_conclusion3,
            R.string.wwk_ecg_conclusion4
    };
    /*
    private static String[] analyseResult = new String[]{
            "正常心电图",
            "窦性心律",
            "心动过速",
            "窦性心动过速",
            "室上性心动过速",
            "阵发性室上性心动过速",
            "室性心动过速",
            "短阵室性心动过速",
            "心动过缓",
            "窦性心动过缓",
            "窦性心动过缓伴不齐",
            "窦性心律不齐",
            "室性早搏",
            "成对室性早搏",
            "多形性室性早搏",
            "多源性室性早搏",
            "频发室性早搏",
            "室性早搏二联律",
            "室性早搏三联律",
            "室上性早搏",
            "偶发室上性早搏",
            "成对室上性早搏",
            "频发室上性早搏",
            "室上性早搏二联律",
            "室上性早搏三联律",
            "心房颤动 ",
            "心房扑动",
            "心室颤动",
            "心室扑动",
            "阵发性心房颤动",
            "全心停搏",
            "I度房室传导阻滞",
            "II度I型房室传导阻滞",
            "II度II型房室传导阻滞",
            "III度房室传导阻滞",
            "不完全右束支传导阻滞",
            "完全右束支传导阻滞",
            "不完全左束支传导阻滞",
            "完全左束支传导阻滞",
            "室内传导阻滞",
            "左前分支阻滞",
            "左后分支阻滞",
            "预激综合症",
            "左心房肥大",
            "怀疑左心房肥大",
            "右心房肥大",
            "怀疑右心房肥大",
            "双心房肥大",
            "怀疑双心房肥大",
            "左心室肥大",
            "怀疑左心室肥大",
            "左心室肥大伴ST-T改变",
            "左室高电压",
            "右心室肥大",
            "怀疑右心室肥大",
            "右室肥大伴ST-T改变",
            "急性前壁心肌梗死",
            "亚急性前壁心肌梗死",
            "陈旧性前壁心肌梗死",
            "急性前间壁心肌梗死",
            "亚急性前间壁心肌梗死",
            "陈旧性前间壁心肌梗死",
            "急性广泛前壁心肌梗死",
            "亚急性广泛前壁心肌梗死",
            "陈旧性广泛前壁心肌梗死",
            "急性侧壁心肌梗死",
            "亚急性侧壁心肌梗死",
            "陈旧性侧壁心肌梗死",
            "急性高侧壁心肌梗死",
            "亚急性高侧壁心肌梗死",
            "陈旧性高侧壁心肌梗死",
            "急性下壁心肌梗死",
            "亚急性下壁心肌梗死",
            "陈旧性下壁心肌梗死",
            "不确定心电轴",
            "轻度电轴左偏",
            "电轴左偏",
            "轻度电轴右偏",
            "电轴右偏",
            "ST-T改变",
            "ST段抬高",
            "ST段下降",
            "T波高耸",
            "T波低平",
            "T波倒置",
            "PR间期缩短",
            "QT间期缩短",
            "QT间期延长",
            "异常Q波",
            "肢体导联低电压",
            "胸导联低电压",
            "全导联低电压",
            "右位心",
            "心房起搏心电图",
            "心室起搏心电图",
            "双腔起搏心电图",
            "早期复极综合征",
            "窦性停搏",
            "顺钟向转位",
            "逆钟向转位",
            "S1S2S3 综合征"
    };
    */
    private static int[] analyseResultRc = new int[]{
            R.string.wwk_ecg_subconclusion1, //正常心电图</string>
            R.string.wwk_ecg_subconclusion2, //窦性心律</string>
            R.string.wwk_ecg_subconclusion3, //心动过速</string>
            R.string.wwk_ecg_subconclusion4, //窦性心动过速</string>
            R.string.wwk_ecg_subconclusion5, //室上性心动过速</string>
            R.string.wwk_ecg_subconclusion6, //阵发性室上性心动过速</string>
            R.string.wwk_ecg_subconclusion7, //室性心动过速</string>
            R.string.wwk_ecg_subconclusion8, //短阵室性心动过速</string>
            R.string.wwk_ecg_subconclusion9, //心动过缓</string>
            R.string.wwk_ecg_subconclusion10, //窦性心动过缓</string>
            R.string.wwk_ecg_subconclusion11, //窦性心动过缓伴不齐</string>
            R.string.wwk_ecg_subconclusion12, //窦性心律不齐</string>
            R.string.wwk_ecg_subconclusion13, //室性早搏</string>
            R.string.wwk_ecg_subconclusion14, //成对室性早搏</string>
            R.string.wwk_ecg_subconclusion15, //多形性室性早搏</string>
            R.string.wwk_ecg_subconclusion16, //多源性室性早搏</string>
            R.string.wwk_ecg_subconclusion17, //频发室性早搏</string>
            R.string.wwk_ecg_subconclusion18, //室性早搏二联律</string>
            R.string.wwk_ecg_subconclusion19, //室性早搏三联律</string>
            R.string.wwk_ecg_subconclusion20, //室上性早搏</string>
            R.string.wwk_ecg_subconclusion21, //偶发室上性早搏</string>
            R.string.wwk_ecg_subconclusion22, //成对室上性早搏</string>
            R.string.wwk_ecg_subconclusion23, //频发室上性早搏</string>
            R.string.wwk_ecg_subconclusion24, //室上性早搏二联律</string>
            R.string.wwk_ecg_subconclusion25, //室上性早搏三联律</string>
            R.string.wwk_ecg_subconclusion26, //心房颤动 </string>
            R.string.wwk_ecg_subconclusion27, //心房扑动</string>
            R.string.wwk_ecg_subconclusion28, //心室颤动</string>
            R.string.wwk_ecg_subconclusion29, //心室扑动</string>
            R.string.wwk_ecg_subconclusion30, //阵发性心房颤动</string>
            R.string.wwk_ecg_subconclusion31, //全心停搏</string>
            R.string.wwk_ecg_subconclusion32, //I度房室传导阻滞</string>
            R.string.wwk_ecg_subconclusion33, //II度I型房室传导阻滞</string>
            R.string.wwk_ecg_subconclusion34, //II度II型房室传导阻滞</string>
            R.string.wwk_ecg_subconclusion35, //III度房室传导阻滞</string>
            R.string.wwk_ecg_subconclusion36, //不完全右束支传导阻滞</string>
            R.string.wwk_ecg_subconclusion37, //完全右束支传导阻滞</string>
            R.string.wwk_ecg_subconclusion38, //不完全左束支传导阻滞</string>
            R.string.wwk_ecg_subconclusion39, //完全左束支传导阻滞</string>
            R.string.wwk_ecg_subconclusion40, //室内传导阻滞</string>
            R.string.wwk_ecg_subconclusion41, //左前分支阻滞</string>
            R.string.wwk_ecg_subconclusion42, //左后分支阻滞</string>
            R.string.wwk_ecg_subconclusion43, //预激综合症</string>
            R.string.wwk_ecg_subconclusion44, //左心房肥大</string>
            R.string.wwk_ecg_subconclusion45, //怀疑左心房肥大</string>
            R.string.wwk_ecg_subconclusion46, //右心房肥大</string>
            R.string.wwk_ecg_subconclusion47, //怀疑右心房肥大</string>
            R.string.wwk_ecg_subconclusion48, //双心房肥大</string>
            R.string.wwk_ecg_subconclusion49, //怀疑双心房肥大</string>
            R.string.wwk_ecg_subconclusion50, //左心室肥大</string>
            R.string.wwk_ecg_subconclusion51, //怀疑左心室肥大</string>
            R.string.wwk_ecg_subconclusion52, //左心室肥大伴ST-T改变</string>
            R.string.wwk_ecg_subconclusion53, //左室高电压</string>
            R.string.wwk_ecg_subconclusion54, //右心室肥大</string>
            R.string.wwk_ecg_subconclusion55, //怀疑右心室肥大</string>
            R.string.wwk_ecg_subconclusion56, //右室肥大伴ST-T改变</string>
            R.string.wwk_ecg_subconclusion57, //急性前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion58, //亚急性前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion59, //陈旧性前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion60, //急性前间壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion61, //亚急性前间壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion62, //陈旧性前间壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion63, //急性广泛前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion64, //亚急性广泛前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion65, //陈旧性广泛前壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion66, //急性侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion67, //亚急性侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion68, //陈旧性侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion69, //急性高侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion70, //亚急性高侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion71, //陈旧性高侧壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion72, //急性下壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion73, //亚急性下壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion74, //陈旧性下壁心肌梗死</string>
            R.string.wwk_ecg_subconclusion75, //不确定心电轴</string>
            R.string.wwk_ecg_subconclusion76, //轻度电轴左偏</string>
            R.string.wwk_ecg_subconclusion77, //电轴左偏</string>
            R.string.wwk_ecg_subconclusion78, //轻度电轴右偏</string>
            R.string.wwk_ecg_subconclusion79, //电轴右偏</string>
            R.string.wwk_ecg_subconclusion80, //ST-T改变</string>
            R.string.wwk_ecg_subconclusion81, //ST段抬高</string>
            R.string.wwk_ecg_subconclusion82, //ST段下降</string>
            R.string.wwk_ecg_subconclusion83, //T波高耸</string>
            R.string.wwk_ecg_subconclusion84, //T波低平</string>
            R.string.wwk_ecg_subconclusion85, //T波倒置</string>
            R.string.wwk_ecg_subconclusion86, //PR间期缩短</string>
            R.string.wwk_ecg_subconclusion87, //QT间期缩短</string>
            R.string.wwk_ecg_subconclusion88, //QT间期延长</string>
            R.string.wwk_ecg_subconclusion89, //异常Q波</string>
            R.string.wwk_ecg_subconclusion90, //肢体导联低电压</string>
            R.string.wwk_ecg_subconclusion91, //胸导联低电压</string>
            R.string.wwk_ecg_subconclusion92, //全导联低电压</string>
            R.string.wwk_ecg_subconclusion93, //右位心</string>
            R.string.wwk_ecg_subconclusion94, //心房起搏心电图</string>
            R.string.wwk_ecg_subconclusion95, //心室起搏心电图</string>
            R.string.wwk_ecg_subconclusion96, //双腔起搏心电图</string>
            R.string.wwk_ecg_subconclusion97, //早期复极综合征</string>
            R.string.wwk_ecg_subconclusion98, //窦性停搏</string>
            R.string.wwk_ecg_subconclusion99, //顺钟向转位</string>
            R.string.wwk_ecg_subconclusion100, //逆钟向转位</string>
            R.string.wwk_ecg_subconclusion101, //S1S2S3 综合征"</string>
    };
}
