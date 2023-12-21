package com.example.demo.controller;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.json.simple.JSONObject;


import com.example.demo.bot.MonkeyServiceBot;

@RestController

public class AccessControler {
    @Autowired
    MonkeyServiceBot bot;

    @GetMapping("/get-access")
    public String getAccess(
            @RequestParam("telegramID") long telegramID,
            @RequestParam("userID") int userID,
            @RequestParam("userName") String userName,
            @RequestParam("folderID") int folderID,
            @RequestParam("folderName") String folderName

    ) {
        String messageText = "Пользователь "
                + userName
                + " (ID: " + userID + ")"
                + " запрашивает доступ к папке "
                + folderName;

        String buttonData = userID
                + ":" + userName
                + ":" + folderID
                + ":" + folderName;

        bot.accessRequest(telegramID, messageText, buttonData);

        return "Функция успешно сработала, переданные значения: "
                + telegramID + " "
                + userID + " "
                + userName + " "
                + folderID + " "
                + folderName;
    }

    @GetMapping("/notification")
    public String notification(
            @RequestParam("telegramId") long telegramId,
            @RequestParam("userId") int userId,
            @RequestParam("username") String username

    ) {
        bot.notificationRequest(telegramId,
                "Ваш профиль " + username + " (ID: " + userId + ") был привязан к вашему телеграмм-аккаунту"
        );

        return "Функция успешно сработала, переданные значения: "
                + telegramId + " "
                + userId + " "
                + username;
    }

    //@PostMapping("{*command}")
    //public void postCommand(@PathVariable String command, @RequestBody String request) throws ParseException {
        //System.out.println(command);
        //System.out.println(request);
        //JSONParser jsonParser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) jsonParser.parse(request);
        //long iDStorage = (long) jsonObject.get("IDStorage");
        //System.out.println("iDStorage " + iDStorage);
    //}
}


