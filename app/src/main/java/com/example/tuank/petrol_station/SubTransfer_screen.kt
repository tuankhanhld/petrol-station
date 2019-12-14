package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.app.Dialog
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
import com.example.tuank.fingerprint_authentication.FingerprintAuthen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_fingerprint_login.*
import java.text.SimpleDateFormat
import java.util.*

class SubTransfer_screen : AppCompatActivity(),AuthenticationListener {
    lateinit var btnBack: ImageView
    lateinit var amountMoneyTrans: EditText
    lateinit var nameFrom: TextView
    lateinit var emailFrom: TextView
    lateinit var nameTo: TextView
    lateinit var emailTo: TextView
    lateinit var btnTransfer: Button

    lateinit var nameIntent:String
    lateinit var rfidIntent:String

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()

    private lateinit var dialog: Dialog
    private lateinit var sharedPreferences: SharedPreferences
    private var fingerprintHandler: FingerprintHandler? = null
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_transfer_screen)

        val window = this@SubTransfer_screen.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this@SubTransfer_screen, R.color.my_statusbar_color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }


        btnBack = findViewById(R.id.btnBack)
        amountMoneyTrans = findViewById(R.id.amountMoneyTrans)
        nameFrom = findViewById(R.id.nameFrom)
        emailFrom = findViewById(R.id.emailFrom)
        nameTo = findViewById(R.id.nameTo)
        emailTo = findViewById(R.id.emailTo)
        btnTransfer = findViewById(R.id.btnTransfer)

        val use = auth.currentUser
        val id: String = use!!.uid
        val mName: String = use.displayName.toString()
        val mEmail: String = use.email.toString()

        nameIntent = intent.getStringExtra("nameTo")
        rfidIntent = intent.getStringExtra("rfidTo")

        nameTo.text = nameIntent
        emailTo.text = rfidIntent
        nameFrom.text = mName
        emailFrom.text = mEmail

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        btnTransfer.setOnClickListener(View.OnClickListener {
            if (sharedPreferences.getString("PUBLIC_KEY_TURN_ON_FINGERPRINT", null) == "1") {
                showDialogCheckPassCode()
            } else {
                checkMoney()
            }

        })
    }

    private fun showDialogCheckPassCode() {

        dialog = Dialog(this@SubTransfer_screen)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.scan_fingerprint_dialog)

        val btnCancelDialog = dialog .findViewById(R.id.btnCancelDialog) as Button

        btnCancelDialog.setOnClickListener {
            startActivity(Intent(this@SubTransfer_screen, scr_home::class.java))
            finish()
            dialog .dismiss()
        }
        dialog.show()
        initSensor()
    }

    private fun checkMoney(){
        if (nameTo.text == null || emailTo.text == null){
            Toast.makeText(this@SubTransfer_screen, "User transfer not chosen!", Toast.LENGTH_LONG).show()

        }else {
            val use = auth.currentUser
            val id: String = use!!.uid
            db.child("Account").child(id).child("money").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val moneyCurent: String = p0.value.toString()
                    if (moneyCurent.toInt() < amountMoneyTrans.text.toString().toInt()) {
                        Toast.makeText(this@SubTransfer_screen, "Your money not enough!", Toast.LENGTH_LONG).show()
                    } else {
                        tranferMoney()
                    }

                }
            })
        }
    }

    private fun checkUserTransfer(moneyTr: String){
        db.child("Account").addListenerForSingleValueEvent(object : ValueEventListener {
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
                        val email = dtUser.child("Email").value.toString()
                        if (email.toUpperCase() == emailTo.text.toString().toUpperCase()){
                            val dt: data = data()
                            dt.addMoney(dtUser.key.toString(), moneyTr)
                        }
                    }
                }
            }
        })
    }
    private fun tranferMoney() {
            val use = auth.currentUser
            val id: String = use!!.uid
            val cTime = SimpleDateFormat("yyyy_M_dd/hh:mm:ss")
            val currentDateFull = cTime.format(Date())
            val add: data = data()

            add.subMoney(id, amountMoneyTrans.text.toString())
            checkUserTransfer(amountMoneyTrans.text.toString())
            db.child("trụ 1").child("history_changes").child("transfer").child(currentDateFull).child(id).child("money").setValue(amountMoneyTrans.text.toString())
            db.child("trụ 1").child("history_changes").child("transfer").child(currentDateFull).child(id).child("from").setValue(use.email)
            db.child("trụ 1").child("history_changes").child("transfer").child(currentDateFull).child(id).child("to").setValue(emailTo.text.toString())
            add.addNumberTrans(id, "no_transfer")
            Toast.makeText(this@SubTransfer_screen, "Successful!", Toast.LENGTH_LONG).show()
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
        checkMoney()
        dialog.dismiss()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationFailure(error: String?) {
        error?.let {
        }
    }
}
