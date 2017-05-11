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

    private ItemClickListener mClickListener;

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
            return new FooterHolder(LayoutInflater.from(mContext).inflate(R.layout.list_footer, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            ((NormalHolder) holder).mTextView.setText(mDataListS.get(position));
        } else {
            if (mShowFooter) {
                ((FooterHolder) holder).mRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                ((FooterHolder) holder).mRelativeLayout.setVisibility(View.GONE);
            }
        }
    }

    public void updateAdapter() {
        mShowFooter = !mShowFooter;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.mClickListener = listener;
    }

    class NormalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_text)
        TextView mTextView;

        public NormalHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null){
                mClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_footer)
        RelativeLayout mRelativeLayout;

        public FooterHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
