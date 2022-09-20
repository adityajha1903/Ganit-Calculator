package com.example.adi.ganitcalculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Variables for vibrating on any button click
    private var vib: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    // Different possible elements that could be present in the textView
    private val numberList = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val opList = arrayOf("+", "-", "x", "/")
    private val brackets = arrayOf("(", ")")

    // Operator entered by the user
    private var op = "?"

    // TextViews displayed to the user
    private var tv: TextView? = null
    private var preTV: TextView? = null

    // Boolean check to know if the number is decimal or not
    private var isDecimalNum = arrayOf(false, false)

    // Boolean check to know if the number is negative or not
    private var isNegNum = arrayOf(false, false)

    // Ongoing number
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Removing night mode from app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initializing vibration variables
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) @RequiresApi(Build.VERSION_CODES.S) {
            vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vib = vibratorManager!!.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vib = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        }

        // Initializing all the numbers
        val zero = findViewById<Button>(R.id.btnZero)
        val one = findViewById<Button>(R.id.btnOne)
        val two = findViewById<Button>(R.id.btnTwo)
        val three = findViewById<Button>(R.id.btnThree)
        val four = findViewById<Button>(R.id.btnFour)
        val five = findViewById<Button>(R.id.btnFive)
        val six = findViewById<Button>(R.id.btnSix)
        val seven = findViewById<Button>(R.id.btnSeven)
        val eight = findViewById<Button>(R.id.btnEight)
        val nine = findViewById<Button>(R.id.btnNine)

        // Initializing all the operators
        val add = findViewById<Button>(R.id.btnAdd)
        val sub = findViewById<Button>(R.id.btnMinus)
        val mul = findViewById<Button>(R.id.btnMultiply)
        val div = findViewById<Button>(R.id.btnDivide)

        // Initializing some more buttons
        val clear = findViewById<Button>(R.id.btnClear)
        val delete = findViewById<Button>(R.id.btnDelete)
        val dot = findViewById<Button>(R.id.btnDot)
        val equals = findViewById<Button>(R.id.btnEquals)

        // Initializing the textViews for display
        tv = findViewById(R.id.tv1)
        preTV = findViewById(R.id.preTextView)

        // OnClickListeners for numbers (0 - 9)
        zero.setOnClickListener {
            clickOnNum(zero)
        }

        one.setOnClickListener {
            clickOnNum(one)
        }

        two.setOnClickListener {
            clickOnNum(two)
        }

        three.setOnClickListener {
            clickOnNum(three)
        }

        four.setOnClickListener {
            clickOnNum(four)
        }

        five.setOnClickListener {
            clickOnNum(five)
        }

        six.setOnClickListener {
            clickOnNum(six)
        }

        seven.setOnClickListener {
            clickOnNum(seven)
        }

        eight.setOnClickListener {
            clickOnNum(eight)
        }

        nine.setOnClickListener {
            clickOnNum(nine)
        }

        // OnClickListeners for operators (+, -, /, x)
        add.setOnClickListener {
            clickOnOp(add)
        }

        sub.setOnClickListener {
            clickOnNegOp(sub)
        }

        mul.setOnClickListener {
            clickOnOp(mul)
        }

        div.setOnClickListener {
            clickOnOp(div)
        }

        // OnClickListeners for some more buttons (clear, delete, dot, equals)
        clear.setOnClickListener {
            clickOnClear()
        }

        delete.setOnClickListener {
            clickOnDelete()
        }

        dot.setOnClickListener {
            clickOnDot()
        }

        equals.setOnClickListener {
            clickOnEquals()
        }
    }

    // Function when a number button is clicked
    private fun clickOnNum(btn: Button) {
        preTV?.text = ""
        vibIt()
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        if (!tv?.text?.isEmpty()!!) {
            var num = if (op != "?") {
                breakIntoNums(tv?.text?.toString(), false)
            } else {
                breakIntoNums(tv?.text?.toString(), true)
            }

            num = abs(num.toDouble()).toString()
            val numParts = num.split(".")
            val intLen = numParts[0].length
            val decLen = if (!isWhole(num.toDouble())) {
                numParts[1].length
            } else {
                0
            }

            if ((!isDecimalNum[index] && intLen < 7) || (isDecimalNum[index] && decLen < 6)) {
                tv?.append(btn.text)
            }
        } else {
            tv?.append(btn.text)
        }
    }

    // Function for breakdown of textView into numbers
    private fun breakIntoNums(str: String?, isFirst: Boolean): String {
        if (str?.isEmpty()!!) {
            return ""
        }

        val j: Int
        var i: Int
        if (isFirst) {
            i = 0
            j = if (op == "?") {
                str.length
            } else if (str.contains(")$op(")) {
                str.indexOf(")$op(")
            } else if (str.contains(")$op")) {
                str.indexOf(")$op")
            } else if (str.contains("$op(")) {
                str.indexOf("$op(")
            } else {
                str.indexOf(op)
            }
        } else {
            i = if (str.contains(")$op(")) {
                str.indexOf(")$op(") + 2
            } else if (str.contains(")$op")) {
                str.indexOf(")$op") + 2
            } else if (str.contains("$op(")) {
                str.indexOf("$op(") + 1
            } else {
                str.indexOf(op) + 1
            }
            j = str.length
        }

        var num = ""
        while (i < j) {
            if (!brackets.contains(str[i].toString())) {
                num += str[i]
            }
            i++
        }

        if (num == "-" || num == "") {
            num += "0.0"
        }

        return num
    }

    // Function when an operator button is clicked
    private fun clickOnOp(btn: Button) {
        preTV?.text = ""
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        vibIt()
        if (!tv?.text?.endsWith("(-")!!) {
            if (!tv?.text?.isEmpty()!! && opList.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
                tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
                op = "?"
            }

            if (!tv?.text?.isEmpty()!! && op == "?") {
                if (tv?.text?.get(tv?.text?.length!!.minus(1)).toString() == ".") {
                    tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
                    isDecimalNum[index] = false
                }
                if (isNegNum[index] && tv?.text?.get(tv?.text?.length!!.minus(1)).toString() != ")") {
                    tv?.append(")")
                }
                op = btn.text.toString()
                index = 1
                tv?.append(btn.text)
            }
        }
    }

    // Function is a negative number needs to be created
    private fun clickOnNegOp(sub: Button) {
        preTV?.text = ""
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        if (!isNegNum[index]) {
            if (tv?.text?.isEmpty()!!) {
                vibIt()
                tv?.append("(-")
                isNegNum[index] = true
            } else if (tv?.text == "(") {
                vibIt()
                tv?.append("-")
                isNegNum[index] = true
            } else if (op != "?") {
                if (opList.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
                    vibIt()
                    tv?.append("(-")
                    isNegNum[index] = true
                } else if (tv?.text?.get(tv?.text?.length!!.minus(1)) == '(') {
                    vibIt()
                    tv?.append("-")
                    isNegNum[index] = true
                } else {
                    clickOnOp(sub)
                }
            } else {
                clickOnOp(sub)
            }
        } else {
            clickOnOp(sub)
        }
    }

    // Function when clear button is clicked
    private fun clickOnClear() {
        preTV?.text = ""
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        vibIt()
        if (!tv?.text?.isEmpty()!!) {
            if (tv?.text?.get(tv?.text?.length!!.minus(1)).toString() == ".") {
                isDecimalNum[index] = false
                tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
            } else if (tv?.text?.endsWith("(-")!! && isNegNum[index]) {
                isNegNum[index] = false
                tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(2))
            } else if (opList.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
                op = "?"
                tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
                if (tv?.text?.get(tv?.text?.length!!.minus(1)).toString() != ")") {
                    index = 0
                }
            } else {
                if (tv?.text?.get(tv?.text?.length!!.minus(1)).toString() == ")") {
                    index = 0
                }
                tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
            }
        }
    }

    // Function when delete button is clicked
    private fun clickOnDelete() {
        vibIt()
        tv?.text = ""
        preTV?.text = ""
        op = "?"
        index = 0
        isDecimalNum = arrayOf(false, false)
        isNegNum = arrayOf(false, false)
    }

    // Function when dot button is clicked
    private fun clickOnDot() {
        preTV?.text = ""
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        vibIt()
        if (!tv?.text?.isEmpty()!! && brackets.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
            tv?.text = tv?.text?.substring(0, tv?.text?.length!!.minus(1))
        }

        if (!isDecimalNum[index]) {
            isDecimalNum[index] = if (tv?.text?.isEmpty()!! || opList.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
                tv?.append("0.")
                true
            } else {
                tv?.append(".")
                true
            }
        }
    }

    // Function when equals button is clicked
    @SuppressLint("SetTextI18n")
    private fun clickOnEquals() {
        preTV?.text = ""
        if (tv?.text?.contains("Infinity")!!) {
            clickOnDelete()
        }
        vibIt()
        if (!tv?.text?.isEmpty()!! && op != "?" && numberList.contains(tv?.text?.get(tv?.text?.length!!.minus(1)).toString())) {
            preTV?.text = if (isNegNum[1]){
                "${tv?.text})="
            } else {
                "${tv?.text}="
            }
            val num1 = breakIntoNums(tv?.text.toString(), true)
            val num2 = breakIntoNums(tv?.text.toString(), false)
            var res = ""
            when (op) {
                "+" -> res = round0ff(num1.toDouble() + num2.toDouble()).toString()
                "-" -> res = round0ff(num1.toDouble() - num2.toDouble()).toString()
                "x" -> res = round0ff(num1.toDouble() * num2.toDouble()).toString()
                "/" -> res = round0ff(num1.toDouble() / num2.toDouble()).toString()
            }

            if (isWhole(res.toDouble())) {
                res = res.toDouble().toInt().toString()
            } else {
                while (res[res.length - 1] == '0') {
                    res = res.substring(0, res.length - 1)
                }
            }
            if (res.toDouble() < 0.0) {
                tv?.text = "($res)"
                isNegNum[0] = true
            } else {
                tv?.text = res
                isNegNum[0] = false
            }
            op = "?"
            isDecimalNum[0] = !isWhole(res.toDouble())
            isNegNum[1] = false
            isDecimalNum[1] = false
            index = 0
        }
    }

    // Function for vibration on any button click
    private fun vibIt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib?.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vib?.vibrate(60)
        }
    }

    // Function for detecting whole number
    private fun isWhole(value: Double): Boolean {
        return value.toInt().toDouble() == value
    }

    // Function for rounding off a double upto 6 decimal places
    private fun round0ff(value: Double): Double {
        return ("%.6f".format(value)).toDouble()
    }
}