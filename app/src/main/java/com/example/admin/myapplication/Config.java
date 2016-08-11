package com.example.admin.myapplication;


import com.example.admin.myapplication.bean.FaceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2016/8/4.
 */
public class Config {

    public static  HashMap<String,Integer> faceMap = new HashMap<>();

    static {
        faceMap.put("f_static_001",R.mipmap.f_static_001);
        faceMap.put("f_static_002",R.mipmap.f_static_002);
        faceMap.put("f_static_003",R.mipmap.f_static_003);
        faceMap.put("f_static_004",R.mipmap.f_static_004);
        faceMap.put("f_static_005",R.mipmap.f_static_005);
        faceMap.put("f_static_006",R.mipmap.f_static_006);
        faceMap.put("f_static_007",R.mipmap.f_static_007);
        faceMap.put("f_static_008",R.mipmap.f_static_008);
        faceMap.put("f_static_009",R.mipmap.f_static_009);
        faceMap.put("f_static_010",R.mipmap.f_static_010);
        faceMap.put("f_static_011",R.mipmap.f_static_011);
        faceMap.put("f_static_012",R.mipmap.f_static_012);
        faceMap.put("f_static_013",R.mipmap.f_static_013);
        faceMap.put("f_static_014",R.mipmap.f_static_014);
        faceMap.put("f_static_015",R.mipmap.f_static_015);
        faceMap.put("f_static_016",R.mipmap.f_static_016);
        faceMap.put("f_static_017",R.mipmap.f_static_017);
        faceMap.put("f_static_018",R.mipmap.f_static_018);
        faceMap.put("f_static_019",R.mipmap.f_static_019);
        faceMap.put("f_static_020",R.mipmap.f_static_020);
    }


    public static List<FaceBean> initFace(){
        List<FaceBean> lists = new ArrayList<>();
        lists.add(new FaceBean(R.mipmap.f_static_001,"f_static_001"));
        lists.add(new FaceBean(R.mipmap.f_static_002,"f_static_002"));
        lists.add(new FaceBean(R.mipmap.f_static_003,"f_static_003"));
        lists.add(new FaceBean(R.mipmap.f_static_004,"f_static_004"));
        lists.add(new FaceBean(R.mipmap.f_static_005,"f_static_005"));
        lists.add(new FaceBean(R.mipmap.f_static_006,"f_static_006"));
        lists.add(new FaceBean(R.mipmap.f_static_007,"f_static_007"));
        lists.add(new FaceBean(R.mipmap.f_static_008,"f_static_008"));
        lists.add(new FaceBean(R.mipmap.f_static_009,"f_static_009"));
        lists.add(new FaceBean(R.mipmap.f_static_010,"f_static_010"));
        lists.add(new FaceBean(R.mipmap.f_static_011,"f_static_011"));
        lists.add(new FaceBean(R.mipmap.f_static_012,"f_static_012"));
        lists.add(new FaceBean(R.mipmap.f_static_013,"f_static_013"));
        lists.add(new FaceBean(R.mipmap.f_static_014,"f_static_014"));
        lists.add(new FaceBean(R.mipmap.f_static_015,"f_static_015"));
        lists.add(new FaceBean(R.mipmap.f_static_016,"f_static_016"));
        lists.add(new FaceBean(R.mipmap.f_static_017,"f_static_017"));
        lists.add(new FaceBean(R.mipmap.f_static_018,"f_static_018"));
        lists.add(new FaceBean(R.mipmap.f_static_019,"f_static_019"));
        lists.add(new FaceBean(R.mipmap.f_static_020,"f_static_020"));
        lists.add(new FaceBean(R.mipmap.f_static_021,"f_static_021"));
        return lists;
    }

}
