package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.CircleViewModel
import com.example.ui.theme.OrbynTheme

@Composable
fun AuthScreen(
    viewModel: CircleViewModel,
    modifier: Modifier = Modifier
) {
    val colors = OrbynTheme.colors
    val colorScheme = MaterialTheme.colorScheme

    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("alex@circle.app") }
    var password by remember { mutableStateOf("••••••••") }
    var name by remember { mutableStateOf("Alex Vance") }
    var username by remember { mutableStateOf("alexvance") }

    val avatarPresets = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80"
    )
    var selectedAvatar by remember { mutableStateOf(avatarPresets[0]) }

    val authInputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.primaryText,
        unfocusedTextColor = colors.primaryText,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colors.secondaryText,
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colors.divider,
        cursorColor = colorScheme.primary,
        focusedContainerColor = colors.cardSurface,
        unfocusedContainerColor = colors.cardSurface
    )
    val authTextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = colors.primaryText,
        fontWeight = FontWeight.Medium
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_circle_logo),
                contentDescription = "ORBYN Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(68.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "orbyn",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.2).sp,
                        fontSize = 34.sp
                    ),
                    color = colors.primaryText
                )
                Text(
                    text = ".",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 34.sp
                    ),
                    color = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your inner orbit. Less noise. Closer circle.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = colors.secondaryText
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Google One-Tap Sign In Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.cardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { viewModel.login("alex@gmail.com", "alexvance") }
                    .testTag("google_login_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = colors.primaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = colors.divider)
                Text(
                    text = " or with email ",
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.secondaryText)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = colors.divider)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSignUp) {
                Text(
                    text = "Choose your avatar",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.secondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(avatarPresets) { url ->
                        val isChosen = selectedAvatar == url
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isChosen) 2.5.dp else 1.dp,
                                    color = if (isChosen) colorScheme.primary else colors.divider,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = url }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = colors.secondaryText) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                    shape = RoundedCornerShape(11.dp),
                    colors = authInputColors,
                    textStyle = authTextStyle,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Text(
                            "@",
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("auth_username_input"),
                    shape = RoundedCornerShape(11.dp),
                    colors = authInputColors,
                    textStyle = authTextStyle,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = colors.secondaryText) },
                modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                shape = RoundedCornerShape(11.dp),
                colors = authInputColors,
                textStyle = authTextStyle,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colors.secondaryText) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                shape = RoundedCornerShape(11.dp),
                colors = authInputColors,
                textStyle = authTextStyle,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login(email, if (isSignUp) username else "alexvance") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(11.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_auth_button")
            ) {
                Text(
                    text = if (isSignUp) "Join Orbyn" else "Enter Orbyn",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { isSignUp = !isSignUp },
                modifier = Modifier.testTag("toggle_auth_mode_button")
            ) {
                Text(
                    text = if (isSignUp) "Already have an account? Sign in" else "New here? Set up your orbit",
                    color = colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}
