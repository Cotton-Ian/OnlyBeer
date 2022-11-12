package mobg5.g55019.mobg5_project

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.room.Room
import mobg5.g55019.mobg5_project.database.AppDatabase
import mobg5.g55019.mobg5_project.database.DateConverter
import mobg5.g55019.mobg5_project.database.User
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding
import java.util.*


class LoginFragment : Fragment() {

    private lateinit var toast: Toast
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)
        setUpSpinner()
        databaseCheck()
        binding.loginButton.setOnClickListener {
            if(isEmailValid()){
                toast = Toast.makeText(context, "Email is valid", Toast.LENGTH_SHORT)
                toast.show()
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_mainPage)
                hideKeyboard(this.requireActivity())
                databaseInsert(binding.emailArea.text.toString())
                databaseCheck()
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

    private fun databaseCheck(){
        val user = AppDatabase.getInstance(this.requireContext()).userDao().getAll()
        println("------------------------------------------ \n Database CHECK : \n Liste of user :")
        for (i in user){
            println(i)
        }
    }

    private fun databaseInsert(mail: String) {
        if (AppDatabase.getInstance(this.requireContext()).userDao().findByName(mail) == null) {
            println("EXISTE PAS")
            val date = Calendar.getInstance().time
            println(mail + "\n" + date)
            val datelong =  DateConverter().fromDate(date)
            println("Database INSERT : " + mail + " | " +date + " | " + datelong)
            val user = datelong?.let { User(mail, it) }
            if (user != null) {
                AppDatabase.getInstance(this.requireContext()).userDao().insert(user)
            }
        }
        else {
            println("Database WARNING : EXISTE déjà")
        }
    }

    private fun setUpSpinner(){
        val users = AppDatabase.getInstance(this.requireContext()).userDao().getAll()
        val mails = mutableListOf<String>()
        mails.add("--Choisir un mail existant--")
        for (i in users){
            mails.add(i.mail)
        }

        val adapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, mails)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0){
                    binding.emailArea.setText(mails[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
               //nothing to add
            }

        }
    }

}