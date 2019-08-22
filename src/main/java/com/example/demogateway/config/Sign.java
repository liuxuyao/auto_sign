package com.example.demogateway.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2019/8/15.
 */
public class Sign {

    //private static ResourceBundle resourceBundle=ResourceBundle.getBundle("application.properties");
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {

        Properties properties = new Properties();
        InputStream inputStream = Sign.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String account = properties.getProperty("account");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String loginUrl = properties.getProperty("loginUrl");
        System.out.println("账户：" + account);
        System.out.println("密码：" + password);
        System.out.println("URL：" + url);
        System.out.println("LOGINURL：" + loginUrl);
        String token = getToken(account, password,loginUrl);
        while (true) {
            String code = getSign(token,url);
            if (code.equals("100200")) {
                break;
            } else if (code.equals("104169")) {
                break;
            } else if (code.equals("100109")) {
                break;
            }  else if (code.equals("100103")) {
                break;
            } else {
                token = getToken(account, password,loginUrl);
            }
            break;

        }

    }

    private static String getSign(String token,String url) {
        String loginUrl = "https://www.bjex.top/activitySign/sign/in";
        if(StringUtils.hasText(url)){
                loginUrl=url;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "token=" + token);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Referer","activeWeb/?token");
        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<JSONObject> jsonpObject;
        jsonpObject = restTemplate.exchange(loginUrl, HttpMethod.GET, httpEntity, JSONObject.class);

        System.out.println("打卡响应=" + jsonpObject.getBody().toJSONString());
        return jsonpObject.getBody().getString("code");

    }

    private static String getToken(String account, String password,String logiUrl) {

        String loginUrl = "https://www.bjex.top/exchangeApi/user/login?brokerId=10003";
        if(StringUtils.hasText(logiUrl)){
            loginUrl=logiUrl;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "login-password=" + password + ",account-no=" + account);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<JSONObject> jsonpObject;
        jsonpObject = restTemplate.exchange(loginUrl, HttpMethod.GET, httpEntity, JSONObject.class);

        System.out.println("登录响应=" + jsonpObject.getBody().toJSONString());
        if (jsonpObject.getBody().getString("code").equals("100200")) {
            String token = jsonpObject.getBody().getJSONObject("data").getString("token");
            System.out.println("----------------");
            System.out.println("token=" + token);
            return token;
        }
        return null;
    }
}
