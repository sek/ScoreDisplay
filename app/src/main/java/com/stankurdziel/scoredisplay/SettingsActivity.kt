package com.stankurdziel.scoredisplay

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.settings_layout.*

data class Display(val key: String, val id: String) {
    fun serialized(): String = "$key:$id"
    fun default(): Boolean = id == DEFAULT_ID

    companion object {
        const val DEFAULT_ID = "0"
    }
}

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, 0);

    fun display(key: String): Display {
        val s = prefs.getString(key, "$key:${Display.DEFAULT_ID}").split(":")
        return Display(s[0], s[1])
    }

    fun saveDisplay(display: Display) = prefs.edit().putString(display.key, display.serialized()).apply()
}

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs
    private lateinit var left: Display
    private lateinit var right: Display
    private var leftScore: Int = 0
    private var rightScore: Int = 0
    private val database = FirebaseDatabase.getInstance()
    private lateinit var leftRef: DatabaseReference
    private lateinit var rightRef: DatabaseReference

    // TODO reduce left/right duplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        prefs = Prefs(this)
        left = prefs.display("left")
        right = prefs.display("right")

        show_id.setOnClickListener { showQrCodeDialog() }
        reset_scores.setOnClickListener {
            setLeftScore(0)
            setRightScore(0)
        }

        subscribeLeft()
        subscribeRight()

        updateUi()
    }

    private fun subscribeLeft() {
        left_title.setOnClickListener {
            startActivityForResult(Intent(this, QrCodeScannerActivity::class.java), CAMERA_LEFT_REQUEST_CODE)
        }
        leftRef = database.getReference("scores/${left.id}")
        left_up.setOnClickListener { setLeftScore(leftScore + 1) }
        left_down.setOnClickListener { setLeftScore(leftScore - 1) }
        leftRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value ?: return
                leftScore = value.toString().toInt()
                left_score.text = leftScore.toString()
            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    private fun setLeftScore(newScore: Int) {
        if (!left.default()) leftRef.setValue(newScore)
    }

    private fun setRightScore(newScore: Int) {
        if (!right.default()) rightRef.setValue(newScore)
    }

    private fun subscribeRight() {
        right_title.setOnClickListener {
            startActivityForResult(Intent(this, QrCodeScannerActivity::class.java), CAMERA_RIGHT_REQUEST_CODE)
        }
        rightRef = database.getReference("scores/${right.id}")
        right_up.setOnClickListener { setRightScore(rightScore + 1) }
        right_down.setOnClickListener { setRightScore(rightScore - 1) }
        rightRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value ?: return
                rightScore = value.toString().toInt()
                right_score.text = rightScore.toString()
            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    private fun showQrCodeDialog() {
        val factory = LayoutInflater.from(this)
        val qrDialogView = factory.inflate(R.layout.qrcode_dialog, null)
        val qrCodeDialog = AlertDialog.Builder(this)
                .setTitle(R.string.qr_dialog_title)
                .setView(qrDialogView)
                .create()

        val writer = QRCodeWriter()
        try {
            val firebaseId = FirebaseInstanceId.getInstance().id
            val bitMatrix = writer.encode(firebaseId, BarcodeFormat.QR_CODE, 500, 500)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }

            qrDialogView.findViewById<ImageView>(R.id.qrcode).setImageBitmap(bmp)
            qrDialogView.findViewById<TextView>(R.id.qrcode_text).text = firebaseId
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        qrDialogView.findViewById<View>(R.id.close).setOnClickListener { qrCodeDialog.dismiss() }
        qrCodeDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = QrCodeScannerActivity.extractId(data)
        if (result != null) {
            assignScoreBoard(requestCode, result)
            updateUi()
        }
    }

    private fun assignScoreBoard(requestCode: Int, result: String) {
        when (requestCode) {
            CAMERA_LEFT_REQUEST_CODE -> {
                left = Display("left", result)
                prefs.saveDisplay(left)
                subscribeLeft()
            }
            CAMERA_RIGHT_REQUEST_CODE -> {
                right = Display("right", result)
                prefs.saveDisplay(right)
                subscribeRight()
            }
        }
    }

    private fun updateUi() {
        fun goneOrVisible(view: Display) = if (view.default()) View.GONE else View.VISIBLE
        left_tablet_icon.visibility = goneOrVisible(left)
        right_tablet_icon.visibility = goneOrVisible(right)
    }

    private var confirmShown: Boolean = false
    override fun onBackPressed() {
        if (!confirmShown && (!left.default() || !right.default())) {
            Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show()
            confirmShown = true
            Handler().postDelayed({ confirmShown = false }, 5000)
            return
        }
        super.onBackPressed()
    }

    companion object {
        const val CAMERA_LEFT_REQUEST_CODE = 1
        const val CAMERA_RIGHT_REQUEST_CODE = 2
    }
}