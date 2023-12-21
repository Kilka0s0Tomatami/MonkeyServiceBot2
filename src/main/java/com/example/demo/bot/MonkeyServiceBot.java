package com.example.demo.bot;

import com.example.demo.configuration.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MonkeyServiceBot extends TelegramLongPollingBot {

    //private static final Logger LOG = LoggerFactory.getLogger(MonkeyServiceBot.class);
    private static final String START = "/start";
    private static final String ID = "/id";
    private static final String HELP = "/help";
    final BotConfig config;

    public MonkeyServiceBot(BotConfig config) {
        this.config = config;
    }

    @Autowired
    CallBackEnd callBackEnd;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message mess = update.getMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();

            switch (message) {
                case START -> {
                    String userName = update.getMessage().getChat().getUserName();
                    startCommand(chatID, userName);
                }
                case HELP -> {
                    String userName = update.getMessage().getChat().getUserName();
                    helpCommand(chatID, userName);
                }
                case ID -> {
                    String userName = update.getMessage().getChat().getUserName();
                    idCommand(chatID, userName);
                }
                default -> {
                    String defaultText = "Не совсем понимаю вас, пожалуйста повторите команду еще раз";
                    sendMessage(chatID, defaultText);
                }

                //LOG.info("Received from user " + message);
            }
        } else if (update.hasCallbackQuery()) {
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            String data = update.getCallbackQuery().getData();

            String[] values = data.split(":");
            String result = values[0];
            String userID = values[1];
            String userName = values[2];
            String folderID = values[3];
            String folderName = values[4];
            if (result.equals("confirm")) {
                // Фрагмент, отвечающий за вызов бекенда
                Map<String, String> arguments = new HashMap<>();
                arguments.put("telegramID", Long.toString(chatId));
                arguments.put("customerID", userID);
                arguments.put("folderID", folderID);

                callBackEnd.sendRequest(HttpMethod.POST, "grant_access", arguments);

                // Фрагмент кода, отвечающий за изменение сообщения
                String text = "Вы подтвердили доступ пользователя "
                        + userName + " (ID:" + userID + ") "
                        + "к папке " + folderName;

                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId(messageId);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    System.out.println("Ошибка отправки сообщения: " + e);
                }


            } else if (result.equals("deny")) {
                // Фрагмент кода, отвечающий за изменение сообщения
                String text = "Вы отклонили доступ пользователя "
                        + userName + " (ID:" + userID + ") "
                        + "к папке " + folderName;

                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId(messageId);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    System.out.println("Ошибка отправки сообщения: " + e);
                }

            }
        }

    }

    private void startCommand(Long chatId, String userName) {
        var text = "Добрый день, меня зовут Леон."
                + "\nЯ обезъянбот и сегодня я обслуживаю вас."
                + "\n\nID вашего telegram-профиля: " + chatId
                + "\n\nВы можете воспользоваться следующими командами:"
                + "\n/start"
                + "\n/id"
                + "\n\nДополнительные команды:"
                + "\n/help";
        var formattedText = String.format(text, userName);
        sendMessage(chatId, text);
    }

    private void idCommand(Long chatId, String userName) {
        var text = "ID вашего telegram-профиля: " + chatId;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
        System.out.println(chatId);
    }

    private void helpCommand(Long chatId, String userName) {
        var text = "Вам доступны следующие команды:"
                + "\n/start - стартовая команда, выводит приветствие, ваш telegram ID и список команд"
                + "\n/id - выводит ваш telegram ID"
                + "\n/help - выводит данное вспомогательное сообщение"
                + "\n\nПривязка telegram-аккаунта к облаку:"
                + "\nДля привязки telegram-аккаунта к вашему облаку, "
                + "вам необходимо получить ваш telegram ID, используя \nкоманду /id. "
                + "После этого, вам следует скопировать данное значение в "
                + "предусмотренное для этого поле в профиле вашего облачного хранилища. "
                + "После успешной привязки telegram-аккаунта, вам придет подтверждающее сообщение от данного бота. "
                + "\n\nПодтверждение доступа к папке:"
                + "\nПосле того, как другой пользователь воспользуется вашей гиперссылкой на указанную папку, "
                + "вам придет сообщение от данного бота, запрашивающее подтверждение предоставляемого доступа. "
                + "Вам следует использовать нужную кнопку, "
                + "после чего доступ будет соответственно либо открыт, либо отклонен.";
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    public void notificationRequest(Long chatId, String text){
        System.out.println("chatId: " + chatId + " text: " + text);
        System.out.println(chatId instanceof Long);
        sendMessage(chatId, text);
    }
    public <buttonText > void accessRequest(Long chatId, String messageText, String buttonData){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var confirmButton = new InlineKeyboardButton();
        confirmButton.setText("Подтвердить");
        confirmButton.setCallbackData("confirm:" + buttonData);

        var denyButton = new InlineKeyboardButton();
        denyButton.setText("Отклонить");
        denyButton.setCallbackData("deny:" + buttonData);

        rowInline.add(confirmButton);
        rowInline.add(denyButton);

        rowsInline.add(rowInline);

        markupInLine.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInLine);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки сообщения: " + e);
            //LOG.error("Ошибка отправки сообщения", e);
        }

        //System.out.println(messageText);
        //System.out.println(buttonText);
    }

    private void sendMessage(Long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки сообщения: " + e);
            //LOG.error("Ошибка отправки сообщения", e);
        }

    }
}
