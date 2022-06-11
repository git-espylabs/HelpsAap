package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.databinding.ItemAdsListBinding
import com.janustech.helpsaap.model.AdsDataModel

class AdsListPagerAdapter(
    private val context: Context,
    private val slides: List<AdsDataModel>
) : RecyclerView.Adapter<AdsListPagerAdapter.PageHolder>() {

    inner class PageHolder(val binding: ItemAdsListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        return PageHolder(
            ItemAdsListBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.binding.model = slides[position]
    }

    override fun getItemCount(): Int = slides.size
}