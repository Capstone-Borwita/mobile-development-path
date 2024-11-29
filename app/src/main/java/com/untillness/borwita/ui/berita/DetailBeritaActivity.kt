package com.untillness.borwita.ui.berita

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityDetailBeritaBinding

class DetailBeritaActivity : AppCompatActivity() {

    companion object {
        const val cons_TitleNews = "cont_TitleNews"
        const val cont_PhotoNews = "cont_PhotoNews"
        const val cont_KontenNews = "cont_KontenNews"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_berita)

        // Ambil data dari Intent
        val newsTitle = intent.getStringExtra(cons_TitleNews) ?: ""
        val newsContent = intent.getStringExtra(cont_KontenNews) ?: ""
        val newsImage = intent.getStringExtra(cont_PhotoNews) ?: ""

        // Update UI sesuai dengan data yang diterima
        val textTitleNews: TextView = findViewById(R.id.text_title_news)
        val textTimeNews: TextView = findViewById(R.id.post_time)
        val imageView: ImageView = findViewById(R.id.img_detail_berita)

        textTitleNews.text = newsTitle
        textTimeNews.text = newsContent

        // Gunakan Glide untuk memuat gambar
        Glide.with(this)
            .load(newsImage.takeIf { it.isNotEmpty() } ?: R.drawable.news)  // Use placeholder image if empty
            .into(imageView)
    }
}