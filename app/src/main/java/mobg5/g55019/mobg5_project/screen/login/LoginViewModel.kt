package mobg5.g55019.mobg5_project.screen.login

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    // LiveData pour suivre l'état de la connexion
    private val connectionStatus = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = connectionStatus

    // LiveData pour suivre le message d'erreur de connexion
    private val errorMessage = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorMessage

    fun login(email: String, password: String, auth : FirebaseAuth) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Mettre à jour l'état de la connexion
                connectionStatus.value = true
            } else {
                // Mettre à jour l'état de la connexion et le message d'erreur
                connectionStatus.value = false
                errorMessage.value = task.exception?.message
            }
        }
    }

    fun setTextView() : SpannableString{
        val text = "Pas encore de compte ? S'inscrire"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("S'inscrire")
        val endIndex = startIndex + "S'inscrire".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

}