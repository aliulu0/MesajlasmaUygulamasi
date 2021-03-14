package com.aliulu.mesajlasmauygulamasi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kullanici(val uid:String, val username:String, val gorselUrl:String,val dogumTarihi:String, val cinsiyet:String) : Parcelable {
    constructor():this("","","","","")
}