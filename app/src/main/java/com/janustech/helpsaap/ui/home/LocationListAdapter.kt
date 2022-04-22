package com.janustech.helpsaap.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemAdsListBinding
import com.janustech.helpsaap.databinding.ItemLocationListBinding
import com.janustech.helpsaap.model.AdsDataModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.ui.startup.AdsListAdapter

class LocationListAdapter internal constructor(private val mData: List<LocationDataModel>): RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {

    inner class ViewHolder(itemLocationListBinding: ItemLocationListBinding) : RecyclerView.ViewHolder(itemLocationListBinding.root) {
        private var itemLocationListBinding: ItemLocationListBinding? = null
        fun bind(obj: Any?) {
            itemLocationListBinding?.setVariable(BR.model, obj)
            itemLocationListBinding?.executePendingBindings()
        }

        init {
            this.itemLocationListBinding = itemLocationListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLocationListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_location_list, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}