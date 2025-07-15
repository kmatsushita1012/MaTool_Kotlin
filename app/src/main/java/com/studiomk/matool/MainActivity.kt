package com.studiomk.matool

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.studiomk.matool.core.theme.AppTheme
import com.studiomk.matool.di.*
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.di.DefaultValues
import com.studiomk.matool.presentation.store_view.app.home.Home
import com.studiomk.matool.presentation.store_view.app.home.HomeStoreView
import com.studiomk.matool.presentation.store_view.app.onboarding.Onboarding
import com.studiomk.matool.presentation.store_view.app.onboarding.OnboardingStoreView
import com.studiomk.ktca.core.store.Store
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import java.util.Locale
import kotlin.getValue

class MainActivity : ComponentActivity(), KoinComponent {
    private val localStore: LocalStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Locale.setDefault(Locale.JAPAN)

        startKoin {
            androidContext(this@MainActivity)
            modules(liveAppModule)
        }

        //Update
        val appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlow(
                    appUpdateInfo,
                    this, // Activity
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }

        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(com.studiomk.matool.App.context)

        setContent {
            AppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val hasLaunchedBefore = localStore.getBoolean(DefaultValues.HAS_LAUNCHED_BEFORE)
                    var hasSet by remember { mutableStateOf(false) }

                    if (hasLaunchedBefore || hasSet) {
                        val store = Store<Home.State, Home.Action>(Home.State(), Home,)
                        HomeStoreView(store)
                    } else {
                        val store = Store(
                            Onboarding.State(
                                onSet = {
                                    hasSet = true 
                                }
                            ),
                            Onboarding
                        )
                        OnboardingStoreView(store)
                    }
                }
            }
        }
    }
}
