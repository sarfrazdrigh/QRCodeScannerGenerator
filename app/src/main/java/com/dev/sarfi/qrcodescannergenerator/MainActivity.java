package com.dev.sarfi.qrcodescannergenerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    Button btnScan, btnOpenNow, btnGenerate;
    TextView txtResult;
    String scannedData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);
        btnOpenNow = findViewById(R.id.btnOpenNow);
        txtResult = findViewById(R.id.txtResult);
        btnGenerate = findViewById(R.id.btnGenerate);

        btnScan.setOnClickListener(view -> scanQRCode());
        btnOpenNow.setOnClickListener(view -> openScannedLink());

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GenerateQRActivity.class);
                startActivity(intent);
            }
        });
    }

    private void scanQRCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(true);
        options.setCameraId(0);
        options.setBarcodeImageEnabled(true);
        qrCodeLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> qrCodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    scannedData = result.getContents();
                    txtResult.setText(scannedData);
                    txtResult.setVisibility(TextView.VISIBLE);

                    if (Patterns.WEB_URL.matcher(scannedData).matches()) {
                        btnOpenNow.setVisibility(Button.VISIBLE);
                    } else {
                        btnOpenNow.setVisibility(Button.GONE);
                    }
                }
            });

    private void openScannedLink() {
        if (!scannedData.isEmpty()) {
            // Ensure the URL has "https://" or "http://"
            if (!scannedData.startsWith("http://") && !scannedData.startsWith("https://")) {
                scannedData = "https://" + scannedData;
            }

            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedData));
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "No browser found to open the link!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
    }


}
