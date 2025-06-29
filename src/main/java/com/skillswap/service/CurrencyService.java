package com.skillswap.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.json.JSONObject;

@Service
public class CurrencyService {
    private static final String API_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    public Double getRubToUsdRate() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            return json.getJSONObject("Valute").getJSONObject("USD").getDouble("Value");
        }
        throw new RuntimeException("Не удалось получить курс валют");
    }

    public Double getRubToEurRate() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            return json.getJSONObject("Valute").getJSONObject("EUR").getDouble("Value");
        }
        throw new RuntimeException("Не удалось получить курс валют");
    }
} 