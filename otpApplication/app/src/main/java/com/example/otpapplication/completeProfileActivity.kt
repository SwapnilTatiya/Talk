package com.example.otpapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.makeramen.roundedimageview.RoundedImageView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap

class completeProfileActivity : AppCompatActivity() {
    lateinit var nameInput:EditText
    lateinit var profileImageView: RoundedImageView
    lateinit var encodedImage: String
    lateinit var nameOfUser:String
    lateinit var buttonLetsStart: Button
    lateinit var progressBar: ProgressBar
    lateinit var addImageTextView: TextView
    val REQUEST_CODE=100
    private lateinit var preferenceManager:PreferenceManager
    lateinit var phoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        nameInput=findViewById(R.id.nameInput)
        profileImageView=findViewById(R.id.profileImageView)
        buttonLetsStart=findViewById(R.id.letsStartButton)
        progressBar=findViewById(R.id.progressBar)
        addImageTextView=findViewById(R.id.addImageTextView)
        phoneNumber=intent.getStringExtra("phoneNumber").toString()
        showToast(phoneNumber)
    }

    fun getStarted(view: View) {
        loading(true)
        if(isValidProfile()){
            showToast("Valid Profile")
            var database=FirebaseFirestore.getInstance()
            var user:HashMap<String,String> = HashMap()
            var constants= Constants()
            user.put(constants.KEY_PHONE_NUMBER,phoneNumber)
            user.put(constants.KEY_NAME,nameOfUser)
            user.put(constants.KEY_IMAGE,encodedImage)
            preferenceManager= PreferenceManager(applicationContext)
            database.collection(constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener {
                loading(false)
                preferenceManager.putString(constants.KEY_PHONE_NUMBER,phoneNumber)
                preferenceManager.putBoolean(constants.KEY_PROFILE_COMPLETE,true)
                preferenceManager.putString(constants.KEY_NAME,nameOfUser)
                preferenceManager.putString(constants.KEY_IMAGE,encodedImage)
                preferenceManager.putString(constants.KEY_USER_ID,it.id)
                var intent=Intent(this@completeProfileActivity, MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            }
            .addOnFailureListener {
                loading(false)
                showToast("failed uploading data")
            }
        }
    }

    fun showToast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bitmap: Bitmap):String{
        var previewWidth=150
        var previewHeight=bitmap.height+previewWidth/bitmap.width
        var previewBitmap=Bitmap.createScaledBitmap(bitmap,previewHeight,previewWidth,false)
        var byteArrayOutputStream:ByteArrayOutputStream= ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream)
        var bytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun isValidProfile():Boolean{
        nameOfUser=nameInput.text.toString().trim()
        if(encodedImage==null){
            showToast("Select a profile Image")
            return false
        }else if(nameOfUser.isEmpty()){
            showToast("Enter your name")
            return false
        }else{
            return true
        }
    }

    fun loading(isLoading:Boolean){
        if(isLoading){
            buttonLetsStart.visibility=View.INVISIBLE
            progressBar.visibility=View.VISIBLE
            return
        }
        buttonLetsStart.visibility=View.VISIBLE
        progressBar.visibility=View.GONE
    }

    fun addProfilePicture(view: View) {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent,REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK&&requestCode == REQUEST_CODE){
            var imageUri=data?.data
            var inputStream=contentResolver.openInputStream(imageUri!!)
            var bitmap=BitmapFactory.decodeStream(inputStream)
            addImageTextView.visibility=View.GONE
            profileImageView.setImageURI(data?.data) // handle chosen image
            encodedImage=encodeImage(bitmap)
        }
    }
}