package com.example.nextrun.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nextrun.R
import com.example.nextrun.other.Constants
import com.example.nextrun.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.nextrun.other.Constants.REQUEST_CODE_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPref: SharedPreferences
    @set:Inject
    var isFirstAppOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        auth.signOut()

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment, R.id.feedFragment -> {
                        bottomNavigationView.visibility = View.VISIBLE
                        bottomAppBar.performShow()
                        fabMain.show()
                    }

                    else -> {
                        bottomNavigationView.visibility = View.INVISIBLE
                        bottomAppBar.performHide()
                        fabMain.hide()
                    }
                }
            }

        fabMain.setOnClickListener {
            findNavController(R.id.navHostFragment).navigate(R.id.action_global_trackingFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    fun registerUser(email:String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            group_loading_register.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                    Timber.d("Create user with email and password")
                    withContext(Dispatchers.Main) {
                        Timber.d("Create user with email and password - Coroutine Scope")
                        checkedLoggedInState()
                        group_loading_register.visibility = View.INVISIBLE
                    }
                } catch (e:Exception) {
                    withContext(Dispatchers.Main) {
                        Timber.d("Create user with email and password - Exception")
                        group_loading_register.visibility = View.INVISIBLE
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    fun loginUser(email: String, password: String){
        if (email.isNotEmpty() && password.isNotEmpty()) {
            group_loading_login.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    Timber.d("Sign in with email and password")
                    withContext(Dispatchers.Main) {
                        // Login
                        Timber.d("Sign in with email and password - Coroutine Scope - ${auth.currentUser?.displayName}")
                        group_loading_login.visibility = View.INVISIBLE
                        checkedLoggedInState()
                    }
                } catch (e:Exception) {
                    withContext(Dispatchers.Main) {
                        Timber.d("Sign in with email and password - Exception")
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                        group_loading_login.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun checkedLoggedInState(){
        if (auth.currentUser == null) {
//            Toast.makeText(this@MainActivity, "Error while processing login!", Toast.LENGTH_LONG).show()
            Timber.d("Error while processing login!")
        } else {
            if (writePersonalDataToSharedPref()) {
                Toast.makeText(this@MainActivity, "Login/register successfull!", Toast.LENGTH_LONG).show()
                findNavController(R.id.navHostFragment).navigate(R.id.action_global_runFragment)
            } else {
                Toast.makeText(this@MainActivity, "Erorr saving settings!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun googleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .requestProfile()
            .build()

        val signInClient = GoogleSignIn.getClient(this, options)
        signInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Successfully logged in!", Toast.LENGTH_LONG).show()
                    checkedLoggedInState()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {

        sharedPref.edit()
            .putString(Constants.KEY_NAME, auth.currentUser?.displayName ?: "")
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "${auth.currentUser?.displayName ?: "Let's go!"}"
        tvToolbarTitle.text = toolbarText
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkedLoggedInState()

//        bottomAppBar.performHide()
//        bottomNavigationView.visibility = View.INVISIBLE
//        fabMain.hide()
    }
}