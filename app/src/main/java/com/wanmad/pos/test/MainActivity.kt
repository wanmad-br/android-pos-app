package com.wanmad.pos.test

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

class MainActivity : AppCompatActivity() {
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        output = findViewById(R.id.tvOutput)

        findViewById<View>(R.id.btnDeviceInfo).setOnClickListener { showDeviceInfo() }
        findViewById<View>(R.id.btnBeep).setOnClickListener { playBeep() }
        findViewById<View>(R.id.btnVibrate).setOnClickListener { vibrateOnce() }
        findViewById<View>(R.id.btnPrint).setOnClickListener { printReceipt() }
    }

    private fun showDeviceInfo() {
        val info = buildString {
            appendLine("Fabricante: ${Build.MANUFACTURER}")
            appendLine("Modelo: ${Build.MODEL}")
            appendLine("Marca: ${Build.BRAND}")
            appendLine("Dispositivo: ${Build.DEVICE}")
            appendLine("Android: ${Build.VERSION.RELEASE}")
            appendLine("SDK: ${Build.VERSION.SDK_INT}")
        }
        output.text = info
        AlertDialog.Builder(this)
            .setTitle("Informações do dispositivo")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun playBeep() {
        val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
        output.text = "Beep executado."
        window.decorView.postDelayed({ tone.release() }, 350)
    }

    @Suppress("DEPRECATION")
    private fun vibrateOnce() {
        val vibrator = getSystemService<Vibrator>()
        if (vibrator?.hasVibrator() != true) {
            output.text = "Este dispositivo não possui vibrador."
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
        output.text = "Vibração executada."
    }

    private fun printReceipt() {
        val receipt = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.receipt_layout, null)
        val printManager = getSystemService<PrintManager>() ?: return
        printManager.print(
            "${getString(R.string.app_name)} - Cupom",
            ViewPrintAdapter(receipt),
            PrintAttributes.Builder().build()
        )
        output.text = "Janela de impressão aberta."
    }
}
