package com.sana.kotlinwithretrofit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sana.kotlinwithretrofit.common.BaseActivity
import com.sana.kotlinwithretrofit.utilities.Constants.REQUEST_CODE
import com.sana.kotlinwithretrofit.utilities.Constants.RESULT_CODE
import com.sana.kotlinwithretrofit.utilities.Utilities
import com.sana.kotlinwithretrofit.view.AddItemDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/* This file sets up the main page */

class UserActivity : BaseActivity(), View.OnClickListener{
    lateinit var fab_addItem: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserListAdapter
    lateinit var progerssProgressDialog: ProgressDialog
    var userList = ArrayList<User>()
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        initialise()
    }

    /* Initialize the users, layout, add button */
    private fun initialise() {
        super.init()
        toolbar?.setTitle("User")

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
            userAdapter = UserListAdapter(this, userList, {user,position -> onItemClicked(user,position)})
            recyclerView.adapter = userAdapter;

        } catch (e: Exception) {
            e.printStackTrace()
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

                            userAdapter.addItem(user)

                        }
                    })
                }
            }
        }
    }
}

