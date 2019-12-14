package com.example.tuank.petrol_station


import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_histories : Fragment() {
    lateinit var carNumHis: TextView
    lateinit var nameCardHis: TextView
    lateinit var dateCardHis: TextView
    lateinit var numAcRecharge: TextView
    lateinit var numAcTransfer: TextView
    lateinit var numAcPay: TextView

    lateinit var btnViewHisRecharge: Button
    lateinit var btnViewHisTransfer: Button
    lateinit var btnViewHisPay: Button
    lateinit var card_inner: RelativeLayout

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid
    lateinit var anim: Animation
    lateinit var animUp100: Animation
    lateinit var animUp200: Animation
    lateinit var animUp300: Animation
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_fragment_histories, container, false)
        // Inflate the layout for this fragment

        val window = activity!!.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.my_statusbar_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (v != null) {
                v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        anim = AnimationUtils.loadAnimation(context, R.anim.scale_item)
        animUp100 = AnimationUtils.loadAnimation(context, R.anim.move_to_up)
        animUp200 = AnimationUtils.loadAnimation(context, R.anim.move_to_up)
        animUp200.startOffset = 200
        animUp300 = AnimationUtils.loadAnimation(context, R.anim.move_to_up)
        animUp300.startOffset = 300

        carNumHis = v.findViewById(R.id.carNumHis)
        nameCardHis = v.findViewById(R.id.nameCardHis)
        dateCardHis = v.findViewById(R.id.dateCardHis)
        numAcRecharge = v.findViewById(R.id.numAcRecharge)
        numAcTransfer = v.findViewById(R.id.numAcTransfer)
        numAcPay = v.findViewById(R.id.numAcPay)
        card_inner = v.findViewById(R.id.card_inner)

        btnViewHisRecharge = v.findViewById(R.id.btnViewHisRecharge)
        btnViewHisTransfer = v.findViewById(R.id.btnViewHisTransfer)
        btnViewHisPay = v.findViewById(R.id.btnViewHisPay)
        var addMonContainer = v.findViewById<RelativeLayout>(R.id.addMonContainer)
        var rechargeContainer = v.findViewById<RelativeLayout>(R.id.rechargeContainer)
        var transferContainer = v.findViewById<RelativeLayout>(R.id.transferContainer)

        card_inner.startAnimation(anim)
        addMonContainer.startAnimation(animUp100)
        rechargeContainer.startAnimation(animUp200)
        transferContainer.startAnimation(animUp300)
        getDataUser()
        getNumTransaction()
        btnViewHisPay.setOnClickListener(View.OnClickListener {
            viewHistoryPayment()
        })
        btnViewHisRecharge.setOnClickListener(View.OnClickListener {
            viewHistoryRecharge()
        })
        btnViewHisTransfer.setOnClickListener(View.OnClickListener {
            viewHistoryTransfer()
        })
        return v
    }



    private fun getDataUser() {

        nameCardHis.text = use!!.displayName.toString()
        db.child("Account").child(id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (ds: DataSnapshot in p0.children){
                    val rfidNum: String = p0.child("RFID").value.toString()
                    val date: String = p0.child("date_creat_rfid").value.toString()
                    carNumHis.text = rfidNum
                    dateCardHis.text = date
                }

            }
        })
    }
    private fun getNumTransaction(){
        db.child("Account").child(id).child("transaction_infor").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                    val numRecharge = p0.child("no_recharge").value.toString()
                    val numTransfer = p0.child("no_transfer").value.toString()
                    val numPay = p0.child("no_payment").value.toString()
                    numAcRecharge.text = numRecharge
                    numAcTransfer.text = numTransfer
                    numAcPay.text = numPay
            }
        })
    }
    private fun viewHistoryPayment() {
        startActivity(Intent(context, HistoryTransactions ::class.java))
    }
    private fun viewHistoryTransfer() {
        startActivity(Intent(context, ViewHistoryTransfer ::class.java))
    }

    private fun viewHistoryRecharge() {
        startActivity(Intent(context, ViewHistoryRecharge ::class.java))
    }



}
