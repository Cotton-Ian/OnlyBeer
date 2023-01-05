package mobg5.g55019.mobg5_project.screen.login

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
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import mobg5.g55019.mobg5_project.R
import mobg5.g55019.mobg5_project.databinding.FragmentLoginBinding


/**
 * A Fragment that allows the user to log in to the app using their email and password.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val auth = FirebaseAuth.getInstance()
    private lateinit var mGoogleAPIClient: GoogleApiClient
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private companion object {
        private const val RC_SIGN_IN = 9001
    }


    /**
     * Sets up the layout of the fragment and sets up listeners for the login button and the "Create an account" text view.
     * If the login is successful, the user is redirected to the swipe fragment. If there is an error, a toast message
     * with the error message is displayed. If the user is already logged in, they are redirected to the swipe fragment.
     *
     * @param inflater the LayoutInflater object that can be used to inflate any views in the fragment
     * @param container the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the saved state of the fragment
     *
     * @return the View for the fragment's UI
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.pwdField.text.toString()
            if (email != "" && password != "") {
                viewModel.login(email, password, auth)
            } else {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleButton.setOnClickListener {
            signIn()
        }

        setUpGoogleButtonVisual()

        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                Toast.makeText(context, "Login!", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isAccountGoogleCreated.observe(viewLifecycleOwner) { isAccountGoogleCreated ->
            if (isAccountGoogleCreated) {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
            }
        }


        binding.inscriptionTextview.text = viewModel.setTextView()
        binding.inscriptionTextview.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }


    private fun setUpGoogleButtonVisual(){
        for (i in 0 until binding.googleButton.childCount) {
            val v: View = binding.googleButton.getChildAt(i)
            if (v is TextView) {
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.google_button)
                v.background = drawable
            }
        }
    }


    /**
     * If the user is already logged in, they are redirected to the swipe fragment.
     */
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_swipeFragment)
        }
    }

    /**
     * This method is called when the activity is first created. It initializes the Google Sign-In client and
     * Google API client with the specified options.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeGoogleSignIn()
    }

    private fun initializeGoogleSignIn(){
        try {
            val dso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), dso)
            mGoogleAPIClient = GoogleApiClient.Builder(requireActivity())
                .enableAutoManage(requireActivity()) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, dso)
                .build()

        }
        catch (e: Exception){
            Log.e("LoginFragment", "Error: $e")
            if(::mGoogleAPIClient.isInitialized  && ::mGoogleSignInClient.isInitialized) {
                activity?.let { mGoogleAPIClient.stopAutoManage(it) }
                mGoogleAPIClient.disconnect()
            }
        }
    }


    /**
     * This method starts the process of signing in with Google.
     */
    private fun signIn() {
        if(::mGoogleAPIClient.isInitialized  && ::mGoogleSignInClient.isInitialized) {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleAPIClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        else{
            initializeGoogleSignIn()
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.let { mGoogleAPIClient.stopAutoManage(it) }
        mGoogleAPIClient.disconnect()
    }

    /**
     * This method is called when an activity you launched exits, giving you the requestCode
     * you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     *
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be
     * attached to Intent "extras").
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    /**
     * This method authenticates the user with Firebase using the provided Google sign-in account.
     *
     * @param acct The Google sign-in account to use for authentication.
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(context, "Authentication réussie.", Toast.LENGTH_SHORT).show()
                    Log.d("googleLoginTest", "signInWithCredential:success")
                    auth.currentUser
                    viewModel.addInDbForBeer(auth)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()

                }
            }
    }

}