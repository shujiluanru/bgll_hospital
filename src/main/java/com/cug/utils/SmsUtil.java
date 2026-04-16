package com.cug.utils;

import cn.hutool.json.JSONUtil;
import com.cug.constant.AuthConstant;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SmsUtil {
    private final static OkHttpClient client = new OkHttpClient();

    public static void getSmsCode(String phone,String url,String appcode,String templateId,String code)
    {
        Map<String,String> params = new HashMap<>();
        params.put("phone_number",phone);
        params.put("template_id",templateId);
        params.put("content","code:"+code);
        String jsonStr = JSONUtil.toJsonStr(params);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType,jsonStr);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "APPCODE " + appcode)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException(AuthConstant.SEND_SMS_FAIL);
            }
            // 可选：记录成功日志
        } catch (IOException e) {
            throw new RuntimeException("短信发送异常: " + e.getMessage(), e);
        }

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
