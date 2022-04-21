package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemDealOfDayBinding
import com.janustech.helpsaap.model.DealOfDayDataModel

class DealOfDayAdapter internal constructor(private val context: Context, private val mData: List<DealOfDayDataModel>): RecyclerView.Adapter<DealOfDayAdapter.ViewHolder>() {

    inner class ViewHolder(itemDealOfDayBinding: ItemDealOfDayBinding) : RecyclerView.ViewHolder(itemDealOfDayBinding.root) {
        private var itemDealOfDayBinding: ItemDealOfDayBinding? = null
        fun bind(obj: Any?) {
            itemDealOfDayBinding?.setVariable(BR.model, obj)
            itemDealOfDayBinding?.executePendingBindings()
        }

        init {
            this.itemDealOfDayBinding = itemDealOfDayBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDealOfDayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_deal_of_day, parent, false
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