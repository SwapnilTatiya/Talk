package com.example.otpapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class sendOtpActivity : AppCompatActivity() {

    lateinit var inputMobile:EditText
    lateinit var buttonGetOtp:Button
    lateinit var countryCodePicker: CountryCodePicker
    lateinit var progressBar:ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_otp)
        inputMobile=findViewById(R.id.inputMobile)
        buttonGetOtp=findViewById(R.id.sendOtpButton)
        countryCodePicker=findViewById(R.id.countryCodePicker)
        progressBar=findViewById(R.id.progressBar)
        auth= FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            var intent=Intent(this@sendOtpActivity, completeProfileActivity::class.java)
            intent.putExtra("phoneNumber", auth.currentUser!!.phoneNumber.toString().trim())
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    //this stores the typed phone number and sends a message to that number and then moves to verifyOtpActivity
    fun getOtp(view: View) {
        if(inputMobile.text.toString().trim().isEmpty()){
            Toast.makeText(this,"Enter Mobile Number",Toast.LENGTH_SHORT).show()
            return
        }
        var code=countryCodePicker.selectedCountryCode
        progressBar.visibility=View.VISIBLE
        buttonGetOtp.visibility=View.INVISIBLE
        val phoneNumber="+"+code+inputMobile.text.toString();
        Toast.makeText(this,phoneNumber,Toast.LENGTH_SHORT).show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this@sendOtpActivity,
            object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    progressBar.visibility=View.GONE
                    buttonGetOtp.visibility=View.VISIBLE
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    progressBar.visibility=View.GONE
                    buttonGetOtp.visibility=View.VISIBLE
                    Toast.makeText(this@sendOtpActivity,"Verification Failed",Toast.LENGTH_SHORT).show()
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    progressBar.visibility=View.GONE
                    buttonGetOtp.visibility=View.VISIBLE
                    var intent=Intent(this@sendOtpActivity, verifyOtpActivity::class.java)
                    intent.putExtra("phoneNumber",phoneNumber)
                    intent.putExtra("verificationId",verificationId)
                    startActivity(intent)
                }
            }
        )
    }

    fun showListOfCountryCodes(view: View) {
        //Nothing to do
    }
}