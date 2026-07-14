package com.example.aguiabrancachallenge

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.repository.AuthRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CredentialLoginScreen(
    profile: String = "Operador",
    authRepository: AuthRepository,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var chaveRede by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val demoChave = when (profile) {
        "Gestor" -> "GS001"
        "Liderança" -> "LD001"
        else -> "OP001"
    }

    val alphaAnim = remember { Animatable(0f) }
    val translateYAnim = remember { Animatable(80f) }

    LaunchedEffect(Unit) {
        delay(50)
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            translateYAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp)
            .graphicsLayer {
                alpha = alphaAnim.value
                translationY = translateYAnim.value
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() }
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voltar",
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "02 / 02",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    chaveRede = demoChave
                    senha = "123"
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "ACESSO - ${profile.uppercase()}",
            color = Color(0xFF666666),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Entrar",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Use sua chave de rede corporativa e senha\nfornecidas pela empresa.",
            color = Color(0xFFAAAAAA),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Chave de rede",
            color = Color(0xFFDDDDDD),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        MinimalistInputField(
            value = chaveRede,
            onValueChange = { chaveRede = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Senha",
            color = Color(0xFFDDDDDD),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        MinimalistInputField(
            value = senha,
            onValueChange = { senha = it },
            isPassword = true,
            passwordVisible = passwordVisible,
            onVisibilityChange = { passwordVisible = !passwordVisible },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Esqueci minha senha",
            color = Color(0xFFAAAAAA),
            fontSize = 12.sp,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Ação futura */ }
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = it,
                color = Color(0xFFE57373),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val canLogin = chaveRede.isNotEmpty() && senha.isNotEmpty() && !isLoading
        val buttonBgColor by animateColorAsState(
            targetValue = if (canLogin) Color.White else Color(0xFF1A1A1A),
            animationSpec = tween(300),
            label = "buttonBg"
        )
        val buttonTextColor by animateColorAsState(
            targetValue = if (canLogin) Color.Black else Color(0xFF555555),
            animationSpec = tween(300),
            label = "buttonText"
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    val result = authRepository.signIn(
                        matricula = chaveRede,
                        password = senha,
                        perfil = profile
                    )

                    result.fold(
                        onSuccess = { onLoginClick() },
                        onFailure = { error ->
                            errorMessage = when (error.message) {
                                "401" -> "Senha incorreta ou não autorizado."
                                "404" -> "Usuário não encontrado."
                                else -> "Erro ao conectar. Verifique suas credenciais."
                            }
                        }
                    )
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBgColor,
                disabledContainerColor = Color(0xFF1A1A1A)
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = canLogin
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoading) "Autenticando..." else "Entrar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttonTextColor
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (canLogin) Color(0xFFE5E5E5) else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continuar",
                            tint = buttonTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Problemas com o acesso? Contate o suporte de TI.",
            color = Color(0xFF555555),
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MinimalistInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    keyboardOptions: KeyboardOptions
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(Color.White),
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    if (isPassword) {
                        Text(
                            text = if (passwordVisible) "Ocultar" else "Mostrar",
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onVisibilityChange() }
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFF222222), thickness = 1.dp)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CredentialPreview() {
    AguiaBrancaChallengeTheme {
        val context = androidx.compose.ui.platform.LocalContext.current
        val dummyPrefs = context.getSharedPreferences("preview_prefs", android.content.Context.MODE_PRIVATE)

        CredentialLoginScreen(
            profile = "Operador",
            authRepository = AuthRepository(dummyPrefs),
            onBackClick = {},
            onLoginClick = {}
        )
    }
}