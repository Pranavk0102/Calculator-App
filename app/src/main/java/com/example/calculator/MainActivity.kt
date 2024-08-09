package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)

        val buttonIds = intArrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.buttonDot, R.id.buttonAdd,
            R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
            R.id.buttonClear, R.id.buttonDelete, R.id.buttonEquals
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(view: Button) {
        when (view.id) {
            R.id.buttonClear -> {
                editText.text.clear()
                lastNumeric = false
                lastDot = false
            }
            R.id.buttonDelete -> {
                val text = editText.text.toString()
                if (text.isNotEmpty()) {
                    editText.text.delete(text.length - 1, text.length)
                    lastNumeric = text.last().isDigit()
                }
            }
            R.id.buttonEquals -> {
                if (lastNumeric) {
                    val value = editText.text.toString()
                    try {
                        val result = evaluate(value)
                        editText.text.clear()
                        editText.append(result.toString())
                        lastDot = false
                    } catch (e: Exception) {
                        editText.text.clear()
                        editText.append("Error")
                    }
                }
            }
            else -> {
                editText.append(view.text)
                if (view.text.toString() == ".") {
                    lastDot = true
                } else {
                    lastNumeric = true
                }
            }
        }
    }

    private fun evaluate(expression: String): Double {
        val tokens = expression.toCharArray()
        val values: Stack<Double> = Stack()
        val ops: Stack<Char> = Stack()
        var i = 0
        while (i < tokens.size) {
            if (tokens[i].isWhitespace()) {
                i++
                continue
            }
            if (tokens[i] in '0'..'9' || tokens[i] == '.') {
                val sb = StringBuilder()
                while (i < tokens.size && (tokens[i] in '0'..'9' || tokens[i] == '.')) {
                    sb.append(tokens[i++])
                }
                values.push(sb.toString().toDouble())
                i--
            } else if (tokens[i] == '(') {
                ops.push(tokens[i])
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.pop()
            } else if (tokens[i] in listOf('+', '-', '*', '/')) {
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(tokens[i])
            }
            i++
        }
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }
        return values.pop()
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero") else a / b
            else -> 0.0
        }
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') {
            return false
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false
        }
        return true
    }
}
