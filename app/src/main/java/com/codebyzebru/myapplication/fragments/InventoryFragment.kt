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
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_inventory).isChecked = true
        (activity as HomeActivity).setTitle("Inventory")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.inventory_recyclerView)

        /*
                1)  Create DataClass with required parameters
                2)  Create AdapterFile
        */
        //  STATIC DATA TO PLACE IN RECYCLE-VIEW
        itemList = ArrayList<InventoryDataClass>()
        itemList.apply {
            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))

            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))

            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))

            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))

            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))

            add(InventoryDataClass("Facewash", 60))
            add(InventoryDataClass("Racto Herb", 120))
            add(InventoryDataClass("Bronco Herb", 110))
            add(InventoryDataClass("Lekhni Kashai", 160))
            add(InventoryDataClass("Dabar Avla", 110))
            add(InventoryDataClass("Sorex", 80))
            add(InventoryDataClass("Glucon-D", 150))
        }

        //  applying `Layout` to Recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //  applying `adapter` to Recyclerview
        recyclerView.adapter = ProductAdapter(requireContext(), itemList)
    }


    //  TO INFLATE OPTION MENU
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.inventory_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.inventoryFrag_menu_addItem -> {
                //  subclass of `Dialog`
                val builder = AlertDialog.Builder(requireContext())

                //  Instantiates a layout XML file into its corresponding `View` objects
                val inflater = LayoutInflater.from(context)
                popupView = inflater.inflate(R.layout.popup_layout_additem, null)
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