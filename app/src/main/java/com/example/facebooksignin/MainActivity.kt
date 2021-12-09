package com.example.facebooksignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val callbackManger= CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        login_button.setPermissions("email","public_profile")
        login_button.registerCallback(callbackManger,object:FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("ok", "facebook:onSuccess:$result")
                if (result != null) {
                   handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
                Log.d("ok", "facebook:onCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d("ok", "facebook:onError", error)
            }

        })

        btnLogout.setOnClickListener {
            auth.signOut()
            updateUI(auth.currentUser)
        }


    }

    override fun onStart() {
        super.onStart()
        val curentUser = auth.currentUser
        updateUI(curentUser)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManger.onActivityResult(requestCode,resultCode,data)

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("ok", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ok", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ok", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user !=null)
        {
            textNAme.setText(user.displayName)
            if(user.photoUrl !=null)
            {
                var photo = user.photoUrl.toString()
                photo = photo + "?type=large"
                Glide.with(this).load(photo).into(imageView)
               // Picasso.get().load(photo).into(imageView)
            }
        }else{
            textNAme.text="hello World"
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }

    }
}

///sign out= Firebase.auth.signOut()

//appid=290878023053385