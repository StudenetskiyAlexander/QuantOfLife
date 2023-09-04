package com.skyfolk.quantoflife

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skyfolk.quantoflife.settings.SettingsInteractor
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

private const val TIME_BETWEEN_LOGIN = 5 * 60 * 1000

class MainActivity : AppCompatActivity() {
    private val settingsInteractor: SettingsInteractor by inject()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt : BiometricPrompt

    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Войдите с помощью биометрии")
        .setSubtitle("Квант Жизни")
        .setNegativeButtonText("Отмена")
        .build()

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
                    settingsInteractor.lastLoginTime = System.currentTimeMillis()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    settingsInteractor.showHidden = false
                }
            })
        biometricPrompt.authenticate(promptInfo)
//        if (!settingsInteractor.isOnboardingComplete) {
//            startActivity(Intent(baseContext, OnBoardingActivity::class.java))
//        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("skyfolk-bio", "onResume: ${System.currentTimeMillis() - settingsInteractor.lastLoginTime}")
        if (settingsInteractor.showHidden && System.currentTimeMillis() - settingsInteractor.lastLoginTime > TIME_BETWEEN_LOGIN) {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}