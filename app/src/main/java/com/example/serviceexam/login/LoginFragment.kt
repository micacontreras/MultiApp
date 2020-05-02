package com.example.serviceexam.login


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
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
class LoginFragment : Fragment() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private var myReCaptchaClient: SafetyNetClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SignInWithGoogle()
        createClientToReCaptcha()
    }

    private fun createClientToReCaptcha() {
        myReCaptchaClient = SafetyNet.getClient(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInButton.setOnClickListener { signIn() }
        reCaptchaButton.setOnClickListener { validateCaptcha() }
    }

    private fun validateCaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(getString(R.string.my_site_key))
            .addOnSuccessListener(requireActivity()) { response ->
                val userResponseToken = response.tokenResult
                if (!userResponseToken.isNullOrEmpty()) {
                    // Validate the user response token using the reCAPTCHA siteverify API.
                    //No aplicable la verificacion con backend
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

    private fun SignInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_key_sign_in))
            .requestEmail()
            .build()
        //Sign in object
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(activity)

        if (account != null) {
            signInButton.isEnabled = false
            reCaptchaButton.visibility = View.VISIBLE
            reCaptchaButton.isChecked =false
            showDialog(
                requireContext(),
                "",
                "Se ha registrado exitosamente. Por favor verifique que es humano para continuar.",
                "Ok"
            )
        }
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
            reCaptchaButton.visibility = View.VISIBLE
        } catch (e: ApiException) {
            Log.e("failed code=", e.statusCode.toString())
            showDialog(requireContext(), "Error", e.statusCode.toString(), "Ok")
        }
    }
}