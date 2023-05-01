package com.sana.kotlinwithretrofit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.sana.kotlinwithretrofit.view.SignInActivity

// The very first Welcome screen
class SplashActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private val SPLASH_TIME_OUT: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, UserActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)


        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        /* if user is not authenticated, send it to SignInActivity to authenticate
        * Else send to UserActivity*/
        Handler().postDelayed({
            if(user != null){
                val dashboardIntent = Intent(this, UserActivity::class.java)
                startActivity(dashboardIntent)
                finish()
            }else{
                val signInIntent = Intent(this, SignInActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        },SPLASH_TIME_OUT)


        /*
        Handler().postDelayed({
            startActivity(Intent(this, UserActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)
        */
    }
}
