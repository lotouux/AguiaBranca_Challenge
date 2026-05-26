package com.example.aguiabrancachallenge

import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.repository.AuthRepository
import com.example.aguiabrancachallenge.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialLoginScreen(
    profile: String = "Operador",
    authRepository: AuthRepository,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    var matricula by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Define os dados baseados no perfil escolhido
    val iconRes = when (profile) {
        "Gestor" -> R.drawable.ic_maleta
        "Liderança" -> R.drawable.ic_empresa
        else -> R.drawable.ic_onibus
    }

    val demoMatricula = when (profile) {
        "Gestor" -> "GS001"
        "Liderança" -> "LD001"
        else -> "OP001"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 32.dp)
    ) {

        // Botão voltar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cabeçalho
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login $profile",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Insira suas credenciais para continuar",
                color = Color.Gray,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Matrícula
        Text(
            "Matrícula",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Ex: $demoMatricula",
                    color = Color.DarkGray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Senha
        Text(
            "Senha",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "••••••••",
                    color = Color.DarkGray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {

                TextButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {

                    Text(
                        if (passwordVisible)
                            "Ocultar"
                        else
                            "Mostrar"
                    )
                }
            },
            singleLine = true,
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Mensagem de erro
        errorMessage?.let {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .clickable {
                    matricula = demoMatricula
                    senha = "123"
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold)) {
                        append("Demo: ")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(.7f))) {
                        append("Matrícula: $demoMatricula | Senha: 123")
                    }
                },
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botão login
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    val result =
                        authRepository.signIn(
                            matricula = matricula,
                            password = senha,
                            perfil = profile
                        )

                    result.fold(
                        onSuccess = {
                            onLoginClick()
                        },
                        onFailure = {
                                error ->
                            errorMessage = when (error) {

                                is HttpException -> {
                                    when (error.code()) {
                                        401 -> "Senha incorreta ou não autorizado"
                                        404 -> "Usuário não encontrado"
                                        500 -> "Erro no servidor. Tente novamente"
                                        502, 503 -> "Serviço indisponível no momento"
                                        else -> "Erro inesperado (${error.code()})"
                                    }
                                }

                                else -> {
                                    "Erro de conexão. Verifique sua internet"
                                }
                            }
                        }
                    )

                    isLoading = false
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            enabled =
                matricula.isNotEmpty()
                        && senha.isNotEmpty()
                        && !isLoading,

            shape = RoundedCornerShape(16.dp)
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Entrar na Plataforma",
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CredentialPreview() {
    AguiaBrancaChallengeTheme {

        CredentialLoginScreen(
            profile = "Gestor",
            authRepository = TODO(),
            onBackClick = {},
            onLoginClick = {}
        )
    }
}