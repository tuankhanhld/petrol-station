package com.example.tuank.petrol_station


import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context.FINGERPRINT_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.*
import com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_fingerprint_login.*
import kotlinx.android.synthetic.main.fragment_fragment_home.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_settings : Fragment(), AuthenticationListener {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    //dialog
    private lateinit var dialog: Dialog
    private var fingerprintHandler: FingerprintHandler? = null
    lateinit var helpCenter: TextView
    lateinit var privacySettings: RelativeLayout
    lateinit var passCodeFingerprint: RelativeLayout
    lateinit var fingerprintPermission: Switch
    lateinit var swPermissionNotifi: Switch
    lateinit var languageSettings: RelativeLayout
    lateinit var btnExit: Button
    //fingerprint
    private lateinit var sharedPreferences: SharedPreferences
    var LOGIN_STATE_KEY: String = "111"
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v:View = inflater.inflate(R.layout.fragment_fragment_settings, container, false)

        setStatusBar()

        // Inflate the layout for this fragment
        val btnSginOutSetting = v.findViewById<Button>(R.id.btnSignoutSetting)

        val mNameSettings = v.findViewById<TextView>(R.id.name_home_settings)
        val mPhoneSettings = v.findViewById<TextView>(R.id.phone_number_settings)
        val avt_home_settings = v.findViewById<CircleImageView>(R.id.avt_home_settings)
        helpCenter = v.findViewById(R.id.helpCenter)
        privacySettings = v.findViewById(R.id.privacySettings)
        passCodeFingerprint = v.findViewById(R.id.passCodeFingerprint)
        fingerprintPermission = v.findViewById(R.id.fingerprintPermission)
        swPermissionNotifi = v.findViewById(R.id.swPermissionNotifi)
        languageSettings = v.findViewById(R.id.languageSettings)
        btnExit = v.findViewById(R.id.btnExit)
        swPermissionNotifi.isChecked = true

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        loadLocate()
        setPermissionNotifi()
        setFingerAuth()
        Picasso.get().load(auth.currentUser!!.photoUrl).into(avt_home_settings)

        val use = auth.currentUser
        val id: String = use!!.uid
        database.child("Account").child(id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val name: String = p0.child("Name").value.toString()
                val numPhone: String = p0.child("Phone number").value.toString()
                mNameSettings.text = name
                mPhoneSettings?.text = numPhone
            }

        })

        btnSginOutSetting.setOnClickListener(View.OnClickListener {
            auth.signOut()
            sharedPreferences.edit().putString(LOGIN_STATE_KEY, "0").apply()
            startActivity(Intent(v.context, MainActivity ::class.java))

        })
        helpCenter.setOnClickListener(View.OnClickListener {
            startActivity(Intent(v.context, HelpCenterScreen ::class.java))
        })
        privacySettings.setOnClickListener(View.OnClickListener {
            startActivity(Intent(v.context, ChosePrivacySettings ::class.java))
        })
        languageSettings.setOnClickListener(View.OnClickListener {
            showDialogChoseLanguage()
        })
        btnExit.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)}
        return v
    }

    private fun setPermissionNotifi() {
        swPermissionNotifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

            }
        }
    }
    private fun setFingerAuth() {
        fingerprintPermission.isChecked = sharedPreferences.getString("PUBLIC_KEY_TURN_ON_FINGERPRINT", null) == "1"

        fingerprintPermission.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                    sharedPreferences.edit().putString("PUBLIC_KEY_TURN_ON_FINGERPRINT", "1").apply()
            }else{
                showDialogCheckPassCode()
            }
        }
    }

    private fun showDialogChoseLanguage(){
        dialog = Dialog(context)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.dialog_chose_lang)

        val radioGroup = dialog .findViewById(R.id.radioGroup) as RadioGroup
        val vietNamese = dialog .findViewById(R.id.vietNamese) as RadioButton
        val english = dialog .findViewById(R.id.english) as RadioButton
        if(sharedPreferences.getString("LANG_KEY", null) == "vi"){
            vietNamese.isChecked = true
        }
        if(sharedPreferences.getString("LANG_KEY", null) == ""){
            english.isChecked = true
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            var language: String = ""
            language += if (R.id.vietNamese == checkedId) "vi" else ""
            sharedPreferences.edit().putString("LANG_KEY", language).apply()
            setLocate(language)
            dialog.dismiss()
            recreate()
        }
        dialog.show()
    }
    private fun setStatusBar() {
        val window = activity!!.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
    }

    private fun setLocate(Language: String) {

        val locale = Locale(Language)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale

        activity!!.baseContext.resources.updateConfiguration(config, activity!!.baseContext.resources.displayMetrics)

    }

    private fun loadLocate() {
        val language = sharedPreferences.getString("LANG_KEY", "")
        setLocate(language)
    }

    fun recreate(){
        val newGamefragment = Fragment_settings()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, newGamefragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun showDialogCheckPassCode() {
        dialog = Dialog(context)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.scan_fingerprint_dialog)

        val btnCancelDialog = dialog .findViewById(R.id.btnCancelDialog) as Button

        btnCancelDialog.setOnClickListener {
            dialog .dismiss()
            recreate()
        }
        dialog.show()
        initSensor()
    }
    @TargetApi(Build.VERSION_CODES.M)
    private fun initSensor() {
        if (Utils.checkSensorState(context!!)) {
            val cryptoObject = Utils.cryptoObject
            if (cryptoObject != null) {
                val fingerprintManager =activity!!.getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintHandler = FingerprintHandler(context!!, sharedPreferences, this)
                fingerprintHandler?.startAuth(fingerprintManager, cryptoObject)
            }
        }
    }

    override fun onAuthenticationSuccess(decryptPassword: String) {
        dialog.dismiss()
        recreate()
        sharedPreferences.edit().putString("PUBLIC_KEY_TURN_ON_FINGERPRINT", "0").apply()

    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationFailure(error: String?) {
        error?.let {

        }
    }
}
