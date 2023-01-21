package com.eneskoylu.bitirme2.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eneskoylu.bitirme2.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()



    }

    fun upload(view: View){

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("image").child(imageName)

        if (selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                val uploadPictureReference = storage.reference.child("image").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()


                    val advertMap = hashMapOf<String, Any>()
                    advertMap.put("downloadUrl",downloadUrl)
                    advertMap.put("userEmail",auth.currentUser!!.email!!)
                    advertMap.put("comment",binding.commentText.text.toString())
                    advertMap.put("city",binding.cityText.text.toString())
                    advertMap.put("district",binding.districtText.text.toString())
                    advertMap.put("address",binding.addressText.text.toString())
                    advertMap.put("transmission",binding.transmissionText.text.toString())

                    firestore.collection("advert").add(advertMap).addOnSuccessListener {

                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }



                }

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    fun selectImage(view: View){

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission Needed For Gallery ", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }

    }

        private fun registerLauncher(){

            activityResultLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    this@UploadActivity.contentResolver,
                                    selectedPicture!!
                                )
                                 selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                 selectedBitmap = Media.getBitmap(
                                    this@UploadActivity.contentResolver,
                                    selectedPicture
                                )
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

          /*  activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),){ result ->
                  if (result.resultCode == RESULT_OK){
                      val intentFromResult = result.data
                     if (intentFromResult != null){
                         selectedPicture = intentFromResult.data
                         selectedPicture?.let {
                             binding.imageView.setImageURI(it)
                         }
                     }
                  }
            }*/

            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission( )){ result ->
                if (result){
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }else{
                    Toast.makeText(this@UploadActivity,"Permission Needed",Toast.LENGTH_LONG).show()
                }

            }


        }

    /*fun address(view: View) {
       val intent = Intent(this@UploadActivity, LocationActivity::class.java)
       startActivity(intent)

   }*/

}