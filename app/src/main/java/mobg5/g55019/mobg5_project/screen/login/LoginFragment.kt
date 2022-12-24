package mobg5.g55019.mobg5_project.screen.login

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var factory : LoginViewModelFactory
    private lateinit var viewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )
        factory = LoginViewModelFactory(this.requireContext())
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.pwdField.text.toString()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login !", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
                } else {
                    Toast.makeText(context, "Wrong email or password !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val text = "Pas encore de compte ? S'inscrire"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("S'inscrire")
        val endIndex = startIndex + "S'inscrire".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#DDFF0059")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.inscriptionTextview.text = spannableString

        binding.inscriptionTextview.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
        }
    }

}