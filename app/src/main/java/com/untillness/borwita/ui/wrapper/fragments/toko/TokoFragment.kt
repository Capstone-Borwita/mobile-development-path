package com.untillness.borwita.ui.wrapper.fragments.toko

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.FragmentTokoBinding
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.about.AboutActivity
import com.untillness.borwita.ui.toko_detail.TokoDetailActivity
import com.untillness.borwita.ui.toko_store.TokoStoreActivity

class TokoFragment : Fragment() {

    private var _binding: FragmentTokoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tokoViewModel: TokoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        this.tokoViewModel = ViewModelFactory.obtainViewModel<TokoViewModel>(this@TokoFragment.requireActivity())

        _binding = FragmentTokoBinding.inflate(inflater, container, false)
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
            buttonFab.setOnClickListener {
                val intent = Intent(this@TokoFragment.context, TokoStoreActivity::class.java)
                startActivity(intent)
            }
            emptyData.emptyDataButton.setOnClickListener {
                this@TokoFragment.tokoViewModel.refreshIndicator(this@TokoFragment.requireContext())
            }
            pullToRefresh.setOnRefreshListener {
                this@TokoFragment.tokoViewModel.refreshIndicator(this@TokoFragment.requireContext())
                pullToRefresh.isRefreshing = false
            }
        }
    }

    private fun listeners() {
        this@TokoFragment.tokoViewModel.apply {
            dataToko.observe(viewLifecycleOwner) {
                when (it) {
                    AppState.Standby, AppState.Loading -> {
                        this@TokoFragment.binding.apply {
                            shimmer.isVisible = true
                            emptyData.emptyData.isVisible = false
                            rvToko.isVisible = false
                        }
                    }

                    is AppState.Error -> {
                        this@TokoFragment.binding.apply {
                            shimmer.isVisible = false
                            emptyData.emptyData.isVisible = true
                            rvToko.isVisible = false
                            emptyData.emptyDataText.text = it.message
                        }
                    }

                    is AppState.Success -> {
                        this@TokoFragment.binding.apply {
                            shimmer.isVisible = false
                            emptyData.emptyData.isVisible = false
                            rvToko.isVisible = true

                            TokoAdapter.setUpRecyclerView(
                                context = this@TokoFragment.requireContext(),
                                recyclerView = rvToko,
                                data = it.data
                            )
                        }
                    }
                }
            }
        }
    }
}