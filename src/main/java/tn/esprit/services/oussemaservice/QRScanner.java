package tn.esprit.services.oussemaservice;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import tn.esprit.services.oussemaservice.QRCodeListener;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScanner {

    private Webcam webcam;
    private ExecutorService executorService;
    private boolean scanning = false;
    private QRCodeListener qrCodeListener;

    public QRScanner(QRCodeListener listener) {
        this.qrCodeListener = listener;
        initWebcam();
    }

    private void initWebcam() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();
        } else {
            System.err.println(" Aucune webcam détectée !");
        }
    }

    public void startScanning() {
        if (webcam == null) return;

        scanning = true;
        executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            System.out.println("📸 Scan du QR Code en cours...");
            while (scanning) {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    String qrData = decodeQRCode(image);
                    if (qrData != null) {
                        System.out.println(" QR Code détecté : " + qrData);
                        if (qrCodeListener != null) {
                            qrCodeListener.onQRCodeDetected(qrData);
                        }
                        break;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            stopScanning();
        });
    }

    public void stopScanning() {
        scanning = false;
        if (executorService != null) {
            executorService.shutdown();
        }
        if (webcam != null) {
            webcam.close();
        }
        System.out.println(" Scanner arrêté.");
    }

    private String decodeQRCode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }
}
