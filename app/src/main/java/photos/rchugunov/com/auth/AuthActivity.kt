package photos.rchugunov.com.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_auth.*
import photos.rchugunov.com.R
import photos.rchugunov.com.ViewModelsFactory
import photos.rchugunov.com.main.MainActivity


class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, ViewModelsFactory).get(AuthViewModel::class.java)
        viewModel.initialize(this)
        viewModel.observeSignInResult().observe(this, Observer { startMainActivity() })

        buttonSignIn.setOnClickListener {
            val signInIntent = viewModel.signInClient()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.checkSignIn()
    }

    private fun startMainActivity() {
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.handleSignInResult(task)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }
}