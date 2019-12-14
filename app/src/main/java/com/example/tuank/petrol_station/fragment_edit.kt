package com.example.tuank.petrol_station


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import android.view.MotionEvent
import android.widget.*
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
class fragment_edit : Fragment() {
    val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id = use!!.uid

    lateinit var avtar_edit: ImageView
    lateinit var dateOB: TextView
    lateinit var c_male: CheckBox
    lateinit var c_female: CheckBox
    lateinit var avatar_edit_top: ImageView

    var gender: String = ""
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_fragment_edit, container, false)
        // Inflate the layout for this fragment
        dateOB = v.findViewById(R.id.dateOB)
        return v
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Lnametxt = view.findViewById<TextView>(R.id.l_name)
        val Emailtxt = view.findViewById<TextView>(R.id.email_edit)
        val Locationtxt = view.findViewById<TextView>(R.id.local_edit)
        val phonetxt = view.findViewById<TextView>(R.id.phone_e)
        val btnsave = view.findViewById<TextView>(R.id.btnsave_e)
        avtar_edit = view.findViewById(R.id.avtar_edit)
        avatar_edit_top = view.findViewById(R.id.avatar_edit_top)
        c_male = view.findViewById(R.id.c_male)
        c_female = view.findViewById(R.id.c_female)

        Emailtxt.isFocusable = false
        Picasso.get().load(auth.currentUser!!.photoUrl).into(avtar_edit)
        //setAvatarListner()
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        val fprofiles = fragment_profiles()
        fprofiles.get_id(Lnametxt,Emailtxt,phonetxt,Locationtxt,dateOB,0)
        btnsave.setOnClickListener {
            val add = fragment_profiles()
            add.get_id(Lnametxt, Emailtxt, phonetxt, Locationtxt,dateOB,1)
            if (gender != null){
                db.child("Account").child(id).child("gender").setValue(gender)
            }
            change_profile()
        }
        btnBack.setOnClickListener(View.OnClickListener {
            backToMainProfiles()
        })

        avtar_edit.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, ChoseAvatar ::class.java))
        })
        showDateTime()
        getCheckBoxGender()

    }

    private fun getCheckBoxGender() {
        c_male.setOnCheckedChangeListener { buttonView, isChecked ->
            gender = "Male"
        }
        c_female.setOnCheckedChangeListener { buttonView1, isChecked1 ->
            gender = "Female"
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showDateTime() {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            dateOB.setText(SimpleDateFormat("dd/MM/yyyy").format(cal.time))
            dateOB.clearFocus()
        }

        dateOB.setOnTouchListener(View.OnTouchListener { arg0, arg1 ->
            DatePickerDialog(context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            false
        })
    }

    private fun backToMainProfiles() {

        val newGamefragment = fragment_profiles()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, newGamefragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun change_profile(){
        val newGamefragment = fragment_profiles()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, newGamefragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }


}
