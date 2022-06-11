package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.databinding.ItemDealOfDayBinding
import com.janustech.helpsaap.model.DealOfDayDataModel

class DealsListPagerAdapter(
    private val context: Context,
    private val slides: List<DealOfDayDataModel>
) : RecyclerView.Adapter<DealsListPagerAdapter.PageHolder>() {

    inner class PageHolder(val binding: ItemDealOfDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PageHolder {
        return PageHolder(
            ItemDealOfDayBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.binding.model = slides[position]
    }

    override fun getItemCount(): Int = slides.size

}