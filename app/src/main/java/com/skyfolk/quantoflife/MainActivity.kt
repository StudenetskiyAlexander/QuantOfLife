package com.skyfolk.quantoflife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skyfolk.quantoflife.settings.SettingsInteractor
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private val settingsInteractor: SettingsInteractor by inject()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    settingsInteractor.showHidden = false
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    settingsInteractor.showHidden = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    settingsInteractor.showHidden = false
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Войдите с помощью биометрии")
            .setSubtitle("Квант Жизни")
            .setNegativeButtonText("Отмена")
            .build()

        biometricPrompt.authenticate(promptInfo)

//        if (!settingsInteractor.isOnboardingComplete) {
//            startActivity(Intent(baseContext, OnBoardingActivity::class.java))
//        }
    }
}