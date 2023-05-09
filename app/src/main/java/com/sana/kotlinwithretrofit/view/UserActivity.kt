package com.sana.kotlinwithretrofit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sana.kotlinwithretrofit.common.BaseActivity
import com.sana.kotlinwithretrofit.utilities.Constants.REQUEST_CODE
import com.sana.kotlinwithretrofit.utilities.Constants.RESULT_CODE
import com.sana.kotlinwithretrofit.utilities.Utilities
import com.sana.kotlinwithretrofit.view.AddItemDialog
import com.sana.kotlinwithretrofit.view.SignInActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/* This file sets up the main page after the login*/

class UserActivity : BaseActivity(), View.OnClickListener{

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    lateinit var fab_addItem: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserListAdapter
    lateinit var progerssProgressDialog: ProgressDialog
    var userList = ArrayList<User>()
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = Firebase.auth

        database = FirebaseDatabase.getInstance().reference

        setContentView(R.layout.activity_user)
        initialise()
    }

    /* Initialize the users, layout, add button */
    private fun initialise() {
        super.init()
        //Firebase.auth.signOut()
        // Get the current user
        val currentUser = mAuth.currentUser
        userId = currentUser?.uid ?: ""
        getUserData()
        // Get the display name or email if the display name is null
        val displayName = currentUser?.displayName ?: currentUser?.email
        // Save user data in SharedPreferences
        saveUserDataInPreferences(displayName, currentUser?.email)
        // Set the display name as the toolbar title
        toolbar?.setTitle(displayName)



        // To hide navigationIcon //
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        fab_addItem = findViewById(R.id.fab_addItem)
        recyclerView = findViewById(R.id.recyclerView)
        fab_addItem.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        /*progerssProgressDialog = ProgressDialog(this)
        progerssProgressDialog.setTitle("Loading")
        progerssProgressDialog.setMessage("Please Wait...")
        progerssProgressDialog.setCancelable(false)
        progerssProgressDialog.show()*/

        try {
            /* display the user as recyclerView */
            //userAdapter = UserListAdapter(this, userList, {user,position -> onItemClicked(user,position)})
            userAdapter = UserListAdapter(this, userList, ::onItemClicked)
            recyclerView.adapter = userAdapter;

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //initalize the logout function
        val logoutButton: ImageButton = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }

    }

    /* To hide menu */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var deleteItem = menu!!.findItem(R.id.menu_delete)
        deleteItem.setVisible(false)
        return true
    }

    /* When click the added user */
    private fun onItemClicked(user: User, position: Int){

        this.position = position
        // Goes to UserDetailsActivity and set up accordingly
        var intent = Intent(this, UserDetailsActivity::class.java)
        intent.putExtra("website",user.website)
        intent.putExtra("username",user.username)
        intent.putExtra("userType",user.userType)
        intent.putExtra("image",user.image)
        intent.putExtra("userId", userId)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /* Sets up delete and update*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // if RESULT_CODE is the code for deletion
        if(resultCode.equals(RESULT_CODE)){
            try {
                val positionToDelete = data?.getIntExtra("itemPositionToDelete", -1) ?: -1
                println(positionToDelete)
                if (positionToDelete == -1) {
                    userAdapter.removeItem(position)
                    Toast.makeText(this, R.string.user_deleted, Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        // if RESULT_CODE is the code for update
        if (resultCode == Activity.RESULT_OK) {
            val updatedItemPosition = data?.getIntExtra("updatedItemPosition", -1) ?: -1
            val updatedUserName = data?.getStringExtra("updatedUserName")
            val updatedUserType = data?.getStringExtra("updatedUserType")
            val updatedWebsite = data?.getStringExtra("updatedWebsite")

            userList[position].username = updatedUserName.toString()
            userList[position].website = updatedWebsite.toString()
            userList[position].userType = updatedUserType.toString()
            userAdapter.notifyItemChanged(position)

            /*
            if (updatedItemPosition != -1) {
                // Update your data list with the updated values
                // Replace "yourDataList" with the name of the list that holds your data
                println(userList[position])
                //userList[updatedItemPosition].username = updatedUserName
                //yourDataList[updatedItemPosition].userType = updatedUserType
                //yourDataList[updatedItemPosition].website = updatedWebsite

                // Notify the adapter that the data has changed
                userAdapter.notifyItemChanged(updatedItemPosition)

            }
            */
        }

    }

    /* When click addItem */
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.fab_addItem -> {
                    var dialog = AddItemDialog(this)
                    dialog.show()
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    dialog.onAddItem(object : AddItemDialog.IAddItemCallback {
                        override fun addItem(user: User) {
                            saveUserData(user) // Save the user data to the Realtime Database
                            userAdapter.addItem(user)

                        }
                    })
                }
            }
        }
    }

    // Helper function to store user data in SharedPreferences
    private fun saveUserDataInPreferences(displayName: String?, email: String?) {
        // Get SharedPreferences instance with a custom name "user_data"
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        // Get SharedPreferences editor to store data
        val editor = sharedPreferences.edit()
        // Store display name and email in SharedPreferences
        editor.putString("display_name", displayName)
        editor.putString("email", email)
        // Apply the changes
        editor.apply()
    }


    // Helper function to load user data from SharedPreferences
    private fun loadUserDataFromPreferences(): Pair<String?, String?> {
        // Get SharedPreferences instance with a custom name "user_data"
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        // Retrieve display name and email from SharedPreferences
        val displayName = sharedPreferences.getString("display_name", null)
        val email = sharedPreferences.getString("email", null)
        // Return the display name and email as a Pair
        return Pair(displayName, email)
    }


    private fun getUserData() {
        val userId = mAuth.currentUser?.uid ?: return
        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            if (user != null) {
                userList.add(user)
                userAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            // Handle errors
        }
    }

    private fun saveUserData(user: User) {
        val userId = mAuth.currentUser?.uid ?: return
        database.child("users").child(userId).setValue(user)
    }


    // Logout function
    private fun logout() {
        // Sign out from Firebase Auth
        mAuth.signOut()
        // Load user data from SharedPreferences
        val (displayName, email) = loadUserDataFromPreferences()
        // Set the display name or email as the toolbar title
        toolbar?.setTitle(displayName ?: email)
        // Navigate to SignInActivity
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}

