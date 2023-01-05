package mobg5.g55019.mobg5_project.screen.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSwipeBinding

/**
 * A Fragment class that displays a beer and allows the user to like or dislike it by swiping left or right.
 * It also has a BroadcastReceiver that listens for connectivity changes and updates the UI accordingly.
 */
class SwipeFragment : Fragment() {

    private lateinit var binding: FragmentSwipeBinding
    private lateinit var viewModel: SwipeViewModel
    private val auth = FirebaseAuth.getInstance()
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    private val monBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    if(viewModel.getBeersSize() == 0){
                        Log.d("testDebug", "Connexion ON, je pull les bières")
                        viewModel.getDataFromDatabaseUser(auth)
                    }
                    setUpLikeButton()
                    setUpDislikeButton()
                    setUpSwipe()
                    setUpSmallConstraintLayout()
                } else {
                    displayNoInternet()
                }
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

    /**
     * Inflates the fragment layout and initializes the binding and view model, as well as sets up
     * the behavior for observing changes in the view model and handling connectivity changes.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          if non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return the View for the fragment's UI, or null
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_swipe, container, false
        )

        viewModel = ViewModelProvider(this)[SwipeViewModel::class.java]

        viewModel.noMoreBeer.observe(viewLifecycleOwner) { noMoreBeer ->
            if (noMoreBeer) {
                noMoreBeer()
            }
        }

        viewModel.setUpSmallConstraintLayout.observe(viewLifecycleOwner) { setUpSmallConstraintLayout ->
            if (setUpSmallConstraintLayout) {
                setUpSmallConstraintLayout()
            }
        }

        setUpColor()
        if(connexionInternetOn()){
            setUpLikeButton()
            setUpDislikeButton()
            setUpSwipe()
        }
        else{
            displayNoInternet()
        }


        return binding.root
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

    /**
     * Displays a message indicating that the device is not connected to the internet.
     */
    @SuppressLint("SetTextI18n")
    private fun displayNoInternet(){
        binding.beerName.text = "Pas de connexion internet"
        binding.shortDesc.text = "Veuillez vous connecter à internet pour pouvoir utiliser l'application"
        Toast.makeText(context, "Pas de connexion internet", Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets up the swipe gesture on the beer card view. When the card is swiped left, the dislike method
     * in the view model is called. When the card is swiped right, the like method in the view model is called.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSwipe(){
        val translateAnimationLeft = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        translateAnimationLeft.duration = 300
        translateAnimationLeft.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Cette méthode est appelée lorsque l'animation commence
            }

            override fun onAnimationEnd(animation: Animation) {
                // Cette méthode est appelée lorsque l'animation se termine
                if(connexionInternetOn()){
                    viewModel.dislike()
                }
                else{
                    displayNoInternet()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Cette méthode est appelée chaque fois que l'animation est répétée
            }
        })

        val translateAnimationRight = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        translateAnimationRight.duration = 300
        translateAnimationRight.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Cette méthode est appelée lorsque l'animation commence
            }

            override fun onAnimationEnd(animation: Animation) {
                // Cette méthode est appelée lorsque l'animation se termine
                if(connexionInternetOn()){
                    viewModel.like(auth)
                }
                else{
                    displayNoInternet()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Cette méthode est appelée chaque fois que l'animation est répétée
            }
        })


        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    val diffX = e2.x - e1.x
                    if (kotlin.math.abs(diffX) > SWIPE_THRESHOLD && kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            binding.frameLayout.startAnimation(translateAnimationRight)
                        } else {
                            binding.frameLayout.startAnimation(translateAnimationLeft)
                        }
                        return true
                    }
                }
                return false
            }
        })
        binding.frameLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    /**
    * Sets up the colors for the bottom navigation bar.
    * The color of the navigation bar is white, the color for the disabled state is blue,
    * the color for the unchecked state is green, and the color for the pressed state is also green.
    */
    private fun setUpColor(){
        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation)
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.white), // Couleur de la barre de nav
            ContextCompat.getColor(requireContext(), R.color.blue_200), // Couleur pour l'état désactivé
            ContextCompat.getColor(requireContext(), R.color.green_200), // Couleur pour l'état non sélectionné
            ContextCompat.getColor(requireContext(), R.color.green_500) // Couleur pour l'état pressé
        )

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // État activé
            intArrayOf(-android.R.attr.state_enabled), // État désactivé
            intArrayOf(-android.R.attr.state_checked), // État non sélectionné
            intArrayOf(android.R.attr.state_pressed) // État pressé
        )
        val colorStateList = ColorStateList(states, colors)
        bottomNav?.backgroundTintList = colorStateList
        bottomNav?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    /**
     * Sets up the "Like" button in the user interface.
     * When the button is clicked, the "like" method of the ViewModel is called with the
     * authentication object as an argument.
     */
    private fun setUpLikeButton(){
        binding.likeBtn.setOnClickListener {
            if(connexionInternetOn()){
                viewModel.like(auth)
            }
            else{
                displayNoInternet()
            }
        }
    }

    /**
     * Sets up the "Dislike" button in the user interface.
     * When the button is clicked, the "dislike" method of the ViewModel is called.
     */
    private fun setUpDislikeButton(){
        binding.dislikeBtn.setOnClickListener {
            if(connexionInternetOn()){
                viewModel.dislike()
            }
            else{
                displayNoInternet()
            }
        }
    }

    /**
     * Displays a message and an image indicating that there are no more beers available.
     * The beer name and short description are set to "No more beer" and the image is set to a
     * predefined drawable.
     */
    @SuppressLint("SetTextI18n")
    private fun noMoreBeer(){
        binding.beerName.text = "No more beer"
        binding.shortDesc.text = "No more beer"
        binding.beerImage.setImageResource(R.drawable.empty_view)
    }

    /**
     * Sets up the small constraint layout with the name, short description, and image of a beer.
     * If there are more beers available in the ViewModel, the name, short description, and image
     * of the current beer are displayed.
     */
    private fun setUpSmallConstraintLayout(){
        if(viewModel.getBeersSize() > 0){
            val beer = viewModel.getBeer()
            binding.beerName.text = beer.name
            binding.shortDesc.text = beer.shortDescription
            Glide.with(this).load(beer.imageUrl).into(binding.beerImage)
        }
    }


}