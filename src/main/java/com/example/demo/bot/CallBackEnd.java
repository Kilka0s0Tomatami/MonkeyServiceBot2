package com.example.demo.bot;

import com.example.demo.configuration.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CallBackEnd {
    @Autowired
    final BotConfig config;

    public CallBackEnd(BotConfig config) {
        this.config = config;
    }

    public void sendRequest(HttpMethod method, String command, Map<String, String> arguments) {
        // Создаем объект RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Long telegramID =  Long.parseLong(arguments.get("telegramID"));
        int customerID = Integer.parseInt(arguments.get("customerID"));
        int folderID = Integer.parseInt(arguments.get("folderID"));

        String jsonBody = "{\"telegramID\": \"" + telegramID + "\", \"customerID\": \"" + customerID + "\", \"folderID\": \"" + folderID + "\"}";
        // Создаем тело запроса (если необходимо)

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);

        // Создаем объект HttpEntity с заголовками и телом запроса
        ResponseEntity<String> response = restTemplate.exchange(config.getBackendUri() + "/grant-access", HttpMethod.POST, httpEntity, String.class);

    }

}
