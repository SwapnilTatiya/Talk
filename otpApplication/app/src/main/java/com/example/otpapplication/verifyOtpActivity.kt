package com.example.otpapplication

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class verifyOtpActivity : AppCompatActivity() {
    lateinit var digit1:EditText
    lateinit var digit2:EditText
    lateinit var digit3:EditText
    lateinit var digit4:EditText
    lateinit var digit5:EditText
    lateinit var digit6:EditText
    lateinit var buttonVerifyOtp: Button
    lateinit var progressBar: ProgressBar
    lateinit var verificationId:String
    lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        digit1=findViewById(R.id.inputCode1)
        digit2=findViewById(R.id.inputCode2)
        digit3=findViewById(R.id.inputCode3)
        digit4=findViewById(R.id.inputCode4)
        digit5=findViewById(R.id.inputCode5)
        digit6=findViewById(R.id.inputCode6)
        var textMobile=findViewById<TextView>(R.id.textMobile)
        phoneNumber=intent.getStringExtra("phoneNumber").toString()
        textMobile.setText(phoneNumber)
        buttonVerifyOtp=findViewById(R.id.verifyOtpButton)
        progressBar=findViewById(R.id.progressBar)
        verificationId= intent.getStringExtra("verificationId").toString()
        setupOTPDigits()
    }
    fun setupOTPDigits(){
        digit1.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()){
                    digit2.requestFocus()
                }
            }
        })
        digit2.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()){
                    digit3.requestFocus()
                }
            }
        })
        digit3.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()){
                    digit4.requestFocus()
                }
            }
        })
        digit4.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()){
                    digit5.requestFocus()
                }
            }
        })
        digit5.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()){
                    digit6.requestFocus()
                }
            }
        })
    }

    fun verifyOtp(view: View) {
        if(digit1.text.toString().trim().isEmpty()||
            digit2.text.toString().trim().isEmpty()||
            digit3.text.toString().trim().isEmpty()||
            digit4.text.toString().trim().isEmpty()||
            digit5.text.toString().trim().isEmpty()||
            digit6.text.toString().trim().isEmpty()){
            Toast.makeText(this@verifyOtpActivity,"Re-check the otp and try again",Toast.LENGTH_SHORT).show()
            return
        }
        var code=digit1.text.toString()+
                    digit2.text.toString()+
                    digit3.text.toString()+
                    digit4.text.toString()+
                    digit5.text.toString()+
                    digit6.text.toString()
        if(verificationId!=null) {
            progressBar.visibility=View.VISIBLE
            buttonVerifyOtp.visibility=View.INVISIBLE
            var phoneAuthCredential=PhoneAuthProvider.getCredential(
                verificationId!!,code
            )
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this){task->
                    if (task.isSuccessful) {
                        progressBar.visibility=View.GONE
                        buttonVerifyOtp.visibility=View.VISIBLE
                        Toast.makeText(this@verifyOtpActivity,"Verification Successful",Toast.LENGTH_SHORT).show()
                        var intent = Intent(this@verifyOtpActivity, completeProfileActivity::class.java)
                        val user = task.result?.user
                        intent.putExtra("phoneNumber",phoneNumber)
                        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
                        Toast.makeText(this@verifyOtpActivity,"had to go to completeProfileActivity",Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                        TODO("GIVE THIS CODE A SECOND OPINION")
                    } else {
                        // Sign in failed, display a message and update the UI
                        progressBar.visibility=View.GONE
                        buttonVerifyOtp.visibility=View.VISIBLE
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this@verifyOtpActivity,"The verification code entered was invalid",Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                }
        }
    }

    fun resendOtp(view: View) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this@verifyOtpActivity,
            object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@verifyOtpActivity,"Verification Failed",Toast.LENGTH_SHORT).show()
                }
                override fun onCodeSent(newVerificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId=newVerificationId
                    Toast.makeText(this@verifyOtpActivity,"New OTP sent",Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

}