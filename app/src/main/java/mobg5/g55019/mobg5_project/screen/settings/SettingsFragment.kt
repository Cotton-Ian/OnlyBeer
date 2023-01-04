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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSettingsBinding
import java.io.IOException


/**
 *
 * @author Cotton Ian | 55019
 *
 * SettingsFragment is a subclass of [Fragment] that displays the user's profile information and allows them to make changes to their profile.
 * It has the following fields:
 * @field binding: a [FragmentSettingsBinding] object that holds references to the views in the layout
 * @field viewModel: a [SettingsViewModel] object that is responsible for handling the data and communication with the repository
 * @field CAMERA_REQUEST: an integer that is used to identify the camera request in [onActivityResult]
 * @field RESULT_LOAD_IMG: an integer that is used to identify the image selection request in [onActivityResult]
 * @field imageProfilTV: a boolean that indicates whether the user has selected to modify their profile image
 * @field imageBannerTV: a boolean that indicates whether the user has selected to modify their banner image
 * @field hasBeenInitialize: a boolean that indicates whether the fragment has been initialized
 * @field monBroadcastReceiver: a [BroadcastReceiver] that listens for connectivity changes and initializes the fragment when the device is connected to the internet
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

    /**
     * Initializes the view for the SettingsFragment, including setting up the ImageButton, checking for
     * the user's current username and description, setting up the modify button and modify description button,
     * setting up the disconnect button, and setting up the observers for the viewModel. If there is no internet
     * connection, a Toast message will be displayed.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The
     * fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as
     * given here.
     *
     * @return Return the View for the fragment's UI, or null.
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

    /**
     * Set up observers to update the UI with data from the view model.
     */
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
     * Unregisters the BroadcastReceiver when the fragment is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()

        // Désenregistrez le BroadcastReceiver lorsque votre fragment est détruit
        context?.unregisterReceiver(monBroadcastReceiver)
    }

    /**
     * Initializes the deconnection button which signs out the user from the Firebase and Google accounts.
     * Displays a toast message and navigates to the login fragment when clicked.
     */
    private fun setUpDeconnexion(){
        binding.deconnexionBtn.setOnClickListener {
            Toast.makeText(context, "Deconnexion", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            // Déconnectez-vous également du compte Google
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            googleSignInClient.signOut()
            view?.findNavController()?.navigate(R.id.action_settingsFragment_to_loginFragment)

        }
    }

    /**
     * Initializes the modify button with a click listener.
     * When clicked, the user's profile information is modified if the device has an active internet connection.
     * If the device does not have an active internet connection, a toast message is displayed.
     */
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

    /**
    * Sets the click listener for the modify button for the user's description.
    * If there is an internet connection, the user's description will be modified in the database.
    * Otherwise, a toast message will be displayed to inform the user to connect to the internet.
     */
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

    /**
     * Sets up the image buttons by adding click listeners to select images for the profile picture or banner.
     */
    private fun setImageProfilBanner(){
        if(connexionInternetOn()){
            viewModel.dlAndSetImage()
        }
        else{
            Toast.makeText(context, "Connectez vous à internet", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sets up the image buttons by adding click listeners to select images for
     * the profile picture or banner.
     */
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

    /**
     * Displays an alert dialog with options to take a picture or choose one from the gallery.
     * If the user chooses to take a picture, it opens the device's camera app. If the user chooses
     * to choose one from the gallery, it opens the device's gallery app. If the user cancels, the
     * dialog is dismissed. If the app does not have the necessary permissions to access the camera or
     * gallery, it prompts the user to grant those permissions.
     */
    private fun selectImage(){
        val options =
            arrayOf<CharSequence>("Prendre une photo", "Choisir depuis la galerie", "Annuler")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ajouter une image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Prendre une photo" -> {
                    if(viewModel.hasPermissionCamera(requireContext())){
                        takePicture()
                    }
                    else{
                        Toast.makeText(context, "L'application à besoin d'accéder à l'appareil photo", Toast.LENGTH_SHORT).show()
                        viewModel.checkPermissionCamera(requireContext(), requireActivity())
                    }
                }
                options[item] == "Choisir depuis la galerie" -> {
                    // Ouvrez la galerie
                    if(viewModel.hasPermissionGallery(requireContext())){
                        openGallery()
                    }
                    else{
                        Toast.makeText(context, "L'application à besoin d'accéder à la gallerie", Toast.LENGTH_SHORT).show()
                        viewModel.checkPermissionGallery(requireContext(), requireActivity())
                    }
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

    /**
     * Launches the device's camera app to take a picture.
     * The resulting image will be passed back to the app via the
     * onActivityResult() method.
     */
    private fun takePicture(){
        // Ouvrez l'application de prise de photos
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
    }

    /**
     * Opens the device's gallery app to select an image.
     * The selected image will be passed back to the app via the
     * onActivityResult() method.
     */
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMG)
    }

    /**
     *  Handles the result of the image being selected for the profile or banner image.
     *  If the image is selected from the gallery or taken from the camera, the selected image is displayed
     *  and pushed to Firebase Storage.
     *
     *  @param requestCode code representing the request for an image
     *  @param resultCode result of the request for an image
     *  @param data data containing the image selected or taken from the camera
     */
    @Override
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

    /**
     *Loads the given [imageBitmap] into the profile image view using Glide.
     * The image is transformed into a circle before being displayed.
     *@param imageBitmap the image to be displayed in the profile image view
     */
    private fun glideImageProfil(imageBitmap: Bitmap){
        Glide.with(this)
            .load(imageBitmap)
            .transform(CircleCrop())
            .into(binding.imageButton)
    }

    /**
     *Loads the given [imageBitmap] into the banner image view using Glide. The image is resized to fit the size of the banner image view and is centered.
     *@param imageBitmap the image to be displayed in the banner image view
     */
    private fun glideImageBanner(imageBitmap: Bitmap){
        Glide.with(this)
            .load(imageBitmap)
            .override(binding.imageBanner.width, binding.imageBanner.height)
            .centerCrop()
            .into(binding.imageBanner)
    }

    /**
    *Checks the current username for errors.
     */
    private fun checkUsername(){
        viewModel.checkUsername()
    }

    /**
     *Checks the current description for errors.
     */
    private fun checkDescription(){
        viewModel.checkDescription()
    }

    /**
     *Pushes the given [selectedImage] to Firebase as the profile image.
     *@param selectedImage the image to be set as the profile image
     */
    private fun pushImageProfilOnFirebase(selectedImage : Bitmap){
       viewModel.pushImageProfilOnFirebase(selectedImage)
    }

    /**
     * Pushes the given image to Firebase Storage as the banner image for the current user.
     * @param selectedImage the image to be stored as the banner image
     */
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