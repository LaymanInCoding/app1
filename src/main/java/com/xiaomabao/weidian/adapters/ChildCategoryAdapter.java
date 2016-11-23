package com.xiaomabao.weidian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.models.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChildCategoryAdapter extends RecyclerView.Adapter<ChildCategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category.ChildCategory> categories;

    private OnItemClickListener onItemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public ChildCategoryAdapter(Context context, List<Category.ChildCategory> categories){
        this.context = context;
        this.categories = categories;
    }

    @Override
    public ChildCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_child,parent,false);;
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ChildCategoryAdapter.ViewHolder holder, int position) {
        Category.ChildCategory category = categories.get(position);
        Glide.with(context)
                .load(category.icon)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.childCategoryImageView);
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener((View v) -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.child_category_img)
        ImageView childCategoryImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
