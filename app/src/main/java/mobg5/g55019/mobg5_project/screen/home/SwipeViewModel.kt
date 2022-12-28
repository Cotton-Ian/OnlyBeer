package mobg5.g55019.mobg5_project.screen.home


import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.model.Beer

/**
 * ViewModel class for the swipe feature.
 *
 * This ViewModel stores a list of beers, a LiveData object indicating whether there are no more beers available,
 * and a LiveData object indicating whether the small constraint layout should be set up.
 *
 * It also includes methods for retrieving data from the Firebase Firestore database:
 * - getDataFromDatabaseUser: Retrieves a list of beer IDs from the "User" collection in the database for the current user.
 * - getDataFromDatabaseBeer: Retrieves the beer data from the "Beer" collection in the database for each beer in the list of IDs.
 * - like: Adds the current beer to the list of liked beers for the current user in the database.
 * - dislike: Removes the current beer from the list of beers for the current user in the database.
 */
class SwipeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var beers: MutableList<Beer> = mutableListOf()
    var noMoreBeer = MutableLiveData<Boolean>()
    var setUpSmallConstraintLayout = MutableLiveData<Boolean>()

    /**
     * Initializes the LiveData objects with default values.
     */
    init {
        noMoreBeer.value = false
        setUpSmallConstraintLayout.value = false
    }

    /**
     * Retrieves a list of beer IDs from the "User" collection in the database for the current user.
     * @param auth The authentication object for the current user.
     */
    fun getDataFromDatabaseUser(auth: FirebaseAuth){
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

    /**
     * Retrieves the beer data from the "Beer" collection in the database for each beer in the list of IDs.
     * @param beerNameList The list of beer IDs.
     */
    private fun getDataFromDatabaseBeer(beerNameList: MutableList<String>) {
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
            .addOnCompleteListener {
                if (beers.size == 0) {
                    noMoreBeer.value = true
                }
            }
    }

    /**
     * Adds a beer to the list of beers.
     * @param beer A map containing the beer data.
     */
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
        Log.d("testDebug", "Bière ajoutée :  : $name")


        if(beers.size == 1){
            setUpSmallConstraintLayout.value = true
        }
    }

    /**
     * Removes the first beer from the list of beers.
     * If the list becomes empty after removing the beer, the value of noMoreBeer is set to true.
     * Otherwise, the value of setUpSmallConstraintLayout is set to true.
     */
    fun dislike(){
        Log.d("testDebug", "Beers size : ${beers.size}")
        if(beers.size > 0){
            beers.removeAt(0)
            if(beers.size == 0){
                noMoreBeer.value = true

            }
            else{
                setUpSmallConstraintLayout.value = true
            }
        }
        else{
            noMoreBeer.value = true
        }
    }

    /**
     * Removes the first beer from the list of beers and adds it to the "Beers" array of the user with the given auth.
     * If the list becomes empty after removing the beer, the value of noMoreBeer is set to true.
     * Otherwise, the value of setUpSmallConstraintLayout is set to true.
     *
     * @param auth the FirebaseAuth object that represents the current user
     */
    fun like(auth: FirebaseAuth){
        if(beers.size > 0){
            val userRef = db.collection("User").document(auth.uid.toString())
            userRef.update("Beers", FieldValue.arrayUnion(beers[0].name))
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Beer added to beers array") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding beer to beers array", e) }
            beers.removeAt(0)
            if(beers.size == 0){
                noMoreBeer.value = true
            }
            else{
                setUpSmallConstraintLayout.value = true
            }
        }
        else{
            noMoreBeer.value = true
        }
    }


    /**
     * Returns the first beer in the list of beers.
     *
     * @return the first beer in the list of beers
     */
    fun getBeer(): Beer {
        return beers[0]
    }

    /**
     * Returns the size of the list of beers.
     *
     * @return the size of the list of beers
     */
    fun getBeersSize(): Int {
        return beers.size
    }



}