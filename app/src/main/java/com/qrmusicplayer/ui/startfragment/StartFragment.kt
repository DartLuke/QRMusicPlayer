package com.qrmusicplayer.ui.startfragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.qrmusicplayer.R
import com.qrmusicplayer.ui.qrscannerframent.QrScannerFragment
import kotlinx.android.synthetic.main.fragment_start.view.*


class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_start, container, false)
        view.button_start.setOnClickListener {
         //  temporyHardCode()
            startQRfragment()
        }
        return view
    }

    private fun startQRfragment() {
        if (allPermissionsGranted()) {
            val navDirection = StartFragmentDirections.actionStartFragmentToQrScannerFragment()
            findNavController().navigate(navDirection)
        } else requestPermissions(
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun temporyHardCode() {
        if (!allPermissionsGranted()) {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            var url = "https://s3-eu-west-1.amazonaws.com/s3.testm.com/AppData/examJson.json"
            val navDirection = StartFragmentDirections.actionStartFragmentToMusicListFragment(url)
            findNavController().navigate(navDirection)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            startQRfragment()
        } else {
            val message = "App doesn't have necessary"
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val REQUEST_CODE_PERMISSIONS = 42
    }

}
