package com.example.tuank.petrol_station


import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.tuank.Users.CustomRecycleUser
import com.example.tuank.Users.UsersData
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scr_home.*
import kotlinx.android.synthetic.main.activity_screen_getstart.*
import kotlinx.android.synthetic.main.fragment_fragment_home.*
import java.io.File
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_home : Fragment() {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val use = auth.currentUser
    val id: String = use!!.uid

    //animation
    lateinit var anim: Animation
    lateinit var anim200: Animation
    lateinit var anim300: Animation
    lateinit var anim400: Animation
    val animator = ValueAnimator.ofFloat(0f, 1f)
    lateinit var userRecycle: RecyclerView
    lateinit var securityRecycle: RecyclerView
    lateinit var findStationRecycle: RecyclerView
    lateinit var avt_home: CircleImageView
    lateinit var notificationRingHome: ImageView
    lateinit var shimmer_view_container: ShimmerFrameLayout
    lateinit var btnGas: Button
    lateinit var btnOil: Button
    lateinit var img_lb_buy_home: ImageView
    lateinit var menu_top_container: RelativeLayout
    lateinit var action_buy_container: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences

    //user data
    var dataUser = ArrayList<UsersData>()
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v: View = inflater.inflate(R.layout.fragment_fragment_home, container, false)
        val window = activity!!.getWindow()

        //loads animation
        anim = AnimationUtils.loadAnimation(context, R.anim.scale_item)
        anim200 = AnimationUtils.loadAnimation(context, R.anim.scale_item)
        anim200.startOffset = 200
        anim300 = AnimationUtils.loadAnimation(context, R.anim.scale_item)
        anim300.startOffset = 300
        anim400 = AnimationUtils.loadAnimation(context, R.anim.scale_item)
        anim400.startOffset = 400
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val local = v.findViewById<TextView>(R.id.local_home)
        btnGas = v.findViewById<Button>(R.id.btngas)
        btnOil = v.findViewById<Button>(R.id.btnoil)
        img_lb_buy_home = v.findViewById<ImageView>(R.id.img_lb_buy_home)
        val btn_transfer_home = v.findViewById<Button>(R.id.btn_transfer_home)
        val btn_addmoney_home = v.findViewById<Button>(R.id.btn_addmoney_home)
        val btnLostCardHome = v.findViewById<Button>(R.id.btnLostCardHome)
        avt_home = v.findViewById(R.id.avt_home)
        userRecycle = v.findViewById(R.id.userRecycle)
        securityRecycle = v.findViewById(R.id.securityRecycle)
        findStationRecycle = v.findViewById(R.id.findStationRecycle)
        notificationRingHome = v.findViewById(R.id.notificationRingHome)
        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)
        shimmer_view_container.visibility = View.VISIBLE
        menu_top_container = v.findViewById(R.id.menu_top_container)
        action_buy_container = v.findViewById(R.id.action_buy_container)

        menu_top_container.startAnimation(anim)

        action_buy_container.startAnimation(anim200)
        checkNotifi()
        setiamgeForLanguages()
        userRecycle.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        securityRecycle.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        findStationRecycle.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)

        var data = ArrayList<DataRcCard>()
        var dataRecent = ArrayList<DataRcCard>()

        getDataUser()
        data.add(DataRcCard(getString(R.string.rc_1), R.drawable.img_drop_card, R.drawable.item_rc_eff))
        data.add(DataRcCard(getString(R.string.rc_2), R.drawable.img_hack_card, R.drawable.card_security_container))

        dataRecent.add(DataRcCard(getString(R.string.rc_3), R.drawable.img_recent_station, R.drawable.card_security_container_green))


        val adapterCard = CustomAdapterRecycleView(data)
        securityRecycle.adapter = adapterCard

        val adapterRecent = CustomAdapterRecycleView(dataRecent)
        findStationRecycle.adapter = adapterRecent

//        securityRecycle.startAnimation(anim200)
//        findStationRecycle.startAnimation(anim200)
        securityRecycle.addOnItemTouchListener(
                RecyclerItemClickListener(this.activity!!, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        database.child("Account").child(id).child("RFID").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.value != null){
                                    val intent = Intent(context, LostCardNotify ::class.java)
                                    intent.putExtra("type lost",position.toString())
                                    startActivity(intent)
                                }
                                else if (p0.value == null){
                                    showDialogMakeCard()
                                }
                            }
                        })

                    }
                })
        )
        findStationRecycle.addOnItemTouchListener(
                RecyclerItemClickListener(this.activity!!, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0){
                            val intent = Intent(context, RecentStationMap ::class.java)
                            startActivity(intent)
                        }
                    }
                })
        )
        userRecycle.addOnItemTouchListener(
            RecyclerItemClickListener(this.activity!!, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val intent = Intent(context, MessageBox::class.java)
                    intent.putExtra("userId", dataUser[position].userId)
                    intent.putExtra("userAvtUrl", dataUser[position].userAvatarUrl)
                    intent.putExtra("userName", dataUser[position].userName)
                    intent.putExtra("userPhone", dataUser[position].userPhone)
                    startActivity(intent)
                }
            })
        )

        Picasso.get()
                .load(auth.currentUser!!.photoUrl)
                .into(object: Target{
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        avt_home.setImageBitmap(bitmap)
                        shimmer_view_container.stopShimmerAnimation()
                        shimmer_view_container.visibility = View.GONE
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }


                })
        btnGas.setOnClickListener(View.OnClickListener {
            view -> showDialog("gasoline")
        })

        btnOil.setOnClickListener(View.OnClickListener {
            view -> showDialog("oil")
        })
        btn_transfer_home.setOnClickListener(View.OnClickListener {
            changeToTransfer()
        })
        btn_addmoney_home.setOnClickListener(View.OnClickListener {
            changeAddMoney()
        })
        btnLostCardHome.setOnClickListener(View.OnClickListener {
            changeLostCard()
        })

        notificationRingHome.setOnClickListener(View.OnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener {
                item ->
                when(item.itemId){
                    R.id.no_mess -> {
                        startActivity(Intent(context, NotificationMessageScreen::class.java))
                        true
                    }
                    R.id.no_card -> {
                        startActivity(Intent(context, NotificationLostCard::class.java))
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.notification)
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
            } catch (e: Exception){
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        })
        val money_home1 = v.findViewById<TextView>(R.id.money_home) as TextView
        val name_home = v.findViewById<TextView>(R.id.name_home) as TextView
        val use = auth.currentUser
        val id: String = use!!.uid
        database.child("Account").child(id).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val money: Double = p0.child("money").value.toString().toDouble()
                val name: String = p0.child("Name").value.toString()
                val numPhone: String = p0.child("Phone number").value.toString()
                money_home1.text = String.format("%,.2f", money) + " VND"
                name_home.text = name
                phone_number_home?.text = numPhone

            }

        })

        return v
    }


    override fun onResume() {
        super.onResume()
        shimmer_view_container.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        shimmer_view_container.stopShimmerAnimation()
        shimmer_view_container.visibility = View.GONE
    }
    private fun getDataUser(){
        database.child("Account").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var dataIdUser: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtId: DataSnapshot in p0.children){
                    dataIdUser = dtId.key.toString()
                    if (dtId.child("Email").value.toString() != use!!.email.toString()){
                        val name = dtId.child("Name").value.toString()
                        val phone = dtId.child("Phone number").value.toString()
                        val userAvatarUrl = dtId.child("avatar_url").value.toString()
                        dataUser.add(UsersData(userAvatarUrl, name, dataIdUser, phone))
                    }
                }
                val adapterUser = CustomRecycleUser(dataUser)
                userRecycle.adapter = adapterUser
            }
        })
    }
    private fun checkNotifi() {
        val use = auth.currentUser
        val id: String = use!!.uid
        database.child("customer_feedback").child("messages").child(id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (dtSubject: DataSnapshot in p0.children) {
                    try{
                        var dataContentNo:String = dtSubject.child("reply").value.toString()
                        var sttRead = dtSubject.child("status_read").value.toString().toInt()

                        if (sttRead == 0){
                            notificationRingHome.setImageResource(R.drawable.notification_ring_home)
                        }
                        else if (dataContentNo != ""){
                            notificationRingHome.setImageResource(R.drawable.notification_ring_home_true)
                        }
                    }catch (e: Exception){}

                }
            }
        })
    }

    fun checkDetect(){
        val use = auth.currentUser
        val id: String = use!!.uid
        database.child("Account").child(id).child("response").child("card lost").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value.toString() != "none"){
                    notificationRingHome.setImageResource(R.drawable.notification_ring_home_true)
//                    db.child("Account").child(id).child("response").child("card lost").setValue("none")
                }
            }
        })

    }

    private fun changeToTransfer() {
        startActivity(Intent(context, Transfer_screen ::class.java))
    }
    private fun changeAddMoney() {
        startActivity(Intent(context, SubAddMoney_screen ::class.java))
    }
    private fun changeLostCard() {
        val intent = Intent(context, LostCardNotify ::class.java)
        intent.putExtra("type lost","1")
        startActivity(intent)
    }


    fun payGas(){
        val intent = Intent(context, screen_scan ::class.java)
        intent.putExtra("typePay","gas")
        startActivity(intent)

    }


    fun payOil(){
        val intent = Intent(context, screen_scan ::class.java)
        intent.putExtra("typePay","oil")
        startActivity(intent)

    }


    private fun showDialog(type: String) {
        val dialog = Dialog(context)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.dialog_guide_scan)

        val btnCancelDialog = dialog .findViewById(R.id.btnCancelDialog) as Button
        val btnScanDialog = dialog .findViewById(R.id.btnScanDialog) as Button

        btnCancelDialog.setOnClickListener {
            dialog .dismiss()
        }
        btnScanDialog.setOnClickListener {
            dialog .dismiss()
            if(type == "gasoline"){
                payGas()
            }else if (type == "oil"){
                payOil()
            }
        }
        dialog.show()
    }

    private fun showDialogMakeCard() {
        val dialog = Dialog(context)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.dialog_require_card)

        val btnScanDialog = dialog .findViewById(R.id.btnOkDialog) as Button

        btnScanDialog.setOnClickListener {
            dialog .dismiss()

        }
        dialog.show()
    }

    private fun setiamgeForLanguages(){
        if(sharedPreferences.getString("LANG_KEY", null) == "vi"){
            btnGas.setBackgroundResource(R.drawable.btngas_vi)
            btnOil.setBackgroundResource(R.drawable.btnoil_vi)
            img_lb_buy_home.setBackgroundResource(R.drawable.buynow_vi)
        }
        if(sharedPreferences.getString("LANG_KEY", null) == ""){
            btnGas.setBackgroundResource(R.drawable.btngas)
            btnOil.setBackgroundResource(R.drawable.btnoil)
            img_lb_buy_home.setBackgroundResource(R.drawable.buynow)
        }
    }


}
