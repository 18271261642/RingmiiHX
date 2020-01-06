package com.guider.health.common.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by haix on 2019/7/25.
 */

public class Judgement {

    public static JudgeResult healthDataAnlysis(ParamHealthRangeAnlysis paramHealthRangeAnlysis) {

        JudgeResult judgeResult = new JudgeResult();
        List<ParamHealthRangeAnlysis> param = new ArrayList<>();
        param.add(paramHealthRangeAnlysis);
        List<String> list = healthDataAnlysis(param);
        if (list.size() == 0){
            return judgeResult;
        }

        judgeResult.setResult(list.get(0));
        judgeResult.setDirection(index);

        return judgeResult;
    }

    private static int index = 0;

    /**
     * .HEARTBEAT
     * ).getHeart
     * aramHealth
     * .JKZS);
     * ).getPNN50
     * aramHealth
     * .YLZS);
     * ).getSDNN(
     * aramHealth
     * .LLZS);
     * ).getLFHF(
     * @param param
     * @return
     */
    public static List<String> healthDataAnlysis(List<ParamHealthRangeAnlysis> param) {
        List<String> result = new ArrayList<>();
        if (param.size() == 0)
            return result;

        HealthRange tmpRange = HealthRange.getInstance();
        if (tmpRange == null) {
            return result;
        }
        for (ParamHealthRangeAnlysis item : param) {

            switch (item.getType()) {
                case ParamHealthRangeAnlysis.BMI:
                    if (Double.parseDouble(item.getValue1().toString()) < tmpRange.getBmiMin()) {
                        result.add("体脂偏低");
                        index = -1;
                    } else if (Double.parseDouble(item.getValue1().toString()) >= tmpRange.getBmiMax()) {
                        result.add("体脂偏高");
                        index = 1;
                    } else {
                        result.add("正常");
                        index = 0;
                    }
                    break;
                case ParamHealthRangeAnlysis.HEARTBEAT:
                    if (Integer.parseInt(item.getValue1().toString()) < tmpRange.getHrMin()) {
                        result.add("心率偏低");
                        index = -1;
                    } else if (Integer.parseInt(item.getValue1().toString()) >= tmpRange.getHrMax()) {
                        result.add("心率偏高");
                        index = 1;
                    } else {
                        result.add("正常");
                        index = 0;
                    }
                    break;
                case ParamHealthRangeAnlysis.BLOODSUGAR:

                    if (item.getBsTime() == ParamHealthRangeAnlysis.TWOHPPG) {
                        if (Double.parseDouble(item.getValue1().toString()) > tmpRange.getPbsMax()) {
                            result.add("血糖偏高");
                            index = 1;
                        } else if (Double.parseDouble(item.getValue1().toString()) < tmpRange.getPbsMin()) {
                            result.add("血糖偏低");
                            index = -1;
                        } else {
                            result.add("正常");
                            index = 0;
                        }
                    } else {
                        if (Double.parseDouble(item.getValue1().toString()) < tmpRange.getFbsMin()) {
                            result.add("血糖偏低");
                            index = -1;
                        } else if (Double.parseDouble(item.getValue1().toString()) > tmpRange.getFbsMax()) {
                            result.add("血糖偏高");
                            index = 1;
                        } else {
                            result.add("正常");
                            index = 0;
                        }
                    }
                    break;
                case ParamHealthRangeAnlysis.BLOODOXYGEN:
                    if (Integer.parseInt(item.getValue1().toString()) < tmpRange.getBoMin()) {
                        result.add("血氧偏低");
                        index = -1;
                    } else {
                        result.add("正常");
                        index = 0;
                    }
                    break;
                case ParamHealthRangeAnlysis.BLOODPRESSURE:

                    if (item.getYear() == 0){
                        String year = UserManager.getInstance().getBirth().substring(0, 4);
                        item.setYear(Integer.parseInt(year));
                    }
                    if (item.getValue1() != null && item.getValue2() != null) {
                        if (item.getYear() != 0 && Calendar.getInstance().get(Calendar.YEAR) - item.getYear() > 60) {
                            if (Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMin()
                                    || Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue1().toString()) > tmpRange.getSbpIdealMin()
                                    && Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMax()
                                    && Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpIdealMin()
                                    && Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else {
                                result.add("疑似高血压");
                                index = 1;
                            }
                        } else {
                            if (Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMin()
                                    || Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue1().toString()) >= tmpRange.getSbpIdealMin()
                                    && Integer.parseInt(item.getValue1().toString()) < tmpRange.getSbpIdealMax()
                                    && Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpIdealMin()
                                    && Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else if (Integer.parseInt(item.getValue1().toString()) > tmpRange.getSbpHMin()
                                    || Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpHMin()) {
                                result.add("疑似高血压");
                                index = 2;
                            } else {
                                result.add("血压偏高");
                                index = 1;
                            }
                        }
                        break;
                    }

                    //shousuo
                    if (item.getValue1() != null) {

                        if (item.getYear() != 0 && Calendar.getInstance().get(Calendar.YEAR) - item.getYear() > 60) {
                            if (Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue1().toString()) > tmpRange.getSbpIdealMin()
                                    && Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else {
                                result.add("疑似高血压");
                                index = 1;
                            }
                        } else {
                            if (Integer.parseInt(item.getValue1().toString()) <= tmpRange.getSbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue1().toString()) >= tmpRange.getSbpIdealMin()
                                    && Integer.parseInt(item.getValue1().toString()) < tmpRange.getSbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else if (Integer.parseInt(item.getValue1().toString()) > tmpRange.getSbpHMin()) {
                                result.add("疑似高血压");
                                index = 2;
                            } else {
                                result.add("血压偏高");
                                index = 1;
                            }
                        }
                    }

                    //shuzhang
                    if (item.getValue2() != null) {

                        if (item.getYear() != 0 && Calendar.getInstance().get(Calendar.YEAR) - item.getYear() > 60) {
                            if (Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpIdealMin()
                                    && Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else {
                                result.add("疑似高血压");
                                index = 1;
                            }
                        } else {
                            if (Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMin()) {
                                result.add("血压偏低");
                                index = -1;
                            } else if (Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpIdealMin()
                                    && Integer.parseInt(item.getValue2().toString()) <= tmpRange.getDbpIdealMax()) {
                                result.add("理想血压");
                                index = 0;
                            } else if (Integer.parseInt(item.getValue2().toString()) > tmpRange.getDbpHMin()) {
                                result.add("疑似高血压");
                                index = 2;
                            } else {
                                result.add("血压偏高");
                                index = 1;
                            }
                        }
                    }

                    break;

                case ParamHealthRangeAnlysis.DMYH:
                    if (Integer.parseInt(item.getValue1().toString()) <= 80) {
                        index = 0;
                        result.add("正常");
                    }

                    if (81 <= Integer.parseInt(item.getValue1().toString()) && Integer.parseInt(item.getValue1().toString()) <= 110) {
                        result.add("临界");
                        index = 1;
                    }

                    if (111 <= Integer.parseInt(item.getValue1().toString()) && Integer.parseInt(item.getValue1().toString()) <= 160) {
                        result.add("轻度");
                        index = 2;
                    }

                    if (161 <= Integer.parseInt(item.getValue1().toString()) && Integer.parseInt(item.getValue1().toString()) <= 230) {
                        result.add("中度");
                        index = 3;
                    }

                    if (Integer.parseInt(item.getValue1().toString()) > 230) {
                        result.add("重度");
                        index = 4;
                    }
                    break;


                case ParamHealthRangeAnlysis.LLZS:
                    if (0.25 < Double.parseDouble(item.getValue1().toString()) && Double.parseDouble(item.getValue1().toString()) <= 4) {
                        result.add("正常");
                        index = 0;
                    }else if (Double.parseDouble(item.getValue1().toString()) >= 0.02 || Double.parseDouble(item.getValue1().toString()) >= 50) {
                        result.add("过劳");
                        index = -1;
                    }else if (Double.parseDouble(item.getValue1().toString()) >= 5 && Double.parseDouble(item.getValue1().toString()) <= 50
                            || Double.parseDouble(item.getValue1().toString()) >= 0.02 && Double.parseDouble(item.getValue1().toString()) <= 0.24) {
                        result.add("轻松");
                        index = 2;
                    }
                    break;


                case ParamHealthRangeAnlysis.YLZS:

                    if (Double.parseDouble(item.getValue1().toString()) < 15) {
                        result.add("不佳");
                        index = -1;
                    }else if (40 < Double.parseDouble(item.getValue1().toString()) && Double.parseDouble(item.getValue1().toString()) <= 100) {
                        result.add("正常");
                        index = 0;
                    }else if (15 <= Double.parseDouble(item.getValue1().toString()) && Double.parseDouble(item.getValue1().toString()) <= 40
                            || Double.parseDouble(item.getValue1().toString()) > 100) {
                        result.add("尚可");
                        index = 1;
                    }
                    break;

                case ParamHealthRangeAnlysis.JKZS:

                    if (Double.parseDouble(item.getValue1().toString()) > 0.75) {
                        result.add("不佳");
                        index = -1;
                    }else if ("".equals(item.getValue1().toString())) {
                        result.add("良好");
                        index = 0;
                    }else if (!"".equals(item.getValue1().toString())) {
                        result.add("注意");
                        index = 1;
                    }
                    break;

                case ParamHealthRangeAnlysis.AVI:
                    int age = Integer.parseInt(item.getValue1().toString());
                    int avi = Integer.parseInt(item.getValue2().toString());
                    int[] Avi = getAviAndApi(age, avi, 0);
                    result.add(Avi[0] == 0 ? "正常" : "异常");
                    index = Avi[0];
                    break;
                case ParamHealthRangeAnlysis.API:
                    int age1 = Integer.parseInt(item.getValue1().toString());
                    int api = Integer.parseInt(item.getValue2().toString());
                    int[] Api = getAviAndApi(age1, 0  , api);
                    result.add(Api[1] == 0 ? "正常" : "异常");
                    index = Api[1];
                    break;

            }
        }
        return result;
    }


    /**
     * 获取评级
     *
     * @param value 需要定位的值
     * @param ls    评级标准 比如 60 <= 正常值 < 100 = {60 , 100}
     * @return
     */
    static int getLevel(double value, double[] ls) {
        for (int i = 0; i < ls.length; i++) {
            if (value < ls[i]) {
                return i;
            }
        }
        return ls.length;
    }
    static int[] getAviAndApi(int age , int avi , int api) {
        int ageInterval = getLevel(age, new double[]{
                20, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79
        });

        int[][] aviList = new int[][]{
                {11, 16},
                {11, 16},
                {11, 18},
                {12, 18},
                {13, 20},
                {14, 22},
                {15, 23},
                {16, 25},
                {17, 27},
                {18, 30},
                {20, 32},
                {22, 35},
                {23, 38},
                {25, 39}
        };

        int[][] apiList = new int[][]{
                {15, 22},
                {15, 22},
                {15, 23},
                {16, 24},
                {16, 25},
                {17, 26},
                {18, 27},
                {18, 27},
                {18, 29},
                {19, 30},
                {20, 31},
                {21, 32},
                {21, 33},
                {22, 33},
                {25, 39}
        };
        int[] aviInterval = aviList[ageInterval];
        int[] apiInterval = apiList[ageInterval];


        int[] aviAndApi = new int[2];

        if (avi < aviInterval[0] || avi > aviInterval[1]) {
            if (avi < aviInterval[0]) {
                //todo 偏低
                aviAndApi[0] = -1;
            } else {
                //todo 偏高
                aviAndApi[0] = 1;
            }
        } else {
            //todo 正常
            aviAndApi[0] = 0;
        }

        if (api < apiInterval[0] || api > apiInterval[1]) {
            if (api < apiInterval[0]) {
                //todo 偏低
                aviAndApi[1] = -1;
            } else {
                //todo 偏高
                aviAndApi[1] = 1;
            }
        } else {
            //todo 正常
            aviAndApi[1] = 0;
        }
        return aviAndApi;
    }

}
