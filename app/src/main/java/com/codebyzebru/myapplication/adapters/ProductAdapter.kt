package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.InventoryDataClass

class ProductAdapter(val context: Context, val itemList: ArrayList<InventoryDataClass>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolders>() {

    class ProductViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName = itemView.findViewById<TextView>(R.id.singleView_product_name)
        val productPrice = itemView.findViewById<TextView>(R.id.singleView_proudct_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolders {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_view_product, parent, false)
        return ProductViewHolders(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolders, position: Int) {
        holder.productName.text = itemList[position].productName
        holder.productPrice.text = itemList[position].productPrice.toString()
    }


}