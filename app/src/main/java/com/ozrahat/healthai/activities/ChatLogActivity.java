package com.ozrahat.healthai.activities;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ozrahat.healthai.R;
import com.ozrahat.healthai.adapters.MessageAdapter;
import com.ozrahat.healthai.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChatLogActivity extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private List<ChatMessage> messageList;

    private RecyclerView recyclerView;

    private EditText inputTextField;
    private ImageButton sendButton;
    private ImageButton recordButton;
    private Toolbar toolbar;

    private static final int REQUEST_CODE_STT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);

        // Setup UI Components
        setupComponents();

        // Setup Toolbar.
        setupToolbar();

        // Handle click events for UI elements.
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void setupListeners() {
        sendButton.setOnClickListener(v -> {
            String message = inputTextField.getText().toString();
            if(!message.isEmpty()){
                // Create a chat message object defined before.
                ChatMessage chatMessage = new ChatMessage(1, message, System.currentTimeMillis());
                // Then add it to the messages list and update the UI.
                messageList.add(chatMessage);
                messageAdapter.notifyDataSetChanged();
                inputTextField.getText().clear();

                // Choosing some hardcoded answers for chatbot to use until the sockets configured.
                String[] replies = new String[] {
                        "Hello there, I am a chatbot and I'm here to help you!",
                        "Hi! I can help you with your healthcare if you tell me yourself a bit more.",
                        "Hello! Please explain your health situation. Later I will make some deductions."};

                // Adding the chatbot answer to the messages list and updating the UI.
                ChatMessage respondMesssage = new ChatMessage(0, replies[new Random().nextInt(replies.length)], System.currentTimeMillis());
                messageList.add(respondMesssage);
                messageAdapter.notifyDataSetChanged();

                recyclerView.smoothScrollToPosition(messageList.size()-1);
            }
        });

        recordButton.setOnClickListener(v -> {
            // User pressed mic button.
            // We will try to launch the Speech to Text interface.
            Intent sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now...");

            try{
                startActivityForResult(sttIntent, REQUEST_CODE_STT);
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.warning_stt_not_supported), Toast.LENGTH_SHORT).show();
            }
            startActivityForResult(sttIntent, REQUEST_CODE_STT);
        });
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
}