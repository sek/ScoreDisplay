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
import android.widget.ImageView
import android.widget.TextView

import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        findViewById<View>(R.id.show_id).setOnClickListener { showQrCodeDialog() }
        findViewById<View>(R.id.left_title).setOnClickListener { startActivityForResult(Intent(this@SettingsActivity, QrCodeScannerActivity::class.java), CAMERA_REQUEST_CODE) }
    }

    private fun showQrCodeDialog() {
        val factory = LayoutInflater.from(this@SettingsActivity)
        val qrDialogView = factory.inflate(R.layout.qrcode_dialog, null)
        val qrCodeDialog = AlertDialog.Builder(this@SettingsActivity)
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

            (qrDialogView.findViewById<View>(R.id.qrcode) as ImageView).setImageBitmap(bmp)
            (qrDialogView.findViewById<View>(R.id.qrcode_text) as TextView).text = firebaseId
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        qrDialogView.findViewById<View>(R.id.close).setOnClickListener { qrCodeDialog.dismiss() }
        qrCodeDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SEK", "Received result. RequestCode: " + requestCode + " data: " + QrCodeScannerActivity.extractId(data))

        // TODO, set left or right scanner name

        // TODO: update firebase on local score changes
    }

    companion object {
        val CAMERA_REQUEST_CODE = 1
    }
}