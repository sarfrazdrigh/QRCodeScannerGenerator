package com.dev.sarfi.qrcodescannergenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

public class GenerateQRActivity extends AppCompatActivity {
    EditText inputText;
    Button btnGenerateQR, btnShareQR;
    ImageView imgQRCode;
    Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qractivity);

        inputText = findViewById(R.id.inputText);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        btnShareQR = findViewById(R.id.btnShareQR);
        imgQRCode = findViewById(R.id.imgQRCode);

        btnGenerateQR.setOnClickListener(view -> generateQRCode());
        btnShareQR.setOnClickListener(view -> shareQRCode());
    }

    private void generateQRCode() {
        String text = inputText.getText().toString();
        if (!text.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                qrBitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
                imgQRCode.setImageBitmap(qrBitmap);
                btnShareQR.setVisibility(View.VISIBLE); // Show share button after QR is generated
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void shareQRCode() {
        if (qrBitmap != null) {
            try {
                // Save QR code image
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "qr_code.png");
                FileOutputStream fos = new FileOutputStream(file);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                // Get URI for sharing
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

                // Create share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error sharing QR Code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Generate a QR Code first!", Toast.LENGTH_SHORT).show();
        }
    }
}
