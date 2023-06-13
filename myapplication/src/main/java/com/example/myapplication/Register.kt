package com.example.myapplication

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private val TAG: String = "RegisterActivity"
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitBuilder.getApiService()

        binding.btnRegister.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()
            val pwRe = binding.editPwRe.text.toString()
            val nickname = binding.editNickname.text.toString()

            // 유저가 항목을 다 채우지 않았을 경우
            if (id.isEmpty() || pw.isEmpty() || nickname.isEmpty()) {
                showDialog("blank")
                return@setOnClickListener
            }

            // 비밀번호가 일치하지 않을 경우
            if (pw != pwRe) {
                showDialog("not same")
                return@setOnClickListener
            }

            // 회원가입 API 호출
            register(id, pw, nickname)
        }
    }

    // 회원가입 API 호출
    private fun register(id: String, pw: String, nickname: String) {
        val call = apiService.insertMember(id, pw, nickname)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val message = response.body()
                    if (message == "회원가입 성공") {
                        // 회원가입 성공 처리
                        showDialog(message)
                    } else {
                        // 회원가입 실패 처리
                        showDialog("회원가입 실패")
                    }
                } else {
                    // 서버 오류 등의 상태코드가 반환된 경우
                    showDialog("회원가입 실패")
                }

                Log.d(TAG, "통신 성공 - HTTP 상태 코드: ${response.code()}")
                Log.d(TAG, "통신 성공 - 응답 메시지: ${response.body()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 통신 실패 처리
                showDialog("통신 실패")

                Log.e(TAG, "통신 실패: ${t.message}")
            }
        })
    }


    // 회원가입 성공/실패 시 다이얼로그를 띄워주는 메소드
    private fun showDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle("회원가입 결과")
        dialogBuilder.setMessage(message)

        val dialogListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Log.d(TAG, "확인 버튼 클릭")
                    if (message == "회원가입 성공") {
                        finish()
                    }
                }
            }
        }

        dialogBuilder.setPositiveButton("확인", dialogListener)
        dialogBuilder.show()
    }
}
