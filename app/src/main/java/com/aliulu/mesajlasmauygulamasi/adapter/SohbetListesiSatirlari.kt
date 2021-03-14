package com.aliulu.mesajlasmauygulamasi.adapter

import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.aliulu.mesajlasmauygulamasi.models.SohbetMesaji
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.groupie_row_yeni_mesaj.view.*
import kotlinx.android.synthetic.main.sohbet_listesi_row.view.*

class SohbetListesiSatirlari(val sohbetMesaji: SohbetMesaji) : Item<GroupieViewHolder>() {
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var sohbetKisisi: Kullanici? = null
    override fun getLayout(): Int {
        return R.layout.sohbet_listesi_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val sohbetMesajiKisisi: String
        if (sohbetMesaji.gelenId == mAuth.uid) {
            sohbetMesajiKisisi = sohbetMesaji.gidenId
        } else {
            sohbetMesajiKisisi = sohbetMesaji.gelenId
        }

        val refrence = database.getReference("/users/$sohbetMesajiKisisi")
        refrence.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                sohbetKisisi = snapshot.getValue(Kullanici::class.java)
                sohbetKisisi?.let {
                    viewHolder.itemView.textViewGecmisMesajlarUsername.text =
                        sohbetKisisi!!.username
                    viewHolder.itemView.textViewGecimisMesajlarMesaj.text = sohbetMesaji.mesaj
                    Picasso.get().load(sohbetKisisi!!.gorselUrl)
                        .into(viewHolder.itemView.imageViewGecmisMesajlar)
                }
            }

        })

    }
}