package com.example.tuank.petrol_station

import android.animation.ValueAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_screen_getstart.*

class screen_getstart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_getstart)
        val btnget = findViewById<View>(R.id.btngetstart) as Button
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation -> tutal_scan.progress = animation.animatedValue as Float }
        animator.start()
        btnget.setOnClickListener(View.OnClickListener {
            view -> sc_scan()
        })
    }
    private fun sc_scan(){
        startActivity(Intent(this, screen_scan() ::class.java))
        finish()
    }
}
