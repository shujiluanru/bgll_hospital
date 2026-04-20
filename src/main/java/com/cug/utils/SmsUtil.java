package com.cug.utils;

import cn.hutool.json.JSONUtil;
import com.cug.constant.AuthConstant;
import com.cug.exception.SmsException;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SmsUtil {
    private final static OkHttpClient client = new OkHttpClient();

    public static void getSmsCode(String phone,String host,String path,String appcode,String templateId,String code) throws Exception {
        Map<String, String>headers=new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String>querys=new HashMap<>();
        Map<String, String>bodys=new HashMap<>();
        bodys.put("phone_number", phone);
        bodys.put("template_id", templateId);
        bodys.put("content", "code:"+code);
        bodys.put("content_type", "application/x-www-form-urlencoded; charset=UTF-8");
        HttpResponse response = HttpUtil.doPost(host, path, "POST", headers, querys, bodys);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity,"UTF-8");
        System.out.println(result);
    }
    public static String generateCode()
    {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++)
        {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
