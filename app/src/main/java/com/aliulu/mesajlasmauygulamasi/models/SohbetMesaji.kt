package com.aliulu.mesajlasmauygulamasi.models

class SohbetMesaji(val id:String,
val mesaj:String,
val gelenId:String,
val gidenId:String,
val tarih:Long) {
    constructor() : this("","","","",-1)}