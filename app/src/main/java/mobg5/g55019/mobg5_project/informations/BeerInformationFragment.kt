package mobg5.g55019.mobg5_project.informations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentBeerInformationBinding
import mobg5.g55019.mobg5_project.model.Beer

//TODO : debug la navigation depuis cette page FIXED
//C'est comme si le fragment était attaché à favouriteFragment
class BeerInformationFragment : Fragment() {
    private lateinit var binding: FragmentBeerInformationBinding
    private lateinit var currentBeer : Beer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beer_information,container , false)
        setUpView()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentBeer = requireArguments().getSerializable("BEER") as Beer
    }


    override fun onStop() {
        super.onStop()
        //val frag = activity?.supportFragmentManager?.findFragmentById(FavouriteFragment().id)
        //activity?.supportFragmentManager?.beginTransaction()?.replace(this.id,frag!!)?.commit()

        //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        //activity?.supportFragmentManager?.popBackStack()

    }


    private fun setUpView(){
        Glide.with(this).load(currentBeer.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(400, 400)
            .into(binding.beerImage)

        binding.beername.text = currentBeer.name
        binding.longDescTV.text = currentBeer.longDescription
        binding.typeTV.text = binding.typeTV.text.toString().plus(currentBeer.type)
        binding.colorTV.text = binding.colorTV.text.toString().plus(currentBeer.color)
        binding.countryTV.text = binding.countryTV.text.toString().plus(currentBeer.country)
        binding.breweryTV.text = binding.breweryTV.text.toString().plus(currentBeer.brewery)
    }
}