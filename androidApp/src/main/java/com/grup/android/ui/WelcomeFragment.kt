package com.grup.android.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.grup.android.AndroidNavigationController
import com.grup.android.R
import com.grup.ui.compose.views.WelcomeView
import com.grup.ui.viewmodel.WelcomeViewModel

class WelcomeFragment : Fragment() {
    private val welcomeViewModel: WelcomeViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (welcomeViewModel.hasUserObject) {
            findNavController().navigate(R.id.startMainFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )

        var byteArray: ByteArray = byteArrayOf()
        val imageCropLauncher =
            registerForActivityResult(CropImageContract()) { result ->
                if (result.isSuccessful) {
                    context?.contentResolver?.openInputStream(
                        result.uriContent ?: Uri.EMPTY
                    )?.let { pictureInputStream ->
                        byteArray = pictureInputStream.readBytes().also {
                            pictureInputStream.close()
                        }
                    }
                }
            }
        val imagePickerLauncher =  registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            imageCropLauncher.launch(
                CropImageContractOptions(
                    uri,
                    CropImageOptions(
                        imageSourceIncludeCamera = true,
                        imageSourceIncludeGallery = true,
                        cropShape = CropImageView.CropShape.OVAL,
                        cornerShape = CropImageView.CropCornerShape.OVAL,
                        guidelines = CropImageView.Guidelines.ON,
                        fixAspectRatio = true,
                        outputCompressFormat = Bitmap.CompressFormat.PNG
                    )
                )
            )
        }

        return ComposeView(requireContext()).apply {
            setContent {
                WelcomeView(
                    welcomeViewModel = welcomeViewModel,
                    navController = AndroidNavigationController(findNavController()),
                    cropImageOnClick = {
                        imagePickerLauncher.launch("image/*")
                        byteArray
                    }
                )
            }
        }
    }
}
