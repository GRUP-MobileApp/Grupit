package com.grup.android

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
import com.grup.android.login.LoginActivity
import com.grup.android.notifications.GroupInvitesViewModel
import com.grup.android.notifications.NotificationsViewModel
import com.grup.ui.compose.MainLayout
import com.grup.ui.viewmodel.MainViewModel

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
                MainLayout(
                    mainViewModel = mainViewModel,
                    navController = AndroidNavigationController(findNavController()),
                    returnToLoginOnClick = {
                        startActivity(
                            Intent(activity, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        mainViewModel.logOut()
                        googleSignInClient.signOut()
                        requireActivity().finish()
                    }
                )
            }
        }
    }
}
