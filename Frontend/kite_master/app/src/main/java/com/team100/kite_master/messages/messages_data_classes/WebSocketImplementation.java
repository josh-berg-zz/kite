package com.team100.kite_master.messages.messages_data_classes;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketImplementation {

    private Activity WSactivity;
    private Context WScontext;

    private String username;

    private OkHttpClient client;
    private Request request;
    private WebSocket webSocket;

    private LinearLayout messageView;
    private TextView errorTextView;

    private String lastMessage;

    public WebSocketImplementation(String username, Activity WSactivity, Context WScontext, LinearLayout messageView, TextView errorTextView, String IP_ADDRESS) {

        this.WSactivity = WSactivity;
        this.WScontext = WScontext;

        this.username = username;

        this.messageView = messageView;
        this.errorTextView = errorTextView;

        this.client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS).build();
        // this.request = new okhttp3.Request.Builder().url("ws://echo.websocket.org").build();
        this.request = new okhttp3.Request.Builder().url("http://chat." + IP_ADDRESS + ":5000").build();
        this.webSocket = client.newWebSocket(request, new KiteWebSocketListener());
    }

    // Helper methods for this class and outer classes
    public boolean sendJSONText(String TextString) {

        JsonObject JsonText = new JsonObject();

        JsonText.addProperty("username", username);
        JsonText.addProperty("text", TextString);

        boolean sent = webSocket.send(JsonText.toString());

        output(username, TextString);

        return sent;
    }

    public void receiveJSONText(String TextString) {

        JsonParser parser = new JsonParser();

        JsonObject JsonText = (JsonObject) parser.parse(TextString);

        JsonElement jsonUsername = JsonText.get("username");
        JsonElement jsonText = JsonText.get("text");

        String stringUsername = jsonUsername.getAsString();
        String stringText = jsonText.getAsString();

        output(stringUsername, stringText);
    }

    public void output(final String username, final String txt) {

        WSactivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Message msg = new Message(username, txt);
                setLastMessage(msg.getMessageTime() + "\n" + msg.getUsername() + ": " + msg.getText() + "\n");

                TextView text = new TextView(WScontext);
                text.setText(getLastMessage());
                messageView.addView(text);
            }
        });
    }

    // Getter and setter methods
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public OkHttpClient getClient() {

        return this.client;
    }

    public okhttp3.Request getRequest() {

        return this.request;
    }

    private class KiteWebSocketListener extends WebSocketListener {

        private static final int NORMAL_CLOSURE_STATUS = 1000;

        // Networking functionality
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

            sendJSONText(getUsername() + " has joined the chat");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {

            receiveJSONText(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {

            sendJSONText(getUsername() + " has left the chat");
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            errorTextView.setText("Error : " + t.getMessage());
        }
    }



    // For Mockito testing
    public String getLastMessage() {

        return this.lastMessage;
    }

    public void setLastMessage(String msg) {

        this.lastMessage = msg;
    }




    // In case retrieving past messages becomes a feature. Used as a Mockito test.
    public JSONArray getMessagesString(int numMessages) {

        return null;
    }

    public String getJSONMessage(JSONArray messages, int index) throws JSONException {

        JSONObject obj = (JSONObject) messages.get(index);

        String text = obj.getString("date") + "\n" + obj.getString("username") + ": " + obj.getString("text") + "\n";

        return text;
    }
}