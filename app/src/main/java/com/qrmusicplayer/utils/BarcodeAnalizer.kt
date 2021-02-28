package com.qrmusicplayer.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.qrmusicplayer.ui.qrscannerframent.BarcodeListener


class BarcodeAnalizer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        // Task completed successfully
                        for (barcode in barcodes) {
                            barcodeListener(barcode.rawValue ?: "")
                        }
                    }
                    .addOnFailureListener {
                        // You should really do something about Exceptions
                    }
                    .addOnCompleteListener {
                        // It's important to close the imageProxy
                        imageProxy.close()
                    }
        }

    }
}
