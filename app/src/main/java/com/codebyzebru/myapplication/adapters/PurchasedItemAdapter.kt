package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass

class PurchasedItemAdapter(val context: Context, private val itemList: List<PurchasedItemDataClass>)
    : RecyclerView.Adapter<PurchasedItemAdapter.PurchaseViewHolder>() {


    class PurchaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name:TextView = itemView.findViewById(R.id.purchased_pName)
        val qty:TextView = itemView.findViewById(R.id.purchased_pQty)
        val price:TextView = itemView.findViewById(R.id.purchased_pPrice)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove_purchased_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_purchase_item, parent, false)
        return PurchaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        holder.apply {
            val iPrice = itemList[position].pPrice
            val iQty = itemList[position].pQty.toInt()
            val total = iPrice * iQty

            name.text = itemList[position].pName
            qty.text = itemList[position].pQty
            price.text = total.toString()

        }
    }
}