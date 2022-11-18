package mobg5.g55019.mobg5_project.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentMainPageBinding
import kotlin.random.Random


class MainPage : Fragment() {

    //TODO : use LiveData

    lateinit var binding: FragmentMainPageBinding
    lateinit var factory: MainPageViewModelFactory
    lateinit var viewModel: MainPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_page, container, false)
        disableBackButton()
        factory = MainPageViewModelFactory(Random.nextInt(0,100))
        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)
        changeText()
        binding.mainPageViewModel = viewModel
        viewModel.eventIncrement.observe(viewLifecycleOwner) { hasIncremented ->
            if (hasIncremented) {
                changeText()
                viewModel.onIncrementComplete()
            }
        }
        return binding.root
    }

    private fun disableBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Handle the back button event
        }
    }

    private fun changeText() {
        binding.textMainpage.text  = "Bienvenue sur la page principale " + viewModel.score.value.toString()
    }

}