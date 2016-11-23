package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int NormalViewType = 0x01;
    private final int SelectedViewType = 0x02;

    private OnItemClickListener onItemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories){
        this.context = context;
        this.categories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == NormalViewType){
            view = LayoutInflater.from(context).inflate(R.layout.item_category_normal,parent,false);
            return new NormalViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_category_selected,parent,false);
            return new SelectedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Category category = categories.get(position);
        if(category.selected == 1){
            ((SelectedViewHolder)(holder)).selectedCategoryTextView.setText(category.cat_name);
        }else{
            ((NormalViewHolder)(holder)).normalCategoryTextView.setText(category.cat_name);
        }
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener((View v) -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(categories.get(position).selected == 1){
            return SelectedViewType;
        }else{
            return NormalViewType;
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.normal_category_text)
        TextView normalCategoryTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SelectedViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.select_category_text)
        TextView selectedCategoryTextView;
        public SelectedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
