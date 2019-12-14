package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_screen_signup.*
import com.example.tuank.petrol_station.R.drawable.user
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class screen_signup : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    lateinit var database: DatabaseReference
    lateinit var btnChoseAvatar: ImageView
    lateinit var imgAvatarChosen: CircleImageView
    private val GALLERY = 1
    lateinit var contentURI: Uri
    lateinit var btnsignup: Button
    lateinit var proGressbar: ProgressBar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_signup)
        val window = this@screen_signup.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@screen_signup, R.color.my_statusbar_color_green))

        btnsignup = findViewById(R.id.btnsignup)
        val btnsignin1 = findViewById<View>(R.id.btnsignin2) as Button
        imgAvatarChosen = findViewById(R.id.imgAvatarChosen)
        btnChoseAvatar = findViewById(R.id.btnChoseAvatar)
        database = FirebaseDatabase.getInstance().getReference("Account")

        proGressbar = findViewById(R.id.proGressbar)
        proGressbar.visibility = View.INVISIBLE

        btnsignup.setOnClickListener(View.OnClickListener {
            view -> register()
        })
        btnsignin1.setOnClickListener(View.OnClickListener {
            view -> exchangesignup()
        })
        btnChoseAvatar.setOnClickListener(View.OnClickListener {
            setAvatarDisplay()
        })
        imgAvatarChosen.setOnClickListener(View.OnClickListener {
            setAvatarDisplay()
        })
    }

    private fun setAvatarDisplay() {
        openGallery()
    }

    private fun register(){
        btnsignup.visibility = View.INVISIBLE
        proGressbar.visibility = View.VISIBLE
        val emailtxt = findViewById<View>(R.id.Email) as EditText
        val usernametxt = findViewById<View>(R.id.name) as EditText
        val passwordtxt = findViewById<View>(R.id.password) as EditText
        val repasswordtxt = findViewById<View>(R.id.repassword) as EditText

        var email = emailtxt.text.toString()
        var password = passwordtxt.text.toString()
        var username = usernametxt.text.toString()
        var repassword = repasswordtxt.text.toString()

        if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty() && !repassword.isEmpty())
        {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener<AuthResult>{ task ->
                        if (task.isSuccessful){
                            val use = auth.currentUser
                            val id = use!!.uid

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build()
                            use.updateProfile(profileUpdates)
                            database.child(id).child("Name").setValue(username)
                            database.child(id).child("Email").setValue(email)
                            database.child(id).child("money").setValue(0)
                            database.child(id).child("PINCODE").setValue(123456)
                            database.child(id).child("responses").child("card lost").setValue("none")
                            database.child(id).child("transaction_infor").child("no_payment").setValue(0)
                            database.child(id).child("transaction_infor").child("no_recharge").setValue(0)
                            database.child(id).child("transaction_infor").child("no_transfer").setValue(0)

                            try {
                                if(contentURI != null){
                                    setAvatarChosen()
                                }
                            }catch (e:Exception){
                                val user = auth.currentUser
                                val downloadUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fuelapp-c00b1.appspot.com/o/default%20avt%2Fuser.png?alt=media&token=7c3c1dc6-4fc4-418a-b737-c329e898ea14")
                                val profileUpdate = UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUri)
                                        .build()

                                user?.updateProfile(profileUpdate)
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                database.child(id).child("avatar_url").setValue(downloadUri.toString())
                                                proGressbar.visibility = View.INVISIBLE
                                                btnsignup.visibility = View.VISIBLE

                                                Toast.makeText(this@screen_signup, "successful!", Toast.LENGTH_LONG).show()
                                                startActivity(Intent(this, MainActivity ::class.java))
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                                finish()
                                            }
                                            else{
                                                proGressbar.visibility = View.INVISIBLE
                                                btnsignup.visibility = View.VISIBLE
                                                Toast.makeText(this@screen_signup, "failed!!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                            }

                            Toast.makeText(this, "Successful!", Toast.LENGTH_LONG).show()

                        }else{
                            proGressbar.visibility = View.INVISIBLE
                            btnsignup.visibility = View.VISIBLE
                            Toast.makeText(this,"Register failed", Toast.LENGTH_LONG).show()
                        }
                    })

        }else if(password != repassword)
        {
            proGressbar.visibility = View.INVISIBLE
            btnsignup.visibility = View.VISIBLE
            Toast.makeText(this, "Password and retype not same", Toast.LENGTH_LONG).show()
        }else{
            proGressbar.visibility = View.INVISIBLE
            btnsignup.visibility = View.VISIBLE
            Toast.makeText(this, "please fill all of filed", Toast.LENGTH_LONG).show()
        }

    }
    private fun exchangesignup(){
        startActivity(Intent(this, MainActivity ::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
                    Toast.makeText(this@screen_signup, "Image Saved!", Toast.LENGTH_SHORT).show()
                    btnChoseAvatar.visibility = View.INVISIBLE
                    imgAvatarChosen!!.setImageBitmap(bitmap)
//                    imgAvatarChosen.alpha = 0f

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@screen_signup, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun setAvatarChosen() {
        val use = auth.currentUser
        val id = use!!.uid
        val mStorage: StorageReference = FirebaseStorage.getInstance().reference.child("users_photos")
        val imageFilePath: StorageReference = mStorage.child(contentURI.lastPathSegment)
        val urlTask = imageFilePath.putFile(contentURI).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageFilePath.downloadUrl
        }).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser
                val downloadUri = task.result
                val profileUpdate = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUri)
                        .build()

                user?.updateProfile(profileUpdate)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.child(id).child("avatar_url").setValue(downloadUri.toString())
                                proGressbar.visibility = View.INVISIBLE
                                btnsignup.visibility = View.VISIBLE

                                Toast.makeText(this@screen_signup, "successful!", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity ::class.java))
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                finish()
                            }
                            else{
                                proGressbar.visibility = View.INVISIBLE
                                btnsignup.visibility = View.VISIBLE
                                Toast.makeText(this@screen_signup, "failed!!", Toast.LENGTH_LONG).show()
                            }
                        }
            } else {
                proGressbar.visibility = View.INVISIBLE
                btnsignup.visibility = View.VISIBLE
                Toast.makeText(this@screen_signup, "failed up!", Toast.LENGTH_LONG).show()
                // Handle failures
                // ...
            }
        }
    }

}
