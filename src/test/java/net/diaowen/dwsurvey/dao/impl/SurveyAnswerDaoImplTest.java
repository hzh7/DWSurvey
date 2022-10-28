package net.diaowen.dwsurvey.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.diaowen.dwsurvey.dao.SurveyAnswerDao;
import net.diaowen.dwsurvey.entity.SurveyAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SurveyAnswerDaoImplTest {

    @Autowired
    SurveyAnswerDao surveyAnswerDao;
    @Test
    void saveAnswer() {
//        SurveyAnswer qq = surveyAnswerDao.get("qq");
//        System.out.println(qq.getQuAnswerInfo());


        Map<String,Map<String,Object>> map=new HashMap<String,Map<String,Object>>();
        //Map 对象存入 用户名,密码,电话号码
        map.put("username", new HashMap<>());
        map.put("pwd",  new HashMap<>());
        map.put("telephone",  new HashMap<>());
        map.get("pwd").put("111", 2222);
        //Map 转成  JSONObject 字符串
        String jsonString = JSON.toJSONString(map);
        System.out.println("json字符串是："+jsonString);


        String str = "{\"telephone\":{},\"pwd\":{\"111\":2222},\"username\":{}}";
//        JSONObject  jsonObject = JSONObject.parseObject(str);
//        System.out.println("json对象是：" + jsonObject);
//        Object object = jsonObject.get("name");
//        System.out.println("name值是："+object);

        JSONObject  jsonObject = JSONObject.parseObject(str);
        //json对象转Map
        Map<String,Object> map1 = jsonObject;
        System.out.println("map对象是：" + map1);
        Object object = map.get("pwd");
        Map<String,Object> map2 = (Map<String, Object>) object;
        System.out.println(map2.get("111"));
        System.out.println(map2.get("222"));
//        System.out.println("pwd的值是"+object);

    }
}