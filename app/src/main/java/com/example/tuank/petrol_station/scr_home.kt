package com.example.tuank.petrol_station

import android.Manifest
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_scr_home.*
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import java.util.*


@Suppress("DEPRECATION")
class scr_home : AppCompatActivity() {

    val manager = supportFragmentManager
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.tuank.petrol_station"
    private val description = "Test notification"
    private lateinit var sharedPreferences: SharedPreferences
    var MY_PERMISSIONS_REQUEST_CAMERA: Int = 1000
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nv_home -> {
                creathome()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nv_pro -> {
                creatprofile()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nv_local -> {
                creatHis()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nv_settings -> {
                creatSettings()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scr_home)
        if (ContextCompat.checkSelfPermission(this@scr_home,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this@scr_home, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this@scr_home,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            }
        }
        

        val window = this@scr_home.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
                window.setStatusBarColor(ContextCompat.getColor(this@scr_home, R.color.colorPrimaryDark))
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        loadLocate()

        val sttCreat = intent.getStringExtra("creatGas")
        if(sttCreat == "1"){
            payGas()
        }else if (sttCreat == "2"){
            payOil()
        }else{
            creathome()
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        checkDetect()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val layoutParams = navigation.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehavior()
    }
    fun creatprofile(){
        val transaction = manager.beginTransaction()
        val fragment = fragment_profiles()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()
    }
    fun creatHis(){
        val transaction = manager.beginTransaction()
        val fragment = Fragment_histories()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()
    }
    fun creathome(){
        val transaction = manager.beginTransaction()
        val fragment = Fragment_home()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()

    }

    fun creatSettings(){
        val transaction = manager.beginTransaction()
        val fragment = Fragment_settings()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()
    }
    fun payGas(){
        val transaction = manager.beginTransaction()
        val fragment = fragment_buy_gas()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()

    }
    fun payOil(){
        val transaction = manager.beginTransaction()
        val fragment = fragment_buy_oil()
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.commit()

    }

    fun checkDetect(){
        val use = auth.currentUser
        val id: String = use!!.uid
        db.child("Account").child(id).child("response").child("card lost").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value.toString() != "none"){
                    createNotificationChannel(p0.value.toString())
//                    db.child("Account").child(id).child("response").child("card lost").setValue("none")
                }
            }
        })

    }
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(url: String) {
        val intent = Intent(this,NotificationLostCard::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val contentView = RemoteViews(packageName,R.layout.custom_notification_detect)
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this,channelId)
                    .setCustomBigContentView(contentView)
                    .setSmallIcon(R.drawable.logo_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo_launcher))
                    .setContentIntent(pendingIntent)
                    .setSound(alarm)
        }else{

            builder = Notification.Builder(this)
                    .setCustomBigContentView(contentView)
                    .setSmallIcon(R.drawable.logo_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo_launcher))
                    .setContentIntent(pendingIntent)
                    .setSound(alarm)
        }

        Picasso.get()
                .load(url).into(object : Target{
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        contentView.setImageViewBitmap(R.id.imgDetect, bitmap)
                        notificationManager.notify(1234,builder.build())
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }
                })


        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {

            }
        }
    }
    private fun setLocate(Language: String) {

        val locale = Locale(Language)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun loadLocate() {
        val language = sharedPreferences.getString("LANG_KEY", "")
        setLocate(language)
    }

    

}
