package com.example.travellog.presentation.country

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travellog.BuildConfig
import com.example.travellog.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun CountryScreen() {
    if (!BuildConfig.USE_STUB &&
        (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_PUBLISHABLE_KEY.isBlank())
    ) {
        Scaffold { padding ->
            Text(stringResource(R.string.config_missing), Modifier.padding(padding).padding(16.dp))
        }
    } else {
        CountryRoute()
    }
}

@Composable
private fun CountryRoute(viewModel: CountryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.countries_title), style = MaterialTheme.typography.headlineMedium)
            if (BuildConfig.USE_STUB) {
                Text(
                    stringResource(R.string.stub_active),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(12.dp),
                )
            }
            when (val current = state) {
                CountryUiState.Loading -> CircularProgressIndicator()
                is CountryUiState.Login -> {
                    Text(stringResource(R.string.login_hint))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (current.error) {
                        Text(stringResource(R.string.login_error), color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        enabled = email.isNotBlank() && password.isNotEmpty(),
                        onClick = {
                            viewModel.login(email, password)
                            password = ""
                        },
                    ) { Text(stringResource(R.string.login)) }
                }
                is CountryUiState.Countries -> {
                    if (!BuildConfig.USE_STUB) {
                        TextButton(onClick = viewModel::logout) { Text(stringResource(R.string.logout)) }
                    }
                    if (current.items.isEmpty()) {
                        Text(stringResource(R.string.countries_empty))
                    } else {
                        LazyColumn {
                            items(current.items, key = { it.id }) { country ->
                                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                    Text(country.nameJa, style = MaterialTheme.typography.titleMedium)
                                    Text("${country.name} (${country.code})")
                                }
                            }
                        }
                    }
                }
                is CountryUiState.Error -> {
                    Text(stringResource(R.string.request_error), color = MaterialTheme.colorScheme.error)
                    Button(onClick = {
                        when (current.action) {
                            RetryAction.SESSION -> viewModel.restoreSession()
                            RetryAction.COUNTRIES -> viewModel.refreshCountries()
                            RetryAction.LOGOUT -> viewModel.logout()
                        }
                    }) { Text(stringResource(R.string.retry)) }
                    if (!BuildConfig.USE_STUB && current.action == RetryAction.COUNTRIES) {
                        TextButton(onClick = viewModel::logout) { Text(stringResource(R.string.logout)) }
                    }
                }
            }
        }
    }
}
