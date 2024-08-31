package com.example.chatapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.adapter.MessagesAdapter
import com.example.chatapplication.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter(messagesList)
        binding.recyclerView.adapter = messagesAdapter

        val contactNumber = intent.getStringExtra("CONTACT_NUMBER")

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(contactNumber, message)
            }
        }

        loadMessages(contactNumber)
    }

    private fun sendMessage(contactNumber: String?, message: String) {
        val currentUser = auth.currentUser
        val messageData = hashMapOf(
            "sender" to currentUser?.uid,
            "receiver" to contactNumber,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("messages").add(messageData)
            .addOnSuccessListener {
                binding.messageInput.text.clear()
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun loadMessages(contactNumber: String?) {
        val currentUserUid = auth.currentUser?.uid
        db.collection("messages")
            .whereIn("receiver", listOf(currentUserUid, contactNumber))
            .whereIn("sender", listOf(currentUserUid, contactNumber))
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val newMessages = snapshots?.documents?.mapNotNull { it.getString("message") } ?: emptyList()
                messagesList.clear()
                messagesList.addAll(newMessages)
                messagesAdapter.updateMessages(messagesList)
            }
    }
}
