package com.example.demo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.demo.util.List2TreeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  list2Tree测试
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since:
 */
public class List2TreeTest {

    public static void main(String[] args) {
        List<Map<String,Object>> data = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("id",0);
        map.put("pid",1);
        map.put("name","水果");
        data.add(map);
        Map<String,Object> map2 = new HashMap<>();
        map2.put("id",1);
        map2.put("pid",2);
        map2.put("name","苹果");
        data.add(map2);
        Map<String,Object> map3 = new HashMap<>();
        map3.put("id",3);
        map3.put("pid",0);
        map3.put("name","葡萄");
        data.add(map3);
        Map<String,Object> map4 = new HashMap<>();
        map4.put("id",4);
        map4.put("pid",0);
        map4.put("name","哈密瓜");
        data.add(map4);
        Map<String,Object> map5 = new HashMap<>();
        map5.put("id",5);
        map5.put("pid",0);
        map5.put("name","桃子");
        data.add(map5);
        Map<String,Object> map6 = new HashMap<>();
        map6.put("id",6);
        map6.put("pid",5);
        map6.put("name","水蜜桃");
        data.add(map6);
        Map<String,Object> map7 = new HashMap<>();
        map7.put("id",7);
        map7.put("pid",6);
        map7.put("name","坏掉的");
        data.add(map7);
        JSONArray result = List2TreeUtil.listToTree(JSONUtil.parseArray(JSONUtil.parse(data)),"id","pid","children");
        System.out.println(JSONUtil.parse(result));
    }
}
