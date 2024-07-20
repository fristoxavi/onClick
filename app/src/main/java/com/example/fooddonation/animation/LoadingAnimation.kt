package com.example.fooddonation.animation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.fooddonation.R

class LoadingAnimation @JvmOverloads constructor (context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val animationView: LottieAnimationView

    init {
        LayoutInflater.from(context).inflate(R.layout.loading_animation, this, true)
        animationView = findViewById(R.id.animation)
    }

    fun startAnimation() {
        animationView.playAnimation()
    }

    fun stopAnimation() {
        animationView.cancelAnimation()
        animationView.visibility = View.GONE
    }
}