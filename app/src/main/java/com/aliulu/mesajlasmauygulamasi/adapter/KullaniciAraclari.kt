package com.aliulu.mesajlasmauygulamasi.adapter

import android.view.View
import com.aliulu.mesajlasmauygulamasi.R
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.groupie_row_yeni_mesaj.view.*

class KullaniciAraclari(val user: Kullanici) : Item<GroupieViewHolder>(){
    val mAuth = FirebaseAuth.getInstance()
    override fun getLayout(): Int {
        return R.layout.groupie_row_yeni_mesaj
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        mAuth.currentUser?.let {
            if (it.uid == user.uid){
                viewHolder.itemView.cardView.visibility = View.GONE
            }else{
                viewHolder.itemView.username_yeni_mesaj.text = user.username
                //Görsel
                Picasso.get().load(user.gorselUrl).into(viewHolder.itemView.yeni_mesaj_image)
            }
        }


    }
}