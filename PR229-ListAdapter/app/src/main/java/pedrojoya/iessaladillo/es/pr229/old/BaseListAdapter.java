package pedrojoya.iessaladillo.es.pr229.old;

import android.view.View;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

// V is ViewModel type, M is Model type.
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseListAdapter<M, V extends BaseViewHolder<M>> extends ListAdapter<M, V> {

    private OnItemClickListener<M> onItemClickListener;
    private OnItemLongClickListener<M> onItemLongClickListener;
    private View emptyView;

    public BaseListAdapter(DiffUtil.ItemCallback<M> diffUtilItemCallback) {
        super(diffUtilItemCallback);
        checkEmptyViewVisibility(getItemCount());
    }

    @Override
    public void submitList(List<M> list) {
        checkEmptyViewVisibility(list == null ? 0 : list.size());
        super.submitList(list);
    }

    public void setOnItemClickListener(OnItemClickListener<M> listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<M> listener) {
        this.onItemLongClickListener = listener;
    }

    public void setEmptyView(@NonNull View emptyView) {
        this.emptyView = emptyView;
        checkEmptyViewVisibility(getItemCount());
    }

    private void checkEmptyViewVisibility(int size) {
        if (emptyView != null) {
            emptyView.setVisibility(size == 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        holder.bind(getItem(position));
    }

    public OnItemClickListener<M> getOnItemClickListener() {
        return onItemClickListener;
    }

    public OnItemLongClickListener<M> getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public View getEmptyView() {
        return emptyView;
    }

    @Override
    public M getItem(int position) {
        return super.getItem(position);
    }

}
