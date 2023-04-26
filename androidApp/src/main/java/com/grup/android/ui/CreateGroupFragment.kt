package com.grup.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.AndroidNavigationController
import com.grup.android.R
import com.grup.ui.compose.views.CreateGroupView
import com.grup.ui.viewmodel.MainViewModel

class CreateGroupFragment : Fragment() {
    private val mainViewModel: MainViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CreateGroupView(
                    mainViewModel = mainViewModel,
                    navController = AndroidNavigationController(findNavController())
                )
            }
        }
    }
}
