package com.grup.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.grup.android.AndroidNavigationController
import com.grup.android.GOOGLE_WEB_CLIENT_ID
import com.grup.android.R
import com.grup.ui.compose.views.MainView
import com.grup.ui.viewmodel.GroupInvitesViewModel
import com.grup.ui.viewmodel.MainViewModel
import com.grup.ui.viewmodel.NotificationsViewModel

class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Eagerly start getting notifications
        ViewModelProvider(this)[NotificationsViewModel::class.java]
        ViewModelProvider(this)[GroupInvitesViewModel::class.java]

        val googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(
                requireActivity(),
                GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                    .build()
            )
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )
        return ComposeView(requireContext()).apply {
            setContent {
                MainView(
                    mainViewModel = mainViewModel,
                    navController = AndroidNavigationController(findNavController()),
                    returnToLoginOnClick = {
                        mainViewModel.logOut()
                        googleSignInClient.signOut()
                        startActivity(
                            Intent(activity, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        requireActivity().finish()
                    }
                )
            }
        }
    }
}
