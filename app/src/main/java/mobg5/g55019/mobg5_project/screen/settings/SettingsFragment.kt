package mobg5.g55019.mobg5_project.screen.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSettingsBinding
import java.io.IOException

/**
 * TODO : repository
 */

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMG = 2
    private var imageProfilTV: Boolean = false
    private var imageBannerTV: Boolean = false
    private var hasBeenInitialize : Boolean = false
    private val monBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    if(!hasBeenInitialize){
                        checkPermission()
                        checkUsername()
                        checkDescription()
                        setUpImageBtn()
                        setImageProfilBanner()
                        setUpModifyBtn()
                        setModifyForDescription()
                        setUpDeconnexion()
                        hasBeenInitialize = true
                    }
                }
            }
        }
    }

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

        if(connexionInternetOn()){
            checkUsername()
            checkDescription()
            setUpImageBtn()
            setImageProfilBanner()
            setUpModifyBtn()
            setModifyForDescription()
            setUpDeconnexion()
            hasBeenInitialize = true
        }

        setUpObserver()

        return binding.root
    }

    private fun setUpObserver(){
        viewModel.profilName.observe(viewLifecycleOwner) { profilName ->
            if (profilName != null) {
                binding.profilTV.text = profilName
            }
        }

        viewModel.descriptionNew.observe(viewLifecycleOwner) { descriptionNew ->
            if (descriptionNew != null) {
                binding.description.text = descriptionNew
            }
        }

        viewModel.profilImageUrl.observe(viewLifecycleOwner) { profilImageUrl ->
            if (profilImageUrl != null) {
                Glide.with(this)
                    .load(profilImageUrl)
                    .transform(CircleCrop())
                    .into(binding.imageButton)
            }
        }

        viewModel.profileBannerUrl.observe(viewLifecycleOwner) { profileBannerUrl ->
            if (profileBannerUrl != null) {
                Glide.with(this)
                    .load(profileBannerUrl)
                    .override(binding.imageBanner.width, binding.imageBanner.height)
                    .centerCrop()
                    .into(binding.imageBanner)
            }
        }

        viewModel.usernameTV.observe(viewLifecycleOwner) { usernameTV ->
            if (usernameTV != null) {
                binding.profilTV.text = usernameTV
            }
        }

        viewModel.descTV.observe(viewLifecycleOwner) { descTV ->
            if (descTV != null) {
                binding.description.text = descTV
            }
        }
    }

    /**
     * Registers the monBroadcastReceiver BroadcastReceiver to listen for connectivity changes
     * when the fragment is created.
     *
     * @param savedInstanceState a Bundle containing the state of the fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enregistrez le BroadcastReceiver pour recevoir les événements de changement de connectivité

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(monBroadcastReceiver, filter)
    }


    /**
     * Unregisters the monBroadcastReceiver BroadcastReceiver when the fragment is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()

        // Désenregistrez le BroadcastReceiver lorsque votre fragment est détruit
        context?.unregisterReceiver(monBroadcastReceiver)
    }

    private fun setUpDeconnexion(){
        binding.deconnexionBtn.setOnClickListener {
            Toast.makeText(context, "Deconnexion", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            view?.findNavController()?.navigate(R.id.action_settingsFragment_to_loginFragment)

        }
    }

    private fun setUpModifyBtn(){
        binding.modifyButton.setOnClickListener{

            if(connexionInternetOn()){
                viewModel.modifyButton(requireContext(), binding.profilTV.text.toString())
            }
            else{
                Toast.makeText(context, "Connectez vous à internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setModifyForDescription(){
        binding.buttonDescriptionModify.setOnClickListener{
            if(connexionInternetOn()){
                viewModel.modifyDesc(requireContext(), binding.description.text.toString())
            }
            else{
                Toast.makeText(context, "Connectez vous à internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setImageProfilBanner(){
        if(connexionInternetOn()){
            viewModel.dlAndSetImage()
        }
        else{
            Toast.makeText(context, "Connectez vous à internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(){
        viewModel.checkPermission(requireContext(), requireActivity())
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
        viewModel.checkUsername()
    }

    private fun checkDescription(){
        viewModel.checkDescription()
    }

    private fun pushImageProfilOnFirebase(selectedImage : Bitmap){
       viewModel.pushImageProfilOnFirebase(selectedImage)
    }

    private fun pushImageBannerOnFirebase(selectedImage : Bitmap){
        viewModel.pushImageBannerOnFirebase(selectedImage)
    }

    /**
     * Checks if the device is connected to the internet.
     *
     * @return true if the device is connected to the internet, false otherwise
     */
    private fun connexionInternetOn(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

}