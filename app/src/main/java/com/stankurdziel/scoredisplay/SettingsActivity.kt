package com.stankurdziel.scoredisplay

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.qrcode_dialog.*
import kotlinx.android.synthetic.main.settings_layout.*

class SettingsActivity : AppCompatActivity() {

    private var leftId: String = ""
    private var leftScore: Int = 0
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        show_id.setOnClickListener { showQrCodeDialog() }
        left_title.setOnClickListener {
            startActivityForResult(Intent(this, QrCodeScannerActivity::class.java), CAMERA_REQUEST_CODE)
        }
        left_up.setOnClickListener {
            if (leftId.length > 0) database.getReference("scores/$leftId").setValue(++leftScore)
        }
        left_down.setOnClickListener {
            if (leftId.length > 0) database.getReference("scores/$leftId").setValue(--leftScore)
        }
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

            qrcode.setImageBitmap(bmp)
            qrcode_text.text = firebaseId
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        close.setOnClickListener { qrCodeDialog.dismiss() }
        qrCodeDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = QrCodeScannerActivity.extractId(data)
        if (result != null) leftId = result
        Log.d("SEK", "Received result. RequestCode: " + requestCode + " data: " + QrCodeScannerActivity.extractId(data))

        // TODO, set left or right scanner name

        // TODO: update firebase on local score changes
    }

    companion object {
        val CAMERA_REQUEST_CODE = 1
    }
}