package com.aliulu.mesajlasmauygulamasi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aliulu.mesajlasmauygulamasi.KayitOlActivity
import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.messages.SohbetListesiActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_giris.*

class GirisActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris)

        mAuth = FirebaseAuth.getInstance()

        registerBack.setOnClickListener {
            val intent = Intent(this,
                KayitOlActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        kayitOlDon.setOnClickListener {
            val intent = Intent(this,
                    KayitOlActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        login.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    // sayfaya git
                    val intent = Intent(this,
                        SohbetListesiActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
                .addOnFailureListener { exception ->
                    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }
}