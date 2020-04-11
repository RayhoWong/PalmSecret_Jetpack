package com.palmapp.master.module_palm.scan;

import androidx.annotation.NonNull;

import com.palmapp.master.module_palm.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

/**
 * @author :     xiemingrui
 * @since :      2020/3/13
 */
public class PalmResultCache implements Serializable {
    private static HashMap<String, Integer> sMap = new HashMap<>();

    {
        sMap.put("Brittany Lannister", R.mipmap.palm_portrait_1);
        sMap.put("Caleb Bates", R.mipmap.palm_portrait_2);
        sMap.put("Carlos Benson", R.mipmap.palm_portrait_3);
        sMap.put("Lilly Briggs", R.mipmap.palm_portrait_4);
        sMap.put("Miranda Coombs", R.mipmap.palm_portrait_5);
        sMap.put("Julian Farmiga", R.mipmap.palm_portrait_6);
    }

    public String life_length = "", business_length = "", love_pos = "", money_pos = "", marry_type = "", thumb_length = "", shizhi_length = "", fuck_length = "", wuming_length = "", small_length = "", palm_type = "",palm_style="";
    public int finger_length = -1;
    public int palm_width = -1;
    public int palm_length = -1;

    public String expert_name;

    public int getExpertIcon() {
        Integer id = sMap.get(expert_name);
        if (id == null) {
            return R.mipmap.palm_portrait_1;
        } else {
            return id;
        }
    }

    public void randomName(){
        int index = new Random().nextInt(6);
        expert_name= (String) sMap.keySet().toArray()[index];
    }
}
