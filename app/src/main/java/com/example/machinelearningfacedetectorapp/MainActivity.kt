package com.example.machinelearningfacedetectorapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.machinelearningfacedetectorapp.databinding.ActivityMainBinding
import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions


class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    val CAMERA_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnCamera.setOnClickListener {
                openPhoneCamera()

            }
        }

    }

    private fun openPhoneCamera()
    {
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                // Request the camera permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                // Permission has already been granted
                openCamera()
            }*/
        openCamera()

    }
        // Handle the permission request response
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    openCamera()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        private fun openCamera() {
            // Code to open the camera

            var cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(cameraIntent.resolveActivity(packageManager)!=null)
            {
                startActivityForResult(cameraIntent,999)
                Log.d("TAG_STATUS","Camera is open Successfully")

            }else
            {
                Toast.makeText(this,"unable to Open the Camera",Toast.LENGTH_LONG);

            }

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==999 && resultCode== RESULT_OK)
        {
            Log.d("TAG_STATUS","image is Featched Successfully")
            val extras= data?.extras
            val bitmap= extras?.get("data") as Bitmap
            if(bitmap !=null)
            {
                detectUserFace(bitmap)
                Log.d("TAG_STATUS","image is capture Successfully")
            }
            else{
                Toast.makeText(this,"unable to Detect the User Face",Toast.LENGTH_LONG);
                Log.d("TAG_STATUS","image is capture UnSuccessfully")

            }
        }
        else{

        }
    }
    private fun detectUserFace(bitmap: Bitmap) {
        Log.d("TAG_STATUS", "Image detection started")

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = ""
                var i = 1
                for (face in faces) {
                    resultText += "Face Number: $i\n" +
                            "Smile Percentage: ${face.smilingProbability?.times(100)}%\n" +
                            "Left Eye Open: ${face.leftEyeOpenProbability?.times(100)}%\n" +
                            "Right Eye Open: ${face.rightEyeOpenProbability?.times(100)}%\n"
                    i++
                }

                if (faces.isNotEmpty()) {
                    Log.d("TAG_STATUS", "Face(s) detected: \n$resultText")
                    Toast.makeText(this, "Result:\n$resultText", Toast.LENGTH_LONG).show()
                } else {
                    Log.d("TAG_STATUS", "No face detected")
                    Toast.makeText(this, "There is no face to detect", Toast.LENGTH_LONG).show()
                }
                Log.d("TAG_STATUS", "Image detection completed successfully")
            }
            .addOnFailureListener { e ->
                Log.d("TAG_STATUS", "Failed to detect faces", e)
                Toast.makeText(this, "Something is wrong, model is not able to give the output", Toast.LENGTH_LONG).show()
            }
    }

}