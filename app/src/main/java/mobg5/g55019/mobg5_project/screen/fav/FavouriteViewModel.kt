package mobg5.g55019.mobg5_project.screen.fav

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.model.Beer

class FavouriteViewModel : ViewModel() {
    private var beers = mutableListOf<Beer>()
    private val db = FirebaseFirestore.getInstance()
    var isListFill = MutableLiveData<Boolean>()

    fun getDataFromDatabaseUser(auth : FirebaseAuth){
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
                isListFill.value = true
            }
    }

    fun clearList(){
        beers.clear()
    }

    fun getBeers() : MutableList<Beer>{
        return beers
    }



}