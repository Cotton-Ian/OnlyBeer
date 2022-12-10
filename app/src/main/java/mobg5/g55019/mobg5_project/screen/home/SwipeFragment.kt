package mobg5.g55019.mobg5_project.screen.home

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentSwipeBinding

class SwipeFragment : Fragment() {

    private lateinit var binding: FragmentSwipeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bottomNav = activity?.findViewById<View>(R.id.bottom_navigation)
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.white), // Couleur de la barre de nav
            ContextCompat.getColor(requireContext(), R.color.blue_200), // Couleur pour l'état désactivé
            ContextCompat.getColor(requireContext(), R.color.green_200), // Couleur pour l'état non sélectionné
            ContextCompat.getColor(requireContext(), R.color.green_500) // Couleur pour l'état pressé
        )

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // État activé
            intArrayOf(-android.R.attr.state_enabled), // État désactivé
            intArrayOf(-android.R.attr.state_checked), // État non sélectionné
            intArrayOf(android.R.attr.state_pressed) // État pressé
        )
        val colorStateList = ColorStateList(states, colors)
        bottomNav?.backgroundTintList = colorStateList
        bottomNav?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

}