package com.untillness.borwita.ui.wrapper.fragments.toko

<<<<<<< HEAD
import android.app.Activity
=======
>>>>>>> 97dcba9 (Initial commit)
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
<<<<<<< HEAD
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.FragmentTokoBinding
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.about.AboutActivity
import com.untillness.borwita.ui.map.MapsActivity
=======
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.databinding.FragmentTokoBinding
import com.untillness.borwita.ui.about.AboutActivity
>>>>>>> 97dcba9 (Initial commit)
import com.untillness.borwita.ui.toko_detail.TokoDetailActivity
import com.untillness.borwita.ui.toko_store.TokoStoreActivity

class TokoFragment : Fragment() {

    private var _binding: FragmentTokoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

<<<<<<< HEAD
    private lateinit var tokoViewModel: TokoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        this.tokoViewModel =
            ViewModelFactory.obtainViewModel<TokoViewModel>(this@TokoFragment.requireActivity())
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tokoViewModel =
            ViewModelProvider(this).get(TokoViewModel::class.java)
>>>>>>> 97dcba9 (Initial commit)

        _binding = FragmentTokoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers()

<<<<<<< HEAD
        this.listeners()

=======
>>>>>>> 97dcba9 (Initial commit)
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
<<<<<<< HEAD
                resultLauncher.launch(intent)
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
                            ) {
                                val intent =
                                    Intent(binding.root.context, TokoDetailActivity::class.java)
                                intent.putExtra(TokoDetailActivity.EXTRA_ID, it.id.toString())

                                this@TokoFragment.resultLauncher.launch(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            this@TokoFragment.tokoViewModel.refreshIndicator(this@TokoFragment.requireContext())
=======
                startActivity(intent)
            }
            emptyData.emptyDataButton.setOnClickListener {
                val intent = Intent(this@TokoFragment.context, TokoDetailActivity::class.java)
                startActivity(intent)
            }
>>>>>>> 97dcba9 (Initial commit)
        }
    }
}