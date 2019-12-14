package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class PaymentScreen : AppCompatActivity(), AuthenticationListener {
    val db = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    lateinit var totalBill:TextView
    lateinit var stationNum:TextView
    lateinit var amountBill:TextView
    lateinit var typeBill:TextView
    lateinit var dateBill:TextView
    lateinit var timeBill:TextView
    lateinit var nameBill:TextView
    lateinit var cardNumBill:TextView
    lateinit var btnBack:ImageView
    var priceFuel:Int = 0
    //variable data
    lateinit var amountMoneyPay:String
    lateinit var typePay:String
    lateinit var stationPay:String
    lateinit var date:String
    lateinit var time:String
    lateinit var dateFull:String
    lateinit var namePay: String

    lateinit var rfidNum: String
    val mon = data()
    val use = auth.currentUser
    val id: String = use!!.uid

    //dialog
    private lateinit var dialog: Dialog
    //finger auth
    private lateinit var sharedPreferences: SharedPreferences
    private var fingerprintHandler: FingerprintHandler? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_screen)
        val window = this@PaymentScreen.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@PaymentScreen, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        totalBill = findViewById<TextView>(R.id.totalBill)
        stationNum = findViewById<TextView>(R.id.stationNum)
        amountBill = findViewById<TextView>(R.id.amountBill)
        typeBill = findViewById<TextView>(R.id.typeBill)
        dateBill = findViewById<TextView>(R.id.dateBill)
        timeBill = findViewById<TextView>(R.id.timeBill)
        nameBill = findViewById<TextView>(R.id.nameBill)
        cardNumBill = findViewById<TextView>(R.id.cardNumBill)
        btnBack = findViewById<ImageView>(R.id.btnBack)

        amountMoneyPay = intent.getStringExtra("amount")
        typePay = intent.getStringExtra("type")
        stationPay = intent.getStringExtra("station")
        date = intent.getStringExtra("date")
        time = intent.getStringExtra("time")
        dateFull = intent.getStringExtra("datefull")
        namePay = intent.getStringExtra("name")
        rfidNum = intent.getStringExtra("rfid")

        getPrice(typePay, amountMoneyPay)

        totalBill.text = amountMoneyPay + "đ"
        stationNum.text = stationPay
        typeBill.text = typePay
        dateBill.text = date
        timeBill.text = time
        nameBill.text = namePay
        cardNumBill.text = rfidNum
        mon.get_station_current(id)
        val btnAcionPay = findViewById<Button>(R.id.btnAcionPay)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnAcionPay.setOnClickListener(View.OnClickListener {
            if (sharedPreferences.getString("PUBLIC_KEY_TURN_ON_FINGERPRINT", null) == "1") {
                showDialogCheckPassCode()
            } else {
                buyGas()
            }

        })

        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })


    }

    private fun showDialogCheckPassCode() {
        dialog = Dialog(this@PaymentScreen)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.scan_fingerprint_dialog)

        val btnCancelDialog = dialog .findViewById(R.id.btnCancelDialog) as Button

        btnCancelDialog.setOnClickListener {
            startActivity(Intent(this@PaymentScreen, scr_home::class.java))
            finish()
            dialog .dismiss()
        }
        dialog.show()
        initSensor()
    }

    public fun buyGas(){

        db.child("Account").child(id).child("money").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val moneyCurent: String = p0.getValue().toString()
                if(moneyCurent.toInt() < amountMoneyPay.toInt()){
                    Toast.makeText(this@PaymentScreen,"Your money not enough!", Toast.LENGTH_LONG).show()
                }
                else{
                    val roundOff = Math.round(amountBill.text.toString().toFloat() * 100.0) / 100.0
                    db.child("trụ 1").child("current_pay").child(typeBill.text.toString()).setValue(amountMoneyPay.toInt())
                    db.child("trụ 1").child("current_pay").child("allow").setValue(1)
                    db.child("trụ 1").child("current_pay").child("amountGas").setValue(roundOff)
                    db.child("trụ 1").child("history_changes").child("payment").child(dateFull).child(id).child("email").setValue(use!!.email)
                    db.child("trụ 1").child("history_changes").child("payment").child(dateFull).child(id).child("money").setValue(amountMoneyPay)
                    db.child("trụ 1").child("history_changes").child("payment").child(dateFull).child(id).child("Amount").setValue(roundOff)
                    db.child("trụ 1").child("history_changes").child("payment").child(dateFull).child(id).child("type").setValue(typePay)

                    mon.subMoney(id, amountMoneyPay)
                    mon.addIncome(id, amountMoneyPay, typePay)
                    mon.addAmount(id, amountBill.text.toString(), typePay)
                    mon.addNumberTrans(id, "no_payment")
                    showDialog(date, time, use.displayName.toString(), use.email.toString(), amountBill.text.toString(), use.photoUrl.toString())
                    db.removeEventListener(this)
                }

            }
        })
    }

    fun getPrice(type: String, amountMoneyPay:String){
        db.child("prices").child(type).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                var amountFuel: Float = amountMoneyPay!!.toFloat()/p0.value.toString().toFloat()

                val roundOff = Math.round(amountFuel * 100.0) / 100.0
                amountBill.text = roundOff.toString()
                db.removeEventListener(this)
            }
        })
    }

    fun backToMain(){
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun showDialog(date: String, time: String, name: String, email: String, amount: String, avtUrl: String) {
        val dialog = Dialog(this@PaymentScreen)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.dialog_success)
        val dateDialog = dialog .findViewById(R.id.dateDialog) as TextView
        dateDialog.text = date
        val timeDialog = dialog .findViewById(R.id.timeDialog) as TextView
        timeDialog.text = time
        val mName = dialog .findViewById(R.id.mName) as TextView
        mName.text = name
        val mEmail = dialog .findViewById(R.id.mEmail) as TextView
        mEmail.text = email
        val amountDialog = dialog .findViewById(R.id.amountDialog) as TextView
        amountDialog.text = amount + "lit"

        val btnOkDialog = dialog .findViewById(R.id.btnOkDialog) as Button

        val avtDialog = dialog .findViewById(R.id.avtDialog) as CircleImageView
        Picasso.get().load(avtUrl).into(avtDialog)
        btnOkDialog.setOnClickListener {
            dialog .dismiss()
            startActivity(Intent(this@PaymentScreen, scr_home::class.java))
            this@PaymentScreen.finish()
        }
        dialog.show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initSensor() {
        if (Utils.checkSensorState(this)) {
            val cryptoObject = Utils.cryptoObject
            if (cryptoObject != null) {
                val fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintHandler = FingerprintHandler(this, sharedPreferences, this)
                fingerprintHandler?.startAuth(fingerprintManager, cryptoObject)
            }
        }
    }

    override fun onAuthenticationSuccess(decryptPassword: String) {
        dialog.dismiss()
        buyGas()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationFailure(error: String?) {
        error?.let {
        }
    }
}
