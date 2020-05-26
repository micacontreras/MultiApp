package com.example.serviceexam.login


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.serviceexam.R
import com.example.serviceexam.showDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetClient
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var myReCaptchaClient: SafetyNetClient? = null
    private var account: GoogleSignInAccount? = null
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInWithGoogle()
        createClientToReCaptcha()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInButton.setOnClickListener { signIn() }
        reCaptchaButton.setOnClickListener { validateCaptcha() }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(activity)

        if(checkSignOff()) signOff()
        account = GoogleSignIn.getLastSignedInAccount(activity)
    }

    override fun onResume() {
        super.onResume()
        if (account != null && !checkSignOff()) {
            val userSaved = checkCredentials()
            if(!userSaved.isNullOrEmpty()){
                findNavController().navigate(LoginFragmentDirections.navigateToMain())
            }
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_key_sign_in))
            .requestEmail()
            .build()
        //Sign in object
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            saveUser(completedTask.result?.givenName)
            reCaptchaButton.visibility = View.VISIBLE
        } catch (e: ApiException) {
            Log.e("failed code=", e.statusCode.toString())
            showDialog(requireContext(), "Error", e.statusCode.toString(), "Ok")
        }
    }

    fun checkCredentials(): String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return  sharedPref?.getString(getString(R.string.name), null)
    }

    private fun saveUser(name: String?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.name), name)
            apply()
        }
    }

    private fun createClientToReCaptcha() {
        myReCaptchaClient = SafetyNet.getClient(requireActivity())
    }

    private fun validateCaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(getString(R.string.my_site_key))
            .addOnSuccessListener(requireActivity()) { response ->
                val userResponseToken = response.tokenResult
                if (!userResponseToken.isNullOrEmpty()) {
                    // Validate the user response token using the reCAPTCHA siteverify API.
                    //No aplicable la verificacion con backend
                    Bundle().apply { putString("Hola", "HolaPrueba") }
                        .also { setFragmentResult("Hola", it) }
                    findNavController().navigate(LoginFragmentDirections.navigateToListRepositories())
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                if (e is ApiException) {
                    Log.d(TAG, "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    Log.d(TAG, "Error: ${e.message}")
                }
                showDialog(requireActivity(), "Error", e.message.toString(), "Ok")
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkBiometrics() {
        when (from(requireContext()).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> apply {
                Log.d("Sucess", "App can authenticate using biometrics.")
                showBiometricPrompt()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("Error", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("Error", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Log.e(
                    "Error", "The user hasn't associated any biometric credentials " +
                            "with their account."
                )
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int,
                                               errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "Authentication error.")
                requireActivity().runOnUiThread { Toast.makeText(requireContext(), "Error. Try again!", Toast.LENGTH_LONG).show() }
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                findNavController().navigate(LoginFragmentDirections.navigateToMain())
            }
        })
        biometricPrompt.authenticate(promptInfo)
    }

    fun checkSignOff(): Boolean {
        var signOff = false
        parentFragmentManager.setFragmentResultListener("IsSignOffSelect", this,
            FragmentResultListener { _, bundle ->
                bundle.getBoolean("SignOff").also {
                    signOff = it
                }
            })
        return signOff
    }

    private fun signOff() {
        mGoogleSignInClient?.signOut()
            ?.addOnCompleteListener {
                showDialog(
                    requireContext(),
                    "Sign off",
                    "To continue you need authenticate",
                    "Ok",
                    { revokeAccess() })
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient?.revokeAccess()
            ?.addOnCompleteListener {
                reCaptchaButton?.visibility = View.INVISIBLE
                val preferences: SharedPreferences? =
                    activity?.getPreferences(Context.MODE_PRIVATE)
                preferences?.edit()?.remove(getString(R.string.name))?.apply()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
