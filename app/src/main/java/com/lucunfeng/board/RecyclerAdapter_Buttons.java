package com.lucunfeng.board;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//*定义离线列表适配器
public class RecyclerAdapter_Buttons extends RecyclerView.Adapter<RecyclerAdapter_Buttons.ViewHolder> {

    interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
    interface OnRecyclerItemFocusChangeListener{
        void OnItemFocusChange(View view, int position,Boolean hasFocus);
    }

    private LayoutInflater mInflater;
    public List<String> texts = null;


    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
    public void setOnRecyclerItemFocusCkhangeListener(OnRecyclerItemFocusChangeListener onRecyclerItemFocusCkhangeListener) {
        this.onRecyclerItemFocusCkhangeListener = onRecyclerItemFocusCkhangeListener;
    }
    public void setText_size(String text_size) {
        this.text_size = text_size;
    }

    private OnRecyclerItemClickListener onRecyclerItemClickListener = null;
    private OnRecyclerItemFocusChangeListener onRecyclerItemFocusCkhangeListener =null;
    private int itemLayout;
    private String text_size="34";

    public RecyclerAdapter_Buttons(Context context, List<String> titles, @LayoutRes int itemLayout) {
        this.mInflater = LayoutInflater.from(context);
        this.texts = titles;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("tcp", "onCreateViewHolder: ");
        final View view = mInflater.inflate(itemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        TextView tv= viewHolder.itemView.findViewById(R.id.item_tv);
        tv.setTextSize(Integer.parseInt(text_size));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClick(viewHolder.itemView, (int) viewHolder.itemView.getTag());
                }
            }
        });
        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onRecyclerItemFocusCkhangeListener != null){
                    onRecyclerItemFocusCkhangeListener.OnItemFocusChange(viewHolder.itemView,(int) viewHolder.itemView.getTag(),hasFocus);
                }
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = texts.get(position);
        holder.item_tv.setText(title);

        holder.itemView.setTag(position);



    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item_tv;

        private ViewHolder(View view) {
            super(view);
            item_tv = view.findViewById(R.id.item_tv);

        }
    }
}
