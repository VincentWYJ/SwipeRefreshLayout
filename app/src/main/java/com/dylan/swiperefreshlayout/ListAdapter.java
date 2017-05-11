package com.dylan.swiperefreshlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<String> mDataListS;

    private final int NormalType = 0;
    private final int FootType = 1;

    private boolean mShowFooter;

    public ListAdapter(Context context, List<String> dataListS) {
        this.mContext = context;
        this.mDataListS = dataListS;
    }

    @Override
    public int getItemCount() {
        return mDataListS.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FootType;
        } else {
            return NormalType;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NormalType) {
            return new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, null));
        } else {
            return new FootHolder(LayoutInflater.from(mContext).inflate(R.layout.list_footer, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            ((NormalHolder) holder).mTextView.setText(mDataListS.get(position));
        } else {
            if (mShowFooter) {
                ((FootHolder) holder).mRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                ((FootHolder) holder).mRelativeLayout.setVisibility(View.GONE);
            }
        }
    }

    public void updateAdapter() {
        mShowFooter = !mShowFooter;
        notifyDataSetChanged();
    }

    static class NormalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_text)
        TextView mTextView;

        public NormalHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class FootHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_footer)
        RelativeLayout mRelativeLayout;

        public FootHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
