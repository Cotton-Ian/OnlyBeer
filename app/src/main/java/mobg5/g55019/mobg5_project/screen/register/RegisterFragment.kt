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
    private val database = FirebaseDatabase.getInstance("https://mobg5-onlybeer-default-rtdb.europe-west1.firebasedatabase.app/").reference;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register, container, false
        )

        binding.buttonRegister.setOnClickListener {
            if(informationValid()){
                //On fait un IDmail snas le point car firestore gère mal les mail comme login
                //CF : https://stackoverflow.com/questions/49904934/invalid-firebase-path-firebase-paths-must-not-contain-or
                val idMail = binding.emailTF.text.toString().replace(".", "")
                val email = binding.emailTF.text.toString()
                val password = binding.pwdTF.text.toString()
                if(checkDatabase(idMail)){
                    database.child("users").child(idMail).child("mail").setValue(email)
                    database.child("users").child(idMail).child("password").setValue(password)
                    Toast.makeText(context, "Register !", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                }
                else{
                    Toast.makeText(context, "Email already used", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(context, "Information not valid !", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    /**
     * Vérifie l'email est valide et si le mot de passe et la confirmation du mot de passe sont
     * valides
     */
    private fun informationValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailTF.text).matches() &&
                binding.pwdTF.text.toString() == binding.pwdConfirmTF.text.toString()
    }

    private fun checkDatabase(idMail : String): Boolean {
        var ok = true

        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.hasChild(idMail)){
                    ok = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show()
            }
        })
        return ok
    }


}