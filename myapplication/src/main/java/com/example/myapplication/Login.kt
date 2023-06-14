package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.controller.ApiService
import com.example.myapplication.controller.LoginResponse
import com.example.myapplication.controller.Member
import com.example.myapplication.controller.RetrofitBuilder
import com.example.myapplication.controller.SharedPreferencesUtil
import com.example.myapplication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private val TAG: String = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitBuilder.createApiService()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바(App Bar)로 지정
        val actionBar = supportActionBar //앱바 제어를 위해 툴바 액세스
        actionBar!!.setDisplayHomeAsUpEnabled(true) // 앱바에 뒤로가기 버튼 만들기

        // 로그인 버튼
        val loginButton = binding.btnLogin
        loginButton.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()

            // 유저가 항목을 다 채우지 않았을 경우
            if (id.isEmpty() || pw.isEmpty()) {
                showDialog("blank")
                return@setOnClickListener
            }

            // 로그인 API 호출
            val nickname = ""
            login(id, pw, nickname)

            val isLoggedIn = SharedPreferencesUtil.isLoggedIn(this)
            Log.d(TAG, "세션 유지 상태: $isLoggedIn")

            if (isLoggedIn) {
                // 사용자가 이미 로그인되어 있으면 MainActivity로 이동합니다.
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        // 회원가입 버튼 클릭시 MainActivity3 이동
        val joinButton = binding.btnRegister
        joinButton.setOnClickListener {
            val intent = Intent(applicationContext, Register::class.java)
            startActivityForResult(intent, 101)
        } // 인텐트에 메뉴액티비티 넣어서 호출

    }

    // 로그인 시 토스트
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (data != null) {
                val name = data.getStringExtra("name")
                val toast = Toast.makeText(
                    baseContext,
                    "result code : $resultCode, $name",
                    Toast.LENGTH_LONG
                )
                toast.show()
            }
        }
    }

    // 로그인 API 호출
    private fun login(id: String, pw: String, nickname: String) {
        val member = Member(id, pw, nickname)
        val call = apiService.login(member)
        Log.d(TAG, "로그인 요청 - ID: $id, PW: $pw")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.message == "Login successful") {
                        // 로그인 성공 처리
                        Log.d(TAG, "로그인 성공")
                        showDialog("success")

                        val sessionId = loginResponse?.sessionId ?: ""
                        val sessionPw = loginResponse?.sessionPw ?: ""
                        val nickname = loginResponse?.nickname ?: ""

                        // 세션 ID 저장
                        SharedPreferencesUtil.saveSessionId(this@Login, sessionId, sessionPw, nickname)
                        Log.d(TAG, "Saving session ID: $sessionId, session PW: $sessionPw, nickname: $nickname")

                        // 로그인 상태를 true로 설정합니다.
                        SharedPreferencesUtil.setLoggedIn(this@Login, true)

                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 로그인 실패 처리
                        Log.d(TAG, "로그인 실패")
                        showDialog("fail")
                    }
                } else {
                    // API 요청 실패 처리
                    Log.d(TAG, "API 요청 실패")
                    showDialog("fail")
                }
                Log.d(TAG, "통신 성공 - HTTP 상태 코드: ${response.code()}")
                Log.d(TAG, "통신 성공 - 응답 메시지: ${response.body()?.toString()}")
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 통신 실패 처리
                showDialog("fail")
                Log.e(TAG, "통신 실패: ${t.message}")
            }
        })
    }



    // 로그인 성공/실패 시 다이얼로그를 띄워주는 메소드
    private fun showDialog(type: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        if (type == "success") {
            dialogBuilder.setTitle("로그인 성공")
            dialogBuilder.setMessage("로그인 성공!")
        } else if (type == "fail") {
            dialogBuilder.setTitle("로그인 실패")
            dialogBuilder.setMessage("아이디와 비밀번호를 확인해주세요")
        }

        val dialogListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Log.d(TAG, "확인 버튼 클릭")
                    if (type == "success") {
                        // 로그인 성공 시 처리할 작업 수행
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        dialogBuilder.setPositiveButton("확인", dialogListener)
        val dialog = dialogBuilder.create() // 다이얼로그 객체 생성
        dialog.show() // 다이얼로그 표시
    }

}
