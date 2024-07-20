package com.example.fooddonation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.fooddonation.home.HomeActivity
import com.example.fooddonation.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            Handler(this.mainLooper).postDelayed(
                {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                }, 3000
            )

        } else {

            Handler(this.mainLooper).postDelayed(
                {
                   startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                }, 3000
            )

        }
    }
}
