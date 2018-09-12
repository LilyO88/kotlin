package es.iessaladillo.pedrojoya.pr178.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

// Kotlin only supports automatic SAM conversions for Java interfaces.
// So we create an extension function for conversion, maintaining compatibility
// with Java.
fun <M, V: BaseViewHolder> BaseListAdapter<M, V>.setOnItemClickListener(
        action: (View, Int) -> Unit) {
    onItemClickListener = object: BaseListAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            action(view, position)
        }
    }
}
@Suppress("unused")
fun <M, V: BaseViewHolder> BaseListAdapter<M, V>.setOnItemLongClickListener(
        action: (View, Int) -> Boolean) {
    onItemLongClickListener = object: BaseListAdapter.OnItemLongClickListener {
        override fun onItemLongClick(view: View, position: Int): Boolean {
            return action(view, position)
        }
    }
}

// V is ViewModel type, M is Model type.
@Suppress("UNUSED")
abstract class BaseListAdapter<M, V : BaseViewHolder>(
        private var data: ArrayList<M> = ArrayList()) : RecyclerView.Adapter<V>() {

    var onItemClickListener: OnItemClickListener? = null

    var onItemLongClickListener: OnItemLongClickListener? = null

    var emptyView: View? = null
        set(value) {
            field?.let { unregisterAdapterDataObserver(adapterDataObserver) }
            field = value
            registerAdapterDataObserver(adapterDataObserver)
            checkEmptyViewVisibility()
        }

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkEmptyViewVisibility()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkEmptyViewVisibility()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkEmptyViewVisibility()
        }
    }

    fun submitList(data: ArrayList<M>) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun checkEmptyViewVisibility() =
            emptyView?.let { it.visibility = if (itemCount == 0) View.VISIBLE else View.INVISIBLE }

    override fun getItemCount() = data.size

    fun getItem(position: Int) = data[position]

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(item: M) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    fun insertItem(item: M, position: Int) {
        data.add(position, item)
        notifyItemInserted(position)
    }

    fun swapItems(from: Int, to: Int) {
        Collections.swap(data, from, to)
        notifyItemMoved(from, to)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int): Boolean
    }
}
