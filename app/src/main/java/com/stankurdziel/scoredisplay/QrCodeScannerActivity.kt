package com.stankurdziel.scoredisplay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.stankurdziel.scoredisplay.SettingsActivity.Companion.CAMERA_REQUEST_CODE
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QrCodeScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var scanner: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode_scanner_layout)
        scanner = findViewById(R.id.qrCodeScanner)
        setScannerProperties()
    }

    private fun setScannerProperties() {
        scanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        scanner.setAutoFocus(true)
        scanner.setLaserColor(R.color.viewfinder_laser)
        scanner.setMaskColor(R.color.viewfinder_mask)
        if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true)) scanner.setAspectTolerance(0.5f)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CAMERA),
                        CAMERA_REQUEST_CODE
                )
                return
            }
        }
        scanner.startCamera()
        scanner.setResultHandler(this)
    }

    override fun handleResult(p0: Result?) {
        if (p0 != null) {
            Toast.makeText(this, p0.text, Toast.LENGTH_LONG).show()
            setResult(RESULT_OK, createResultIntent(p0.text))
            finish()
        }
    }

    private fun createResultIntent(text: String?): Intent? {
        val result = Intent()
        result.putExtra("id", text)
        return result
    }

    companion object {
        fun extractId(result: Intent): String? {
            return result.getStringExtra("id")
        }
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }
}
