package mobg5.g55019.mobg5_project.informations

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentBeerInformationBinding
import mobg5.g55019.mobg5_project.model.Beer
import mobg5.g55019.mobg5_project.model.ColorModel


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


    private fun setUpView() {
        // Image Loading
        Glide.with(this).load(currentBeer.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(400, 400)
            .into(binding.beerImage)

        binding.beername.text = currentBeer.name
        binding.longDescTV.text = currentBeer.longDescription

        // Liste des TextViews à mettre à jour
        val textViews = listOf(binding.typeTV, binding.colorTV, binding.countryTV, binding.breweryTV)

        // Fonction pour mettre à jour le texte d'un TextView
        fun updateTextView(textView: TextView, text: String) {
            val spannable = SpannableString(textView.text.toString().plus(text))
            val colonPosition = spannable.indexOf(":")
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, colonPosition + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Mettre à jour la couleur du texte si nécessaire
            if (textView == binding.colorTV) {
                val color = colorFinder(currentBeer.color)
                val colorSpan = ForegroundColorSpan(Color.parseColor(color))
                spannable.setSpan(colorSpan, spannable.length - currentBeer.color.length,
                    spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            textView.text = spannable
        }

        // Itérer sur chaque TextView et mettre à jour le texte
        for (textView in textViews) {
            when (textView) {
                binding.typeTV -> updateTextView(textView, currentBeer.type)
                binding.colorTV -> updateTextView(textView, currentBeer.color)
                binding.countryTV -> updateTextView(textView, currentBeer.country)
                binding.breweryTV -> updateTextView(textView, currentBeer.brewery)
            }
        }
    }


    /*
    private fun setUpView(){
        var colonPosition = 0

        //Image Loading
        Glide.with(this).load(currentBeer.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(400, 400)
            .into(binding.beerImage)

        binding.beername.text = currentBeer.name
        binding.longDescTV.text = currentBeer.longDescription

        var spannable = SpannableString(binding.typeTV.text.toString().plus(currentBeer.type))
        colonPosition = spannable.indexOf(":")
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, colonPosition + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.typeTV.text = spannable

        //Color for the type of color
        spannable = SpannableString(binding.colorTV.text.toString().plus(currentBeer.color))
        val color = colorFinder(currentBeer.color)
        val colorSpan = ForegroundColorSpan(Color.parseColor(color))
        colonPosition = spannable.indexOf(":")
        spannable.setSpan(colorSpan, spannable.length - currentBeer.color.length,
            spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, colonPosition + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.colorTV.text = spannable

        spannable = SpannableString(binding.countryTV.text.toString().plus(currentBeer.country))
        colonPosition = spannable.indexOf(":")
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, colonPosition + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.countryTV.text = spannable

        spannable = SpannableString(binding.breweryTV.text.toString().plus(currentBeer.brewery))
        colonPosition = spannable.indexOf(":")
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, colonPosition + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.breweryTV.text = spannable
    }
    */


    private fun colorFinder(color :String): String{
        return when (color.uppercase()) {
            ColorModel.BLONDE.name -> ColorModel.BLONDE.hex
            ColorModel.BRUNE.name -> ColorModel.BRUNE.hex
            ColorModel.ROUSSE.name -> ColorModel.ROUSSE.hex
            ColorModel.AMBREE.name -> ColorModel.AMBREE.hex
            ColorModel.DOREE.name -> ColorModel.DOREE.hex
            ColorModel.CUIVREE.name -> ColorModel.CUIVREE.hex
            ColorModel.NOIRE.name -> ColorModel.NOIRE.hex
            ColorModel.VERTE.name -> ColorModel.VERTE.hex
            ColorModel.BLANCHE.name -> ColorModel.BLANCHE.hex
            ColorModel.ROUGE.name -> ColorModel.ROUGE.hex
            ColorModel.ORANGE.name -> ColorModel.ORANGE.hex
            ColorModel.JAUNE.name -> ColorModel.JAUNE.hex
            ColorModel.VIOLETTE.name -> ColorModel.VIOLETTE.hex
            else -> ColorModel.DEFAULT.hex
        }

    }
}