package com.grup.android.transaction

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
import com.grup.ui.compose.ActionAmountView
import com.grup.ui.viewmodel.TransactionViewModel

class ActionAmountFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ActionAmountView(
                    transactionViewModel = transactionViewModel,
                    navController = AndroidNavigationController(findNavController()),
                    actionType = requireArguments().getString("actionType")!!,
                    existingActionId = requireArguments().getString("actionId")
                )
            }
        }
    }
}
