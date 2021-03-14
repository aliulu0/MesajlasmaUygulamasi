package com.aliulu.mesajlasmauygulamasi

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aliulu.mesajlasmauygulamasi.R.drawable
import com.aliulu.mesajlasmauygulamasi.R.drawable.*
import com.aliulu.mesajlasmauygulamasi.account.GirisActivity
import com.aliulu.mesajlasmauygulamasi.messages.SohbetListesiActivity
import com.aliulu.mesajlasmauygulamasi.models.Kullanici
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_kayit_ol.*
import java.util.*

class KayitOlActivity : AppCompatActivity() {

    private lateinit var storage : FirebaseStorage
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    var secilenGorsel : Uri? = null
    var secilenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ol)

        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        zatenHesapVar.setOnClickListener {
            val intent = Intent(this,
                GirisActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        register.setOnClickListener {
            //kayıt
            register()
        }
        selectImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin alınacak
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }
            else{
                //izin verilmiş galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galeriIntent.type = "image/*"
                startActivityForResult(galeriIntent,2)
            }

        }
        register_dogum_tarihi.setOnClickListener {
            val calendar = Calendar.getInstance()
            val yil = calendar.get(Calendar.YEAR)
            val ay = calendar.get(Calendar.MONTH)
            val gun = calendar.get(Calendar.DAY_OF_MONTH)
            val datepicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                register_dogum_tarihi.setText("$dayOfMonth/${month + 1}/$year")
            },gun,ay,yil)
            datepicker.setTitle("Tarih Seçiniz")
            datepicker.setButton(DialogInterface.BUTTON_POSITIVE,"Ayarla",datepicker)
            datepicker.setButton(DialogInterface.BUTTON_NEGATIVE,"İptal",datepicker)
            datepicker.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izin verilince
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galeriIntent.type = "image/*"
                startActivityForResult(galeriIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data
            if (secilenGorsel != null){
                if (Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    selectImage.setImageBitmap(secilenBitmap)
                }
                else{
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    selectImage.setImageBitmap(secilenBitmap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun register(){
        val email = register_email.text.toString()
        val password = register_password.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Lütfen bilgileri kontrol ediniz", Toast.LENGTH_LONG).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val intent = Intent(this,SohbetListesiActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    uploadImageFireStore()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
    fun uploadImageFireStore(){
        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"
        val refrence = storage.reference
        val gorselRefrence = refrence.child("images").child(gorselIsmi)
        if (secilenGorsel != null){
            gorselRefrence.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot ->
                //gorsel uri alma
                val yuklenenGorselRefrence = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselRefrence.downloadUrl.addOnSuccessListener { uri ->
                    saveUserToFirebaseDatabase(uri)
                }
            }
                .addOnFailureListener {exception ->
                    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun saveUserToFirebaseDatabase(uri: Uri){
        var cinsiyet = ""
        if (radioButtonErkek.isChecked){
            cinsiyet = "erkek"
        }
        else if (radioButtonKadin.isChecked){
            cinsiyet = "kadın"
        }
        else{
            Toast.makeText(this,"Lütfen cinsiyet seçiniz", Toast.LENGTH_LONG).show()
        }
        //veritabanı kayıt işlemleri
        val uid = mAuth.uid
        uid?.let {
            val refrence = database.getReference("/users/$uid")
            val downloadUri = uri.toString()
            val username = register_username.text.toString()
            val tarih = register_dogum_tarihi.text.toString()
            val user = Kullanici(
                uid,
                username,
                downloadUri,
                tarih,
                cinsiyet
            )

            refrence.setValue(user).addOnSuccessListener {
                val intent = Intent(this,
                    SohbetListesiActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
                .addOnFailureListener { exception ->
                    Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

}