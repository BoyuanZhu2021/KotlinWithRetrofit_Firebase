package com.sana.kotlinwithretrofit.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.sana.kotlinwithretrofit.R
import android.widget.Toast
import com.google.firebase.auth.GoogleAuthProvider
import com.sana.kotlinwithretrofit.UserActivity
import com.sana.kotlinwithretrofit.UserDetailsActivity


class SignInActivity: AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 120
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Set up Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        mAuth = FirebaseAuth.getInstance()

        val signInBtn = findViewById<Button> (R.id.sign_in_btn)

        signInBtn.setOnClickListener{
            signIn()
        }

    }

    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception

            if(task.isSuccessful){
                try{
                    //Google Sign in successful, authenticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e:ApiException){
                    // sign in failed, update UI
                    Log.w("SignInActivity","Google sign in failed")
                }
            }else{
                Log.w("SignInActivity",exception.toString())
            }
            }
    }

    private fun firebaseAuthWithGoogle(idToken:String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    // sign in success, update the UI with user info
                    Log.d("SignInActivity", "signInWithCredentialSuccess")
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    // if sign in failed, prompt message
                    Log.d("SignInActivity", "signInWithCredentialFailed")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignInActivity", "signInWithCredentialFailure", exception)
                Toast.makeText(this, "Authentication failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}