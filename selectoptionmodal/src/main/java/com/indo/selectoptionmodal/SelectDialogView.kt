package com.indo.selectoptionmodal

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

class SelectDialogView<T> private constructor(
    private val context: Context,
    private val title: String?,
    private val items: List<T>,
    private val onItemSelected: (T) -> Unit
) {
    private var dialog: BottomSheetDialog? = null

    class Builder<T> {
        private var items: List<T> = emptyList()
        private var onItemSelected: (T) -> Unit = {}
        private var title: String? = null

        fun setTitle(title: String?): Builder<T> {
            this.title = title
            return this
        }

        fun setItems(items: List<T>): Builder<T> {
            this.items = items
            return this
        }


        fun setOnItemSelected(listener: (T) -> Unit): Builder<T> {
            this.onItemSelected = listener
            return this
        }

        fun build(context: Context): SelectDialogView<T> {
            return SelectDialogView(context, title, items, onItemSelected)
        }
    }

    fun show() {
        dialog = BottomSheetDialog(context).apply {
            val view = LayoutInflater.from(context).inflate(R.layout.select_dialog_fragment, null)
            setContentView(view)

            val recyclerView: RecyclerView = view.findViewById(R.id.sd_rv)
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = SelectorAdapter(
                items = items,
                onItemSelected = onItemSelected,
                onClose = { dismiss() }
            )
            adapter.setItem(items)
            recyclerView.adapter = adapter

            val titleText: TextView = view.findViewById(R.id.sd_title)
            title?.let {
                titleText.text = it
                titleText.visibility = View.VISIBLE
            } ?: run {
                titleText.visibility = View.GONE
            }

            val maxHeight = (context.resources.displayMetrics.heightPixels * 0.4).toInt() // 50% of screen height
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                if (recyclerView.height > maxHeight) {
                    recyclerView.layoutParams.height = maxHeight
                    recyclerView.requestLayout()
                }
            }

            val edSearch : EditText = view.findViewById(R.id.so_search)
            edSearch.afterTextChanged {
                adapter.filter.filter(it)
            }
            setOnDismissListener { dialog = null }
        }
        dialog?.show()
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }


    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    private class SelectorAdapter<T>(
        private var items: List<T>,
        private val onItemSelected: (T) -> Unit,
        private val onClose: () -> Unit
    ) : RecyclerView.Adapter<SelectorAdapter.ViewHolder>(), Filterable {

        private var filteredList: List<T> = items

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.sd_item)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sd, parent, false)
            return ViewHolder(view)
        }

        fun setItem(list: List<T>) {
            this.items = list
            this.filteredList= list;
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = filteredList[position]
            holder.textView.text = item.toString()
            holder.itemView.setOnClickListener {
                onItemSelected(item)
                onClose()
            }
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val query = constraint?.toString()?.lowercase(Locale.getDefault())
                    val resultList = if (query.isNullOrEmpty()) {
                        items
                    } else {
                        items.filter {
                            it.toString().lowercase(Locale.getDefault()).contains(query)
                        }
                    }

                    return FilterResults().apply {
                        values = resultList
                    }
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredList = results?.values as? List<T> ?: emptyList()
                    notifyDataSetChanged()
                }
            }
        }

        override fun getItemCount(): Int = filteredList.size
    }
}