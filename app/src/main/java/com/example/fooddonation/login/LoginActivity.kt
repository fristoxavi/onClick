package com.example.fooddonation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.R
import com.example.fooddonation.databinding.ActivityLoginBinding
import com.example.fooddonation.home.HomeActivity
import com.example.fooddonation.register.RegisterActivity
import com.example.fooddonation.utils.Common

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
   private lateinit var viewModel:LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupObserver()

    }
    private fun setupBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this)[(LoginViewModel::class.java)]
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this
        binding.newuser.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }
    }

    private fun setupObserver() {
        viewModel.getClicked().observe(this) {
            startActivity(Intent(this, HomeActivity::class.java))

            Toast.makeText(this, "Okk", Toast.LENGTH_SHORT).show()
        }

        viewModel.getError().observe(this) {
            Common.materialalertdialog(
                context = this,
                title = "",
                msg = it,
                postxt = "OK",
                negtxt = "",
                posclick = { dialogInterface, _ ->
                    dialogInterface.cancel()
                },
                negclick = null
            )
        }
    }
}
