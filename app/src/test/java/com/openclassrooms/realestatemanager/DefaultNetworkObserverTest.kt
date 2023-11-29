package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.utils.network.DefaultNetworkObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP], manifest = "src/main/AndroidManifest.xml")
class DefaultNetworkObserverTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var defaultNetworkObserver: DefaultNetworkObserver

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        defaultNetworkObserver = DefaultNetworkObserver(context)
    }

    @Test
    fun isConnectedShouldReturnTrueWhenNetworkAvailable() = runTest {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        shadowOf(connectivityManager).setActiveNetworkInfo(connectivityManager.activeNetworkInfo)

        assertTrue(defaultNetworkObserver.isConnected.first())
    }

    @Test
    fun isConnectedShouldReturnFalseWhenNetworkNotAvailable()  = runTest {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        shadowOf(connectivityManager).setActiveNetworkInfo(null)

        assertFalse(defaultNetworkObserver.isConnected.first())
    }
}
