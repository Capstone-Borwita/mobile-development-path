package com.untillness.borwita.ui.berita

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.untillness.borwita.databinding.ItemBeritaBinding
import com.untillness.borwita.ui.wrapper.fragments.home.News

class NewsAdapter(
    private val context: Context,
    private var newsList: List<News> = listOf()
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // Fungsi untuk mengupdate data adapter
    fun submitList(newList: List<News>) {
        newsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemBeritaBinding.inflate(LayoutInflater.from(context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentNews = newsList[position]
        holder.bind(currentNews)
    }

    override fun getItemCount(): Int = newsList.size

    inner class NewsViewHolder(private val binding: ItemBeritaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: News) {
            // Set title and time text
            binding.tvTitleItem.text = newsItem.title
            binding.tvTimeItem.text = newsItem.date

            // Set the image using Glide
            Glide.with(context)
                .load(newsItem.photo)
                .placeholder(com.untillness.borwita.R.drawable.news)
                .into(binding.imgItemNews)

            // Set click listener to open DetailBeritaActivity
            binding.root.setOnClickListener {
                val intent = Intent(context, DetailBeritaActivity::class.java).apply {
                    putExtra(DetailBeritaActivity.cons_TitleNews, newsItem.title)
                    putExtra(DetailBeritaActivity.cont_KontenNews, newsItem.date)
                    putExtra(DetailBeritaActivity.cont_PhotoNews, newsItem.photo)
                }
                context.startActivity(intent)
            }
        }
    }
}