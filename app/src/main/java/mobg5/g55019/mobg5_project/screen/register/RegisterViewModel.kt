package mobg5.g55019.mobg5_project.screen.register

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class RegisterViewModel : ViewModel() {
    private var seConnecter : SpannableString
    private val mAccountCreated = MutableLiveData<Boolean>()
    private val mErrorMessage = MutableLiveData<String>()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val text = "Déjà inscrit ? Se connecter"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Se connecter")
        val endIndex = startIndex + "Se connecter".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        seConnecter = spannableString
    }

    fun getloginTv() : SpannableString{
        return seConnecter
    }

    /**
     * Vérifie l'email est valide et si le mot de passe et la confirmation du mot de passe sont
     * valides
     */
    fun informationValid(mail : String, password : String, passwordConfirm: String ,context : Context?): Boolean {
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            Toast.makeText(context, "This mail is not valid !", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != passwordConfirm) {
            Toast.makeText(context, "Passwords are not the same !", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Quand un user s'inscrit, je lui crée un doc dans la collection user qui permettra de connaitre
     * les bières qui like
     */
    fun addInDbForBeer() {
        val db = FirebaseFirestore.getInstance()
        db.collection("User").document(auth.uid.toString())
            .set(mapOf("Beers" to emptyList<String>()), SetOptions.merge())
            .addOnSuccessListener { Log.d(ContentValues.TAG, "User added in collection") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error user collection", e) }
    }

    fun createAccount(email: String?, password: String?) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mAccountCreated.setValue(true)
                } else {
                    mErrorMessage.setValue(task.exception!!.message)
                }
            }
    }

    fun getAccountCreated(): LiveData<Boolean> {
        return mAccountCreated
    }

    fun getErrorMessage(): LiveData<String> {
        return mErrorMessage
    }

}