@file:Suppress("DEPRECATION")

package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.ProductAdapter
import com.codebyzebru.myapplication.databinding.FragmentInventoryBinding
import com.codebyzebru.myapplication.databinding.FragmentProductUpdateBinding
import com.codebyzebru.myapplication.databinding.PopupLayoutAdditemBinding
import com.codebyzebru.myapplication.databinding.ToastErrorBinding
import com.codebyzebru.myapplication.databinding.ToastSuccessBinding
import com.codebyzebru.myapplication.dataclasses.AddInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class InventoryFragment : Fragment() {

    lateinit var binding: FragmentInventoryBinding
    private lateinit var addBinding: PopupLayoutAdditemBinding
    private lateinit var updateBinding: FragmentProductUpdateBinding

    private var itemList = arrayListOf<ViewInventoryDataClass>()

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_inventory).isChecked = true
        (activity as HomeActivity).title = "Inventory"

        //  Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  applying `Layout` to Recyclerview
        binding.inventoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //  Fetching Inventory Data
        FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").orderByChild("productName")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemList.clear()
                    if (snapshot.exists()) {
                        binding.inventoryFragNoDataFrameLayout.visibility = View.GONE
                        for (item in snapshot.children) {
                            val listedData = item.getValue(ViewInventoryDataClass::class.java)
                            listedData!!.key = item.key.toString()
                            itemList.add(listedData)
                        }

                        binding.inventoryRecyclerView.adapter = ProductAdapter(requireActivity(), itemList,
                            object : ProductAdapter.OnItemClick {
                                override fun onClick(listDataClass: ViewInventoryDataClass) {
                                    openUpdatePopup(listDataClass)
                                }
                            })
                    }
                    else
                    {
                        binding.inventoryFragNoDataFrameLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })

        //  ADD NEW PRODUCT
        binding.inventoryFragFab.setOnClickListener {
            addBinding = PopupLayoutAdditemBinding.inflate(LayoutInflater.from(requireContext()))
            val addDialog = Dialog(requireContext())
            addDialog.setContentView(addBinding.root)
            addDialog.show()

            val pName = addBinding.addItemEdtxtProductName
            val pPrice = addBinding.addItemEdtxtPurchasePrise
            val sPrice = addBinding.addItemEdtxtSellingPrice
            val qty = addBinding.addItemEdtxtProductQty

            //  `TextWatcher` on editTexts
            pName.addTextChangedListener(addTextWatcher)
            pPrice.addTextChangedListener(addTextWatcher)
            sPrice.addTextChangedListener(addTextWatcher)
            qty.addTextChangedListener(addTextWatcher)

            //  ADD BUTTON
            addBinding.btnSaveItem.setOnClickListener {
                if (pName.text.toString().trim() == "") {
                    addBinding.til1.helperText = "Product name is require"
                }
                else if (sPrice.text.toString().trim() == "") {
                    addBinding.til3.helperText = "Selling Price is require"
                }
                else if (qty.text.toString().trim() == "") {
                    addBinding.til4.helperText = "Quantity is require"
                }
                else {      //  accepting condition
                    itemList.clear()
                    //  generating new `push()` key first so can be used later if required, so push ID does no get changed
                    val newKey = database.child("Inventory Data").push().key.toString()

                    val addItem = AddInventoryDataClass(
                        key= newKey,
                        productName = pName.text.toString(),
                        purchasingPrice = pPrice.text?.toString(),
                        sellingPrice = sPrice.text.toString().toInt(),
                        productQty = qty.text.toString().toInt()
                    )

                    FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(newKey).setValue(addItem)
                        .addOnSuccessListener {
                            addDialog.dismiss()
                            greenToast("Item added to inventory.")
                        }
                        .addOnFailureListener {
                            redToast(it.message.toString())
                        }

                }
            }

            //  CANCEL BUTTON
            addBinding.btnAdditemCancel.setOnClickListener {
                addDialog.dismiss()
            }
        }
    }

    //  UPDATE DATA DIALOG
    private fun openUpdatePopup(listDataClass: ViewInventoryDataClass) {
        updateBinding = FragmentProductUpdateBinding.inflate(LayoutInflater.from(requireContext()))

        val updateDialog = Dialog(requireContext())
        updateDialog.setContentView(updateBinding.root)
        updateDialog.show()

        val pName = updateBinding.updateProductName
        val pPrice = updateBinding.updatePurchasePrise
        val sPrice = updateBinding.updateSellingPrice
        val qty = updateBinding.updateProductQty

        //  `TextWatcher` on editTexts
        pName.addTextChangedListener(updateTextWatcher)
        pPrice.addTextChangedListener(updateTextWatcher)
        sPrice.addTextChangedListener(updateTextWatcher)
        qty.addTextChangedListener(updateTextWatcher)

        //  Setting data came from database
        key = listDataClass.key
        pName.setText(listDataClass.productName)
        pPrice.setText(listDataClass.purchasingPrice.toString())
        sPrice.setText(listDataClass.sellingPrice.toString())
        qty.setText(listDataClass.productQty.toString())

        //  UPDATE BUTTON
        updateBinding.btnUpdateItem.setOnClickListener {
            if (pName.text.toString().trim() == "") {
                updateBinding.til1.helperText = "Product name is required"
            }
            else if (sPrice.text.toString().trim() == "") {
                updateBinding.til3.helperText = "Selling price is required"
            }
            else if (qty.text.toString().trim() == "") {
                updateBinding.til4.helperText = "Quantity is required"
            }
            else {      //  accepting condition
                val addItem = AddInventoryDataClass(
                    key = key,
                    productName = pName.text.toString(),
                    purchasingPrice = pPrice.text?.toString(),
                    sellingPrice = sPrice.text.toString().toInt(),
                    productQty = qty.text.toString().toInt()
                )

                val thisKey = database.child("Inventory Data").push().key.toString()
                FirebaseDatabase.getInstance().getReference("Users/$userID").child("Inventory Data").child(thisKey).setValue(addItem)

                FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                    .addOnSuccessListener {
                        updateDialog.dismiss()
                        greenToast("Item data updated.")
                    }
                    .addOnFailureListener {
                        redToast(it.message.toString())
                    }
            }
        }

        //  DELETE BUTTON
        updateBinding.btnDeleteItem.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data").child(key).removeValue()
                .addOnSuccessListener {
                    updateDialog.dismiss()
                    greenToast("Item removed from inventory.")
                }
                .addOnFailureListener {
                    redToast(it.message.toString())
                }
        }
    }

    //  `TextWatcher` for Add-Item-Dialog fields
    private val addTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            addBinding.til1.helperText = ""
            addBinding.til3.helperText = ""
            addBinding.til4.helperText = ""

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s.hashCode()) {
                addBinding.addItemEdtxtProductName.text.hashCode() -> {
                    if (addBinding.addItemEdtxtProductName.text.toString().trim() == "") {
                        addBinding.til1.helperText = "Product name is required"
                    }
                }
                addBinding.addItemEdtxtSellingPrice.text.hashCode() -> {
                    if (addBinding.addItemEdtxtSellingPrice.text.toString().trim() == "") {
                        addBinding.til3.helperText = "Selling price is required"
                    }
                }
                addBinding.addItemEdtxtProductQty.text.hashCode() -> {
                    if (addBinding.addItemEdtxtProductName.text.toString().trim() == "") {
                        addBinding.til4.helperText = "Quantity is required"
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (s.hashCode()) {
                addBinding.addItemEdtxtProductName.text.hashCode() -> {
                    if (addBinding.addItemEdtxtProductName.text.toString().trim() == "") {
                        addBinding.til1.helperText = "Product name is require"
                    }
                }
                addBinding.addItemEdtxtSellingPrice.text.hashCode() -> {
                    if (addBinding.addItemEdtxtSellingPrice.text.toString().trim() == "") {
                        addBinding.til3.helperText = "Selling price is require"
                    }
                }
                addBinding.addItemEdtxtProductQty.text.hashCode() -> {
                    if (addBinding.addItemEdtxtProductName.text.toString().trim() == "") {
                        addBinding.til4.helperText = "Quantity is require"
                    }
                }
            }
        }
    }

    //  `TextWatcher` for Update-Item-Dialog fields
    private val updateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            updateBinding.til1.helperText = ""
            updateBinding.til3.helperText = ""
            updateBinding.til4.helperText = ""

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s.hashCode()) {
                updateBinding.updateProductName.text.hashCode() -> {
                    if (updateBinding.updateProductName.text.toString().trim() == "") {
                        updateBinding.til1.helperText = "Product name is require"
                    }
                }
                updateBinding.updateSellingPrice.text.hashCode() -> {
                    if (updateBinding.updateSellingPrice.text.toString().trim() == "") {
                        updateBinding.til3.helperText = "Selling price is require"
                    }
                }
                updateBinding.updateProductQty.text.hashCode() -> {
                    if (updateBinding.updateProductQty.text.toString().trim() == "") {
                        updateBinding.til4.helperText = "Quantity is require"
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (s.hashCode()) {
                updateBinding.updateProductName.text.hashCode() -> {
                    if (updateBinding.updateProductName.text.toString().trim() == "") {
                        updateBinding.til1.helperText = "Product name is require"
                    }
                }
                updateBinding.updateSellingPrice.text.hashCode() -> {
                    if (updateBinding.updateSellingPrice.text.toString().trim() == "") {
                        updateBinding.til3.helperText = "Selling price is require"
                    }
                }
                updateBinding.updateProductQty.text.hashCode() -> {
                    if (updateBinding.updateProductQty.text.toString().trim() == "") {
                        updateBinding.til4.helperText = "Quantity is require"
                    }
                }
            }
        }
    }

    private fun greenToast(message: String) {
        val toastBinding = ToastSuccessBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.setView(toastBinding.root)
        toast.setDuration(Toast.LENGTH_LONG)
        toast.show()
    }

    private fun redToast(message: String) {
        val toastBinding = ToastErrorBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.setView(toastBinding.root)
        toast.setDuration(Toast.LENGTH_LONG)
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        itemList.clear()
    }

}