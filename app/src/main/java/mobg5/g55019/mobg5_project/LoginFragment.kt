package mobg5.g55019.mobg5_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var toast: Toast
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)

        binding.loginButton.setOnClickListener {
            if(isEmailValid()){

                toast = Toast.makeText(context, "Email is valid", Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                toast = Toast.makeText(context, "Email is not valid", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        return binding.root
    }

    private fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailArea.text).matches()

    }

}