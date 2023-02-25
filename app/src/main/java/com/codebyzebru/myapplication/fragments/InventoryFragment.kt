package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.ProductAdapter
import com.codebyzebru.myapplication.dataclasses.InventoryDataClass


class InventoryFragment : Fragment() {

    lateinit var popupView: View
    var itemList = arrayListOf<InventoryDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TO ENABLE OPTION MENU IN FRAGMENT
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_inventory).isChecked = true
        (activity as HomeActivity).setTitle("Inventory")
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.inventory_recyclerView)

        itemList = ArrayList<InventoryDataClass>()
        itemList.add(InventoryDataClass("Facewash", 60))
        itemList.add(InventoryDataClass("Racto Herb", 120))
        itemList.add(InventoryDataClass("Bronco Herb", 110))
        itemList.add(InventoryDataClass("Lekhni Kashai", 160))
        itemList.add(InventoryDataClass("Dabar Avla", 110))
        itemList.add(InventoryDataClass("Sorex", 80))
        itemList.add(InventoryDataClass("Glucon-D", 150))

        itemList.add(InventoryDataClass("Facewash", 60))
        itemList.add(InventoryDataClass("Racto Herb", 120))
        itemList.add(InventoryDataClass("Bronco Herb", 110))
        itemList.add(InventoryDataClass("Lekhni Kashai", 160))
        itemList.add(InventoryDataClass("Dabar Avla", 110))
        itemList.add(InventoryDataClass("Sorex", 80))
        itemList.add(InventoryDataClass("Glucon-D", 150))

        itemList.add(InventoryDataClass("Facewash", 60))
        itemList.add(InventoryDataClass("Racto Herb", 120))
        itemList.add(InventoryDataClass("Bronco Herb", 110))
        itemList.add(InventoryDataClass("Lekhni Kashai", 160))
        itemList.add(InventoryDataClass("Dabar Avla", 110))
        itemList.add(InventoryDataClass("Sorex", 80))
        itemList.add(InventoryDataClass("Glucon-D", 150))

        itemList.add(InventoryDataClass("Facewash", 60))
        itemList.add(InventoryDataClass("Racto Herb", 120))
        itemList.add(InventoryDataClass("Bronco Herb", 110))
        itemList.add(InventoryDataClass("Lekhni Kashai", 160))
        itemList.add(InventoryDataClass("Dabar Avla", 110))
        itemList.add(InventoryDataClass("Sorex", 80))
        itemList.add(InventoryDataClass("Glucon-D", 150))

        itemList.add(InventoryDataClass("Facewash", 60))
        itemList.add(InventoryDataClass("Racto Herb", 120))
        itemList.add(InventoryDataClass("Bronco Herb", 110))
        itemList.add(InventoryDataClass("Lekhni Kashai", 160))
        itemList.add(InventoryDataClass("Dabar Avla", 110))
        itemList.add(InventoryDataClass("Sorex", 80))
        itemList.add(InventoryDataClass("Glucon-D", 150))


        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ProductAdapter(requireContext(), itemList)


//        view.findViewById<Button>(R.id.inventoryFrag_addItem).setOnClickListener {
//            val popupView = layoutInflater.inflate(R.layout.popup_layout_additem, null)
//            val width = LinearLayout.LayoutParams.MATCH_PARENT
//            val high = LinearLayout.LayoutParams.WRAP_CONTENT
//            val focus= true
//            val popup = PopupWindow(popupView, width, high, focus)
//
//            popupView.findViewById<Button>(R.id.btnSaveItem).setOnClickListener {
//                Toast.makeText(context, "Item Added to Inventory", Toast.LENGTH_SHORT).show()
//            }
//
//            popupView.findViewById<Button>(R.id.btn_additem_cancel).setOnClickListener {
//                popup.dismiss()
//            }
//            popup.showAtLocation(view, Gravity.CENTER,0, 0)
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.inventory_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.inventoryFrag_menu_addItem -> {
                val builder = AlertDialog.Builder(requireContext())
//                builder.setTitle("Add Item")
//                builder.setMessage("Enter Details:")

                val inflater = LayoutInflater.from(context)
                popupView = inflater.inflate(R.layout.popup_layout_additem, null)

//                activity?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                builder.setView(popupView)

                val alertDialog = builder.create()

                alertDialog.show()

                popupView.findViewById<Button>(R.id.btnSaveItem).setOnClickListener {
                    alertDialog.dismiss()
                }

                popupView.findViewById<Button>(R.id.btn_additem_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }

            }
        }

        return super.onOptionsItemSelected(item)
    }


}