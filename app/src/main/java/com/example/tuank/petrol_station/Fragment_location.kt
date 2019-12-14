package com.example.tuank.petrol_station


import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_screen_signup.*
import kotlinx.android.synthetic.main.fragment_fragment_location.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_location : Fragment() {
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    lateinit var arrayListHistory: ArrayList<PayHis>
    lateinit var listViewHistory: ListView
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fragment_location, container, false)
        val window = activity!!.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.my_statusbar_color))
        // Inflate the layout for this fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }


        getDataHistory()
        listViewHistory = view.findViewById(R.id.listViewHistory)
        arrayListHistory = ArrayList()

        return view
    }

    fun getDataHistory(){
        val use = auth.currentUser
        val id: String = use!!.uid
        db.child("trụ 1").child("history").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var dataAmountHis: String = ""
            var dataMoneyHis: String = ""
            var dataDateHis: String = ""
            var dataMonthHis: String = ""
            var dataYearHis: String = ""
            var timeFullHis: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtDate: DataSnapshot in p0.children){
                    dataDateHis = dtDate.key.toString()
                    println(p0.key)

                    println(dtDate.value)
                    for(dtMonth: DataSnapshot in dtDate.children){
                        println(dtMonth.value)
                        dataMonthHis = dtMonth.key.toString()
                        for(dtYear: DataSnapshot in dtMonth.children){
                            dataYearHis = dtYear.key.toString()
                            println(dtYear.value)
                            for(dtHisUser: DataSnapshot in dtYear.children){
                                if (dtHisUser.key.toString().equals(id)){
                                    timeFullHis = dataDateHis + "/" + dataMonthHis + "/" + dataYearHis

                                    dataAmountHis = dtHisUser.child("Amount").value.toString()
                                    dataMoneyHis = dtHisUser.child("money").value.toString()
                                    try {
                                        val roundOff = Math.round(dataAmountHis.toFloat() * 100.0) / 100.0
                                        dataAmountHis = roundOff.toString()
                                        addToHistoriesList(dataAmountHis, dataMoneyHis, timeFullHis)
                                    }catch (e: Exception){

                                    }
                                }

                            }
                        }
                    }

                }

            }
        })
    }

    fun addToHistoriesList(amount: String, money: String, date: String){
        arrayListHistory.add(PayHis("Pay Gasoline", amount + "l", money + "đ", date))
        listViewHistory.adapter = CustomAdapterHistory(context!!, arrayListHistory)
    }

}
