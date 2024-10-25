package com.example.farmpur

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun GetStartedActivity(){

   Box(modifier = Modifier
       .fillMaxSize()
       .background(Color(0xFFF8C471))){
       Column (modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally){

           Text(text = "Welcome To Farmpur", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
           Image(painter = painterResource(id = R.drawable.getstarted), contentDescription ="Alt")
           Button(onClick = { /*TODO*/ }) {
               Text(text = "Get Started", fontSize = 30.sp)
               
           }
       }
       
       
   }


}