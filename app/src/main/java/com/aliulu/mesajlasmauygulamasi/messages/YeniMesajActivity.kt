package com.aliulu.mesajlasmauygulamasi.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aliulu.mesajlasmauygulamasi.adapter.KullaniciAraclari
import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_yeni_mesaj.*
import kotlinx.android.synthetic.main.groupie_row_yeni_mesaj.*

class YeniMesajActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yeni_mesaj)

        toolbarYeniMesaj.setTitle("")
        setSupportActionBar(toolbarYeniMesaj)
        supportActionBar
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        kullanicilariGetir()
        sohbetListDon.setOnClickListener {
            val intent = Intent(this,SohbetListesiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    private fun kullanicilariGetir() {
        val refrence = database.getReference("users")
        refrence.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    val user = it.getValue(Kullanici::class.java)
                    user?.let {
                        adapter.add(
                                KullaniciAraclari(it)
                        )
                        adapter.notifyDataSetChanged()
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val kullaniciAraci = item as KullaniciAraclari
                    val intent = Intent(view.context, MesajActivity::class.java)
                    intent.putExtra("user", kullaniciAraci.user)
                    startActivity(intent)
                    finish()
                }
                recyclerViewYeniMesaj.adapter = adapter


            }

        })
    }
}