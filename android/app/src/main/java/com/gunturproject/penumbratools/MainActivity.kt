/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 5L0P-F1NG3RPR1NT: AI-assisted Android wrapper scaffold for the user's fork.
 */
package com.gunturproject.penumbratools

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var output: TextView
    private lateinit var usbManager: UsbManager
    private lateinit var binary: File
    private val permissionAction = "com.gunturproject.penumbratools.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != permissionAction) return
            val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
            append(if (granted) "USB permission granted" else "USB permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = getSystemService(USB_SERVICE) as UsbManager
        binary = installBinary()
        ContextCompat.registerReceiver(
            this,
            usbReceiver,
            IntentFilter(permissionAction),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        setContentView(buildView())
        append("Penumbra Tools ready")
        append("Binary: ${binary.absolutePath}")
        append("Root: tap Check root")
    }

    private fun buildView(): ScrollView {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        val title = TextView(this).apply {
            text = "Penumbra Tools\nMediaTek Android wrapper"
            textSize = 22f
        }
        output = TextView(this).apply { textSize = 14f }
        root.addView(title)
        root.addView(button("List USB devices") { listUsb() })
        root.addView(button("Request USB permission") { requestUsbPermission() })
        root.addView(button("Check root") { runRoot("id") })
        root.addView(button("Run antumbra --help") { runRoot("--help") })
        root.addView(output)
        return ScrollView(this).apply { addView(root) }
    }

    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        setOnClickListener { action() }
    }

    private fun listUsb() {
        val devices = usbManager.deviceList.values
        append("USB devices: ${devices.size}")
        devices.forEach { append("- ${it.deviceName} VID=${it.vendorId} PID=${it.productId}") }
    }

    private fun requestUsbPermission() {
        val device = usbManager.deviceList.values.firstOrNull()
        if (device == null) {
            append("No USB device detected; connect the phone in USB host/OTG mode")
            return
        }
        val pending = PendingIntent.getBroadcast(
            this, 0, Intent(permissionAction).setPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(device, pending)
        append("Requested permission for ${device.deviceName}")
    }

    private fun runRoot(arguments: String) {
        executor.execute {
            val command = "${binary.absolutePath} $arguments"
            try {
                val process = ProcessBuilder("su", "-c", command)
                    .redirectErrorStream(true)
                    .start()
                val text = process.inputStream.bufferedReader().use { it.readText() }
                val code = process.waitFor()
                runOnUiThread { append("$ $command\n$text(exit=$code)") }
            } catch (error: Exception) {
                runOnUiThread { append("Root execution failed: ${error.message}") }
            }
        }
    }

    private fun installBinary(): File {
        val destination = File(filesDir, "antumbra")
        if (!destination.exists()) {
            assets.open("bin/arm64-v8a/antumbra").use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            destination.setExecutable(true, false)
        }
        return destination
    }

    private fun append(line: String) {
        if (::output.isInitialized) output.append("\n$line")
    }

    override fun onDestroy() {
        unregisterReceiver(usbReceiver)
        executor.shutdownNow()
        super.onDestroy()
    }
}
