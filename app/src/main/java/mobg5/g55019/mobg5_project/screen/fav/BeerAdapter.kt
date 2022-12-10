package mobg5.g55019.mobg5_project.screen.fav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mobg5.g55019.mobg5_project.databinding.FragmentBeerBinding
import mobg5.g55019.mobg5_project.model.Beer

class BeerAdapter(private val beers: List<Beer>) : RecyclerView.Adapter<BeerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val cardView = LayoutInflater.from(parent.context)
        val binding = FragmentBeerBinding.inflate(cardView, parent, false)
        return BeerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        holder.bind(beers[position])
    }

    override fun getItemCount(): Int {
        return beers.size
    }


}