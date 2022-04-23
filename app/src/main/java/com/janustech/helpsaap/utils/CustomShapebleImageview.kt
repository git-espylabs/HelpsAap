package com.janustech.helpsaap.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.R

class CustomShapebleImageview: ShapeableImageView {

    var url: String = ""
        set(value){
            field = value
            Glide.with(this).load(BuildConfig.IMAGE_URL + value).into(this)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs : AttributeSet) : super(context,attrs){
        val array = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
        val url = array.getString(R.styleable.CustomImageView_url)
        if(url!=null){
            Glide.with(this).load(url).into(this)
        }
        array.recycle()
    }

    constructor(context: Context,  attrs: AttributeSet , defStyleAttr : Int) : super(context, attrs, defStyleAttr)
}