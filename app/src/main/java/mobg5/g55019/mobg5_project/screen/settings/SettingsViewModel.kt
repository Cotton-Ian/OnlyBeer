package mobg5.g55019.mobg5_project.screen.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mobg5.g55019.mobg5_project.R
import java.io.ByteArrayOutputStream

class SettingsViewModel : ViewModel()  {
    private val db = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    var profilName = MutableLiveData<String>()
    var descriptionNew = MutableLiveData<String>()
    val profilImageUrl = MutableLiveData<String>()
    private val REQUEST_CAMERA_PERMISSION = 3
    private val PERMISSION_REQUEST_STORAGE = 1
    val usernameTV = MutableLiveData<String>()
    val descTV = MutableLiveData<String>()

    val profileBannerUrl = MutableLiveData<String>()



    fun modifyButton(context: Context, profilTV : String){
        val customView = LayoutInflater.from(context).inflate(R.layout.alert, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(customView)
        val cancelButton = customView.findViewById<Button>(R.id.button_cancel)
        val okButton  = customView.findViewById<Button>(R.id.button_ok)
        val editText = customView.findViewById<EditText>(R.id.edit_text)
        editText.setText(profilTV)
        val dialog = builder.create()
        dialog.show()

        okButton.setOnClickListener{
            val username = customView.findViewById<EditText>(R.id.edit_text).text.toString()
            if(username != ""){
                val newText = username.trim()
                if (newText.isNotBlank()) {
                    profilName.value = newText
                    FirebaseAuth.getInstance().currentUser?.let { it1 ->
                        db.collection("User").document(
                            it1.uid).update("username", newText)
                    }
                }
                dialog.dismiss()
            }
        }

        cancelButton.setOnClickListener{
            dialog.cancel()
        }
    }

    fun modifyDesc(context: Context, desc : String){
        val customView = LayoutInflater.from(context).inflate(R.layout.alert_for_description, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(customView)
        val cancelButton = customView.findViewById<Button>(R.id.button_cancel)
        val okButton  = customView.findViewById<Button>(R.id.button_ok)
        val editText = customView.findViewById<EditText>(R.id.edit_text)
        editText.setText(desc)
        val dialog = builder.create()
        dialog.show()

        okButton.setOnClickListener{
            val description = customView.findViewById<EditText>(R.id.edit_text).text.toString()
            if(description != ""){
                val newText = description.trim()
                if (newText.isNotBlank()) {
                    descriptionNew.value = newText
                    FirebaseAuth.getInstance().currentUser?.let { it1 ->
                        db.collection("User").document(
                            it1.uid).update("description", newText)
                    }
                }
                dialog.dismiss()
            }
        }

        cancelButton.setOnClickListener{
            dialog.cancel()
        }
    }

    fun dlAndSetImage(){
        val query = db.collection("/User").document(FirebaseAuth.getInstance().uid.toString())
        query.get()
            .addOnSuccessListener { result ->
                val profilImageUrl = result.data?.get("profilImageUrl") as String
                val profileBannerUrl = result.data?.get("profileBannerUrl") as String
                if(profilImageUrl != ""){
                    this.profilImageUrl.value = profilImageUrl
                }
                if(profileBannerUrl != ""){
                    this.profileBannerUrl.value = profileBannerUrl
                }
            }
            .addOnFailureListener { exception ->
                Log.d("testQuery", "Error getting documents.", exception)
            }
    }

    fun checkPermission(context: Context, activity: Activity){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Demandez la permission à l'utilisateur ici
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    fun checkUsername(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.get().addOnSuccessListener { result ->
            val username = result.get("username").toString()
            if (username == "null") {
                usernameTV.value = "error"
            } else {
                usernameTV.value = result.get("username").toString()
            }
        }
    }

    fun checkDescription(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.get().addOnSuccessListener { result ->
            val desc = result.get("description").toString()
            if (desc == "null") {
                descTV.value = "error"
            } else {
                descTV.value = result.get("description").toString()
            }
        }
    }

    fun updateBanner(url: String){
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.update("profileBannerUrl", url)
            .addOnSuccessListener {
                Log.d("SettingsFragment", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("SettingsFragment", "Error updating document", e)
            }
    }

    fun updateProfil(url: String){
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.update("profilImageUrl", url)
            .addOnSuccessListener {
                Log.d("SettingsFragment", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("SettingsFragment", "Error updating document", e)
            }
    }

    fun pushImageProfilOnFirebase(selectedImage : Bitmap){
        val storageRef = storage.reference
        val uid = FirebaseAuth.getInstance().uid.toString()
        val imageRef: StorageReference = storageRef.child("profilPicture/$uid.jpg")
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("SettingsFragment", "Erreur d'upload de la bannière", it)
        }.addOnCompleteListener{ taskSnapshot ->
            if (taskSnapshot.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Got the download URL for 'images/selectedImage.jpg'
                    val downloadUrl = uri.toString()
                    Log.d("SettingsFragment", "downloadUrl: $downloadUrl")
                    updateProfil(downloadUrl)
                }
            }
        }
    }

    fun pushImageBannerOnFirebase(selectedImage : Bitmap){
        val storageRef = storage.reference
        val uid = FirebaseAuth.getInstance().uid.toString()
        val imageRef: StorageReference = storageRef.child("bannerPicture/$uid.jpg")
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e("SettingsFragment", "Erreur d'upload de l'image", it)
        }.addOnCompleteListener{ taskSnapshot ->
            if (taskSnapshot.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Got the download URL for 'images/selectedImage.jpg'
                    val downloadUrl = uri.toString()
                    Log.d("SettingsFragment", "downloadUrl: $downloadUrl")
                    updateBanner(downloadUrl)
                }
            }
        }
    }

}