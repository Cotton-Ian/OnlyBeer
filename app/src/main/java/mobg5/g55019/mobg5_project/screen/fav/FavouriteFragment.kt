package mobg5.g55019.mobg5_project.screen.fav

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentFavouriteBinding
import mobg5.g55019.mobg5_project.model.Beer

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private var beers = mutableListOf<Beer>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite,container , false)
        //getListOfBeer()
        beers.clear() //To avoid duplicate after going back from information fragment
        getDataFromDatabaseUser()
        return binding.root
    }

    private fun getListOfBeer() {
        val docRef = db.collection("/Beer")

        // Créez une requête qui filtre les documents où le champ "favorite" est égal à false
        val query = docRef.whereEqualTo("Favourite", true)
        query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    beers.add(Beer(
                        name = document["BeerName"].toString(),
                        brewery = document["Brewery"].toString(),
                        country = document["Country"].toString(),
                        shortDescription = document["ShortDesc"].toString(),
                        longDescription = document["LongDesc"].toString(),
                        alcoholMin = document["AlcoholMin"].toString().toDouble(),
                        alcoholMax = document["AlcoholMax"].toString().toDouble(),
                        color = document["Color"].toString(),
                        imageUrl = document["ImageUrl"].toString(),
                        type = document["Type"].toString(),
                    ))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("testQuery", "Error getting documents.", exception)
            }
            .addOnCompleteListener {
                Log.w("testQuery", "GG WP.")
                Log.w("testQuery", beers.toString())
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = BeerAdapter(beers)
                }
            }
    }

    private fun getDataFromDatabaseUser(){

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

        val query = db.collection("/Beer").whereIn("BeerName", beerNameList)
        query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    beers.add(Beer(
                        name = document["BeerName"].toString(),
                        brewery = document["Brewery"].toString(),
                        country = document["Country"].toString(),
                        shortDescription = document["ShortDesc"].toString(),
                        longDescription = document["LongDesc"].toString(),
                        alcoholMin = document["AlcoholMin"].toString().toDouble(),
                        alcoholMax = document["AlcoholMax"].toString().toDouble(),
                        color = document["Color"].toString(),
                        imageUrl = document["ImageUrl"].toString(),
                        type = document["Type"].toString(),
                    ))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("testQuery", "Error getting documents.", exception)
            }
            .addOnCompleteListener {
                Log.w("testQuery", "GG WP.")
                Log.w("testQuery", beers.toString())
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = BeerAdapter(beers)
                }
            }
    }
}