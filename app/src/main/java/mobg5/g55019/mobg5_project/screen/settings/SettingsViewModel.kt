package mobg5.g55019.mobg5_project.screen.settings

import android.Manifest
import android.annotation.SuppressLint
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

/**
 *
 * @author Cotton Ian | 55019
 *
 * SettingsViewModel is a class that extends ViewModel and is responsible for handling the data
 * and logic related to the settings screen of an Android app.
 *
 * It contains a number of MutableLiveData objects that can be observed by the UI and updated with new values.
 * It also contains a number of functions that are called when certain actions are taken on the UI,
 * such as modifying the username or description, or uploading an image to Firebase Storage.
 * It also has functions for checking and requesting permissions for the camera and external storage
 */
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
    val askPermissionGallery = MutableLiveData<Boolean>()
    val askPermissionCamera = MutableLiveData<Boolean>()




    /**
     * Shows an alert dialog that allows the user to modify their username.
     *
     * @param context the context in which the dialog is being displayed
     * @param profilTV the current value of the username
     */
    @SuppressLint("CutPasteId")
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

    /**
     * Shows an alert dialog to modify the user's description.
     *
     * @param context The context from which this method is called.
     * @param desc The current description of the user.
     */
    @SuppressLint("CutPasteId")
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

    /**
     * Retrieves the image urls for the user's profile image and banner image from the Firebase database.
     * If the user has not set a profile image or banner image, the default image url is used.
     * The function then sets the values of the [profilImageUrl] and [profileBannerUrl] LiveData objects to the retrieved image urls.
     */
    fun dlAndSetImage(){
        val query = db.collection("/User").document(FirebaseAuth.getInstance().uid.toString())
        query.get()
            .addOnSuccessListener { result ->
                if(result != null){
                    val profilImageUrl = result.data?.get("profilImageUrl") as String
                    val profileBannerUrl = result.data?.get("profileBannerUrl") as String
                    if(profilImageUrl != ""){
                        this.profilImageUrl.value = profilImageUrl
                    }
                    if(profileBannerUrl != ""){
                        this.profileBannerUrl.value = profileBannerUrl
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("testQuery", "Error getting documents.", exception)
            }
    }

    /**
     * Check if the user has granted the READ_EXTERNAL_STORAGE permission.
     * If the permission has not been granted, the method will request the permission.
     * @param context The context of the application.
     * @param activity The current activity.
     */
    fun checkPermissionGallery(context: Context, activity: Activity){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }
    }

    /**
     * Check if the user has granted the CAMERA permission.
     * If the permission has not been granted, the method will request the permission.
     * @param context The context of the application.
     * @param activity The current activity.
     */
    fun checkPermissionCamera(context: Context, activity: Activity){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    /**
     * Check if the app has permission to access the external storage.
     * @param context The context of the app.
     * @return true if the app has permission to access the external storage, false otherwise.
     */
    fun hasPermissionGallery(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the app has permission to access the camera.
     * @param context The context of the app.
     * @return true if the app has permission to access the camera, false otherwise.
     */
    fun hasPermissionCamera(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }


    /**
     * checkUsername() retrieves the username of the currently logged in user from the Firebase Firestore database.
     * If the username is "null", the "usernameTV" MutableLiveData object is set to "error".
     * If the username is not "null", the "usernameTV" MutableLiveData object is set to the retrieved username.
     *
     * @return void
     */
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

    /**
     * checkDescription() retrieves the description of the currently logged in user from the Firebase Firestore database.
     * If the description is an empty string, the "descTV" MutableLiveData object is set to "Votre description...".
     * If the description is not an empty string, the "descTV" MutableLiveData object is set to the retrieved description.
     *
     * @return void
     */
    fun checkDescription(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.get().addOnSuccessListener { result ->
            val desc = result.get("description").toString()
            if (desc == "") {
                descTV.value = "Votre description..."
            } else {
                descTV.value = result.get("description").toString()
            }
        }
    }

    /**
     * Updates the banner profile picture URL in the Firestore database.
     *
     * @param url The new banner profile picture URL.
     */
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

    /**
     * Updates the profile picture URL in the Firestore database.
     *
     * @param url The new profile picture URL.
     */
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

    /**
     *
     * This method pushes the selected image to Firebase Storage, specifically in the "profilPicture" folder, with the name "uid.jpg".
     * It then gets the download url for the image and calls the updateProfil method.
     * @param selectedImage The selected image to be pushed to Firebase Storage.
     * @see updateProfil
     */
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

    /** *
     * Pushes the given [selectedImage] to the user's banner image on Firebase Storage.
     * If the image upload is successful, the URL for the image is retrieved and used to update the user's banner image on Firebase Database.
     *
     * @param selectedImage the image to be uploaded as the user's banner image
     */
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