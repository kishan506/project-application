package com.example.project1.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.model.ChatAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRooms extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;

    private List<String> chatMessages;
    private ChatAdapter chatAdapter;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        // Listen for new messages from Firestore
        listenForMessages();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private boolean initialDataLoaded = false;
    private void listenForMessages() {
        firestore.collection("chatMessages")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error listening for messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            DocumentSnapshot document = dc.getDocument();

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String message = document.getString("message");
                                String sender = document.getString("sender");

                                // Skip initial data if already processed
                                if (initialDataLoaded) {
                                    String fullMessage = sender + ": " + message;
                                    chatMessages.add(fullMessage);
                                    chatAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        // Set the flag to true after processing initial data
                        initialDataLoaded = true;
                    }
                });
    }



    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Add the message to the list and notify the adapter
            chatMessages.add(message);
            chatAdapter.notifyDataSetChanged();

            // Clear the input field
            editTextMessage.getText().clear();

            // Send the message to Cloud Firestore
            saveMessageToFirestore(message);
        } else {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessageToFirestore(String message) {
        String senderName = "YourSenderName"; // Replace with the actual sender's name

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        messageData.put("sender", senderName);

        // Add a new document with a generated ID
        firestore.collection("chatMessages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Message sent to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message to Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
