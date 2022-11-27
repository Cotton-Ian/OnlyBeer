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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding
import java.util.EventListener

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var factory : LoginViewModelFactory
    private lateinit var viewModel: LoginViewModel
    private val database = FirebaseDatabase.getInstance("https://mobg5-onlybeer-default-rtdb.europe-west1.firebasedatabase.app/").reference;


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

        binding.loginButton.setOnClickListener {
            database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(binding.emailArea.text.toString().replace(".", ""))){
                        val password = snapshot.child(binding.emailArea.text.toString().replace(".", "")).child("password").value.toString()
                        if(password == binding.pwdField.text.toString()){
                            Toast.makeText(context, "Login !", Toast.LENGTH_SHORT).show()
                            //TODO
                            //view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        else{
                            Toast.makeText(context, "Wrong password !", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(context, "Email not found !", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.RegisterTextViewButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    /*
    private fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailArea.text).matches()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    */

    /*
    private fun databaseInsert(mail: String) {
        if (AppDatabase.getInstance(this.requireContext()).userDao().findByName(mail) == null) {
            println("EXISTE PAS")
            val date = Calendar.getInstance().time
            println(mail + "\n" + date)
            val datelong = DateConverter().fromDate(date)
            println("Database INSERT : " + mail + " | " + date + " | " + datelong)
            val user = datelong?.let { User(mail, it) }
            if (user != null) {
                AppDatabase.getInstance(this.requireContext()).userDao().insert(user)
            }
        } else {
            println("Database WARNING : EXISTE déjà")
        }
    }
    */


}