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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.quizr.main.MainActivity
import com.ncs.quizr.R
import com.ncs.quizr.admin.AdminActivity
import com.ncs.quizr.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

   lateinit var binding : ActivityAuthBinding

    private lateinit var auth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null
    var RC_SIGN_IN = 0
    private lateinit var sharedPref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var authViewModel : AuthViewModel
    val DB = Firebase.firestore
    lateinit var dialog : BottomSheetDialog


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
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        dialog = BottomSheetDialog(this)

       // val animation = AnimationUtils.loadAnimation(this,R.anim.rotate_fadein_fast)
      //  binding.imageViewew.startAnimation(animation)



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

    private fun showFormDialog(userAccount: FirebaseUser){




            val view = layoutInflater.inflate(R.layout.username_btm, null)
            val userNameEditBox = view.findViewById<EditText>(R.id.usernameEdit)
            val userCollegeIDEditBox = view.findViewById<EditText>(R.id.userCollegeEditBox)
            val userPnoIDEditBox = view.findViewById<EditText>(R.id.pnoEditBox)
            val desc = view.findViewById<TextView>(R.id.desc)
            val btnGO = view.findViewById<Button>(R.id.btnGo)
            buttonEffect(btnGO)
            val prog = view.findViewById<ProgressBar>(R.id.formProg)

            //Init bottomSheet
            val arr: Array<String> = arrayOf(
                "Coder",
                "Ninja",
                "Techno",
                "Technical",
                "Flyleaper",
                "Bugger",
                "Reacter",
                "Fizzr",
                "Papercut",
                "LittleOwl",
                "Byte"
            );
            val ranDom = (0..(arr.size)-1).random()
            val txt = (arr[ranDom] +" "+ userAccount.displayName!!.split(" ")[0])
            desc.text = "${txt}, you aren't a bot na ?!"
            dialog.setCancelable(false
            )
            dialog.dismissWithAnimation = true
            dialog.setContentView(view)
            dialog.show()


            btnGO.setOnClickListener {

                val userName = userNameEditBox.text.toString()
                val userCollegeID = userCollegeIDEditBox.text.toString()
                val pno = userPnoIDEditBox.text.toString()

                prog.visibility= View.VISIBLE
                prog.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slower)
                prog.animation.start()
                btnGO.visibility = View.GONE

                if (inputsGood(userName,userCollegeID,pno)){

                    val email = userAccount.email

                getToken(email!!)
                val userData = hashMapOf(
                    "userID" to userAccount.uid,
                    "username" to userName,
                    "collegeID" to userCollegeID.uppercase(),
                    "emailID" to email,
                    "photoURL" to userAccount.photoUrl,
                    "phone" to pno,

                )

                DB.collection("users").document(email)
                    .set(userData)
                    .addOnSuccessListener { documentReference ->

                        Log.d(AuthActivity.TAG, "DocumentSnapshot added with ID: $email")
                        dialog.dismiss()

                        editor.putString("userName", userName)
                        editor.putString("collegeID", userCollegeID)
                        editor.putString("uID", userAccount.uid)
                        editor.putString("email", email)
                        editor.putString("profileurl", userAccount.photoUrl.toString())
                        editor.putBoolean("isFormComplete", true)

                        editor.apply()
                        editor.commit()

                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(AuthActivity.TAG, "Error adding document", e)
                        Toast.makeText(this, "Error, retry again sometime later.", Toast.LENGTH_SHORT).show()
                        prog.clearAnimation()
                        prog.visibility = View.GONE
                        btnGO.visibility = View.VISIBLE

                    }


            }else {
                    prog.clearAnimation()
                    prog.visibility = View.GONE
                    btnGO.visibility = View.VISIBLE

                    Toast.makeText(this, "Stop sparky, fill all blanks first!", Toast.LENGTH_SHORT).show()

                }
            }

    }

    override fun onPause() {
        super.onPause()
        dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }


    fun inputsGood(userName: String, userCollegeID: String, pno: String) : Boolean{
        return !(userName.isBlank() || userCollegeID.isBlank() || pno.isBlank())
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

    fun getToken(email:String):Unit  {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
                DB.collection("users").document(email)
                    .set(hashMapOf("fcmToken" to result))

                Log.d("MainActivity", "Token : $result")
                // DO your thing with your firebase token

            }

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
                Toast.makeText(this, "Failed due to error code : ${e.message}", Toast.LENGTH_SHORT).show()
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
                    if (account.email=="fourtytwogamer@gmail.com"){
                        startActivity(Intent(this,AdminActivity::class.java))
                    }else {
                    updateUI(account)
                    }
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
            showFormDialog(account)
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



            val isFormCompleted = sharedPref.getBoolean("isFormComplete",false)
            if (isFormCompleted){
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                if (currentUser.email=="fourtytwogamer@gmail.com"){
                    startActivity(Intent(this, AdminActivity::class.java))
                }else {
                updateUI(currentUser)
            }
            }



        }
    }




}