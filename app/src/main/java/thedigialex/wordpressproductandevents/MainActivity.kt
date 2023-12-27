package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.content.Intent
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var signUp = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun showSignFormLayout(view: View) {
        if(view.id == R.id.signupButton){
            signUp = true
        }
        val entryButtonLayout = findViewById<ConstraintLayout>(R.id.entryButtonLayout)
        entryButtonLayout.visibility = View.GONE
        setUpFormLayout()
    }
    fun changeSignFormLayout(view: View){
        signUp = !signUp
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
        Toast.makeText(this, "GOOD", Toast.LENGTH_SHORT).show()
        if(!signUp){
            logInUser(editTextEmail.text.toString(), editTextPassword.text.toString())
        }
    }
    private fun logInUser(username: String, password: String) {
        val signFormLayout = findViewById<ConstraintLayout>(R.id.signformLayout)
        signFormLayout.visibility = View.GONE
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loadingLayout)
        loadingLayout.visibility = View.VISIBLE
        val context = applicationContext
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val apiHost = "https://honey.thedigialex.net/wp-json"
        val authEndpoint = "$apiHost/jwt-auth/v1/token"

        val params = JSONObject()
        params.put("username", username)
        params.put("password", password)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, authEndpoint, params,
            { response ->
                loadingLayout.visibility = View.GONE
                signFormLayout.visibility = View.VISIBLE
                val token = response.getString("token")
                val sharedPrefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("jwt_token", token).apply()
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            },
            { error ->
                loadingLayout.visibility = View.GONE
                signFormLayout.visibility = View.VISIBLE
                Toast.makeText(context, "Authentication failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

}