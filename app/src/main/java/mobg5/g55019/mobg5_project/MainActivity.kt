package mobg5.g55019.mobg5_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import mobg5.g55019.mobg5_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //TODO question  1 seul activity ou plusieurs (login, register, main) ?
    //Si plusisuers quand créer une activity et quand un fragment ?q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

    }






}