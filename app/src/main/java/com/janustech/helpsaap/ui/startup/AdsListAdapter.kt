package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemAdsListBinding
import com.janustech.helpsaap.model.AdsDataModel

class AdsListAdapter internal constructor(private val context: Context, private val mData: List<AdsDataModel>): RecyclerView.Adapter<AdsListAdapter.ViewHolder>()  {

    inner class ViewHolder(itemAdsListBinding: ItemAdsListBinding) : RecyclerView.ViewHolder(itemAdsListBinding.root) {
        private var itemAdsListBinding: ItemAdsListBinding? = null
        fun bind(obj: Any?) {
            itemAdsListBinding?.setVariable(BR.model, obj)
            itemAdsListBinding?.executePendingBindings()
        }

        init {
            this.itemAdsListBinding = itemAdsListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAdsListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_ads_list, parent, false
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