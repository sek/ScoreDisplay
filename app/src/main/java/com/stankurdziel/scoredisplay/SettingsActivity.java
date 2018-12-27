package com.stankurdziel.scoredisplay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class SettingsActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        findViewById(R.id.show_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrCodeDialog();
            }
        });
        findViewById(R.id.left_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SettingsActivity.this, QrCodeScannerActivity.class), CAMERA_REQUEST_CODE);
            }
        });
    }

    private void showQrCodeDialog() {
        LayoutInflater factory = LayoutInflater.from(SettingsActivity.this);
        final View qrDialogView = factory.inflate(R.layout.qrcode_dialog, null);
        final AlertDialog qrCodeDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.qr_dialog_title)
                .setView(qrDialogView)
                .create();

        QRCodeWriter writer = new QRCodeWriter();
        try {
            final String firebaseId = FirebaseInstanceId.getInstance().getId();
            BitMatrix bitMatrix = writer.encode(firebaseId, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ((ImageView) qrDialogView.findViewById(R.id.qrcode)).setImageBitmap(bmp);
            ((TextView) qrDialogView.findViewById(R.id.qrcode_text)).setText(firebaseId);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        qrDialogView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeDialog.dismiss();
            }
        });
        qrCodeDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SEK", "Received result. RequestCode: " + requestCode + " data: " + QrCodeScannerActivity.Companion.extractId(data));

        // TODO, set left or right scanner name

        // TODO: update firebase on local score changes
    }
}