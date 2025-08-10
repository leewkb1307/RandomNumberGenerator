package com.gmail.leewkb1307.randomnumbergenerator

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
        val minNumberInput: EditText = findViewById(R.id.editNumberMin)
        minNumberInput.setText("0")
        val maxNumberInput: EditText = findViewById(R.id.editNumberMax)
        maxNumberInput.setText("999999")
        generateRandom()
    }

    private fun generateRandom() {
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
                val randInt = minNumberValue + rand.nextInt(maxNumberValue - minNumberValue + 1)
                val textRandomOutput: TextView = findViewById(R.id.textRandomView)
                textRandomOutput.text = "Generated: " + randInt
            }
        } catch (_: NumberFormatException) {
            Toast.makeText(this, "Invalid number input!", Toast.LENGTH_SHORT).show()
        }
    }

    fun onButtonGenerateClick(view: View) {
        generateRandom()
    }
}