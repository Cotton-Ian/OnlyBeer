package mobg5.g55019.mobg5_project.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainPageViewModel(finalScore : Int) : ViewModel() {
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val hasincremented = MutableLiveData<Boolean>()
    val eventIncrement: LiveData<Boolean>
        get() = hasincremented

    init {
        _score.value = finalScore
    }

    fun onIncrement() {
        _score.value = (_score.value)?.plus(1)
        hasincremented.value = true
    }

    fun onIncrementComplete() {
        hasincremented.value = false
    }

}