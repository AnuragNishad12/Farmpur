package com.example.farmpur

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@Composable
fun LoginActivity(navController: NavController){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var showError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    auth.currentUser?.let { currentUser ->
        fetchUserData(currentUser.uid, navController, context)
    }

    BackHandler {
        if (navController.previousBackStackEntry == null) {
            (context as? Activity)?.finish() // Close the app if at the root screen
        } else {
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier
        .fillMaxHeight()
        .background(color = Color(0xFFF8C471))){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 16.dp) // Add padding at bottom for better scroll experience
        ){
            Image(
                painter = painterResource(id = R.drawable.signup),
                contentDescription = "Alt",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))
            Column( modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

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
                 Spacer(modifier = Modifier.height(10.dp))
                Button( onClick = {
                    showError = email.text.isEmpty() || password.text.isEmpty()
                    if (!showError) {
                        loading = true
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        loginUser(email.text, password.text, navController, context)
                    }
                }) {
                    Text(text = "Login")
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
                    Text(text = "Don't Have Account ?")
                    TextButton(onClick = { navController.navigate("registrationScreen")}) {
                        Text(text = "SIGN UP")
                    }
                }



            }

        }
    }
}


fun loginUser(
    email: String,
    password: String,
    navController: NavController,
    context: Context
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    fetchUserData(userId, navController, context)
                }
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
fun fetchUserData(
    userId: String,
    navController: NavController,
    context: Context
) {
    val database = FirebaseDatabase.getInstance().getReference("FarmPurUsers/$userId")
    database.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val registrationType = snapshot.child("registrationType").value.toString()
            navigateBasedOnRegistrationType(registrationType, navController)
        } else {
            Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to load user data: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}

fun navigateBasedOnRegistrationType(
    registrationType: String,
    navController: NavController
) {
    when (registrationType) {
        "Farmer" -> navController.navigate("farmerScreen") {
            popUpTo("login") { inclusive = true }
        }
        "customer" -> navController.navigate("customer") {
            popUpTo("login") { inclusive = true }
        }
        else -> {
            Toast.makeText(navController.context, "Unknown registration type: $registrationType", Toast.LENGTH_SHORT).show()
        }
    }
}