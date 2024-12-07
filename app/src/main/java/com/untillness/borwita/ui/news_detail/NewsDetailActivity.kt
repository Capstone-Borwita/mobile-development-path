package com.untillness.borwita.ui.news_detail

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.untillness.borwita.data.remote.responses.DataNewsResponse
import com.untillness.borwita.databinding.ActivityNewsDetailBinding
import com.untillness.borwita.helpers.AppHelpers

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding
    private var dataNewsResponse: DataNewsResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(this.binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dataNewsResponse = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra<DataNewsResponse>(
                EXTRA_DETAIL_NEWS, DataNewsResponse::class.java
            )
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<DataNewsResponse>(EXTRA_DETAIL_NEWS)
        }

        AppHelpers.log(dataNewsResponse?.title ?: "")

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Detail Berita"

        binding.apply {
            Glide.with(binding.root).load(dataNewsResponse?.poster)
                .placeholder(AppHelpers.circularProgressDrawable(binding.root.context)).centerCrop()
                .into(imageNews)

            textTitle.text = dataNewsResponse?.title
            textDate.text = AppHelpers.formatDate(dataNewsResponse?.createdAt.toString())
            textContent.text = Html.fromHtml(dataNewsResponse?.content, Html.FROM_HTML_MODE_COMPACT)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_DETAIL_NEWS = "EXTRA_DETAIL_NEWS"
    }
}