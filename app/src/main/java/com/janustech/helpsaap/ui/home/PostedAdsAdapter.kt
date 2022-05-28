package com.janustech.helpsaap.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemPostedAdsBinding
import com.janustech.helpsaap.databinding.ItemPublishLocationBinding
import com.janustech.helpsaap.model.PostedAdDataModel
import com.janustech.helpsaap.model.PublishTypeModel
import com.janustech.helpsaap.utils.CommonUtils

class PostedAdsAdapter internal constructor(private val context: Context, private val mData: List<PostedAdDataModel>): RecyclerView.Adapter<PostedAdsAdapter.ViewHolder>() {

    inner class ViewHolder(itemPostedAdsBinding: ItemPostedAdsBinding) : RecyclerView.ViewHolder(itemPostedAdsBinding.root) {
        private var itemPostedAdsBinding: ItemPostedAdsBinding? = null
        fun bind(obj: PostedAdDataModel) {
            itemPostedAdsBinding?.apply {
                setVariable(BR.model, obj)
                executePendingBindings()

                if (obj.start_date.isNotEmpty() && obj.end_date.isNotEmpty()){
                    val period = CommonUtils.getConvertedDateWithMonthN(obj.start_date) + " to " + CommonUtils.getConvertedDateWithMonthN(obj.start_date)
                    tvDate.text = period
                }

                if (obj.publish_type.isNotEmpty()){
                    tvPublishType.text = getPublishLocationType(obj.panchayath)
                }
            }
        }

        init {
            this.itemPostedAdsBinding = itemPostedAdsBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPostedAdsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_posted_ads, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun getPublishLocationType(id: String): String{
        var loc = ""
        when(id){
            "4" -> loc = "State"
            "5" -> loc = "District"
            "3" -> loc = "Corporation"
            "2" -> loc = "Municipality"
            "1" -> loc = "Panchayath"
        }

        return loc
    }
}