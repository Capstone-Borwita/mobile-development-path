package com.untillness.borwita.ui.wrapper.fragments.toko

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.databinding.FragmentTokoBinding
import com.untillness.borwita.ui.about.AboutActivity
import com.untillness.borwita.ui.toko_store.TokoStoreActivity

class TokoFragment : Fragment() {

    private var _binding: FragmentTokoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tokoViewModel =
            ViewModelProvider(this).get(TokoViewModel::class.java)

        _binding = FragmentTokoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.triggers()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun triggers(){
        this.binding.apply {
            buttonFab.setOnClickListener{
                val intent = Intent(this@TokoFragment.context, TokoStoreActivity::class.java)
                startActivity(intent)
            }
        }
    }
}