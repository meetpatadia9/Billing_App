package com.codebyzebru.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import kotlinx.android.synthetic.main.single_view_product.view.*

/*
        RECYCLERVIEW ADAPTER:

        1)  AdapterFile requires two parameters
            i.  Context
            ii. ArrayList<DataClass>
        2)  AdapterFile extends RecyclerView.Adapter<AdapterFile.viewHolders>()
            ~ where "viewHolders" is a ViewHolder class   --->  get IDs of view to be implement
                having View as parameters and
                extending RecyclerView.ViewHolder(view)
        3) Now, Implement three methods of RecyclerView
            i.   onCreateViewHolder()  --->  inflate view here
            ii.  getItemCount()        --->  returns the size of DtaClass
            iii. onBindViewHolder()    --->  fetch appropriate data and uses data to fill in ViewHolder's layout
*/

class ProductAdapter(val context: Context, private val itemList: ArrayList<ViewInventoryDataClass>, private val onItemClick: OnItemClick)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolders>() {

    class ProductViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.singleView_product_name
        val productQty: TextView = itemView.singleView_product_qty
        val productPrice: TextView = itemView.singleView_product_price
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolders {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_view_product, parent, false)
        return ProductViewHolders(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolders, position: Int) {
        //  scope functions `apply`
        holder.apply {
            productName.text = itemList[position].productName
            productQty.text = itemList[position].productQty.toString()
            productPrice.text = itemList[position].sellingPrice.toString()

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