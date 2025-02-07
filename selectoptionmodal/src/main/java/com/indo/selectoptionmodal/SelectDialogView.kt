package com.indo.selectoptionmodal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class SelectDialogView<T> private constructor(
    private val context: Context,
    private val title: String?,
    private val items: List<T>,
    private val itemToString: (T) -> String = { it.toString() },
    private val onItemSelected: (T) -> Unit
) {
    private var dialog: BottomSheetDialog? = null

    class Builder<T> {
        private var items: List<T> = emptyList()
        private var itemToString: (T) -> String = { it.toString() }
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

        fun setItemToString(converter: (T) -> String): Builder<T> {
            this.itemToString = converter
            return this
        }

        fun setOnItemSelected(listener: (T) -> Unit): Builder<T> {
            this.onItemSelected = listener
            return this
        }

        fun build(context: Context): SelectDialogView<T> {
            return SelectDialogView(context, title, items, itemToString, onItemSelected)
        }
    }

    fun show() {
        dialog = BottomSheetDialog(context).apply {
            val view = LayoutInflater.from(context).inflate(R.layout.select_dialog_fragment, null)
            setContentView(view)

            val recyclerView: RecyclerView = view.findViewById(R.id.sd_rv)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = SelectorAdapter(
                items = items,
                itemToString = itemToString,
                onItemSelected = onItemSelected,
                onClose = { dismiss() }
            )

            val titleText: TextView = view.findViewById(R.id.sd_title)
            title?.let {
                titleText.text = it
                titleText.visibility = View.VISIBLE
            } ?: run {
                titleText.visibility = View.GONE
            }

            setOnDismissListener { dialog = null }
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    private class SelectorAdapter<T>(
        private val items: List<T>,
        private val itemToString: (T) -> String,
        private val onItemSelected: (T) -> Unit,
        private val onClose: () -> Unit
    ) : RecyclerView.Adapter<SelectorAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.sd_item)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sd, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textView.text = itemToString(item)
            holder.itemView.setOnClickListener {
                onItemSelected(item)
                onClose()
            }
        }

        override fun getItemCount(): Int = items.size
    }
}