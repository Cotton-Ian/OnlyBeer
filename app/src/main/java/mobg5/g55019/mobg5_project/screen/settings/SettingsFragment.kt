package mobg5.g55019.mobg5_project.screen.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSettingsBinding
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * TODO : deconnexion
 * TODO : Description de l'utilisateur
 * TODO : viewModel
 * TODO : repository
 * TODO : gestion de la connexion
 */

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMG = 2
    private val PERMISSION_REQUEST_STORAGE = 1
    private val REQUEST_CAMERA_PERMISSION = 3
    private var imageProfilTV: Boolean = false
    private var imageBannerTV: Boolean = false
    private val db = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings, container, false
        )

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        checkPermission()

        checkUsername()

        checkDescription()

        setUpImageBtn()

        setImageProfilBanner()

        setUpModifyBtn()

        setModifyForDescription()

        return binding.root
    }

    private fun setUpModifyBtn(){
        binding.modifyButton.setOnClickListener{

            val customView = LayoutInflater.from(context).inflate(R.layout.alert, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(customView)
            val cancelButton = customView.findViewById<Button>(R.id.button_cancel)
            val okButton  = customView.findViewById<Button>(R.id.button_ok)
            val dialog = builder.create()
            dialog.show()

            okButton.setOnClickListener{
                val username = customView.findViewById<EditText>(R.id.edit_text).text.toString()
                if(username != ""){
                    val newText = username.trim()
                    if (newText.isNotBlank()) {
                        binding.profilTV.text = newText
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
    }

    private fun setModifyForDescription(){
        binding.buttonDescriptionModify.setOnClickListener{
            val customView = LayoutInflater.from(context).inflate(R.layout.alert_for_description, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(customView)
            val cancelButton = customView.findViewById<Button>(R.id.button_cancel)
            val okButton  = customView.findViewById<Button>(R.id.button_ok)
            val dialog = builder.create()
            dialog.show()

            okButton.setOnClickListener{
                val description = customView.findViewById<EditText>(R.id.edit_text).text.toString()
                if(description != ""){
                    val newText = description.trim()
                    if (newText.isNotBlank()) {
                        binding.description.text = newText
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
    }

    private fun setImageProfilBanner(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("/User").document(FirebaseAuth.getInstance().uid.toString())
        query.get()
            .addOnSuccessListener { result ->
                val profilImageUrl = result.data?.get("profilImageUrl") as String
                val profileBannerUrl = result.data?.get("profileBannerUrl") as String
                if(profilImageUrl != ""){
                    Glide.with(this)
                        .load(profilImageUrl)
                        .transform(CircleCrop())
                        .into(binding.imageButton)
                }
                if(profileBannerUrl != ""){
                    Glide.with(this)
                        .load(profileBannerUrl)
                        .override(binding.imageBanner.width, binding.imageBanner.height)
                        .centerCrop()
                        .into(binding.imageBanner)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("testQuery", "Error getting documents.", exception)
            }
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Demandez la permission à l'utilisateur ici
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }


    private fun setUpImageBtn(){
        binding.imageButton.setOnClickListener {
            imageProfilTV = true
            selectImage()
        }

        binding.imageBanner.setOnClickListener {
            imageBannerTV = true
            selectImage()
        }
    }

    private fun selectImage(){
        val options =
            arrayOf<CharSequence>("Prendre une photo", "Choisir depuis la galerie", "Annuler")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ajouter une image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Prendre une photo" -> {
                    takePicture()
                }
                options[item] == "Choisir depuis la galerie" -> {
                    // Ouvrez la galerie
                    openGallery()
                }
                options[item] == "Annuler" -> {
                    dialog.dismiss()
                    imageBannerTV = false
                    imageProfilTV = false
                }
            }
        }
        builder.show()
    }

    private fun takePicture(){
        // Ouvrez l'application de prise de photos
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            val filePath = data?.data
            try {
                val imageStream = filePath?.let { context?.contentResolver?.openInputStream(it) }
                val selectedImage = BitmapFactory.decodeStream(imageStream)

                if (imageProfilTV){
                    glideImageProfil(selectedImage)
                    pushImageProfilOnFirebase(selectedImage)
                } else if (imageBannerTV){
                    glideImageBanner(selectedImage)
                    pushImageBannerOnFirebase(selectedImage)
                }

            } catch (e: IOException) {
                Log.e("SettingsFragment", "Erreur de chargement de l'image", e)
                e.printStackTrace()
            }
        }
        else{
            if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
                val imageBitmap = data?.extras?.get("data") as Bitmap
                if(imageProfilTV){
                    glideImageProfil(imageBitmap)
                    pushImageProfilOnFirebase(imageBitmap)
                } else if (imageBannerTV){
                    glideImageBanner(imageBitmap)
                    pushImageBannerOnFirebase(imageBitmap)
                }
            }
        }
        imageBannerTV = false
        imageProfilTV = false
    }

    private fun glideImageProfil(imageBitmap: Bitmap){
        Glide.with(this)
            .load(imageBitmap)
            .transform(CircleCrop())
            .into(binding.imageButton)
    }

    private fun glideImageBanner(imageBitmap: Bitmap){
        Glide.with(this)
            .load(imageBitmap)
            .override(binding.imageBanner.width, binding.imageBanner.height)
            .centerCrop()
            .into(binding.imageBanner)
    }


    private fun checkUsername(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.get().addOnSuccessListener { result ->
            val username = result.get("username").toString()
            if (username == "null") {
                binding.profilTV.text = "error"
            } else {
                binding.profilTV.text = result.get("username").toString()
            }
        }
    }

    private fun checkDescription(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("User")
            .document(FirebaseAuth.getInstance().uid.toString())

        query.get().addOnSuccessListener { result ->
            val desc = result.get("description").toString()
            if (desc == "null") {
                binding.description.text = "error"
            } else {
                binding.description.text = result.get("description").toString()
            }
        }
    }

    private fun pushImageProfilOnFirebase(selectedImage : Bitmap){
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
                    updateProfile(downloadUrl)
                }
            }
        }
    }

    private fun pushImageBannerOnFirebase(selectedImage : Bitmap){
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

    private fun updateProfile(url: String){
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

    private fun updateBanner(url: String){
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

}