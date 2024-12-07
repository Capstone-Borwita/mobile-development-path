package com.untillness.borwita.ui.wrapper.fragments.home

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
import com.untillness.borwita.data.remote.responses.DataNewsResponse
import com.untillness.borwita.databinding.ItemBeritaBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.ui.toko_detail.TokoDetailActivity


class NewsAdapter(private val onClickItem: (DataNewsResponse) -> Unit) :
    ListAdapter<DataNewsResponse, NewsAdapter.ThisHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NewsAdapter.ThisHolder {
        val binding = ItemBeritaBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ThisHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ThisHolder, position: Int) {
        val data = getItem(position)

        holder.bind(data)

        holder.itemView.setOnClickListener {
            onClickItem.invoke(data)
        }
    }

    inner class ThisHolder(private val binding: ItemBeritaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataNewsResponse) {
            binding.apply {

                Glide.with(binding.root).load(item.poster)
                    .placeholder(AppHelpers.circularProgressDrawable(binding.root.context))
                    .centerCrop().into(imageNews)

                textTitle.text = item.title
                textDate.text = AppHelpers.formatDate(item.createdAt.toString())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataNewsResponse>() {
            override fun areItemsTheSame(
                oldItem: DataNewsResponse, newItem: DataNewsResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataNewsResponse, newItem: DataNewsResponse
            ): Boolean {
                return oldItem == newItem
            }
        }

        fun setUpRecyclerView(
            context: Context,
            data: List<DataNewsResponse>,
            recyclerView: RecyclerView,
            onClickItem: (DataNewsResponse) -> Unit
        ) {
            val adapter = NewsAdapter() {
                onClickItem.invoke(it)
            }
            adapter.submitList(data)

            recyclerView.adapter = adapter

            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
        }

    }
}