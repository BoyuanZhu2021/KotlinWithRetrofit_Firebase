package com.sana.kotlinwithretrofit.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

    private lateinit var auth: FirebaseAuth
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

        auth = FirebaseAuth.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val signInBtn = findViewById<Button> (R.id.sign_in_btn)

        signInBtn.setOnClickListener{
            signIn()
        }

    }

    private fun signIn() {
        // Sign out the current user to ensure they are prompted to choose an account
        googleSignInClient.signOut().addOnCompleteListener {
            // Proceed with signing in after successful sign-out
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
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

    fun signInWithEmailPassword(view: View) {
        // Get user input from EditText views
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        // Check if email and password are not empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, go to the next activity
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Show error message if email or password is empty
            Toast.makeText(baseContext, "Please enter email and password.",
                Toast.LENGTH_SHORT).show()
        }
    }

    // Register a new email psw account
    fun registerWithEmailPassword(view: View) {
        // Get user input from EditText views
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        // Check if email and password are not empty
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success, go to the next activity
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If registration fails, display a message to the user
                        Toast.makeText(baseContext, "Registration failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Show error message if email or password is empty
            Toast.makeText(baseContext, "Please enter email and password.",
                Toast.LENGTH_SHORT).show()
        }
    }


}