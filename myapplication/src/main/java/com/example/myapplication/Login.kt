package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.controller.ApiService
import com.example.myapplication.controller.LoginResponse
import com.example.myapplication.controller.Member
import com.example.myapplication.controller.RetrofitBuilder
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

        binding.btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()

            // 유저가 항목을 다 채우지 않았을 경우
            if (id.isEmpty() || pw.isEmpty()) {
                showDialog("blank")
                return@setOnClickListener
            }

            // 로그인 API 호출
            login(id, pw)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }
    }

    // 로그인 API 호출
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
                }
            }
        }

        dialogBuilder.setPositiveButton("확인", dialogListener)
        dialogBuilder.show()
    }
}
