package com.osiel.gymflow.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.osiel.gymflow.databinding.ActivityLoginBinding
import com.osiel.gymflow.presentation.home.HomeActivity
import com.osiel.gymflow.presentation.viewmodel.AuthState
import com.osiel.gymflow.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (authViewModel.isUserLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        applyWindowInsets()
        setupListeners()
        setupDynamicValidation()
        observeAuthState()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            clearInputErrors()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.textInputLayoutPassword.error = "A senha deve ter no mínimo 6 caracteres"
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }

        binding.createAccountButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            clearInputErrors()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.textInputLayoutPassword.error = "A senha deve ter no mínimo 6 caracteres"
                return@setOnClickListener
            }

            authViewModel.register(email, password)
        }
    }

    private fun setupDynamicValidation() {
        // Validação dinâmica da senha
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                if (password.length >= 6) {
                    binding.textInputLayoutPassword.error = null
                } else if (password.isNotEmpty()) {
                    binding.textInputLayoutPassword.error = "A senha deve ter no mínimo 6 caracteres"
                } else {
                    binding.textInputLayoutPassword.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeAuthState() {
        lifecycleScope.launchWhenStarted {
            authViewModel.state.collectLatest { state ->
                when (state) {
                    is AuthState.Idle -> {
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Entrar"
                    }

                    is AuthState.Loading -> {
                        binding.loginButton.isEnabled = false
                        binding.loginButton.text = "Carregando..."
                    }

                    is AuthState.Success -> {
                        Toast.makeText(this@LoginActivity, "Login realizado!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }

                    is AuthState.Error -> {
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Entrar"

                        val message = state.message.lowercase()

                        if (message.contains("the supplied auth credential is incorrect, malformed or has expired.") || message.contains("no user record")) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Usuário ou senha incorretos",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.passwordEditText.text?.clear()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Erro: ${state.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        authViewModel.resetState()
                    }
                }
            }
        }
    }

    private fun clearInputErrors() {
        binding.textInputLayoutEmail.error = null
        binding.textInputLayoutPassword.error = null
    }
}
