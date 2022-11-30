package mobg5.g55019.mobg5_project.screen.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register, container, false
        )

        auth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            if(informationValid()){
                //https://firebase.google.com/docs/auth/android/password-auth
                val email = binding.emailTF.text.toString()
                val password = binding.pwdTF.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Register !", Toast.LENGTH_SHORT).show()
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(context, "This mail is already used !", Toast.LENGTH_SHORT).show()
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
}