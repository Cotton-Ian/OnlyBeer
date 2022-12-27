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


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel : RegisterViewModel

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

                /*
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Register !", Toast.LENGTH_SHORT).show()
                        viewModel.addInDbForBeer(auth)
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(context, "Error : " + task.exception!!.message , Toast.LENGTH_SHORT).show()
                    }
                }
                 */

            }
        }
    }

    /**
     * val viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

    viewModel.accountCreated.observe(this, Observer { accountCreated ->
    if (accountCreated) {
    Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
    view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
    }
    })

    viewModel.errorMessage.observe(this, Observer { errorMessage ->
    if (errorMessage != null) {
    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }
    })

    viewModel.createAccount(email, password)
     */

    private fun setUpTv(){
        binding.seConnecterTextView.text = viewModel.getloginTv()

        binding.seConnecterTextView.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}