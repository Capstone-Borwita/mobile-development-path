package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.FragmentHomeBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.wrapper.WrapperViewModel
import com.untillness.borwita.ui.wrapper.fragments.profile.ProfileFragment.Companion.doLogout
=======
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.databinding.FragmentHomeBinding
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.berita.DetailBeritaActivity
import com.untillness.borwita.ui.berita.NewsAdapter
import com.untillness.borwita.ui.wrapper.fragments.home.News
import com.untillness.borwita.ui.berita.NewsModel
import com.untillness.borwita.ui.wrapper.WrapperViewModel
>>>>>>> 97dcba9 (Initial commit)
import com.untillness.borwita.widgets.AppDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var wrapperViewModel: WrapperViewModel
<<<<<<< HEAD

    // This property is only valid between onCreateView and
    // onDestroyView.
=======
    private lateinit var newsAdapter: NewsAdapter

>>>>>>> 97dcba9 (Initial commit)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
<<<<<<< HEAD
        this.wrapperViewModel = ViewModelFactory.obtainViewModel<WrapperViewModel>(this.requireActivity())
        this.homeViewModel = ViewModelFactory.obtainViewModel<HomeViewModel>(this.requireActivity())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers()

        this.listeners()
=======
        // Inisialisasi ViewModel
        this.wrapperViewModel = ViewModelFactory.fromFragment<WrapperViewModel>(this.requireActivity())
        this.homeViewModel = ViewModelFactory.fromFragment<HomeViewModel>(this.requireActivity())

        // Inisialisasi Adapter dan RecyclerView
        newsAdapter = NewsAdapter(requireContext())
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup RecyclerView dan Observer
        setupRecyclerView()
        setupObservers()

        // Setup SwipeRefresh
        setupSwipeRefresh()

        // Setup tombol dan logout
        triggers()
>>>>>>> 97dcba9 (Initial commit)

        return root
    }

<<<<<<< HEAD
=======
    // Setup RecyclerView untuk carousel dan berita lainnya
    private fun setupRecyclerView() {
        binding.carouselRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.carouselRecyclerView.adapter = newsAdapter

        binding.carouselRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }
    }

>>>>>>> 97dcba9 (Initial commit)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

<<<<<<< HEAD
    private fun triggers() {
        this.binding.apply {
            refreshIndicator.setOnRefreshListener {
                this@HomeFragment.wrapperViewModel.initState(this@HomeFragment.requireContext())
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
    }

=======
    // Setup observer untuk data dari ViewModel
    private fun setupObservers() {
        // Observer untuk headline news dari homeViewModel
        homeViewModel.headlineNews.observe(viewLifecycleOwner, Observer { news ->
            binding.tvTitleHeadline.text = news.title
            binding.tvDasHeadLine.text = news.date
            Glide.with(requireContext())
                .load(news.photo) // Jika news.photo adalah URL
                .into(binding.imgNews)
        })

        // Observer untuk carousel news (newsList) dari homeViewModel
        homeViewModel.carouselNews.observe(viewLifecycleOwner, Observer { newsList ->
            newsAdapter.submitList(newsList) // Update RecyclerView dengan daftar berita
        })

        // Observer untuk isLoading dari homeViewModel
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.refreshIndicator.isRefreshing = isLoading
            binding.widgetHeaderHomeShimmer.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })
    }

    // Setup SwipeRefresh untuk menyegarkan data
    private fun setupSwipeRefresh() {
        binding.refreshIndicator.setOnRefreshListener {
            homeViewModel.refreshData() // Menyegarkan data dari homeViewModel
        }
    }

    // Setup tombol untuk logout
    private fun triggers() {
        binding.widgetHeaderHome.buttonLogout.setOnClickListener {
            confirmLogout(requireContext())
        }
    }

    // Konfirmasi logout dengan dialog
>>>>>>> 97dcba9 (Initial commit)
    private fun confirmLogout(context: Context) {
        AppDialog.confirm(context,
            title = getString(R.string.keluar_dari_akun),
            message = getString(R.string.kamu_akan_keluar_dari_akun_dan_harus_masuk_kembali),
            callback = object : AppDialog.Companion.AppDialogCallback {
                override fun onDismiss() {
<<<<<<< HEAD
                }

                override fun onConfirm() {
                    doLogout(fragment = this@HomeFragment, removeSessions = {
                        this@HomeFragment.homeViewModel.removeToken()
                    })
                }

            })
    }
}
=======
                    // Logic saat dialog ditutup
                }

                override fun onConfirm() {
                    // Logic untuk logout, seperti menghapus session atau token
                    // Redirect ke halaman login atau wrapper activity
                }
            })
    }
}



>>>>>>> 97dcba9 (Initial commit)
