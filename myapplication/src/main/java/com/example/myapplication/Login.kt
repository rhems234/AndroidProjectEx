package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {

    private val TAG: String = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            val intent = Intent(this@Login, MainActivity::class.java)

            val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:9010/api/") // 스프링 부트 API의 기본 URL을 입력합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val loginRequest = LoginRequest(id, pw)
            val call = apiService.login(loginRequest)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.status == "success") {
                            // 로그인 성공 처리
                            showDialog("success")
                        } else {
                            // 로그인 실패 처리
                            showDialog("fail")
                        }
                    } else {
                        // API 요청 실패 처리
                        showDialog("fail")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // 통신 실패 처리
                    showDialog("fail")
                }
            })
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }
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
                        startActivity(Intent(this@Login, MainActivity::class.java))
                    }
                }
            }
        }

        dialogBuilder.setPositiveButton("확인", dialogListener)
        dialogBuilder.show()
    }
}

private fun <T> Call<T>.enqueue(callback: Callback<LoginResponse>) {

}
