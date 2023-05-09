package com.sana.kotlinwithretrofit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sana.kotlinwithretrofit.common.BaseActivity
import com.sana.kotlinwithretrofit.utilities.Constants.RESULT_CODE
import java.lang.Exception
import com.sana.kotlinwithretrofit.common.ScaleListener
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase





/* this class controls the activity_user_details.xml */
class UserDetailsActivity : BaseActivity() {

    // Add FirebaseDatabase and DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference

    var userType: String = ""
    var userName: String = ""
    var website: String = ""
    var itemPosition: Int = -1
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        initialise()

        // Initialize FirebaseDatabase and DatabaseReference
        database = FirebaseDatabase.getInstance()
        userReference = database.getReference("users")

    }

    private fun initialise() {

        super.init()
        //image = findViewById(R.id.image)

        try {

            var intent = getIntent()

            if (intent != null) {

                /* initalize hints from the saved user information */
                if (intent.hasExtra("userId")) {
                    userId = intent.getStringExtra("userId").toString()
                }

                // bounding the current object position
                if (intent.hasExtra("itemPosition")) {
                    itemPosition = intent.getIntExtra("itemPosition", -1)
                }

                if (intent.hasExtra("website")) {
                    website = intent.getStringExtra("website").toString()
                    val etWebsite = findViewById<TextInputEditText>(R.id.et_website)
                    etWebsite.hint = website
                }

                if (intent.hasExtra("username")) {
                    userName = intent.getStringExtra("username").toString()
                    val etUserName = findViewById<TextInputEditText>(R.id.et_name)
                    etUserName.hint = userName
                }

                if (intent.hasExtra("userType")) {
                    userType = intent.getStringExtra("userType").toString()
                    val etUserType = findViewById<TextInputEditText>(R.id.et_userType)
                    etUserType.hint = "*************"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // When Press button "update"
        val btnUpdate = findViewById<Button>(R.id.btn_update)
        btnUpdate.setOnClickListener {

            val etUserName = findViewById<TextInputEditText>(R.id.et_name)
            val etUserType = findViewById<TextInputEditText>(R.id.et_userType)
            val etWebsite = findViewById<TextInputEditText>(R.id.et_website)

            val updatedUserName = etUserName.text.toString()
            val updatedUserType = etUserType.text.toString()
            val updatedWebsite = etWebsite.text.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("updatedItemPosition", itemPosition)
            resultIntent.putExtra("updatedUserName", updatedUserName)
            resultIntent.putExtra("updatedUserType", updatedUserType)
            resultIntent.putExtra("updatedWebsite", updatedWebsite)

            // Update the user data in Firebase
            updateUser(userId, updatedUserName, updatedUserType, updatedWebsite)

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // When Press button "Show Password"
        val btnShow = findViewById<Button>(R.id.btn_show)
        btnShow.setOnClickListener {
            Toast.makeText(this, userType, Toast.LENGTH_SHORT).show()
        }

        // When Press button "Delete"
        val btnDelete = findViewById<Button>(R.id.btn_delete)
        btnDelete.setOnClickListener {
            Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show()

            val intent = Intent(
                this,
                UserActivity::class.java
            ) // Replace 'MainActivity' with the name of your main screen activity
            //startActivity(intent)
            // Delete the user data from Firebase
            deleteUser(userId)
            setResult(RESULT_CODE, intent)
            finish()
        }
        }
    // Method to update the user data in Firebase
    private fun updateUser(
        userId: String,
        userName: String,
        userType: String,
        website: String
    ) {
        val userUpdates = hashMapOf<String, Any>(
            "login" to userName,
            "type" to userType,
            "website" to website
        )

        userReference.child(userId).updateChildren(userUpdates).addOnCompleteListener {
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    // Method to delete the user data from Firebase
    private fun deleteUser(userId: String) {
        userReference.child(userId).removeValue().addOnCompleteListener {
            Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CODE)
            finish()
        }
    }
}

    /* initalize hints from the saved user information *//*
        website = intent.getStringExtra("website")
        val etWebsite = findViewById<TextInputEditText>(R.id.et_website)
        etWebsite.hint = website

        userName = intent.getStringExtra("username")
        val etUserName = findViewById<TextInputEditText>(R.id.et_name)
        etUserName.hint = userName

        userType = intent.getStringExtra("userType")
        val etUserType = findViewById<TextInputEditText>(R.id.et_userType)
        etUserType.hint = userType

    }*/

    /* Delete user */

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when (item.itemId){
            R.id.btn_delete ->{

                var intent = Intent(this, UserActivity::class.java)
                setResult(RESULT_CODE, intent)
                finish()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

   /* private fun initialise(){
        super.init()
        //image = findViewById(R.id.image)
        try{

            var intent = getIntent()

            if(intent != null){

                if(intent.hasExtra("username")){
                    userName = intent.getStringExtra("username")
                }

                if(intent.hasExtra("userType")){
                    userType = intent.getStringExtra("userType")
                }

                *//*if(intent.hasExtra("image") && !intent.getStringExtra("image").equals("")){

                    Glide.with(this).load(intent.getStringExtra("image"))
                        .apply(RequestOptions().centerCrop())
                        .into(image)
                }*//*
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

        toolbar!!.setTitle(userName)
        toolbar!!.setSubtitle(userType)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when (item.itemId){
            R.id.menu_delete ->{

                var intent = Intent(this, UserActivity::class.java)
                setResult(RESULT_CODE, intent)
                finish()

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    *//*override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector!!.onTouchEvent(event)
        return true
    }

}*/