package com.example.firebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var edtemail: EditText
    private lateinit var edtpassw: EditText
    private lateinit var btnregister: Button
    private lateinit var btnlogin: Button
    private lateinit var btnshow: Button
    private lateinit var txtshow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        edtemail = findViewById(R.id.edtemail)
        edtpassw = findViewById(R.id.edtpassw)
        btnregister = findViewById(R.id.btnregister)
        btnlogin = findViewById(R.id.btnlogin)
        btnshow = findViewById(R.id.btnshow)
        txtshow = findViewById(R.id.txtshow)

        btnregister.setOnClickListener {
            registerUser()
        }
        btnlogin.setOnClickListener {
            loginUser()
        }

        btnshow.setOnClickListener {
            showUserData()
        }
    }

    private fun registerUser() {
        val email = edtemail.text.toString().trim()
        val password = edtpassw.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserData(email)
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    edtemail.text.clear()
                    edtpassw.text.clear()
                } else {
                    Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser() {
        val email = edtemail.text.toString().trim()
        val password = edtpassw.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(email: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("Users")
        val user = mapOf("email" to email)

        database.child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Lưu dữ liệu thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu dữ liệu thất bại!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUserData() {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("Users")

        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child("email").value?.toString()
                txtshow.text = "Email: ${email ?: "Không có dữ liệu"}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
