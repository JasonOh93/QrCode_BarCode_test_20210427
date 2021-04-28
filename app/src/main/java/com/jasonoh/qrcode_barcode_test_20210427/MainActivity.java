package com.jasonoh.qrcode_barcode_test_20210427;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView tvTestText;

    private CodeScanner mCodeScanner;
    private final int CAMERA_REQUEST_CODE = 1101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPermissions();

        tvTestText = findViewById(R.id.test_text);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        tvTestText.setText(result.getText());
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Exception error) {
                Log.e("Main", "Camera initalization error : " + error.getMessage());
            }
        });
        scannerView.setOnClickListener(v -> {
            mCodeScanner.startPreview();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();
    }

    private void setupPermissions(){
//        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
////        if(permission != PackageManager.PERMISSION_GRANTED){
////            makeRequest();
////        }

        //동적퍼미션 두개 동시에 적용
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //원래는 두개다 따로 퍼미션을 줘야한다
            int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if(permissionResult == PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions( permissions, CAMERA_REQUEST_CODE );
            }

        }
    }

//    private void makeRequest(){
//        ActivityCompat.requestPermissions(this, Arrays.asList({Manifest.permission.CAMERA}), 101);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:

                if(grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Camera 사용 불가", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}