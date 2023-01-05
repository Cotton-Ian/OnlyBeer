package mobg5.g55019.mobg5_project.screen.login

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import mobg5.g55019.mobg5_project.R
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding

class GoogleLoginSingleton private constructor(activity: FragmentActivity) {

    companion object {
        private var instance: GoogleApiClient? = null

        fun getInstance(activity: FragmentActivity): GoogleApiClient {
            if (instance == null) {
                instance = createApiGoogle(activity)
            }
            return instance!!
        }

        private fun createApiGoogle(activity: FragmentActivity): GoogleApiClient {
            val dso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(activity, dso)
            return  GoogleApiClient.Builder(activity)
                .enableAutoManage(activity) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, dso)
                .build()
        }
    }
}