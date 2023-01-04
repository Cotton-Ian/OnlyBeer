package mobg5.g55019.mobg5_project.screen.fav

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentBeerBinding
import mobg5.g55019.mobg5_project.informations.BeerInformationFragment
import mobg5.g55019.mobg5_project.model.Beer

/**
 * ViewHolder class for displaying a beer in a RecyclerView.
 *
 * @param binding the data binding object for the layout of the ViewHolder
 */
class BeerViewHolder(private val binding: FragmentBeerBinding) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Binds the provided beer to the ViewHolder.
     *
     * This method updates the text and image of the ViewHolder to reflect the properties of the provided beer. It also
     * sets up a click listener for the ViewHolder that navigates to the BeerInformationFragment when clicked.
     *
     * @param beer the beer to bind to the ViewHolder
     */
    fun bind(beer: Beer) {
        binding.beerName.text = beer.name
        binding.beerDescription.text = beer.shortDescription

        Glide.with(binding.beerImage.context)
            .load(beer.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(400, 400)
            .into(binding.beerImage)

        binding.cardView.setOnClickListener {view : View ->
            val args = Bundle()
            args.putSerializable("BEER", beer)
            val fragment = BeerInformationFragment()
            fragment.arguments = args

            view?.findNavController()?.navigate(R.id.action_favouriteFragment_to_beerInformationFragment, args)
        }
        //Glide.with(this).load(beers[0].imageUrl).into(binding.beerImage)
        //binding.beerImage.setImageResource(beer.image)
        binding.executePendingBindings()
    }

}
