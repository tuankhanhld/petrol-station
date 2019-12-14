package com.example.tuank.petrol_station


import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_fragment_profiles.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class fragment_profiles : Fragment() {
    val auth = FirebaseAuth.getInstance()
    val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    val use = auth.currentUser
    val id = use!!.uid
    lateinit var shimmer_view_container: ShimmerFrameLayout
    lateinit var gender: TextView
    lateinit var card_id_pro: TextView
    lateinit var descr_card_date: TextView
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v:View = inflater.inflate(R.layout.fragment_fragment_profiles, container, false)
        setStatusBar()

        val namepro = v.findViewById<TextView>(R.id.name_pro)
        val phonepro = v.findViewById<TextView>(R.id.num_phone)
        val emailpro = v.findViewById<TextView>(R.id.email_pro)
        val localpro = v.findViewById<TextView>(R.id.address_pro)
        val dateOfBirth = v.findViewById<TextView>(R.id.dateOfBirth)
        card_id_pro = v.findViewById(R.id.card_id_pro)
        gender = v.findViewById(R.id.gender)
        descr_card_date = v.findViewById(R.id.card_date)

        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)
        shimmer_view_container.visibility = View.VISIBLE
        val btnEdit = v.findViewById<Button>(R.id.btnedit)
        val imgAvtPro = v.findViewById<CircleImageView>(R.id.imgAvtPro)
        val btn_feedback_profiles = v.findViewById<Button>(R.id.btn_feedback_profiles)

        Picasso.get().load(auth.currentUser!!.photoUrl).into(imgAvtPro)
        get_id(namepro,emailpro,phonepro,localpro,dateOfBirth, 2)
        getinfor(id, gender, "Account", "gender")

        getinfor(id, card_id_pro, "Account", "RFID")
        getinfor(id, descr_card_date, "Account", "date_creat_rfid")

        btnEdit.setOnClickListener(View.OnClickListener {
            view -> edit()
        })
        btn_feedback_profiles.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, HelpCenterScreen ::class.java))
        })
        return v
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

    override fun onResume() {
        super.onResume()
        shimmer_view_container.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
    }
    fun edit(){
        val newGamefragment = fragment_edit()
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, newGamefragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    fun get_id(n: TextView, e: TextView, p: TextView, a: TextView,dOb: TextView, chose: Int){
        db.child("id_auth").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(chose == 0) {
                    getinfor(id, n, "Account", "Name")
                    getinfor(id, e, "Account", "Email")
                    getinfor(id, p, "Account", "Phone number")
                    getinfor(id, a, "Account", "location")
                    getinfor(id, dOb, "Account", "date_of_birth")
                }
                else if(chose == 1){
                    addinfor(id, n.text.toString(), "Account", "Name")
                    addinfor(id, p.text.toString(), "Account", "Phone number")
                    addinfor(id, a.text.toString(), "Account", "location")
                    addinfor(id, dOb.text.toString(), "Account", "date_of_birth")
                }
                else if(chose == 2){
                    getinfor(id, n, "Account", "Name")
                    getinfor(id, e, "Account", "Email")
                    getinfor(id, p, "Account", "Phone number")
                    getinfor(id, a, "Account", "location")
                    getinfor(id, dOb, "Account", "date_of_birth")
                    shimmer_view_container.stopShimmerAnimation()
                    shimmer_view_container.visibility = View.GONE
                }
            }
        })
    }
    fun getinfor(id: String, input: TextView ,child: String, tag: String){
        db.child(child).child(id).child(tag).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null){
                    input.text = p0.value.toString()
                }else{
                    input.text = null
                }

            }
        })
    }
    fun addinfor(id: String, input: String ,child: String, tag: String){

        db.child(child).child(id).child(tag).setValue(input)
    }


}
