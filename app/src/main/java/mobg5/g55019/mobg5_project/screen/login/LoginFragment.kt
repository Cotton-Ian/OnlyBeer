package mobg5.g55019.mobg5_project.screen.login

import android.content.ContentValues
import android.os.Bundle
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

        binding.RegisterTextViewButton.setOnClickListener {
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