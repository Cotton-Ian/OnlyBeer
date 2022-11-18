package mobg5.g55019.mobg5_project.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var factory : LoginViewModelFactory
    private lateinit var viewModel: LoginViewModel

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

        setUpSpinner()
        binding.loginButton.setOnClickListener {
            if (viewModel.isEmailValid(binding.emailArea.text.toString())) {
                Toast.makeText(context, "Email is valid", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_mainPage)
                viewModel.hideKeyboard(this.requireActivity())
                viewModel.databaseInsert(binding.emailArea.text.toString())
            } else {
                Toast.makeText(context, "Email is not valid", Toast.LENGTH_SHORT).show()
            }
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

    private fun setUpSpinner() {
        val users = viewModel.getUsers()
        val mails = mutableListOf<String>()
        mails.add("--Choisir un mail existant--")
        for (i in users) {
            mails.add(i.mail)
        }

        val adapter =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, mails)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    binding.emailArea.setText(mails[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //nothing to add
            }
        }
    }

}