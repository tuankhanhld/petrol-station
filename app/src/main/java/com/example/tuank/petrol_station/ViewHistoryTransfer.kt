package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewHistoryTransfer : AppCompatActivity() {
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    lateinit var arrayListHistory: ArrayList<TransferHis>
    lateinit var listViewHistory: ListView
    lateinit var btnBack: ImageView
    lateinit var nameSearch: EditText
    var init: Int = 0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_history_transfer)
        val window = this@ViewHistoryTransfer.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ViewHistoryTransfer, R.color.my_statusbar_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        listViewHistory = findViewById(R.id.listViewHistoryTransfer)
        btnBack = findViewById(R.id.btnBack)
        nameSearch = findViewById(R.id.nameSearch)
        arrayListHistory = ArrayList()
        getDataHistory(" ")
        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })
        nameSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == null){
                    listViewHistory.adapter = null
                    arrayListHistory.clear()
                }else{
                    getDataHistory(p0.toString())
                }
            }

        })
    }

    fun getDataHistory(date: String){
        listViewHistory.adapter = null
        arrayListHistory.clear()
        val use = auth.currentUser
        val id: String = use!!.uid
        db.child("trụ 1").child("history_changes").child("transfer").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var dataFromHis: String = ""
            var dataMoneyHis: String = ""
            var dataDateHis: String = ""
            var dataTimeHis: String = ""
            var timeFullHis: String = ""
            var dataToHis: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtDate: DataSnapshot in p0.children){
                    dataDateHis = dtDate.key.toString()
                    for(dtTime: DataSnapshot in dtDate.children){
                        dataTimeHis = dtTime.key.toString()
                        for(dtHisUser: DataSnapshot in dtTime.children){
                            if (dtHisUser.key.toString().equals(id)){
                                val dateSplit = dataDateHis.split("_")
                                timeFullHis = dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0] + " " + dataTimeHis

                                dataFromHis = dtHisUser.child("from").value.toString()
                                dataMoneyHis = dtHisUser.child("money").value.toString()
                                dataToHis = dtHisUser.child("to").value.toString()
                                try {
                                    if (timeFullHis.toUpperCase().contains(date.toUpperCase()) && init == 1){
                                        addToHistoriesList(dataFromHis,dataToHis, dataMoneyHis, timeFullHis)
                                    }else if (init == 0){
                                        addToHistoriesList(dataFromHis,dataToHis, dataMoneyHis, timeFullHis)
                                    }


                                }catch (e: Exception){

                                }
                            }

                        }
                    }


                }
                setInitStart(1)
            }
        })
    }

    fun addToHistoriesList(from: String, to: String, money: String, time:String){
        arrayListHistory.add(TransferHis(from, to, money+ "vnd", time))
        listViewHistory.adapter = CustomAdapterTransferHistory(this@ViewHistoryTransfer, arrayListHistory)
    }
    fun backToMain(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
    private fun setInitStart(init: Int){
        this.init = init
    }
}
