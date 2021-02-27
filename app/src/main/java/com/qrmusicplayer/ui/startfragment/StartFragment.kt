package com.qrmusicplayer.ui.startfragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.qrmusicplayer.R
import kotlinx.android.synthetic.main.fragment_start.view.*


class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var  view = inflater.inflate(R.layout.fragment_start, container, false)
        if (!allPermissionsGranted()) {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
      view.button_start.setOnClickListener {
            temporyHardCode()
            //startQRfragment()
        }

return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun temporyHardCode() {
        var url = "https://s3-eu-west-1.amazonaws.com/s3.testm.com/AppData/examJson.json"
        val navDirection = StartFragmentDirections.actionStartFragmentToMusicListFragment(url)
        findNavController().navigate(navDirection)
    }

    fun startQRfragment() {
        val navDirection = StartFragmentDirections.actionStartFragmentToQrScannerFragment()
        findNavController().navigate(navDirection)
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
        private const val REQUEST_CODE_PERMISSIONS = 9
    }

}
