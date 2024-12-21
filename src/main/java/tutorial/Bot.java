package tutorial;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    Map<String,Double> map1 = new HashMap<>();
    private String name;


    public String getBotUsername() {
        //какой-то код
        return "TutorialBot";
    }

    @Override
    public String getBotToken() {
        return "7641801953:AAFNbzraA6O41AZPCdscuQQiSLdl--ZKAfs";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            //это ответ бота
            String answer = "hello";
            Long userId = null;

            if (update.hasMessage() && update.getMessage().hasText()) { //если пользователь отправил боту текст

                userId = whenUserSendTextToBot(update);

            } else if (update.hasCallbackQuery()) { //проверка что именно нажатие на кнопку
                CallbackQuery callbackQuery = update.getCallbackQuery(); //информация о нажатии на кнопку
                whenUserClickOnButtonInBot(callbackQuery, userId, answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendMessageFromBot(String answer, Long userId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(userId.toString());
        sm.setText(answer);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * внутри этого метода код, который срабатывает, когда пользователь боту отправляет какой-то текст
     * @param update
     * @return id пользователя, который отправил боту текст
     */
    Long whenUserSendTextToBot(Update update) {
        Long id;
        Message msg = update.getMessage();
        String message = msg.getText();//сообщение пользователя

        User user = msg.getFrom(); //сам пользователь
        id = user.getId(); // id пользователя
        String userName = user.getUserName(); //юзернейм пользователя

        if (message.equals("/catfact"))  {
            String json = getCatFact();
            sendMessageFromBot("Данные о крипте: " + json, id);

        }
        return id;
    }


    void whenUserClickOnButtonInBot(CallbackQuery callbackQuery, Long userId, String answer) {
        userId = callbackQuery.getFrom().getId();

        String callbackData = callbackQuery.getData(); //то что записали в setCallbackData
        if (callbackData.equals("1")) {
            sendSticker(userId, "CAADBQADiQMAAukKyAPZH7wCI2BwFxYE");
        }
        if (callbackData.equals("5")) {
            sendSticker(userId, "CAACAgIAAxkBAAELFqxnZrGLFSocTeIr51B0aZxrtKeeHQACixkAAsdQEUgftNzUEaWFKDYE");
        }
        if (callbackData.equals("2")) {
            answer = "привет";
        }
    }


    // отдельный метод
    private void sendSticker(Long chatID, String name) {
        try {
            SendSticker sticker_msg = new SendSticker();
            sticker_msg.setChatId(chatID.toString());
            sticker_msg.setSticker(new InputFile(name));
            execute(sticker_msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getCatFact() {
        String s = "";
        try {
            URL url = new URL("https://api.kucoin.com/api/v1/market/stats?symbol=BTC-USDT");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                    s = s + inputLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    //убрать клавиатуру
    //добавить доп команду /catfact
}
