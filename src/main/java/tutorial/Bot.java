package tutorial;
import org.json.JSONObject;
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
        return "635235291:AAGqrFFBa9y4UW2ej-Sxg9htXx8PXu3ep5w";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            if ("/start".equals(text)) {
                sendMenu(message.getChatId());
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            String[] parts = callbackData.split(":"); // Например: BTC-USDT:STATS

            if (parts.length == 2) {
                String symbol = parts[0];
                String type = parts[1];

                if ("STATS".equals(type)) {
                    String stats = getMarketStats(symbol);
                    sendText(update.getCallbackQuery().getMessage().getChatId(), stats);
                } else if ("HIGH_LOW".equals(type)) {
                    String highLow = getMarketHighLow(symbol);
                    sendText(update.getCallbackQuery().getMessage().getChatId(), highLow);
                } else if ("VOLUME".equals(type)) {
                    String volume = getMarketVolume(symbol);
                    sendText(update.getCallbackQuery().getMessage().getChatId(), volume);
                }
            }
        }
    }

    private void sendMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите валюту и информацию:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Список валют
        List<String> symbols = Arrays.asList("BTC-USDT", "ETH-USDT", "ADA-USDT");

        for (String symbol : symbols) {
            // Кнопки для каждой валюты
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton statsButton = new InlineKeyboardButton();
            statsButton.setText(symbol + " Stats");
            statsButton.setCallbackData(symbol + ":STATS");

            InlineKeyboardButton highLowButton = new InlineKeyboardButton();
            highLowButton.setText(symbol + " High/Low");
            highLowButton.setCallbackData(symbol + ":HIGH_LOW");

            InlineKeyboardButton volumeButton = new InlineKeyboardButton();
            volumeButton.setText(symbol + " Volume");
            volumeButton.setCallbackData(symbol + ":VOLUME");

            row.add(statsButton);
            row.add(highLowButton);
            row.add(volumeButton);
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMarketStats(String symbol) {
        try {
            URL url = new URL("https://api.kucoin.com/api/v1/market/stats?symbol=" + symbol);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Парсинг текущей цены
            String responseStr = response.toString();
            int lastIndex = responseStr.indexOf("\"last\":\"") + 8;
            String lastPrice = responseStr.substring(lastIndex, responseStr.indexOf("\"", lastIndex));

            return "Текущая цена " + symbol + ": " + lastPrice + " USDT";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка получения данных.";
        }
    }

    private String getMarketHighLow(String symbol) {
        try {
            URL url = new URL("https://api.kucoin.com/api/v1/market/stats?symbol=" + symbol);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Парсинг high и low
            String responseStr = response.toString();
            int highIndex = responseStr.indexOf("\"high\":\"") + 8;
            String high = responseStr.substring(highIndex, responseStr.indexOf("\"", highIndex));

            int lowIndex = responseStr.indexOf("\"low\":\"") + 7;
            String low = responseStr.substring(lowIndex, responseStr.indexOf("\"", lowIndex));

            return "Максимум за сутки: " + high + " USDT\nМинимум за сутки: " + low + " USDT";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка получения данных.";
        }
    }

    private String getMarketVolume(String symbol) {
        try {
            URL url = new URL("https://api.kucoin.com/api/v1/market/stats?symbol=" + symbol);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Парсинг объема торгов
            String responseStr = response.toString();
            int volIndex = responseStr.indexOf("\"vol\":\"") + 7;
            String volume = responseStr.substring(volIndex, responseStr.indexOf("\"", volIndex));

            return "Объем торгов " + symbol + ": " + volume + " BTC";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка получения данных.";
        }
    }
}
