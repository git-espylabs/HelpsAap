package com.janustech.helpsaap.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemPublishLocationBinding
import com.janustech.helpsaap.model.PublishTypeModel

class PublishTypesAdapter internal constructor(private val context: Context, private val mData: List<PublishTypeModel>, val adsPackageSelectedListener: (packageTypeId: String, packageTypeName: String, packageId: String, packagePrice: String, packageDuration: Int) -> Unit): RecyclerView.Adapter<PublishTypesAdapter.ViewHolder>() {

    inner class ViewHolder(itemPublishLocationBinding: ItemPublishLocationBinding) : RecyclerView.ViewHolder(itemPublishLocationBinding.root) {
        private var itemPublishLocationBinding: ItemPublishLocationBinding? = null
        fun bind(obj: PublishTypeModel) {
            itemPublishLocationBinding?.apply {
                setVariable(BR.model, obj)
                executePendingBindings()

//                btnPrice1.setTextColor(ContextCompat.getColor(context, R.color.white))
//                btnPrice1.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                btnPrice1.setOnClickListener { v->
                    btnPrice1.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                    btnPrice2.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice3.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[0].packageId, obj.packageList[0].packagePrice, obj.packageList[0].packageDuration)
                }

                btnPrice2.setOnClickListener {
                    btnPrice2.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                    btnPrice1.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice3.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[1].packageId, obj.packageList[1].packagePrice, obj.packageList[1].packageDuration)
                }

                btnPrice3.setOnClickListener {
                    btnPrice3.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btnPrice3.setBackgroundResource(R.drawable.rounded_rect_green_filled)

                    btnPrice1.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice1.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    btnPrice2.setTextColor(ContextCompat.getColor(context, R.color.black))
                    btnPrice2.setBackgroundResource(R.drawable.rounded_rect_grey_filled)

                    adsPackageSelectedListener(obj.publishTypeId, obj.publishTypeIdName, obj.packageList[2].packageId, obj.packageList[2].packagePrice, obj.packageList[2].packageDuration)
                }
            }
        }

        init {
            this.itemPublishLocationBinding = itemPublishLocationBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPublishLocationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_publish_location, parent, false
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