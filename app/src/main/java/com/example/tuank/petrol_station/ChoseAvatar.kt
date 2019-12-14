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
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ChoseAvatar : AppCompatActivity() {
    lateinit var imgAvatarChosen: CircleImageView
    lateinit var btnChoseAvatar: Button
    lateinit var btnChoseCapture: ImageView
    lateinit var btnChoseGallery: ImageView
    lateinit var proGressbar: ProgressBar
    private var IMGE_PICK_CODE = 0;
    private val PERMISSION_CODE = 1001;

    private val GALLERY = 1
    private val CAMERA = 2

    lateinit var contentURI:Uri
    val db = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chose_avatar)

        val window = this@ChoseAvatar.getWindow()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChoseAvatar, R.color.my_statusbar_color_green))

        imgAvatarChosen = findViewById(R.id.imgAvatarChosen)
        btnChoseAvatar = findViewById(R.id.btnChoseAvatar)
        btnChoseCapture = findViewById(R.id.btnChoseCapture)
        btnChoseGallery = findViewById(R.id.btnChoseGallery)
        proGressbar = findViewById(R.id.proGressbar)

        proGressbar.visibility = View.INVISIBLE
        if(IMGE_PICK_CODE == 0){
            Picasso.get().load(auth.currentUser!!.photoUrl).into(imgAvatarChosen)
        }
        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnChoseCapture.setOnClickListener(View.OnClickListener {
            openCameraCapture()
        })
        btnChoseGallery.setOnClickListener(View.OnClickListener {
            openGallery()
        })
        btnChoseAvatar.setOnClickListener(View.OnClickListener {
            setAvatarChosen()
        })

        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })
    }

    private fun setAvatarChosen() {
        btnChoseAvatar.visibility = View.INVISIBLE
        proGressbar.visibility = View.VISIBLE
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
                                db.child("Account").child(id).child("avatar_url").setValue(downloadUri.toString())
                                proGressbar.visibility = View.INVISIBLE
                                btnChoseAvatar.visibility = View.VISIBLE
                                Toast.makeText(this@ChoseAvatar, "successful!", Toast.LENGTH_LONG).show()
                            }
                            else{
                                proGressbar.visibility = View.INVISIBLE
                                btnChoseAvatar.visibility = View.VISIBLE
                                Toast.makeText(this@ChoseAvatar, "failed!!", Toast.LENGTH_LONG).show()
                            }
                        }
            } else {
                proGressbar.visibility = View.INVISIBLE
                btnChoseAvatar.visibility = View.VISIBLE
                Toast.makeText(this@ChoseAvatar, "failed up!", Toast.LENGTH_LONG).show()
                // Handle failures
                // ...
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun openCameraCapture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            IMGE_PICK_CODE = 1
            if (data != null)
            {
                contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
                    Toast.makeText(this@ChoseAvatar, "Image Saved!", Toast.LENGTH_SHORT).show()

                    saveImage(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ChoseAvatar, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            IMGE_PICK_CODE = 1
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imgAvatarChosen!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)

        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        imgAvatarChosen!!.setImageBitmap(myBitmap)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }
        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())
            contentURI = Uri.fromFile(File(f.absolutePath))
            Log.d("TAG", "File Saved1::--->" + contentURI.toString())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }

    fun backToMain(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
