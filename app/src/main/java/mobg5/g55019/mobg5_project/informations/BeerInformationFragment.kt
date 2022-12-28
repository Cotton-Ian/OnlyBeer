package mobg5.g55019.mobg5_project.informations

import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentBeerInformationBinding
import mobg5.g55019.mobg5_project.model.Beer
import mobg5.g55019.mobg5_project.model.ColorModel

/**
 * A fragment that displays information about a beer.
 *
 * @param Beer the beer object to display information about
 */

class BeerInformationFragment : Fragment() {
    private lateinit var binding: FragmentBeerInformationBinding
    private lateinit var currentBeer : Beer

    /**
     * Called when the fragment's view needs to be created.
     *
     * @param inflater the LayoutInflater object that can be used to inflate any views in the fragment
     * @param container the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the saved instance state of the fragment
     *
     * @return the View for the fragment's UI, or null
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beer_information,container , false)
        setUpView()
        setUpLocalisation()

        return binding.root
    }

    /**
     * Called when the fragment is created.
     *
     * @param savedInstanceState the saved instance state of the fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentBeer = requireArguments().getSerializable("BEER") as Beer
    }


    private fun setUpLocalisation(){
        binding.locationButton.setOnClickListener {

            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                Toast.makeText(context, "Activer le GPS", Toast.LENGTH_SHORT).show()
            }
            else{
                val address = binding.beername.text.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
                intent.setPackage("com.google.android.apps.maps")
                if (context?.let { it1 -> intent.resolveActivity(it1.packageManager) } != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Google Maps n'est pas installé sur cet appareil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Set up the view for the fragment.
     *
     * This method loads the beer image, sets the name and long description for the beer, and updates the text for various
     * text views with information about the beer. The text views are also styled with bold text and, in the case of the
     * color text view, the text color is updated to match the color of the beer.
     */
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

    /**
     * Find the hex code for the specified color.
     *
     * @param color the color to find the hex code for
     *
     * @return the hex code for the color, or the default hex code if the color is not found
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