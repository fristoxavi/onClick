package com.example.fooddonation.chat

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddonation.R
import com.example.fooddonation.adapter.ChatAdapter
import com.example.fooddonation.databinding.ActivityChatBinding
import com.example.patientapp.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var viewModel:ChatViewModel
    private var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var name: String = ""
    private var dates: String? = ""
    private var description: String? = ""
    private var imageData: Uri? = null
    private var url: String? = ""
    private var chatList = ArrayList<ChatModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpChat()
        setUpNavigation()

    }
    private fun setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ViewModelProvider(this)[(ChatViewModel::class.java)]
        binding.chatVm = viewModel
        binding.lifecycleOwner = this
        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
    private fun setUpChat() {
        val intent = intent
        val userId =  intent.getStringExtra("userId")
        name = intent.getStringExtra("userName").toString()
        // var docImage = intent.getStringExtra("DocImage")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Doctor").child(userId!!)
        readMessage(firebaseUser!!.uid, userId)

        binding.imageChooser.setOnClickListener {
            //chooseImage()
        }

        binding.btnSendMessage.setOnClickListener {
            val message: String = binding.etMessage.text.toString()

            if (message.isEmpty()) {
                if (imageData == null) {
                    Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    // If message is empty but an image is selected, upload the image
                    uploadImageAndSendMessage(firebaseUser!!.uid, userId)
                    binding.etMessage.setText("")
                    return@setOnClickListener
                }
            }


            sendMessage(firebaseUser!!.uid, userId, message)
            binding.etMessage.setText("")
//            topic = "/topics/$userId"
//            val tokens  = "dTXrZnkjQZGtNFWltEPeFn:APA91bFh1rAyUlmErfa1YLPJ9EuxUI2jve7MTD4dmUDqfTOgN20yX8MhreYZ8FxlTUHNbMYLprvl_LQF55lhJov838BJKKEbB8twjVtvkRDPBfjpikcMS_vwDDXAci6LRSV2l4S0wT9p"
//            PushNotification(
//                NotificationData( docName,message),
//                topic).also {
//                sendNotification(it)
//            }

        }
    }

    private fun uploadImageAndSendMessage(senderId: String, receiverId: String) {
        // Upload the selected image to Firebase Storage
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(imageData!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    sendMessage(senderId, receiverId, "", imageUrl)
                    clearImageData()
                }
            }
            .addOnFailureListener {
                // Handle image upload failure
                Toast.makeText(applicationContext, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String, imageUrl: String = "") {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        if (imageUrl.isNotEmpty()) {
            hashMap["sendImage"] = imageUrl
        }
        reference.child("Chat").push().setValue(hashMap)
        clearImageData()
    }
    private fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(ChatModel::class.java)

                    if (chat!!.senderId == senderId && chat.receiverId.equals(receiverId) ||
                        chat.senderId == (receiverId) && chat.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
    }
    private fun clearImageData() {
        url = ""
    }
    private fun setUpNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}