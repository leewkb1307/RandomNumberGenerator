package com.gmail.leewkb1307.randomnumbergenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {
    private val rng = SecureRandom.getInstanceStrong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val randRange = getRandomRangePref()
        var minNumberValue = randRange.min
        var maxNumberValue = randRange.max
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
                errorDialog(R.string.bad_minimum)
            } else if (maxNumberValue < 0) {
                errorDialog(R.string.bad_maximum)
            } else if (minNumberValue >= maxNumberValue) {
                errorDialog(R.string.bad_range)
            } else {
                randInt = minNumberValue + rng.nextInt(maxNumberValue - minNumberValue + 1)

                val sharedPref = getSharedPref()
                val randRange = getRandomRangePref(sharedPref)
                if (minNumberValue != randRange.min) {
                    sharedPref.edit {
                        putInt(getString(R.string.saved_minimum), minNumberValue)
                    }
                }
                if (maxNumberValue != randRange.max) {
                    sharedPref.edit {
                        putInt(getString(R.string.saved_maximum), maxNumberValue)
                    }
                }
            }
        } catch (_: NumberFormatException) {
            errorDialog(R.string.bad_input)
        }

        val textRandomOutput: TextView = findViewById(R.id.textRandomView)
        textRandomOutput.text = when (randInt >= 0) {
            true -> randInt.toString()
            else -> "none"
        }
    }

    private fun getSharedPref(): SharedPreferences {
        return this.getPreferences(MODE_PRIVATE)
    }

    private fun getRandomRangePref(): RandomRange {
        return getRandomRangePref(getSharedPref())
    }

    private fun getRandomRangePref(sharedPref: SharedPreferences): RandomRange {
        val minNumberValue = sharedPref.getInt(
            getString(R.string.saved_minimum),
            DEFAULT_MIN_NUMBER
        )
        val maxNumberValue = sharedPref.getInt(
            getString(R.string.saved_maximum),
            DEFAULT_MAX_NUMBER
        )

        return RandomRange(minNumberValue, maxNumberValue)
    }

    private fun getGeneratedNumber(): String {
        val textRandomOutput: TextView = findViewById(R.id.textRandomView)
        var textRandom = textRandomOutput.text.toString()
        try {
            if (textRandom.toInt() < 0) {
                textRandom = ""
            }
        } catch (_: NumberFormatException) {
            textRandom = ""
        }

        return textRandom
    }

    fun onButtonGenerateClick(view: View) {
        generateRandom()
    }

    fun onButtonDetailsClick(view: View) {
        val details = buildString {
            append(getString(R.string.provider))
            append(": ")
            append(rng.provider.toString())
            append("\n")
            append(getString(R.string.algorithm))
            append(": ")
            append(rng.algorithm.toString())
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.generator))
            .setMessage(details)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun onButtonClipboardCopyClick(view: View) {
        val textRandom = getGeneratedNumber()

        if (textRandom.isNotEmpty()) {
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

    fun onButtonShareClick(view: View) {
        val textRandom = getGeneratedNumber()

        if (textRandom.isNotEmpty()) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, "Generated number")
                putExtra(Intent.EXTRA_TEXT, textRandom)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } else {
            Toast.makeText(this, "Nothing to share.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun errorDialog(resId: Int) {
        errorDialog(getString(resId))
    }

    private fun errorDialog(msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.error))
            .setMessage(msg)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}