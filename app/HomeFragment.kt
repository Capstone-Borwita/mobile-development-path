package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.untillness.borwita.widgets.AppDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var wrapperViewModel: WrapperViewModel
    private lateinit var newsAdapter: NewsAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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

        return root
    }

    // Setup RecyclerView untuk carousel dan berita lainnya
    private fun setupRecyclerView() {
        binding.carouselRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.carouselRecyclerView.adapter = newsAdapter

        binding.carouselRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup observer untuk data dari ViewModel
    private fun setupObservers() {
        homeViewModel.headlineNews.observe(viewLifecycleOwner, Observer { news ->
            binding.tvTitleHeadline.text = news.title
            binding.tvDasHeadLine.text = news.desc
            Glide.with(requireContext())
                .load(news.photo) // Jika news.photo adalah URL
                .into(binding.imgNews)
        })

        homeViewModel.carouselNews.observe(viewLifecycleOwner, Observer { newsList ->
            newsAdapter.submitList(newsList) // Update RecyclerView dengan daftar berita
        })

        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.refreshIndicator.isRefreshing = isLoading
            binding.widgetHeaderHomeShimmer.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })
    }

    // Setup SwipeRefresh untuk menyegarkan data
    private fun setupSwipeRefresh() {
        binding.refreshIndicator.setOnRefreshListener {
            homeViewModel.refreshData()
        }
    }

    // Setup tombol untuk logout
    private fun triggers() {
        binding.widgetHeaderHome.buttonLogout.setOnClickListener {
            confirmLogout(requireContext())
        }
    }

    // Konfirmasi logout dengan dialog
    private fun confirmLogout(context: Context) {
        AppDialog.confirm(context,
            title = getString(R.string.keluar_dari_akun),
            message = getString(R.string.kamu_akan_keluar_dari_akun_dan_harus_masuk_kembali),
            callback = object : AppDialog.Companion.AppDialogCallback {
                override fun onDismiss() {
                    // Logic saat dialog ditutup
                }

                override fun onConfirm() {
                    // Logic untuk logout, seperti menghapus session atau token
                    // Redirect ke halaman login atau wrapper activity
                }
            })
    }
}
