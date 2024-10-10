package com.example.moveSmart.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@Slf4j
public class BusService {
    private static final String ODSAY_API_KEY = "VyjPkBhOaGJPOxjlcqVe9jUmvlfR5zvMh3IwZBMoi8w";
    private static final String ODSAY_BASE_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";

    public String searchPubTransPath(double startX, double startY, double endX, double endY) throws IOException {
        String urlInfo = ODSAY_BASE_URL + "?SX=" + URLEncoder.encode(String.valueOf(startX), "UTF-8")
                + "&SY=" + URLEncoder.encode(String.valueOf(startY), "UTF-8")
                + "&EX=" + URLEncoder.encode(String.valueOf(endX), "UTF-8")
                + "&EY=" + URLEncoder.encode(String.valueOf(endY), "UTF-8")
                + "&apiKey=" + URLEncoder.encode(ODSAY_API_KEY, "UTF-8");

        // Kết nối HTTP
        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        // Đọc dữ liệu từ API
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        conn.disconnect();

        return sb.toString();
    }
}
