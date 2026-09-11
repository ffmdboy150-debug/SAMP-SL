package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SA-MP Launcher", appName)
  }

  @Test
  fun `verify hardcoded single server configuration`() {
    assertEquals("[SERVER_NAME]", com.example.data.ServerConfig.SERVER_NAME)
    assertEquals("[SERVER_IP]:[PORT]", com.example.data.ServerConfig.SERVER_DISPLAY)
    assertEquals("[FILE_HOST_URL]", com.example.data.ServerConfig.FILE_HOST_URL)
  }
}
