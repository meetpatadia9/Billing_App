@file:Suppress("DEPRECATION")

package com.codebyzebru.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.PurchasedItemAdapter
import com.codebyzebru.myapplication.databinding.FragmentBillBinding
import com.codebyzebru.myapplication.databinding.PopupLayoutBillfragAdditemBinding
import com.codebyzebru.myapplication.databinding.ToastErrorBinding
import com.codebyzebru.myapplication.dataclasses.BillDataClass
import com.codebyzebru.myapplication.dataclasses.PurchasedItemDataClass
import com.codebyzebru.myapplication.dataclasses.ViewInventoryDataClass
import com.codebyzebru.myapplication.dataclasses.ViewPartyDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class BillFragment : Fragment(), PurchasedItemAdapter.SubTotalListener {

    lateinit var binding: FragmentBillBinding
    private lateinit var addItemBinding: PopupLayoutBillfragAdditemBinding

    private var billNo = 0

    private lateinit var userID: String
    private lateinit var database: DatabaseReference
    private lateinit var partyDB: DatabaseReference
    private lateinit var invDB: DatabaseReference

    private lateinit var editor: SharedPreferences.Editor
    private var dataList = arrayListOf<String>()
    private var itemList = arrayListOf<String>()
    private var purchasedItemList = arrayListOf<PurchasedItemDataClass>()
    private var invItemList = arrayListOf<ViewInventoryDataClass>()

    private lateinit var partyID: String
    private var totalPurchasedAmt = 0
    private var productQty = 0

    private lateinit var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        database = Firebase.database.reference

        partyDB = FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data")
        invDB = FirebaseDatabase.getInstance().getReference("Users/$userID/Inventory Data")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
            when the fragment come in picture, respected navigation `menu item` must be highlighted
            and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_bill).isChecked = true
        (activity as HomeActivity).title = "Create Bill"

        //  Inflate the layout for this fragment with binding
        binding = FragmentBillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val autoCompleteTextView = binding.billFragAutoTxtName
        val email = binding.billFragEdtxtEmail
        val organization = binding.billFragEdtxtCompanyName
        val address = binding.billFragEdtxtCompanyAddr
        val contact = binding.billFragEdtxtContact
        val billTotal = binding.billFragEdtxtTotal
        val purchaseRecyclerView = binding.purchaseRecyclerView

        purchaseRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //  SharedPreferences for Bill-Number
        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        billNo = sharedPreferences.getInt("billNo", 1)      //  setting default as 1
        binding.billFragTxtBillNo.setText(billNo.toString())
        Log.d("billNo", billNo.toString())

        binding.billFragTxtDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString()
        /**
        OR YOU CAN USE THIS METHOD ALSO TO GET LOCAL DATE, BY GETTING INSTANCE OF CALENDAR

        val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)        //  month start from 0. (ex. Jan-0, Feb-1,...,Dec-11)
        val year = calendar.get(Calendar.YEAR)

        binding.billFragTxtDate.text = day.toString() + "-" + (month+1).toString() + "-" + year.toString()
         */

        invDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (inv in snapshot.children) {
                        val invData = inv.getValue(ViewInventoryDataClass::class.java)
                        invData!!.key = inv.key.toString()
                        invItemList.add(invData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Failed to fetch Inventory data", error.message)
            }
        })

        //  Fetching Party Data
        partyDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewPartyDataClass::class.java)
                        listedData!!.key = item.key.toString()
                        dataList.add(listedData.partyName)      //  storing `partyName` into separate arraylist

                        //  Setting adapter to `AutoCompleteTextView`
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataList)
                        autoCompleteTextView.setAdapter(adapter)

                        //  `AutoCompleteTextView` item-click-listener
                        autoCompleteTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val selected = parent!!.getItemAtPosition(position) as String       //  getting text of selected item

                                autoCompleteTextView.clearFocus()

                                //  Loop to set data as per Data-Class
                                for (party in snapshot.children) {
                                    val partyNameData = party.getValue(ViewPartyDataClass::class.java)
                                    //  Checking, Is selected item exist in `snapshot.children`
                                    if (partyNameData?.partyName.equals(selected)) {
                                        partyID = party?.key.toString()
                                        email.setText(partyNameData?.email)
                                        organization.setText(partyNameData?.companyName)
                                        address.setText(partyNameData?.address)
                                        contact.setText(partyNameData?.contact)
                                        totalPurchasedAmt = partyNameData?.totalPurchase.toString().toInt()

                                        // hide keyboard after filling these details
                                        hideKeyboard()
                                    }
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.toString())
            }
        })

        //  ADD-ITEM BUTTON
        binding.billFragBtnAddItem.setOnClickListener {
            itemList.clear()

            addItemBinding = PopupLayoutBillfragAdditemBinding.inflate(LayoutInflater.from(requireContext()))

            val dialog = Dialog(requireContext())
            dialog.setContentView(addItemBinding.root)
            dialog.show()

            var itemKey = ""
            val pName = addItemBinding.additemBillFragAutotxtProductName
            val pQty = addItemBinding.additemBillFragEdittxtQty
            val pPrice = addItemBinding.additemBillFragEdittxtPrice

            //  Applying padding between Floating Label and EditText
//            pName.setPadding(25, 25, 25, 25)

            //  Fetching Inventory Data
            invDB.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (item in snapshot.children) {
                            val listedItem = item.getValue(ViewInventoryDataClass::class.java)
                            listedItem!!.key = item.key.toString()
                            itemList.add(listedItem.productName)

                            //  Setting adapter to `AutoCompleteTextView`
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                itemList
                            )
                            pName.setAdapter(adapter)

                            //  `AutoCompleteTextView` item-click-listener
                            pName.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val selected = parent!!.getItemAtPosition(position) as String
                                    hideKeyboard()

                                    //  Loop to set data as per Data-Class
                                    for (product in snapshot.children) {
                                        val pNameData = product.getValue(ViewInventoryDataClass::class.java)
                                        //  Checking, Is selected item exist in `snapshot.children`
                                        if (pNameData?.productName.equals(selected)) {
                                            pPrice.setText(pNameData?.sellingPrice.toString())
                                            itemKey = pNameData!!.key

                                            productQty = pNameData.productQty.toString().toInt()
                                        }
                                    }
                                    pQty.addTextChangedListener(qtyWatcher)
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Error", error.toString())
                }
            })

            //  POPUP ADD-ITEM BUTTON
            addItemBinding.billFragAddItemBtnAdd.setOnClickListener {
                if (pName.text.toString().trim() == "") {
                    addItemBinding.til1.helperText = "Product name is require"
                }
                else if (pQty.text.toString().trim() == "") {
                    addItemBinding.til2.helperText = "Quantity is require"
                }
                else if (pPrice.text.toString().trim() == "") {
                    addItemBinding.til3.helperText = "Selling price is require"
                }
                else {
                    purchasedItemList.add(
                        PurchasedItemDataClass(
                            itemKey,
                            pName.text.toString(),
                            pQty.text.toString().toInt(),
                            pPrice.text.toString().toInt()
                        )
                    )

                    purchaseRecyclerView.adapter = PurchasedItemAdapter(requireContext(), purchasedItemList, this,
                        object : PurchasedItemAdapter.OnClick {
                            override fun removeItem(purchasedItemDataClass: PurchasedItemDataClass) {
                                purchasedItemList.remove(purchasedItemDataClass)
                                if (purchasedItemList.isEmpty()) {
                                    binding.billFragEdtxtTotal.text.clear()
                                }
                            }
                        })
                    dialog.dismiss()
                }
            }
        }

        //  GENERATE BILL BUTTON
        binding.btnGenerateBill.setOnClickListener {
            if (autoCompleteTextView.text.toString().trim() == "") {
                binding.til1.helperText = "Required"
            }
            /*else if (contact.text.toString().trim() == "") {
                binding.til4.helperText = "Required"
            }*/
            else if (binding.billFragTxtBillNo.text.toString().trim() == "") {
                redToast("You cannot proceed without bill number!")
            }
            else if (billTotal.text.toString().trim() == "") {
                redToast("You cannot proceed without total of bill!")
            }
            else {      //  accepting condition
                generateBill()
            }
        }

        keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (requireActivity().isKeyboardOpen()) {
                scrollUpToMyWantedPosition()
            }
        }
    }

    private val qtyWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            addItemBinding.til2.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (addItemBinding.additemBillFragEdittxtQty.text.toString().trim().isNotEmpty()) {
                if (addItemBinding.additemBillFragEdittxtQty.text.toString().toInt() > productQty) {
                    addItemBinding.til2.helperText = "Not enough in stoke"
                    addItemBinding.billFragAddItemBtnAdd.isEnabled = false
                } else {
                    addItemBinding.til2.helperText = ""
                    addItemBinding.billFragAddItemBtnAdd.isEnabled = true
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (addItemBinding.additemBillFragEdittxtQty.text.toString().trim().isNotEmpty()) {
                if (addItemBinding.additemBillFragEdittxtQty.text.toString().toInt() > productQty) {
                    addItemBinding.til2.helperText = "Not enough in stoke"
                    addItemBinding.billFragAddItemBtnAdd.isEnabled = false
                } else {
                    addItemBinding.til2.helperText = ""
                    addItemBinding.billFragAddItemBtnAdd.isEnabled = true
                }
            }
        }
    }

    private fun generateBill() {
        val txtDate = binding.billFragTxtDate
        val edtxtBillNo = binding.billFragTxtBillNo
        val autoCompleteTextView = binding.billFragAutoTxtName
        val email = binding.billFragEdtxtEmail
        val organization = binding.billFragEdtxtCompanyName
        val address = binding.billFragEdtxtCompanyAddr
        val contact = binding.billFragEdtxtContact
        val billTotal = binding.billFragEdtxtTotal

        totalPurchasedAmt += billTotal.text.toString().toInt()

        billNo += 1     //  incrementing Bill-Number by 1
        Log.d("billNo", billNo.toString())
        editor.putInt("billNo", billNo)     //  saving new value in editor
            .apply()

        val bill = BillDataClass(
            date = txtDate.text.toString(),
            billNo = edtxtBillNo.text.toString(),
            buyer = autoCompleteTextView.text.toString(),
            email = email.text?.toString(),
            organization = organization.text?.toString(),
            address = address.text?.toString(),
            contact = contact.text.toString(),
            purchasedItems = purchasedItemList,
            billTotal = billTotal.text.toString().toInt()
        )

        val billKey = database.child("Bills").push().key.toString()

        FirebaseDatabase.getInstance().getReference("Users/$userID").child("Bills").child(billKey).setValue(bill)
            .addOnCompleteListener {
                FirebaseDatabase.getInstance().getReference("Users/$userID/Party Data/$partyID")
                    .child("totalPurchase").setValue(totalPurchasedAmt)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (purchasedItemList.isNotEmpty()) {
                                for (purchasedItemList in purchasedItemList) {
                                    invItemList.find { inv ->
                                        inv.key == purchasedItemList.key
                                    }
                                        .let { inv ->
                                            inv!!.productQty -= purchasedItemList.pQty

                                            FirebaseDatabase.getInstance().getReference("Users/$userID")
                                                .child("Inventory Data").child(purchasedItemList.key).child("productQty")
                                                .setValue(inv?.productQty)
                                        }
                                }
                            }

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.layout_home, HomeFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
            }
    }

    /*
    private fun greenToast() {
        val toastBinding = ToastSuccessBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = "Bill created"
        toast.setView(toastBinding.root)
        toast.setDuration(Toast.LENGTH_LONG)
        toast.show()
    }
    */

    private fun redToast(message: String) {
        val toastBinding = ToastErrorBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.setView(toastBinding.root)
        toast.setDuration(Toast.LENGTH_LONG)
        toast.show()
    }

    private fun scrollUpToMyWantedPosition() = with(binding.billFragScrollView) {
        postDelayed({
            smoothScrollBy(0, binding.billFragEdtxtTotal.y.toInt())
        }, 200)
    }

    fun Fragment.hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onSubTotalUpdate(total: Int) {
        binding.billFragEdtxtTotal.setText(total.toString())
    }

    /*
       TO GET STATE OF KEYBOARD
    */
    //  get root view of activity
    private fun Activity.getRootView(): View {
        return findViewById(android.R.id.content)
    }

    //  convert density-independent pixels (dp) to pixels (px) based on the device's display metrics.
    //  `Context` class represents the current state and information about an application environment.
    private fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,    // indicates that the value being converted is in density-independent pixels (dp).
            dp,
            this.resources.displayMetrics   // provides the display metrics of the current `Context`, which includes information such as screen density.
        )
    }

    private fun Activity.isKeyboardOpen(): Boolean {
        // A `Rect` represents a rectangle with four integer coordinates (left, top, right, and bottom).
        val visibleBounds = Rect()
        // get the visible portion of the window, excluding any system decorations or the soft keyboard.
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        // calculates the height difference between the root view's height and the visible bounds' height.
        // This difference represents the height of the soft keyboard.
        val heightDiff = getRootView().height - visibleBounds.height()
        // convert 50dp to pixels. This value is stored in the marginOfError variable.
        // The Math.round() function is used to round the converted value to the nearest whole number.
        val marginOfError = Math.round(this.convertDpToPx(50F))
        // compares the heightDiff with the marginOfError to determine whether the height difference is greater than the margin of error.
        // If the height difference is greater, it means that the soft keyboard is likely open, and the function returns true.
        // Otherwise, it returns false
        return heightDiff > marginOfError
    }

    /**
    fun Activity.isKeyboardClosed(): Boolean {
    return !isKeyboardOpen()
    }
    */

    override fun onResume() {
        super.onResume()
        val view = requireActivity().getRootView()
        view.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
    }
}