package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Transfer_screen : AppCompatActivity() {
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    lateinit var arrayListTransfer: ArrayList<dataSearchTransfer>
    lateinit var listviewSearchUser: ListView
    lateinit var nameSearch: EditText

    var init: Int = 0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_screen)
        val window = this@Transfer_screen.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@Transfer_screen, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSearch = findViewById<ImageView>(R.id.btnSearch)

        listviewSearchUser = findViewById(R.id.listviewSearchUser)
        nameSearch = findViewById(R.id.nameSearch)
        arrayListTransfer = ArrayList()
        getDataTransfer(" ")
        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        nameSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == null){
                    listviewSearchUser.adapter = null
                    arrayListTransfer.clear()
                }else{
                    getDataTransfer(p0.toString())
                }
            }

        })

        listviewSearchUser.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, SubTransfer_screen ::class.java)
            intent.putExtra("nameTo", arrayListTransfer[position].nameReceiver)
            intent.putExtra("rfidTo", arrayListTransfer[position].refIdReceiver)
            startActivity(intent)
        }
    }

    private fun getDataTransfer(name: String) {
        listviewSearchUser.adapter = null
        arrayListTransfer.clear()
        val userName = name
        val use = auth.currentUser
        val id: String = use!!.uid
        db.child("Account").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var dataIdUser: String = ""
            var dataName: String = ""
            var datarfid: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtId: DataSnapshot in p0.children){
                    dataIdUser = dtId.key.toString()
                    for (dtUser: DataSnapshot in p0.children){
                        val name = dtUser.child("Name").value.toString()
                        val email = dtUser.child("Email").value.toString()
                        if (name.toUpperCase().contains(userName.toUpperCase()) &&
                                email.toUpperCase() != use.email.toString().toUpperCase() && init == 1){
                            addListviewSearch(name, email)
                        }
                        else if (init == 0 && email.toUpperCase() != use.email.toString().toUpperCase()){
                            addListviewSearch(name, email)
                        }
                    }
                }
                setInitStart(1)
            }
        })
    }

    private fun addListviewSearch(userName: String, rfidCard: String){
        if (!arrayListTransfer.contains(dataSearchTransfer(userName, rfidCard))){
            arrayListTransfer.add(dataSearchTransfer(userName, rfidCard))
            listviewSearchUser.adapter = CustomAdapterSearchTransfer(this@Transfer_screen, arrayListTransfer)
        }

    }

    private fun setInitStart(init: Int){
        this.init = init
    }
}
