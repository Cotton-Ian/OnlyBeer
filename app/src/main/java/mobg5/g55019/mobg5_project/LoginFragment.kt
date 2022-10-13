package mobg5.g55019.mobg5_project

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_mainPage)
                hideKeyboard(this.requireActivity())
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


}