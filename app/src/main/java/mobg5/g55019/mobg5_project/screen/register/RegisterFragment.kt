package mobg5.g55019.mobg5_project.screen.register


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentRegisterBinding

/**
 * A Fragment that allows the user to create an account using their email and password.
 */
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel : RegisterViewModel

    /**
     * Sets up the layout of the fragment and sets up listeners for the "Create an account" button and the "Already have
     * an account? Log in" text view. If the account creation is successful, the user is redirected to the login fragment.
     * If there is an error, a toast message with the error message is displayed.
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
            R.layout.fragment_register, container, false
        )

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setUpTv()
        setUpButtonRegister()

        return binding.root
    }

    /**
     * Sets up the "Create an account" button. If the account creation is successful, the user is redirected to the login
     * fragment. If there is an error, a toast message with the error message is displayed.
     */
    private fun setUpButtonRegister(){
        viewModel.mAccountCreated.observe(viewLifecycleOwner) { mAccountCreated ->
            if (mAccountCreated) {
                Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
                view?.findNavController()
                    ?.navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        viewModel.mErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            if(viewModel.informationValid(binding.emailTF.text.toString(),
                    binding.pwdTF.text.toString(), binding.pwdConfirmTF.text.toString(), context )){

                val email = binding.emailTF.text.toString()
                val password = binding.pwdTF.text.toString()


                viewModel.createAccount(email, password)

            }
        }
    }

    /**
     * Sets up the "Already have an account? Log in" text view. When clicked, the user is redirected to the login fragment.
     */
    private fun setUpTv(){
        binding.seConnecterTextView.text = viewModel.getloginTv()

        binding.seConnecterTextView.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}