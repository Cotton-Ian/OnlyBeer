package mobg5.g55019.mobg5_project.screen.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSwipeBinding
import mobg5.g55019.mobg5_project.model.Beer
class SwipeFragment : Fragment() {

    private lateinit var binding: FragmentSwipeBinding
    private val db = FirebaseFirestore.getInstance()
    private var beers: MutableList<Beer> = mutableListOf()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_swipe, container, false
        )

        setUpColor()
        getDataFromDatabaseUser()
        setUpLikeButton()
        setUpDislikeButton()


        return binding.root
    }

    private fun getDataFromDatabaseUser(){
        /*
        val docRef = db.collection("/Beer")
        // Créez une requête qui filtre les documents où le champ "favorite" est égal à false
        val query = docRef.whereEqualTo("Favourite", false)
        query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    addBeerInList(document.data)
                    //Log.d("testQuery", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("testQuery", "Error getting documents.", exception)
            }
            .addOnCompleteListener {
                //Nothing
            }*/

        val query = db.collection("/User").document(auth.uid.toString())
        query.get()
            .addOnSuccessListener { result ->
                val beerList = result.data?.get("Beers") as MutableList<String>
                getDataFromDatabaseBeer(beerList)
            }
            .addOnFailureListener { exception ->
                Log.d("testQuery", "Error getting documents.", exception)
            }
    }

    private fun getDataFromDatabaseBeer(beerNameList: MutableList<String>){
        //java.lang.IllegalArgumentException: Invalid Query. A non-empty array is required for 'not_in' filters.
        beerNameList.add(" ")

        val query = db.collection("/Beer").whereNotIn("BeerName", beerNameList)
        query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    addBeerInList(document.data)
                    //Log.d("testQuery", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("testQuery", "Error getting documents.", exception)
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
            if(beers.size > 0){
                val userRef = db.collection("User").document(auth.uid.toString())
                userRef.update("Beers", FieldValue.arrayUnion(beers[0].name))
                    .addOnSuccessListener { Log.d(TAG, "Beer added to beers array") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding beer to beers array", e) }
                beers.removeAt(0)
                if(beers.size == 0){
                    noMoreBeer()
                }
                else{
                    setUpSmallConstraintLayout()
                }
            }
            else{
                noMoreBeer()
            }

        }
    }

    private fun setUpDislikeButton(){
        binding.dislikeBtn.setOnClickListener {
            if(beers.size > 0){
                beers.removeAt(0)
                if(beers.size == 0){
                    noMoreBeer()

                }
                else{
                    setUpSmallConstraintLayout()
                }
            }
            else{
                noMoreBeer()
            }
        }
    }

    private fun noMoreBeer(){
        binding.beerName.text = "No more beer"
        binding.shortDesc.text = "No more beer"
        binding.beerImage.setImageResource(R.drawable.empty_view)
    }


    private fun addBeerInList(beer : Map<String, Any>){
        val name = beer["BeerName"].toString()
        val brewery = beer["Brewery"].toString()
        val country = beer["Country"].toString()
        val shortDescription = beer["ShortDesc"].toString()
        val longDescription = beer["LongDesc"].toString()
        val alcoholMin = beer["AlcoholMin"].toString().toDouble()
        val alcoholMax = beer["AlcoholMax"].toString().toDouble()
        val color = beer["Color"].toString()
        val imageUrl = beer["ImageUrl"].toString()
        val type = beer["Type"].toString()

        beers.add(Beer(name, brewery, country, shortDescription, longDescription, alcoholMin,
            alcoholMax, color, imageUrl, type))

        if(beers.size == 1){
            setUpSmallConstraintLayout()
        }

    }

    private fun setUpSmallConstraintLayout(){
        binding.shortDesc.text = beers[0].shortDescription
        binding.beerName.text = beers[0].name

        Glide.with(this).load(beers[0].imageUrl).into(binding.beerImage)
    }


    fun insertChimayRouge() {
        Toast.makeText(context, "Chimay Rouge ajoutée à vos favoris", Toast.LENGTH_SHORT).show()

        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "AlcoholMax" to 8.5,
            "AlcoholMin" to 8.5,
            "BeerName" to "Delirium Tremens",
            "Brewery" to "Brasserie Huyghe",
            "Color" to "Blonde",
            "Country" to "Belgique",
            "LongDesc" to "Delirium Tremens est une bière blonde belge produite par la Brasserie Huyghe. Elle est brassée avec des maltes de qualité supérieure et des houblons saisonniers, et est refermentée en bouteille. Avec une teneur en alcool de 8,5%, Delirium Tremens est une bière complexe et aromatique, avec des notes de fruits et de coriandre. Elle a remporté de nombreux prix dans les concours de bières internationaux.",
            "ShortDesc" to "Delirium Tremens est une bière blonde belge à 8,5% d'alcool.",
            "Type" to "Bière belge de type Ale"
        )

        // Insérez les données dans la collection "beers" de votre base de données
        db.collection("Beer").document("delirium_tremens")
            .set(data)
            .addOnSuccessListener { Log.d(TAG, "Document Delirium Tremens successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        Toast.makeText(context, "Delirium Tremens ajoutée à vos favoris", Toast.LENGTH_SHORT).show()
    }



}