package com.gmail.leewkb1307.randomnumbergenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        var minNumberValue = sharedPref.getInt(
            getString(R.string.saved_minimum),
            DEFAULT_MIN_NUMBER
        )
        var maxNumberValue = sharedPref.getInt(
            getString(R.string.saved_maximum),
            DEFAULT_MAX_NUMBER
        )
        if (minNumberValue < 0 || maxNumberValue < 0 || minNumberValue >= maxNumberValue) {
            minNumberValue = DEFAULT_MIN_NUMBER
            maxNumberValue = DEFAULT_MAX_NUMBER
            Toast.makeText(this, "Defaults restored", Toast.LENGTH_SHORT).show()
        }
        val minNumberInput: EditText = findViewById(R.id.editNumberMin)
        minNumberInput.setText(minNumberValue.toString())
        val maxNumberInput: EditText = findViewById(R.id.editNumberMax)
        maxNumberInput.setText(maxNumberValue.toString())
        generateRandom()
    }

    private fun generateRandom() {
        var randInt = -1
        val minNumberInput: EditText = findViewById(R.id.editNumberMin)
        val minNumberText = minNumberInput.text.toString()
        val maxNumberInput: EditText = findViewById(R.id.editNumberMax)
        val maxNumberText = maxNumberInput.text.toString()
        try {
            val minNumberValue = minNumberText.toInt()
            val maxNumberValue = maxNumberText.toInt()

            if (minNumberValue < 0) {
                Toast.makeText(this, "Invalid minimum number!", Toast.LENGTH_SHORT).show()
            } else if (maxNumberValue < 0) {
                Toast.makeText(this, "Invalid maximum number!", Toast.LENGTH_SHORT).show()
            } else if (minNumberValue >= maxNumberValue) {
                Toast.makeText(this, "Invalid number range!", Toast.LENGTH_SHORT).show()
            } else {
                val rand = SecureRandom.getInstanceStrong()
                randInt = minNumberValue + rand.nextInt(maxNumberValue - minNumberValue + 1)
            }
        } catch (_: NumberFormatException) {
            Toast.makeText(this, "Invalid number input!", Toast.LENGTH_SHORT).show()
        }

        val textRandomOutput: TextView = findViewById(R.id.textRandomView)
        textRandomOutput.text = when (randInt >= 0) {
            true -> randInt.toString()
            else -> "none"
        }
    }

    fun onButtonGenerateClick(view: View) {
        generateRandom()
    }

    fun onButtonClipboardCopyClick(view: View) {
        val textRandomOutput: TextView = findViewById(R.id.textRandomView)
        val textRandom = textRandomOutput.text.toString()
        var randInt = -1
        try {
            randInt = textRandom.toInt()
        } catch (_: NumberFormatException) {
        }

        if (randInt >= 0) {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Numeric string", textRandom)
            clipboard.setPrimaryClip(clip)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing to copy!", Toast.LENGTH_SHORT).show()
        }
    }
}