package com.npd.logmein.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.npd.logmein.AuthViewModel
import com.npd.logmein.R
import com.npd.logmein.ui.splash.SplashFragmentDirections

class LoginFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 850
        fun newInstance() = LoginFragment()
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var mView: View
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_login, container, false)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        initGetDataFromTextField()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun initGetDataFromTextField() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        val actionToNavHome = LoginFragmentDirections.actionLoginFragmentToNavHome()

        authViewModel.user.observe(requireActivity(), Observer {
            Log.d("Login Result", it.email ?: "no email")
            it?.let {
                navController.navigate(actionToNavHome)
            }
        })
        mView.findViewById<AppCompatButton>(R.id.login_button).setOnClickListener {
            signIn()
        }
        mView.findViewById<SignInButton>(R.id.sign_in_google_button).setOnClickListener {
            googleSingIn()
        }
    }

    private fun signIn() {
        authViewModel.onSignInWithEmailAndPassword(requireActivity(),
                mView.findViewById<TextInputEditText>(R.id.email_text_field).text.toString(),
                mView.findViewById<TextInputEditText>(R.id.password_text_field).text.toString()
        )
    }

    private fun googleSingIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Google login", "firebaseAuthWithGoogle:" + account.id)
                authViewModel.firebaseAuthWithGoogle(requireActivity(), account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google login", "Google sign in failed", e)
            }
        }
    }

}