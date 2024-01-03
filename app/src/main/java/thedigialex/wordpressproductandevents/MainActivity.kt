package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private var signUp = false
    private lateinit var accountDao: AccountDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = AppDatabase.getDatabase(this)
        accountDao = db.accountDao()
        checkLoggedInAccount()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun checkLoggedInAccount() = GlobalScope.launch(Dispatchers.IO) {
        accountDao.getAllAccounts().find { it.loggedIn }?.let {
            withContext(Dispatchers.Main) { sendToDashboard(it.username) }
        }
    }
    private fun sendToDashboard(username: String) {
        startActivity(Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("USERNAME", username)
        })
    }
    fun showSignFormLayout(view: View) {
        signUp = when (view.id) {
            R.id.signupButton -> true
            R.id.loginButton -> false
            else -> !signUp
        }
        if (view.id == R.id.signupButton || view.id == R.id.loginButton) {
            findViewById<ConstraintLayout>(R.id.entryButtonLayout).visibility = View.INVISIBLE
        }
        setUpFormLayout()
    }
    private fun setUpFormLayout(){
        val signFormLayout = findViewById<ConstraintLayout>(R.id.signformLayout)
        signFormLayout.visibility = View.VISIBLE
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val changeFormLayout = findViewById<Button>(R.id.changeFormLayout)
        editTextName.visibility = View.GONE
        changeFormLayout.text = "Sign Up"
        if(signUp){
            editTextName.visibility = View.VISIBLE
            changeFormLayout.text = "Login"
        }
    }
    fun signUpOrLogIn(view: View) {
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        if (signUp && editTextName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (editTextEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (editTextPassword.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if(!signUp){
            logInUser(editTextEmail.text.toString(), editTextPassword.text.toString())
        }
    }
    private fun logInUser(username: String, password: String) {
        val signFormLayout = findViewById<ConstraintLayout>(R.id.signformLayout).apply { visibility = View.INVISIBLE }
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loadingLayout).apply { visibility = View.VISIBLE }

        val context = applicationContext
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val apiHost = getString(R.string.website_url) + "/wp-json"
        val authEndpoint = "$apiHost/jwt-auth/v1/token"

        val params = JSONObject()
        params.put("username", username)
        params.put("password", password)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, authEndpoint, params,
            { response ->
                val responseString = response.toString()
                Log.d("Response", responseString)
                val userName = response.optString("first_name", "")
                loadingLayout.visibility = View.INVISIBLE
                signFormLayout.visibility = View.VISIBLE
                val token = response.getString("token")
                val sharedPrefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("jwt_token", token).apply()
                val newAccount = Account(username = username, token = token, name = userName, loggedIn = true)
                CoroutineScope(Dispatchers.IO).launch {
                    val existingAccount = accountDao.findAccountByUsername(username)
                    if (existingAccount == null) {
                        accountDao.insert(newAccount)
                    } else {
                        existingAccount.loggedIn = true
                        existingAccount.token = token
                        accountDao.update(existingAccount)
                    }
                    withContext(Dispatchers.Main) {
                        sendToDashboard(username)
                    }
                }
            },
            { error ->
                loadingLayout.visibility = View.INVISIBLE
                signFormLayout.visibility = View.VISIBLE
                Toast.makeText(context, "Authentication failed: ${error.message}", Toast.LENGTH_LONG).show()
            }

        )
        queue.add(jsonObjectRequest)
    }
}