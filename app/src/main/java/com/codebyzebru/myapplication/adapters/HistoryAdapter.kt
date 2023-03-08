package com.codebyzebru.myapplication.adapters

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

class HistoryAdapter() {
}