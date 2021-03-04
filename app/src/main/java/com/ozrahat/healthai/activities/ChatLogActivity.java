package com.ozrahat.healthai.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ozrahat.healthai.R;
import com.ozrahat.healthai.adapters.MessageAdapter;
import com.ozrahat.healthai.models.ChatMessage;
import com.ozrahat.healthai.models.PlaceType;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.gusavila92.websocketclient.WebSocketClient;

public class ChatLogActivity extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;

    private RecyclerView recyclerView;

    private EditText inputTextField;
    private ImageButton sendButton;
    private ImageButton recordButton;
    private Toolbar toolbar;

    private WebSocketClient webSocketClient;

    private static final int REQUEST_CODE_STT = 1;
    private static final int MESSAGE_SENDER_SERVER = 0;
    private static final int MESSAGE_SENDER_CLIENT = 1;

    private FirebaseAuth firebaseAuth;

    static ChatLogActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);

        instance = this;

        // Initialize Firebase Auth.
        firebaseAuth = FirebaseAuth.getInstance();

        // Setup UI Components
        setupComponents();

        // Setup Toolbar.
        setupToolbar();

        // Handle click events for UI elements.
        setupListeners();

        // Initialize the WebSocket connection.
        setupConnection();
    }

    public static ChatLogActivity getInstance(){
        return instance;
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupComponents() {
        recyclerView = findViewById(R.id.chatlog_recyclerview);
        inputTextField = findViewById(R.id.chatlog_input_edittext);
        sendButton = findViewById(R.id.chatlog_send_button);
        recordButton = findViewById(R.id.chatlog_record_button);
        toolbar = findViewById(R.id.chatlog_toolbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatLogActivity.this, messageList);
        recyclerView.setAdapter(messageAdapter);
    }

    public void addMessageToChatlog(String message, @NonNull Integer sender) {
        if(sender.equals(MESSAGE_SENDER_CLIENT)){
            // Sender sent a message, turn it into a ChatMessage object.
            ChatMessage chatMessage = new ChatMessage(sender, message, System.currentTimeMillis(), false, true);

            // Then add it into the RecyclerView and update the UI.
            messageList.add(chatMessage);
            runOnUiThread(() -> {
                messageAdapter.notifyDataSetChanged();
                inputTextField.getText().clear();

                // We need to check if there is an previous message.
                // If there is, then check if the previous message and latest message has same sender.
                // If yes, then check if they sent at same time.
                // Like 10:15 and 10:15
                // We should hide the time of the previous message for better UI.

                if(messageAdapter.getItemCount() > 1){
                    int position = messageAdapter.getItemCount()-1;

                    while (position > 0){
                        if(messageList.get(position-1).sender == MESSAGE_SENDER_CLIENT){
                            // We compare the time of the messages.
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                            Date current = new Date(messageList.get(position).date);
                            Date previous = new Date(messageList.get(position-1).date);

                            if(sdf.format(current).equals(sdf.format(previous))){
                                // We hide the time of the previous message.
                                messageList.get(position-1).showTime = false;
                            }

                            // We should update the adapter in order to hide the message date.
                            messageAdapter.notifyItemChanged(position-1);
                            position--;
                        }else {
                            break;
                        }
                    }
                }

                recyclerView.smoothScrollToPosition(messageList.size()-1);
            });

            // Finally turn it into a JSON Object and,
            // send the same message to the server via WebSocket.
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", chatMessage.message);
                webSocketClient.send(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            // Server sent the message.
            // So we need to get the 'message' object of the JSON Object.
            try {
                JSONObject jsonObject = new JSONObject(message);

                // We got our text message.
                String textMessage = jsonObject.getString("message");

                // Now we should turn it to a ChatMessage object.
                ChatMessage chatMessage = new ChatMessage(sender, textMessage, System.currentTimeMillis(), true, true);

                // Then add it into the RecyclerView and update the UI.
                messageList.add(chatMessage);
                runOnUiThread(() -> {
                    messageAdapter.notifyDataSetChanged();

                    // We need to check if there is an previous message.
                    // If there is, then check if the previous message and latest message has same sender.
                    // If yes, we should hide the profile picture of the previous message.
                    // Also, check if they sent at same time.
                    // Like 10:15 and 10:15
                    // We should hide the time of the previous message for better UI.

                    if(messageAdapter.getItemCount() > 1){
                        int position = messageAdapter.getItemCount()-1;

                        while (position > 0){
                            if(messageList.get(position-1).sender == MESSAGE_SENDER_SERVER){
                                // We hide the profile picture of the previous message.
                                messageList.get(position-1).showProfile = false;

                                // We compare the time of the messages.
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                                Date current = new Date(messageList.get(position).date);
                                Date previous = new Date(messageList.get(position-1).date);

                                if(sdf.format(current).equals(sdf.format(previous))){
                                    // We hide the time of the previous message.
                                    messageList.get(position-1).showTime = false;
                                }

                                // We should update the adapter in order to hide the message date.
                                messageAdapter.notifyItemChanged(position-1);
                                position--;
                            }else {
                                break;
                            }
                        }
                    }

                    recyclerView.smoothScrollToPosition(messageList.size()-1);
                });

                // We need check for certain codes.
                // For example if we got code 18, we should show the user near hospitals etc.
                int responseCode = jsonObject.getInt("code");

                switch (responseCode){
                    case 17:
                        // Show the near pharmacies to the user.
                        showNearPlaces(PlaceType.PHARMACY);
                        break;
                    case 18:
                        // Show the near hospitals to the user.
                        showNearPlaces(PlaceType.HOSPITAL);
                        break;
                    case 21:
                        // Navigate to BMI Calculator.
                        startActivity(new Intent(this, BMICalculatorActivity.class));
                        break;
                    case 22:
                        // Navigate to Profile Activity.
                        // But first, check if user logged in or not.
                        if(firebaseAuth.getCurrentUser() != null){
                            // User has logged in, send him/her to Profile Activity.
                            startActivity(new Intent(ChatLogActivity.this, ProfileActivity.class));
                        }else {
                            // User hasn't logged in yet.
                            // Warn him/her with a chat message.
                            JSONObject warningJSONObject = new JSONObject();

                            try {
                                warningJSONObject.put("status", 1);
                                warningJSONObject.put("code", 10);
                                warningJSONObject.put("message", getString(R.string.warning_not_logged_in));

                                addMessageToChatlog(warningJSONObject.toString(), MESSAGE_SENDER_SERVER);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    default:
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNearPlaces(PlaceType placeType) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("place", placeType);

        startActivity(intent);
    }

    private void setupListeners() {
        sendButton.setOnClickListener(v -> {
            String message = inputTextField.getText().toString();
            if(!message.isEmpty()){
                // Add message to the messages list and update the UI.
                addMessageToChatlog(message, MESSAGE_SENDER_CLIENT);
            }
        });

        recordButton.setOnClickListener(v -> {
            // User pressed mic button.
            // We will try to launch the Speech to Text interface.
            Intent sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.tts_speak));

            try{
                startActivityForResult(sttIntent, REQUEST_CODE_STT);
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.warning_stt_not_supported), Toast.LENGTH_SHORT).show();
            }
            startActivityForResult(sttIntent, REQUEST_CODE_STT);
        });
    }

    private void setupConnection() {
        URI uri;
        try {
            uri = new URI(getString(R.string.server_address));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                // Add the welcome message into a JSON Object.
                // Then add it into the ChatLog.
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("status", 1);
                    jsonObject.put("code", 10);


                    jsonObject.put("message", getString(R.string.chat_welcome1));
                    addMessageToChatlog(jsonObject.toString(), MESSAGE_SENDER_SERVER);

                    jsonObject.put("message", getString(R.string.chat_welcome2));
                    addMessageToChatlog(jsonObject.toString(), MESSAGE_SENDER_SERVER);

                    jsonObject.put("message", getString(R.string.chat_welcome3));
                    addMessageToChatlog(jsonObject.toString(), MESSAGE_SENDER_SERVER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Also set the Toolbar subtitle as 'Online'
                runOnUiThread(() -> toolbar.setSubtitle(getString(R.string.chat_online)));
            }

            @Override
            public void onTextReceived(String message) {
                // Send the text message to the user.
                addMessageToChatlog(message, MESSAGE_SENDER_SERVER);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                // Send the byte array message to the user.
                addMessageToChatlog(new String(data), MESSAGE_SENDER_SERVER);
            }

            @Override
            public void onPingReceived(byte[] data) {}

            @Override
            public void onPongReceived(byte[] data) {}

            @Override
            public void onException(Exception e) {
                Log.d("WebSocket", e.getMessage());
                runOnUiThread(() -> Toast.makeText(ChatLogActivity.this, getString(R.string.chat_error), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onCloseReceived() {
                Log.d("Web Socket", "Closed.");

                // Remove the 'Online' label from Toolbar.
                runOnUiThread(() -> toolbar.setSubtitle(null));
            }
        };

        webSocketClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_STT){
            if(resultCode == Activity.RESULT_OK && data != null){
                // Getting the results as an array list.
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(!result.isEmpty()){
                    // This is the text recognized by API.
                    String recognizedText = result.get(0);
                    inputTextField.setText(recognizedText);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // We should close the WebSocket connection when the view destroyed.
        // This function may be called when switching light/dark theme.
        // We don't want the app to crash while changing theme!
        webSocketClient.close();
    }
}