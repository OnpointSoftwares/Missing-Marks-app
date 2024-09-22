package nt.vn.missingmarks

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
if(auth.uid != "")
{
    startActivity(Intent(this, MainActivity2::class.java))
}
        // Get references to UI elements
        val emailEditText = findViewById<EditText>(R.id.emailField)
        val passwordEditText = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val regBtn=findViewById<Button>(R.id.registerButton)
        val pb:ProgressBar=findViewById(R.id.progress)
        // Handle login button click
        regBtn.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
        loginButton.setOnClickListener {
            pb.visibility= View.VISIBLE
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    // Function to login the user with email and password
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, navigate to main activity
                    val user = auth.currentUser
                    Toast.makeText(this, "Login successful: ${user?.email}", Toast.LENGTH_SHORT).show()
                    findViewById<ProgressBar>(R.id.progress).visibility= View.GONE
                    startActivity(Intent(this, MainActivity2::class.java))
                    finish() // Prevent going back to the login screen
                } else {
                    // Login failed, display error message
                    Log.e("LoginActivity", "Login failed", task.exception)
                    findViewById<ProgressBar>(R.id.progress).visibility= View.GONE
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
