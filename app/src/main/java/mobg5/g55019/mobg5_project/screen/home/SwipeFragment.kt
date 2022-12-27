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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSwipeBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enregistrez le BroadcastReceiver pour recevoir les événements de changement de connectivité

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(monBroadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Désenregistrez le BroadcastReceiver lorsque votre fragment est détruit
        context?.unregisterReceiver(monBroadcastReceiver)
    }


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

    private fun connexionInternetOn(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    @SuppressLint("SetTextI18n")
    private fun displayNoInternet(){
        binding.beerName.text = "Pas de connexion internet"
        binding.shortDesc.text = "Veuillez vous connecter à internet pour pouvoir utiliser l'application"
    }

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
                viewModel.dislike()
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
                viewModel.like(auth)
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

    //FAUT AJOUTER LE BeerName et pas le nom du document
    private fun setUpLikeButton(){
        binding.likeBtn.setOnClickListener {
            viewModel.like(auth)
        }
    }

    private fun setUpDislikeButton(){
        binding.dislikeBtn.setOnClickListener {
            viewModel.dislike()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun noMoreBeer(){
        binding.beerName.text = "No more beer"
        binding.shortDesc.text = "No more beer"
        binding.beerImage.setImageResource(R.drawable.empty_view)
    }

    private fun setUpSmallConstraintLayout(){
        if(viewModel.getBeersSize() > 0){
            val beer = viewModel.getBeer()
            binding.beerName.text = beer.name
            binding.shortDesc.text = beer.shortDescription
            Glide.with(this).load(beer.imageUrl).into(binding.beerImage)
        }
    }
}

/**
fun insertChimayRouge() {
Toast.makeText(context, "Chimay Rouge ajoutée à vos favoris", Toast.LENGTH_SHORT).show()

val db = FirebaseFirestore.getInstance()

val data = hashMapOf(
"AlcoholMax" to 8.5,
"AlcoholMin" to 8.5,
"BeerName" to "Orval Trappist Ale",
"Brewery" to "Abbaye Notre-Dame d'Orval",
"Color" to "Blonde",
"Country" to "Belgique",
"LongDesc" to "Orval Trappist Ale est une bière blonde belge produite par l'Abbaye Notre-Dame d'Orval. Elle est brassée avec des maltes et des houblons de qualité supérieure, et est refermentée en bouteille avec une levure sauvage. Avec une teneur en alcool de 8,5%, Orval Trappist Ale est une bière complexe et aromatique, avec des notes de fruits, de levure et de houblon. Elle est également connue pour son goût légèrement amer, grâce à sa recette originale qui inclut du houblon noble et du houblon américain. Orval Trappist Ale est une bière de choix pour les amateurs de bières trappistes et de bières de fermentation haute.",
"ShortDesc" to "Orval Trappist Ale est une bière blonde belge à 8,5% d'alcool.",
"Type" to "Bière belge de type Trappiste",
"ImageUrl" to "https://firebasestorage.googleapis.com/v0/b/mobg5-onlybeer.appspot.com/o/Orval-Trappist-Beer.jpg?alt=media&token=0e0560b2-50aa-4abf-8b83-8998179156f1"
)

// Insérez les données dans la collection "beers" de votre base de données
db.collection("Beer").document("Orval Trappist Ale")
.set(data)
.addOnSuccessListener { Log.d(TAG, "Document Delirium Tremens successfully written!") }
.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

Toast.makeText(context, "Delirium Tremens ajoutée à vos favoris", Toast.LENGTH_SHORT).show()
}
 */