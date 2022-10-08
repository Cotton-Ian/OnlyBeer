package mobg5.g55019.mobg5_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import mobg5.g55019.mobg5_project.databinding.ActivityMainBinding

//TODO : navabar + about section in the navbar with a page
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.loginButton.setOnClickListener {
            if(isEmailValid()){
                //display a toast
                toast = Toast.makeText(this, "Email is valid", Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                toast = Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailArea.text).matches()

    }
}