package mobg5.g55019.mobg5_project.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import mobg5.g55019.mobg5_project.database.AppDatabase
import mobg5.g55019.mobg5_project.database.DateConverter
import mobg5.g55019.mobg5_project.database.User
import java.util.*
import android.content.Context

class LoginViewModel(context: Context) : ViewModel() {

    private val context = context

    fun isEmailValid(mail : String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

     fun databaseInsert(mail: String) {
        if (AppDatabase.getInstance(context).userDao().findByName(mail) == null) {
            println("EXISTE PAS")
            val date = Calendar.getInstance().time
            println(mail + "\n" + date)
            val datelong = DateConverter().fromDate(date)
            println("Database INSERT : " + mail + " | " + date + " | " + datelong)
            val user = datelong?.let { User(mail, it) }
            if (user != null) {
                AppDatabase.getInstance(context).userDao().insert(user)
            }
        } else {
            val dateLong = DateConverter().fromDate(Calendar.getInstance().time)
            if (dateLong != null) {
                AppDatabase.getInstance(context).userDao().updateDate(mail, dateLong)
            }
            println("Database WARNING : EXISTE déjà")
        }
    }

    fun getUsers(): List<User> {
        return AppDatabase.getInstance(context).userDao().getAll()
    }



}