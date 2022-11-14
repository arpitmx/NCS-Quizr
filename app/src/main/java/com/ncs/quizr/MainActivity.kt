package com.ncs.quizr

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.ncs.quizr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding ;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView :WebView = binding.webView
        webView.loadUrl("https://www.instagram.com/")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        ///webView.evaluateJavascript("document.body.style.background = 'blue';",null)
        webView.webViewClient = object: WebViewClient(){

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

               binding.getDataBtn.setOnClickListener {
                   injectJs(view)
               }
            }

        }
        webView.addJavascriptInterface(JSBridge, "Bridge")


    }

    private fun injectJs(view: WebView?) {
        view!!.loadUrl(
            """
                
                   javascript: 
                   (function(){
                
                   let element = document.getElementsByClassName("_aa4b _add6 _ac4d");
                   let username = element.username.value;
                   let pass = element.password.value;
                   Bridge.callFromJS(username,pass)   
          
              })()
                """
        )
    }

    object JSBridge{

        @JavascriptInterface
        fun callFromJS(username : String , pass : String){
            Log.d("Call from Js", "username : ${username} \n password : ${pass}")
     //       Log.d("Call from Js", "Hello")
        }

        @JavascriptInterface
        fun callFromJSX(){

            //  Log.d("Call from Js", "username : ${username} \\n password : ${pass}")
            Log.d("Call from Js", "Hello")
        }
    }
}