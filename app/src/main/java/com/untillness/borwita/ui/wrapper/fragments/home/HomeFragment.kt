package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.FragmentHomeBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.news_detail.NewsDetailActivity
import com.untillness.borwita.ui.wrapper.WrapperViewModel
import com.untillness.borwita.ui.wrapper.fragments.profile.ProfileFragment.Companion.doLogout
import com.untillness.borwita.widgets.AppDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var wrapperViewModel: WrapperViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        this.wrapperViewModel =
            ViewModelFactory.obtainViewModel<WrapperViewModel>(this.requireActivity())
        this.homeViewModel = ViewModelFactory.obtainViewModel<HomeViewModel>(this.requireActivity())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers()

        this.listeners()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun triggers() {
        this.binding.apply {
            refreshIndicator.setOnRefreshListener {
                this@HomeFragment.wrapperViewModel.initState(this@HomeFragment.requireContext())
                this@HomeFragment.homeViewModel.refreshIndicator(this@HomeFragment.requireContext())
                refreshIndicator.isRefreshing = false
            }
            widgetHeaderHome.buttonLogout.setOnClickListener {
                this@HomeFragment.confirmLogout(this@HomeFragment.requireContext())
            }
        }
    }

    private fun listeners() {
        this.wrapperViewModel.apply {
            profileState.observe(viewLifecycleOwner) {

                when (it) {
                    AppState.Loading -> {
                        this@HomeFragment.binding.apply {
                            widgetHeaderHome.main.isVisible = false
                            widgetHeaderHomeShimmer.isVisible = true
                        }
                    }

                    AppState.Standby -> {
                        this@HomeFragment.binding.apply {
                            widgetHeaderHome.main.isVisible = false
                            widgetHeaderHomeShimmer.isVisible = false
                        }
                    }

                    is AppState.Error -> {
                        this@HomeFragment.binding.apply {
                            widgetHeaderHome.main.isVisible = true
                            widgetHeaderHomeShimmer.isVisible = false

                            widgetHeaderHome.textName.text = "-"
                        }

                        doLogout(fragment = this@HomeFragment, removeSessions = {
                            this@HomeFragment.homeViewModel.removeToken()
                        })
                    }

                    is AppState.Success -> {
                        this@HomeFragment.binding.apply {
                            widgetHeaderHome.main.isVisible = true
                            widgetHeaderHomeShimmer.isVisible = false

                            widgetHeaderHome.textName.text = it.data.data?.name ?: "-"

                            Glide.with(this@HomeFragment).load(it.data.data?.avatarPath ?: "-")
                                .placeholder(AppHelpers.circularProgressDrawable(this@HomeFragment.requireContext()))
                                .fitCenter().into(widgetHeaderHome.avatar)
                        }
                    }
                }
            }
        }

        this@HomeFragment.homeViewModel.apply {
            dataNews.observe(viewLifecycleOwner) {
                when (it) {
                    AppState.Standby, AppState.Loading -> {
                        this@HomeFragment.binding.apply {
                            shimmer.isVisible = true
                            emptyData.emptyData.isVisible = false
                            rvBerita.isVisible = false
                        }
                    }

                    is AppState.Error -> {
                        this@HomeFragment.binding.apply {
                            shimmer.isVisible = false
                            emptyData.emptyData.isVisible = true
                            rvBerita.isVisible = false
                            emptyData.emptyDataText.text = it.message
                        }
                    }

                    is AppState.Success -> {
                        this@HomeFragment.binding.apply {
                            shimmer.isVisible = false
                            emptyData.emptyData.isVisible = false
                            rvBerita.isVisible = true

                            NewsAdapter.setUpRecyclerView(
                                context = this@HomeFragment.requireContext(),
                                recyclerView = rvBerita,
                                data = it.data
                            ) {dataNews ->
                                val intent =
                                    Intent(binding.root.context, NewsDetailActivity::class.java)

                                intent.putExtra(NewsDetailActivity.EXTRA_DETAIL_NEWS, dataNews)

                                startActivity(intent)
                            }
                        }
                    }
                }
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
                    doLogout(fragment = this@HomeFragment, removeSessions = {
                        this@HomeFragment.homeViewModel.removeToken()
                    })
                }

            })
    }
}