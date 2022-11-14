package com.ncs.quizr.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.MainActivity
import com.ncs.quizr.R
import com.ncs.quizr.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

   lateinit var binding : ActivityAuthBinding

    private lateinit var auth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null
    var RC_SIGN_IN = 0
    lateinit var sharedPref : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    val DB = Firebase.firestore

    companion object{
        private val TAG : String = "AuthActivity"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        sharedPref = this.getSharedPreferences("userDetails", MODE_PRIVATE);
        editor = sharedPref.edit()




        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.button.setOnClickListener{
            signIn()
        }
        buttonEffect(binding.button)



    }

    private fun showDialog(userAccount: FirebaseUser){
        val dialog = BottomSheetDialog(this)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.username_btm, null)
        val userNameEditBox = view.findViewById<EditText>(R.id.usernameEdit)
        val userCollegeIDEditBox = view.findViewById<EditText>(R.id.userCollegeEditBox)
        val btnGO = view.findViewById<Button>(R.id.btnGo)

        //Init bottomSheet
        userNameEditBox.hint = "@"+ userAccount.displayName!!.split(" ")[0]
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.dismissWithAnimation= true
        dialog.show()


        btnGO.setOnClickListener {
            val userName = userNameEditBox.text.toString()
            val userCollegeID = userCollegeIDEditBox.text.toString()

            editor.putString("userName", userName)
            editor.putString("collegeID", userCollegeID)
            editor.putString("uID", userAccount.uid)
            editor.apply()
            editor.commit()

            val userData = hashMapOf(
                "userID" to userAccount.uid,
                "username" to userName,
                "collegeID" to userCollegeID.uppercase(),
                "emailID" to userAccount.email,
                "photoURL" to userAccount.photoUrl,
                "phone" to userAccount.phoneNumber,
            )

            DB.collection("users")
                .add(userData)
                .addOnSuccessListener { documentReference ->

                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Error, retry again sometime later.", Toast.LENGTH_SHORT).show()

                }
        }



    }






    @SuppressLint("ClickableViewAccessibility")
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
               // Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken.toString())
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                disableProgress(1)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
       // binding.progressBar.setVisibility(View.VISIBLE)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val account: FirebaseUser = auth.getCurrentUser()!!
                    updateUI(account)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                    disableProgress(1)
                }
            }
    }
    fun disableProgress(choice : Int){
        if(choice == 1) {
            binding.progressBar.clearAnimation()
            binding.progressBar.visibility = View.GONE
            binding.button.isEnabled = true

        }else {
            binding.progressBar.visibility= View.VISIBLE
            binding.progressBar.animation = AnimationUtils.loadAnimation(this,
                R.anim.fade_in_slower
            )
            binding.progressBar.animation.start()
            binding.button.isEnabled = false

        }

        }

    fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            disableProgress(1)

            showDialog(account)
        } else {
            disableProgress(1)
            Toast.makeText(this, "Sign in with your google account", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        disableProgress(2)
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }




}