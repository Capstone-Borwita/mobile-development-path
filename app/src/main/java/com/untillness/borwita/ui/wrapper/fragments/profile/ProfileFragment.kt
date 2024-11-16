package com.untillness.borwita.ui.wrapper.fragments.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.R
import com.untillness.borwita.databinding.FragmentProfileBinding
import com.untillness.borwita.ui.about.AboutActivity
import com.untillness.borwita.ui.login.LoginActivity
import com.untillness.borwita.ui.profile_edit.ProfileEditActivity
import com.untillness.borwita.ui.profile_password.ProfilePasswordActivity
import com.untillness.borwita.widgets.AppDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers(context = requireContext())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun triggers(context: Context) {
        binding.apply {
            listUbahProfil.setOnClickListener {
                val intent = Intent(this@ProfileFragment.context, ProfileEditActivity::class.java)
                startActivity(intent)
            }
            listUbahKataSandi.setOnClickListener {
                val intent =
                    Intent(this@ProfileFragment.context, ProfilePasswordActivity::class.java)
                startActivity(intent)
            }
            listTentangAplikasi.setOnClickListener {
                val intent = Intent(this@ProfileFragment.context, AboutActivity::class.java)
                startActivity(intent)
            }
            listLogout.setOnClickListener {
                this@ProfileFragment.confirmLogout(context)
            }
        }
    }


    private fun confirmLogout(context: Context) {
        AppDialog.confirm(context,
            title = getString(R.string.keluar_dari_akun),
            message = getString(R.string.kamu_akan_keluar_dari_akun_dan_harus_masuk_kembali),
            callback = object : AppDialog.Companion.AppDialogCallback {
                override fun onDismiss() {
                }

                override fun onConfirm() {
                    doLogout()
                }

            })
    }

    private fun doLogout() {
//        homeViewModel.removeName()
//        homeViewModel.removeToken()

        val intent = Intent(this.context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}