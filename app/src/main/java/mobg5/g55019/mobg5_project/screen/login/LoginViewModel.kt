package mobg5.g55019.mobg5_project.screen.login

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * A ViewModel that handles the login process for the app.
 */
class LoginViewModel : ViewModel() {

    // LiveData pour suivre l'état de la connexion
    private val connectionStatus = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = connectionStatus

    // LiveData pour suivre le message d'erreur de connexion
    private val errorMessage = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorMessage

    /**
     * Attempts to log in the user with the given email and password using FirebaseAuth.
     * If the login is successful, the value of isConnected is set to true.
     * Otherwise, the value of isConnected is set to false and the value of error is set to the error message.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param auth the FirebaseAuth object to use for the login
     */
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

    /**
     * Returns a SpannableString with the text "Pas encore de compte ? S'inscrire" in which the text "S'inscrire" is
     * bold and has a color of #DDFF0059.
     *
     * @return a SpannableString with the text "Pas encore de compte ? S'inscrire"
     */
    fun setTextView() : SpannableString{
        val text = "Pas encore de compte ? S'inscrire"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("S'inscrire")
        val endIndex = startIndex + "S'inscrire".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /**
     * Adds a new document in the "User" collection for the authenticated user with an empty "Beers" field.
     *
     * @param auth the authenticated user
     */
     fun addInDbForBeer(auth : FirebaseAuth) {
        val db = FirebaseFirestore.getInstance()
        val usernameValue = FirebaseAuth.getInstance().uid?.substring(0, 10)

        var docRef = db.collection("User").document(auth.uid.toString())

        docRef.get().addOnSuccessListener { document ->
            if (document == null) {
                db.collection("User").document(auth.uid.toString())
                    .set(
                        mapOf(
                            "Beers" to emptyList<String>(),
                            "username" to usernameValue,
                            "profilImageUrl" to "",
                            "profileBannerUrl" to "",
                            "description" to ""
                        ), SetOptions.merge()
                    )
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "User added in collection") }
                    .addOnFailureListener { e ->
                        Log.w(
                            ContentValues.TAG,
                            "Error user collection",
                            e
                        )
                    }
            }
        }
    }



}