package com.grup.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
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
        return ComposeView(requireContext()).apply {
            setContent {
                WelcomeView(
                    welcomeViewModel = welcomeViewModel,
                    navController = AndroidNavigationController(findNavController()),
                    cropImageOnClick = { byteArrayOf() }
                )
            }
        }
    }
}
