package com.example.carrepair.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrepair.R;
import com.example.carrepair.model.CarShopModel;
import com.example.carrepair.model.CommentModel;

import java.util.ArrayList;

public class AdapterCommentList extends RecyclerView.Adapter<AdapterCommentList.VersionViewHolder> {

    ArrayList<CommentModel> model;
    Context context;
    OnItemClickListener clickListener;

    public AdapterCommentList(Context applicationContext, ArrayList<CommentModel> model) {
        this.context = applicationContext;
        this.model = model;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comment_item, viewGroup, false);
        return new VersionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final VersionViewHolder holder, final int i) {
        holder.txtTitle.setText(model.get(i).getUsername());
        holder.txtComment.setText(model.get(i).getComment());
    }

    @Override
    public int getItemCount() {
        return model == null ? 0 : model.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle,txtComment;
        VersionViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtComment = itemView.findViewById(R.id.txtComment);
        }

        @Override
        public void onClick(View v) {

//            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
