package com.example.fooddonation.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.fooddonation.R
import com.example.fooddonation.chat.ChatActivity
import com.example.fooddonation.databinding.ActivityServiceDetailBinding
import com.example.fooddonation.map.MapActivity

class ServiceDetailActivity : AppCompatActivity() {
    private  lateinit var binding:ActivityServiceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUp()
    }
    private fun setUpBinding(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_service_detail)

    }
    private fun setUp(){
        val intent = intent
        val userId =  intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName").toString()
        val serviceName = intent.getStringExtra("serviceName").toString()
        val place = intent.getStringExtra("place").toString()
        val phoneNumber = intent.getStringExtra("phoneNumber").toString()
        binding.phoneNumber.setText(phoneNumber)
        binding.place.setText(place)
        binding.username.setText(userName)
        binding.serviceNames.setText(serviceName)
        binding.chat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }
        binding.place.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("place",place)
            startActivity(intent)
        }



    }
}