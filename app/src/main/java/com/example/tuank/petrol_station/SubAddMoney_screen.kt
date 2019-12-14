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
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class SubAddMoney_screen : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var inputSerialNum: EditText
    lateinit var nameFrom: TextView
    lateinit var emailFrom: TextView
    lateinit var cardValue: TextView
    lateinit var serialNum: TextView
    lateinit var btnRecharge: Button
    lateinit var card_container: RelativeLayout

    lateinit var cardValueTemp: String
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_add_money_screen)

        val window = this@SubAddMoney_screen.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@SubAddMoney_screen, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        btnBack = findViewById(R.id.btnBack)
        inputSerialNum = findViewById(R.id.inputSerialNum)
        nameFrom = findViewById(R.id.nameFrom)
        emailFrom = findViewById(R.id.emailFrom)
        cardValue = findViewById(R.id.cardValue)
        serialNum = findViewById(R.id.serialNum)
        btnRecharge = findViewById(R.id.btnRecharge)
        card_container = findViewById(R.id.card_container)

        val use = auth.currentUser
        val id: String = use!!.uid
        val mName: String = use.displayName.toString()
        val mEmail: String = use.email.toString()

        nameFrom.text = mName
        emailFrom.text = mEmail

        card_container.visibility = View.INVISIBLE
        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        inputSerialNum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length == 13){

                    getDataCardValue(p0.toString())
                }
                else{
                    card_container.visibility = View.INVISIBLE
                    serialNum.text = null
                    cardValue.text = null
                }
            }
        })
        btnRecharge.setOnClickListener(View.OnClickListener {
            rechargeMoney()
        })
    }

    private fun getDataCardValue(serialNum: String){
        db.child("recharges").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var serialNumKey: String = ""
            var dataName: String = ""
            var datarfid: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtCard: DataSnapshot in p0.children){

                    serialNumKey = dtCard.key.toString()

                    if (serialNumKey == serialNum){
                        setTextRecharge(dtCard.value.toString(), serialNumKey)
                    }
                }
            }
        })
    }

    private fun setTextRecharge(cardValue: String, num: String){
        card_container.visibility = View.VISIBLE
        this.cardValue.text = cardValue + "vnd"
        this.serialNum.text = num
        this.cardValueTemp = cardValue
    }

    private fun rechargeMoney(){
        if (cardValue.text == null || serialNum.text == null){
            Toast.makeText(this@SubAddMoney_screen, "Please fill serial card numbers correct!", Toast.LENGTH_LONG).show()

        }else{
            val use = auth.currentUser
            val id: String = use!!.uid
            val cTime = SimpleDateFormat("yyyy_M_dd/hh:mm:ss")
            val currentDateFull = cTime.format(Date())
            val add: data = data()
            add.addMoney(id, cardValueTemp)
            db.child("trụ 1").child("history_changes").child("recharge").child(currentDateFull).child(id).child("email").setValue(use.email)
            db.child("trụ 1").child("history_changes").child("recharge").child(currentDateFull).child(id).child("money").setValue(cardValueTemp)
            db.child("trụ 1").child("history_changes").child("recharge").child(currentDateFull).child(id).child("serial").setValue(serialNum.text.toString())

            Toast.makeText(this@SubAddMoney_screen, "Successful!", Toast.LENGTH_LONG).show()
        }
    }
}
