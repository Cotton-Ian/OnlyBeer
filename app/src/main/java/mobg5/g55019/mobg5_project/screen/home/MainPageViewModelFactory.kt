package mobg5.g55019.mobg5_project.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainPageViewModelFactory(private val finalScore : Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPageViewModel::class.java)) {
            return MainPageViewModel(finalScore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
