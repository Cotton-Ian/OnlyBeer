package mobg5.g55019.mobg5_project.screen.fav

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var viewModel: FavouriteViewModel

    private val auth = FirebaseAuth.getInstance()
    private val monBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    viewModel.clearList() //To avoid duplicate after going back from information fragment
                    viewModel.getDataFromDatabaseUser(auth)
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite,container , false)

        viewModel = ViewModelProvider(this)[FavouriteViewModel::class.java]

        viewModel.isListFill.observe(viewLifecycleOwner) { isListFill ->
            if (isListFill) {
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = BeerAdapter(viewModel.getBeers())
                }
            }
        }

        return binding.root
    }

}


/**
BACKUP
package mobg5.g55019.mobg5_project.screen.fav

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
private val monBroadcastReceiver = object : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent) {
if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
val connectivityManager =
context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val activeNetworkInfo = connectivityManager.activeNetworkInfo
if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
beers.clear() //To avoid duplicate after going back from information fragment
getDataFromDatabaseUser()
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
binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite,container , false)
//getListOfBeer()

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
 */