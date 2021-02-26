package com.vb.anonymous.ui.main.dialog

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.vb.anonymous.databinding.DialogWebviewBinding

class ResourcesDialog(private val resUrl: String?) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = with(AlertDialog.Builder(activity)) {
        val binding =
            DialogWebviewBinding.inflate(requireActivity().layoutInflater, null, false)
        setView(binding.root)
        binding.webviewWebview.settings.javaScriptEnabled = true
        binding.webviewWebview.settings.builtInZoomControls = true
        binding.webviewWebview.settings.displayZoomControls = false
        binding.webviewWebview.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webviewWebview.settings.mediaPlaybackRequiresUserGesture = false
        if (resUrl != null) binding.webviewWebview.loadUrl(resUrl)
        create()
    }


    override fun onStart() {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onStart()
    }
}