package mobg5.g55019.mobg5_project.screen.fav

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import mobg5.g55019.mobg5_project.databinding.FragmentBeerBinding
import mobg5.g55019.mobg5_project.model.Beer

class BeerViewHolder(private val binding: FragmentBeerBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(beer: Beer) {
        binding.beerName.text = beer.name
        binding.beerDescription.text = beer.shortDescription
        binding.beerImage.setImageResource(beer.image)
        binding.executePendingBindings()
    }

}