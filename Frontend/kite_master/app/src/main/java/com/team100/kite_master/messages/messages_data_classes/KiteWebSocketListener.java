package com.team100.kite_master.messages.messages_data_classes;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class KiteWebSocketListener extends WebSocketListener {

    // Connection Variables
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private Activity WSactivity;
    private Context WScontext;

    private OkHttpClient client;
    private okhttp3.Request request;
    private WebSocket websocket;

    private String username;
    private String statusText;

    public KiteWebSocketListener(Activity WSactivity, Context WScontext, String username, String statusText) {

        this.WSactivity = WSactivity;
        this.WScontext = WScontext;

        this.username = username;
        this.statusText = statusText;

        this.client = new OkHttpClient.Builder().readTimeout(3,TimeUnit.SECONDS).build();
        this.request = new okhttp3.Request.Builder().url("http://chat.kite.onn.sh").build();
        // websocket = this.client.newWebSocket(this.request, new KiteWebSocketListener(username, "Status"));
    }

    // Create new connection



    // Networking functionality
    @Override
    public void onOpen(WebSocket webSocket, okhttp3.Response response) {

        sendJSONText(username + " has joined the chat");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

        receiveJSONText(text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {

        sendJSONText(username + " has left the chat");
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
        output("Error : " + t.getMessage());
    }



    // Getter and setter methods
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getStatusText() {

        return statusText;
    }

    public void setStatusText(String statusText) {

        this.statusText = statusText;
    }



    // Helper methods for this class and outer classes
    public void sendJSONText(String TextString) {

        JsonObject JsonText = new JsonObject();

        JsonText.addProperty("username", username);
        JsonText.addProperty("text", TextString);

        // websocket.send(JsonText.toString());
    }

    public void receiveJSONText(String TextString) {

        JsonParser parser = new JsonParser();

        JsonObject JsonText = (JsonObject) parser.parse(TextString);

        JsonElement jsonUsername = JsonText.get("username");
        JsonElement jsonText = JsonText.get("text");

        String stringUsername = jsonUsername.getAsString();
        String stringText = jsonText.getAsString();

        // output(stringUsername + ": " + stringText);
    }

    public void output(final String txt) {

        WSactivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                TextView text = new TextView(WScontext);

                text.setText(txt);

                // messageView.addView(text);

                // statusText.setText(txt);
            }
        });
    }

    public OkHttpClient getClient() {

        return this.client;
    }

    public okhttp3.Request getRequest() {

        return this.request;
    }

}
