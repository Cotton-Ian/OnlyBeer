package mobg5.g55019.mobg5_project.screen.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSettingsBinding
import java.io.IOException

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMG = 2
    private val PERMISSION_REQUEST_STORAGE = 1
    private val REQUEST_CAMERA_PERMISSION = 3

    /**
     * TODO : l'image view doit être un rond
     * TODO : l'image doit être adaptté à l'image view
     * TODO : l'image doit être enregistré dans la base de donnée
     * TODO : l'image doit être save en local pour pouvoir être affiché même si l'utilisateur n'est pas connecté
     */


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

        setUpImageBtn()

        return binding.root
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
                    }
                }
            }
            builder.show()
        }
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
                binding.imageButton.setImageBitmap(selectedImage)
                //binding.imageButton.background = BitmapDrawable(resources, selectedImage)
                //binding.imageButton.setImageBitmap(MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath))
                // Récupérez et traitez l'image sélectionnée ici
            } catch (e: IOException) {
                Log.e("SettingsFragment", "Erreur de chargement de l'image", e)
                e.printStackTrace()
            }
        }
        else{
            if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imageButton.setImageBitmap(imageBitmap)
            }
            else{
            }
        }
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


        /**
         * firestore.collection("users").document(user.uid)
        .collection("trainings").document(docId).collection("favorites")
         */
    }

}