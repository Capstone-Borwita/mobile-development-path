package com.untillness.borwita.ui.wrapper.fragments.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.data.states.ApiState
import com.untillness.borwita.databinding.FragmentProfileBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.about.AboutActivity
import com.untillness.borwita.ui.login.LoginActivity
import com.untillness.borwita.ui.profile_edit.ProfileEditActivity
import com.untillness.borwita.ui.profile_password.ProfilePasswordActivity
import com.untillness.borwita.ui.wrapper.WrapperViewModel
import com.untillness.borwita.widgets.AppDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var wrapperViewModel: WrapperViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        this.profileViewModel =
            ViewModelFactory.fromFragment<ProfileViewModel>(this.requireActivity())
        this.wrapperViewModel =
            ViewModelFactory.fromFragment<WrapperViewModel>(this.requireActivity())

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers()

        this.listeners()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun listeners() {
        this.wrapperViewModel.apply {
            profileState.observe(viewLifecycleOwner) {

                when (it) {
                    ApiState.Loading -> {
                        this@ProfileFragment.binding.apply {
                            widgetProfileSection.main.isVisible = false
                            widgetProfileSectionShimmer.main.isVisible = true
                        }
                    }

                    ApiState.Standby -> {
                        this@ProfileFragment.binding.apply {
                            widgetProfileSection.main.isVisible = false
                            widgetProfileSectionShimmer.main.isVisible = false
                        }
                    }

                    is ApiState.Error -> {
                        this@ProfileFragment.binding.apply {
                            widgetProfileSection.main.isVisible = false
                            widgetProfileSectionShimmer.main.isVisible = false
                        }
                    }

                    is ApiState.Success -> {
                        this@ProfileFragment.binding.apply {
                            widgetProfileSection.main.isVisible = true
                            widgetProfileSectionShimmer.main.isVisible = false

                            widgetProfileSection.textNameProfile.text = it.data.data?.name ?: "-"
                            widgetProfileSection.textEmailProfile.text = it.data.data?.email ?: "-"

                            Glide.with(this@ProfileFragment).load(it.data.data?.avatarPath ?: "-")
                                .placeholder(AppHelpers.circularProgressDrawable(this@ProfileFragment.requireContext()))
                                .fitCenter().into(widgetProfileSection.imageProfile)
                        }
                    }
                }
            }
        }
    }

    private fun triggers() {
        binding.apply {
            refreshIndicator.setOnRefreshListener {
                this@ProfileFragment.wrapperViewModel.initState(this@ProfileFragment.requireContext())
                refreshIndicator.isRefreshing = false
            }
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
                this@ProfileFragment.confirmLogout(this@ProfileFragment.requireContext())
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
                    doLogout(fragment = this@ProfileFragment, removeSessions = {
                        this@ProfileFragment.profileViewModel.removeToken()
                    })
                }

            })
    }


    companion object {
        fun doLogout(fragment: Fragment, removeSessions: () -> Unit) {
            removeSessions()

            val intent = Intent(fragment.requireContext(), LoginActivity::class.java)
            fragment.requireContext().startActivity(intent)
            fragment.requireActivity().finish()
        }
    }

}