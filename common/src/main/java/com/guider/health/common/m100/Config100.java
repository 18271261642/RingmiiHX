package com.guider.health.common.m100;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haix on 2019/8/1.
 */

public class Config100 {


    public static final List<FamilyMember> MEMBERS = new ArrayList<>();
    public static int MEMBERS_GROUP_ID;
    static{
        FamilyMember familyMember2 = new FamilyMember();
        familyMember2.setName("测试");
        familyMember2.setGender("女");
        MEMBERS.add(familyMember2);
    }
}
