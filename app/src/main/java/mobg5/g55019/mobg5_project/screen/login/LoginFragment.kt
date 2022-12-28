package mobg5.g55019.mobg5_project.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding

/**
 * A Fragment that allows the user to log in to the app using their email and password.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val auth = FirebaseAuth.getInstance()

    /**
     * Sets up the layout of the fragment and sets up listeners for the login button and the "Create an account" text view.
     * If the login is successful, the user is redirected to the swipe fragment. If there is an error, a toast message
     * with the error message is displayed. If the user is already logged in, they are redirected to the swipe fragment.
     *
     * @param inflater the LayoutInflater object that can be used to inflate any views in the fragment
     * @param container the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the saved state of the fragment
     *
     * @return the View for the fragment's UI
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.pwdField.text.toString()
            if(email != "" && password != ""){
                viewModel.login(email, password, auth)
            }
            else{
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                Toast.makeText(context, "Login!", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }



        binding.inscriptionTextview.text = viewModel.setTextView()
        binding.inscriptionTextview.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }


    /**
     * If the user is already logged in, they are redirected to the swipe fragment.
     */
    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
        }
    }

}


/**
 * Backup :
class LoginFragment : Fragment() {

private lateinit var binding: FragmentLoginBinding
private lateinit var auth: FirebaseAuth
private lateinit var viewModel: LoginViewModel

override fun onCreateView(
inflater: LayoutInflater, container: ViewGroup?,
savedInstanceState: Bundle?
): View? {
binding = DataBindingUtil.inflate(
inflater,
R.layout.fragment_login, container, false
)

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


binding.inscriptionTextview.text = viewModel.setTextView()

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
 */