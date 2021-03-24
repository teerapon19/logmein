package com.npd.logmein.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.npd.logmein.AuthViewModel
import com.npd.logmein.R
import java.util.*

class SplashFragment : Fragment() {

    private var timeDelay = 0L
    private var time = 3000L

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        val actionToLogin = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        val actionToNavHome = SplashFragmentDirections.actionSplashFragmentToNavHome()

        authStateListener = FirebaseAuth.AuthStateListener {
            it.currentUser?.let { firebaseUser ->
                authViewModel.user.postValue(firebaseUser)
                navController.navigate(actionToNavHome)
            } ?: run {
                navController.navigate(actionToLogin)
            }
        }

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        }

    }

    override fun onResume() {
        super.onResume()
        timeDelay = time
        handler.postDelayed(runnable, timeDelay)
        time = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        time = timeDelay - (System.currentTimeMillis() - time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}