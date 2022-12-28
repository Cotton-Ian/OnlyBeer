package mobg5.g55019.mobg5_project.screen.fav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mobg5.g55019.mobg5_project.databinding.FragmentBeerBinding
import mobg5.g55019.mobg5_project.model.Beer

/**
 * Adapter class for displaying a list of beers in a RecyclerView.
 *
 * @param beers the list of beers to display in the RecyclerView
 */
class BeerAdapter(private val beers: List<Beer>) : RecyclerView.Adapter<BeerViewHolder>() {

    /**
     * Called when a new ViewHolder needs to be created.
     *
     * This method creates a new ViewHolder by inflating the layout for a single item in the RecyclerView and returning
     * it.
     *
     * @param parent the parent ViewGroup for the ViewHolder
     * @param viewType the view type of the ViewHolder
     * @return a new ViewHolder for the provided view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val cardView = LayoutInflater.from(parent.context)
        val binding = FragmentBeerBinding.inflate(cardView, parent, false)
        return BeerViewHolder(binding)
    }

    /**
     * Called by the RecyclerView to display the data at the specified position.
     *
     * This method binds the data for a single item in the RecyclerView to the provided ViewHolder.
     *
     * @param holder the ViewHolder to bind the data to
     * @param position the position of the data in the list
     */
    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        holder.bind(beers[position])
    }

    /**
     * Returns the number of items in the list of beers.
     *
     * @return the number of items in the list of beers
     */
    override fun getItemCount(): Int {
        return beers.size
    }


}