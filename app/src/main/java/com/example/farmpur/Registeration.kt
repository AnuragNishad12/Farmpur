package com.example.farmpur

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable

fun RegistrationActivity(navController: NavHostController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8C471))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp) // Add padding at bottom for better scroll experience
        ) {
            Image(
                painter = painterResource(id = R.drawable.signup),
                contentDescription = "Alt",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                var name by remember { mutableStateOf(TextFieldValue("")) }
                var email by remember { mutableStateOf(TextFieldValue("")) }
                var password by remember { mutableStateOf(TextFieldValue("")) }
                var selectedOption by remember { mutableStateOf("") }
                var showError by remember { mutableStateOf(false) }
                var loading by remember { mutableStateOf(false) }

                Text(text = "Full Name", modifier = Modifier.align(Alignment.Start))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = name,
                    onValueChange = { newText -> name = newText },
                    placeholder = { Text("Your Name") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Email", modifier = Modifier.align(Alignment.Start))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = email,
                    onValueChange = { newEmail -> email = newEmail },
                    placeholder = { Text("Your Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Password", modifier = Modifier.align(Alignment.Start))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = password,
                    onValueChange = { newPassword -> password = newPassword },
                    placeholder = { Text("Your password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Select Your Registration Type",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = selectedOption == "Customer",
                        onClick = {
                            selectedOption = "Customer"
                            showError = false
                        }
                    )
                    Text(text = "Customer", modifier = Modifier.padding(start = 8.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == "Farmer",
                        onClick = {
                            selectedOption = "Farmer"
                            showError = false
                        }
                    )
                    Text(text = "Farmer", modifier = Modifier.padding(start = 8.dp))
                }

                Button(
                    onClick = {
                        showError = name.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty() || selectedOption.isEmpty()
                        if (!showError) {
                            loading = true
                            keyboardController?.hide()
                            focusManager.clearFocus()

                            // Authenticate and store data
                            val auth = FirebaseAuth.getInstance()
                            auth.createUserWithEmailAndPassword(email.text, password.text)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // User authentication successful
                                        val userId = auth.currentUser?.uid
                                        val database = FirebaseDatabase.getInstance().reference

                                        // Create a user map to store in the database
                                        val userMap = mapOf(
                                            "name" to name.text,
                                            "email" to email.text,
                                            "password" to password.text,
                                            "registrationType" to selectedOption
                                        )
                                        // Store user information in the database with their UID
                                        userId?.let {
                                            database.child("FarmPurUsers").child(it).setValue(userMap)
                                                .addOnCompleteListener { dbTask ->
                                                    if (dbTask.isSuccessful) {
                                                        loading = false
                                                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                                        if (selectedOption == "Customer") {
                                                            navController.navigate("customerScreen")
                                                        } else if (selectedOption == "Farmer") {
                                                            navController.navigate("farmerScreen")
                                                        }
                                                    } else {
                                                        loading = false
                                                        Toast.makeText(context, "Failed to store user data.", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                        }
                                    } else {
                                        loading = false
                                        showError = true
                                        val errorMessage = task.exception?.localizedMessage ?: "Authentication failed."
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = "Register")
                }

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }

                if (showError) {
                    Text(
                        text = "Please fill in all fields and select an option to continue.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Already Have Account?")
                    TextButton(onClick = { }) {
                        Text(text = "LOG IN")
                    }
                }
            }
        }
    }
}


