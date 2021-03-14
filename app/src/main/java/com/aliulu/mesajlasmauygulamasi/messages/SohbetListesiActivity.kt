package com.aliulu.mesajlasmauygulamasi.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.aliulu.mesajlasmauygulamasi.*
import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.adapter.SohbetListesiSatirlari
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.aliulu.mesajlasmauygulamasi.models.SohbetMesaji
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_sohbet_listesi.*

class SohbetListesiActivity :AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    companion object{
        var guncelKullanici : Kullanici? = null
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    val sohbetListesiHashMap = HashMap<String, SohbetMesaji>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_listesi)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        toolbarSohbetList.setTitle("")
        toolbarSohbetList.inflateMenu(R.menu.nav_menu)
        setSupportActionBar(toolbarSohbetList)

        SohbetListesiDinle()
        guncelKullaniciyiGetir()
        girisKontrol()
        recyclerViewGecmisMesajlar.adapter = adapter
        recyclerViewGecmisMesajlar.addItemDecoration(
            DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        )
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,
                MesajActivity::class.java)
            val row = item as SohbetListesiSatirlari
            val mesajlasilanKisi = row.sohbetKisisi
            intent.putExtra("user",mesajlasilanKisi)
            startActivity(intent)
        }
    }

    private fun SohbetListesiDinle() {
        val gelenId = mAuth.uid
        val refrence = database.getReference("/lates-messages/$gelenId").orderByChild("tarih")
        refrence.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val sohbetMesaji = snapshot.getValue(SohbetMesaji::class.java)
                sohbetMesaji?.let {
                    adapter.add(
                        SohbetListesiSatirlari(it)
                    )
                    sohbetListesiHashMap[snapshot.key!!] = sohbetMesaji
                    mesajlariYenile()
                }

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val sohbetMesaji = snapshot.getValue(SohbetMesaji::class.java)
                sohbetMesaji?.let {
                    adapter.add(
                            SohbetListesiSatirlari(it)
                    )
                    sohbetListesiHashMap[snapshot.key!!] = sohbetMesaji
                    mesajlariYenile()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })
                recyclerViewGecmisMesajlar.scrollToPosition(adapter.itemCount)
    }
    private fun mesajlariYenile(){
        adapter.clear()
        sohbetListesiHashMap.values.forEach {
            adapter.add(
                    SohbetListesiSatirlari(it)
            )
        }
    }

    private fun girisKontrol(){
        val uid = mAuth.uid
        if (uid == null){
            val intent = Intent(this, KayitOlActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    private fun guncelKullaniciyiGetir(){
        val uid = mAuth.uid
        val refrence = database.getReference("users/$uid")
        refrence.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                guncelKullanici = snapshot.getValue(Kullanici::class.java)
                guncelKullanici?.let {
                    textViewSohbetListesi.text = it.username
                    Picasso.get().load(it.gorselUrl).into(imageViewSohbetListesi)
                }


            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater =menuInflater
        menuInflater.inflate(R.menu.nav_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.yeni_mesaj->{
                val intent = Intent(this, YeniMesajActivity::class.java)
                startActivity(intent)
            }
            R.id.cikis ->{
                mAuth.signOut()
                val intent = Intent(this, KayitOlActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}

