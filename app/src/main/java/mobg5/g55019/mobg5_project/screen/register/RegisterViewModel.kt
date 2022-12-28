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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * A ViewModel that handles the creation of an account using email and password. It also provides a LiveData object
 * to indicate if the account creation was successful or if there was an error, and another LiveData object to hold the
 * error message if there was an error.
 */
class RegisterViewModel : ViewModel() {
    private var seConnecter : SpannableString
    val mAccountCreated = MutableLiveData<Boolean>()
    val mErrorMessage = MutableLiveData<String>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Initializes the "Already have an account? Log in" text view with a bold and color-coded text.
     */
    init {
        val text = "Déjà inscrit ? Se connecter"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Se connecter")
        val endIndex = startIndex + "Se connecter".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        seConnecter = spannableString
    }

    /**
     * Returns the "Already have an account? Log in" text view.
     *
     * @return the SpannableString for the "Already have an account? Log in" text view
     */
    fun getloginTv() : SpannableString{
        return seConnecter
    }


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
     * Adds a new document in the "User" collection for the authenticated user with an empty "Beers" field.
     *
     * @param auth the authenticated user
     */
    private fun addInDbForBeer(auth : FirebaseAuth) {
        val db = FirebaseFirestore.getInstance()
        db.collection("User").document(auth.uid.toString())
            .set(mapOf("Beers" to emptyList<String>()), SetOptions.merge())
            .addOnSuccessListener { Log.d(ContentValues.TAG, "User added in collection") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error user collection", e) }
    }

    /**
     * Creates a new account with the given email and password using FirebaseAuth. If the account creation is successful,
     * the "User" collection is updated with a new document for the authenticated user with an empty "Beers" field. If
     * there is an error, the error message is stored in the mErrorMessage LiveData object.
     *
     * @param email the email for the new account
     * @param password the password for the new account
     */
    fun createAccount(email: String?, password: String?) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addInDbForBeer(auth)
                    mAccountCreated.setValue(true)
                } else {
                    mErrorMessage.setValue(task.exception!!.message)
                }
            }
    }

}