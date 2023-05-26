package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.databinding.SingleViewProductBinding
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass

class ProductAdapter(val context: Context, private val itemList: ArrayList<ViewInventoryDataClass>, private val onItemClick: OnItemClick)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolders>() {

    class ProductViewHolders(val itemBinding: SingleViewProductBinding): RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolders {
        val itemBinding = SingleViewProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolders(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolders, position: Int) {
        //  scope functions `apply`
        holder.apply {
            with(itemList[position]) {
                itemBinding.singleViewProductName.text = this.productName
                itemBinding.singleViewProductQty.text = this.productQty.toString()
                itemBinding.singleViewProductPrice.text = this.sellingPrice.toString()
            }

            itemView.setOnClickListener {
                onItemClick.onClick(itemList[position])
            }

            //  Animation on Recyclerview, when it loads new ItemView
            itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recyclerview)
        }
    }

    interface OnItemClick {
        fun onClick(listDataClass: ViewInventoryDataClass)
    }

}