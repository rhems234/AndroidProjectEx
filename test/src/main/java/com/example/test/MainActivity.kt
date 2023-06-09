package com.example.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var join_btn // 회원가입 버튼
            : Button? = null
    var login_btn // 로그인 버튼
            : Button? = null
    var id_edit // id 에디트
            : EditText? = null
    var pw_edit // pw 에디트
            : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        join_btn = findViewById<View>(R.id.join_btn) as Button // 회원가입 버튼을 찾고
        login_btn = findViewById<View>(R.id.login_btn) as Button // 로그인 버튼을 찾고
        join_btn!!.setOnClickListener(this) // 리스너를 달아줌.
        login_btn!!.setOnClickListener(this) // 리스너를 달아줌.
        id_edit = findViewById<View>(R.id.id_edit) as EditText // id 에디트를 찾음.
        pw_edit = findViewById<View>(R.id.pw_edit) as EditText // pw 에디트를 찾음.
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.join_btn -> {
                val intent = Intent(this@MainActivity, join::class.java)
                startActivity(intent) // 새 액티비티를 열어준다.
                finish() // 현재의 액티비티는 끝내준다.
            }

            R.id.login_btn -> {
                val intent2 = Intent(this@MainActivity, login::class.java)
                startActivity(intent2)
                finish()
            }
        }
        // 서버와 연동하기
        fun login() {
            Log.w("login","로그인 하는중");
            try {
                val id = id_edit?.getText().toString();
                val pw = pw_edit?.getText().toString();
                Log.d("앱에서 보낸값",id+", "+pw);

                val task = CustomTask()
                val result: String = task.execute(id, pw).get()
                Log.w("받은값", result)

                val intent2 = Intent(this, login::class.java)
                startActivity(intent2)
                finish()
            } catch (e: Exception) {

            }
        }

    }
}
class CustomTask : AsyncTask<String, Void, String>() {
    private var sendMsg: String? = null
    private var receiveMsg: String? = null

    override fun doInBackground(vararg strings: String): String? {
        try {
            var str: String
            val url = URL("http://localhost:8080/connection")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.requestMethod = "POST"
            conn.doOutput = true

            sendMsg = "id=" + strings[0] + "&pw=" + strings[1]
            val osw = OutputStreamWriter(conn.outputStream)
            osw.write(sendMsg)
            osw.flush()

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val tmp = InputStreamReader(conn.inputStream, "UTF-8")
                val reader = BufferedReader(tmp)
                val buffer = StringBuffer()
                while (reader.readLine().also { str = it } != null) {
                    buffer.append(str)
                }
                receiveMsg = buffer.toString()
            } else {
                Log.i("통신 결과", conn.responseCode.toString() + "에러")
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return receiveMsg
    }

}