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

/**
 * Fragment class for displaying a list of the user's favorite beers.
 */
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

    /**
     * Called to do initial creation of a fragment.
     *
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enregistrez le BroadcastReceiver pour recevoir les événements de changement de connectivité
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(monBroadcastReceiver, filter)
    }

    /**
     * Called when the fragment is no longer in use.
     *
     * This is called after onStop() and before onDetach().
     */
    override fun onDestroy() {
        super.onDestroy()

        // Désenregistrez le BroadcastReceiver lorsque votre fragment est détruit
        context?.unregisterReceiver(monBroadcastReceiver)
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * This is optional, and non-graphical fragments can return null (which is the default implementation).
     *
     * This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the
     *                  LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
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