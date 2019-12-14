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
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class fragment_buy_oil : Fragment() {
    val db = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    lateinit var EbuyOil: EditText
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_fragment_buy_oil, container, false)

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

        val btnBuyOil = v.findViewById<Button>(R.id.btnPayOil)
        EbuyOil = v.findViewById(R.id.EbuyOil)
        val btnBack = v.findViewById<View>(R.id.btnBack)
        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })

        btnBuyOil.setOnClickListener(View.OnClickListener {
            if (!EbuyOil.text.isEmpty() || EbuyOil.text.toString().toInt()>10000){
                buyOil()
            }
        })

        return v
    }

    private fun backToMain() {
        val newGamefragment = Fragment_home()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, newGamefragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    public fun buyOil(){
        val use = auth.currentUser
        val id: String = use!!.uid
        val time = SimpleDateFormat("dd/M/yyyy")
        val date = SimpleDateFormat("hh:mm:ss")
        val cTime = SimpleDateFormat("yyyy_M_dd/hh:mm:ss")
        val currentTime = time.format(Date())
        val currentDate = date.format(Date())
        val currentDateFull = cTime.format(Date())

        val moneyGas: String = EbuyOil?.text.toString()
        db.child("Account").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                var stNumber: String = p0.child("pay_id").value.toString()
                val currentStation = Character.toString(stNumber[stNumber.length-1])
                val currentName: String = p0.child("Name").value.toString()
                val currentRfid: String = p0.child("RFID").value.toString()

                val intent = Intent(context, PaymentScreen ::class.java)
                intent.putExtra("amount",moneyGas)
                intent.putExtra("type","Oil")
                intent.putExtra("station",currentStation)
                intent.putExtra("date",currentDate)
                intent.putExtra("time",currentTime)
                intent.putExtra("datefull",currentDateFull)
                intent.putExtra("name", currentName)
                intent.putExtra("rfid", currentRfid)

                startActivity(intent)

            }
        })
    }
}
