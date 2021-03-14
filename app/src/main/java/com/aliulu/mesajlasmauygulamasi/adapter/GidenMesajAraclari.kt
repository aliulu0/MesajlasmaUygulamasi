package com.aliulu.mesajlasmauygulamasi.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.aliulu.mesajlasmauygulamasi.models.SohbetMesaji
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_mesaj.view.*
import kotlinx.android.synthetic.main.giden_mesaj_row.view.*

class GidenMesajAraclari(val mesaj:String,val user: Kullanici?) : Item<GroupieViewHolder>() {
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    override fun getLayout(): Int {
        return R.layout.giden_mesaj_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textViewGidenMesaj.text = mesaj
    }

}