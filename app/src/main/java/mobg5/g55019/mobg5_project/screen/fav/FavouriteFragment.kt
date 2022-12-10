package mobg5.g55019.mobg5_project.screen.fav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentFavouriteBinding
import mobg5.g55019.mobg5_project.model.Beer

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val beers = getListOfBeer()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite,container , false)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BeerAdapter(beers)
        }

        return binding.root
    }

    private fun getListOfBeer(): List<Beer> {
        return listOf(
            Beer(
                name = "Duvel",
                brewery = "Brouwerij Duvel Moortgat",
                country = "Belgique",
                shortDescription = "Bière blonde belge",
                longDescription = "La Duvel est une bière blonde belge, brassée depuis 1923 par la brasserie Duvel Moortgat. Elle est caractérisée par sa forte teneur en alcool (8,5 %) et par sa mousse abondante et crémeuse. La Duvel est considérée comme l'une des meilleures bières belges.",
                alcoholMin = 8.5,
                alcoholMax = 8.5,
                color = "Blonde",
                image = R.drawable.ic_baseline_image_24,
                type = "Bière blonde",
                favourite = true
            ),
            Beer(
                name = "Guinness",
                brewery = "Guinness Brewery",
                country = "Irlande",
                shortDescription = "Stout irlandais",
                longDescription = "La Guinness est une stout irlandaise, brassée depuis 1759 par la brasserie Guinness. Elle est caractérisée par sa couleur noire opaque, sa mousse dense et crémeuse, et son goût malté et épicé. La Guinness est l'une des bières les plus célèbres au monde, avec des ventes annuelles d'environ 1,8 milliard de pintes.",
                alcoholMin = 4.2,
                alcoholMax = 4.2,
                color = "Noire",
                image = R.drawable.ic_baseline_image_24,
                type = "Stout",
                favourite = true
            )
        )
    }
}