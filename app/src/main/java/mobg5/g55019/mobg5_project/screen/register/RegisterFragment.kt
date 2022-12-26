package mobg5.g55019.mobg5_project.screen.register

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel : RegisterViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register, container, false
        )

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        val text = "Déjà inscrit ? Se connecter"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Se connecter")
        val endIndex = startIndex + "Se connecter".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.seConnecterTextView.text = spannableString

        binding.seConnecterTextView.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            if(informationValid()){
                //https://firebase.google.com/docs/auth/android/password-auth
                val email = binding.emailTF.text.toString()
                val password = binding.pwdTF.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Register !", Toast.LENGTH_SHORT).show()
                        addInDbForBeer(auth)
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(context, "Error : " + task.exception!!.message , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    /**
     * Vérifie l'email est valide et si le mot de passe et la confirmation du mot de passe sont
     * valides
     */
    private fun informationValid(): Boolean {
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTF.text).matches()){
            Toast.makeText(context, "This mail is not valid !", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.pwdTF.text.toString() != binding.pwdConfirmTF.text.toString()) {
            Toast.makeText(context, "Passwords are not the same !", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Quand un user s'inscrit, je lui crée un doc dans la collection user qui permettra de connaitre
     * les bières qui like
     */
    private fun addInDbForBeer(auth: FirebaseAuth) {
        val db = FirebaseFirestore.getInstance()
        db.collection("User").document(auth.uid.toString())
            .set(mapOf("Beers" to emptyList<String>()), SetOptions.merge())
            .addOnSuccessListener { Log.d(ContentValues.TAG, "User added in collection") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error user collection", e) }
    }
}
