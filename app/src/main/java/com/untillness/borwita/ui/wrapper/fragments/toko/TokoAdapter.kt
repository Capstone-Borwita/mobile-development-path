package com.untillness.borwita.ui.wrapper.fragments.toko

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.untillness.borwita.data.remote.responses.DataToko
import com.untillness.borwita.databinding.ItemTokoBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.ui.toko_detail.TokoDetailActivity


class TokoAdapter(private val onClickItem: (DataToko) -> Unit) :
    ListAdapter<DataToko, TokoAdapter.ThisHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TokoAdapter.ThisHolder {
        val binding = ItemTokoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ThisHolder(binding)
    }

    override fun onBindViewHolder(holder: TokoAdapter.ThisHolder, position: Int) {
        val data = getItem(position)

        holder.bind(data)

        holder.itemView.setOnClickListener {
            onClickItem.invoke(data)
        }
    }

    inner class ThisHolder(private val binding: ItemTokoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataToko) {
            binding.apply {

                Glide.with(binding.root).load(item.storePhotoPath)
                    .placeholder(AppHelpers.circularProgressDrawable(binding.root.context))
                    .centerCrop().into(imageToko)
                Glide.with(binding.root)
                    .load("https://ui-avatars.com/api/?name=${item.name}&bold=true&size=124&background=random")
                    .placeholder(AppHelpers.circularProgressDrawable(binding.root.context))
                    .centerCrop().into(imageAvatar)

                textName.text = item.name
                textAddress.text = item.georeverse
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataToko>() {
            override fun areItemsTheSame(
                oldItem: DataToko, newItem: DataToko
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataToko, newItem: DataToko
            ): Boolean {
                return oldItem == newItem
            }
        }

        fun setUpRecyclerView(
            context: Context,
            data: List<DataToko>,
            recyclerView: RecyclerView,
            onClickItem: (DataToko) -> Unit
        ) {
            val adapter = TokoAdapter() {
                onClickItem.invoke(it)
            }
            adapter.submitList(data)

            recyclerView.adapter = adapter

            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
        }

    }
}