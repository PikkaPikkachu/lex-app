package com.example.prakritibansal.minerva_cards.Bot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import android.support.v7.widget.RecyclerView;

import com.amazonaws.services.lexrts.model.Button;
import com.example.prakritibansal.minerva_login.R;

import java.util.ArrayList;

import static com.example.prakritibansal.minerva_cards.Login.AppController.TAG;

/**
 * Created by prakritibansal on 12/23/17.
 */

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int count;

    public MsgAdapter(Context context) {
        this.context = context;
        count = Convo.getCount();
    }


    public class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subTitle;
        public ImageView imageUrl;
        public ListView btnlist;

        public CardViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.titleTextView);
            subTitle = (TextView) view.findViewById(R.id.subTitleView);
            imageUrl = (ImageView) view.findViewById(R.id.coverImageView);
            btnlist = (ListView) view.findViewById(R.id.btnlistview);

        }
    }

    public class ResponseViewHolder extends RecyclerView.ViewHolder {
        public TextView data;

        public ResponseViewHolder(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.editTextUserDetailInput_rx);
        }
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {
        public TextView data;

        public SentViewHolder(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.editTextUserDetailInput_tx);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(Convo.inCardSet(position)){
            return 0;
        }else if(Convo.inSentSet(position)){
            return 1;
        }else{
            return 2;
        }
    }

    public int getCount() {
        return count;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card, parent, false);
                return new CardViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
                return new SentViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_response, parent, false);
                return new ResponseViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message item = Convo.getMessage(position);
        switch (holder.getItemViewType()) {
            case 0:
                final BtnAdapter btnAdapter = new BtnAdapter(this.context, (ArrayList<Button>)item.getBtnList());
                CardViewHolder cvh = (CardViewHolder) holder;
                ((CardViewHolder) holder).title.setText(item.getMessage());
                ((CardViewHolder) holder).subTitle.setText(item.getSubTitle());
                ((CardViewHolder) holder).btnlist.setAdapter(btnAdapter);
                ((CardViewHolder) holder).btnlist.setDivider(null);
                setListViewHeightBasedOnChildren(((CardViewHolder) holder).btnlist);
                Log.d(TAG, "----IMAGE-----"+item.getImageUrl());
                Picasso.with(context).load(item.getImageUrl()).fit().into(((CardViewHolder) holder).imageUrl);
                break;

            case 1:
                SentViewHolder svh = (SentViewHolder)holder;
                ((SentViewHolder) holder).data.setText(item.getMessage());
                break;

            case 2:
                ResponseViewHolder rvh = (ResponseViewHolder)holder;
                ((ResponseViewHolder) holder).data.setText(item.getMessage());
                break;
        }


    }




//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        int theType = getItemViewType(position);
//        Holder holder = null;
//            if (convertView == null) {
//
//                holder = new Holder();
//
//                if (theType == 0) {
//                    convertView = layoutInflater.inflate(R.layout.message_card, null);
//                    holder.data = convertView.findViewById(R.id.titleTextView);
//                    holder.subTitle = convertView.findViewById(R.id.subTitleView);
//                    holder.imageUrl = convertView.findViewById(R.id.coverImageView);
//                    holder.btnlist = convertView.findViewById(R.id.btnlistview);
//
//
//                } else if (theType == 1) {
//                    convertView = layoutInflater.inflate(R.layout.message_sent, null);
//                    holder.data = (TextView) convertView.findViewById(R.id.editTextUserDetailInput_tx);
//                }else if(theType == 2){
//                    convertView = layoutInflater.inflate(R.layout.message_response, null);
//                    holder.data = (TextView) convertView.findViewById(R.id.editTextUserDetailInput_rx);
//
//                }
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//        Message item = Convo.getMessage(position);
//            if(item!= null){
//                if(theType == 0){
//
//
//                    final BtnAdapter btnAdapter = new BtnAdapter(this.context, (ArrayList<Button>)item.getBtnList());
//                    holder.data.setText(item.getMessage());
//                    holder.subTitle.setText(item.getSubTitle());
//                    holder.btnlist.setAdapter(btnAdapter);
//                    holder.btnlist.setDivider(null);
//                    setListViewHeightBasedOnChildren(holder.btnlist);
//                    Log.d(TAG, "----IMAGE-----"+item.getImageUrl());
//                    Picasso.with(context).load(item.getImageUrl()).into(holder.imageUrl);
//                    //Picasso.with(context).load("http://camrosenow7barz.s3.amazonaws.com/wp4fe71d2c_06.png").fit().centerCrop().into(holder.imageUrl);
//
//
//                }else if(theType == 1){
//                    holder.data.setText(item.getMessage());
//                }else if(theType == 2){
//                    holder.data.setText(item.getMessage());
//                }
//            }
//        return convertView;
//    }

    // Helper class to recycle View's
//    static class Holder {
//        TextView data;
//        TextView subTitle;
//        ImageView imageUrl;
//        ListView btnlist;
//    }
//
//
//    // Add new items
//    public void refreshList(Message message) {
//        Convo.add(message);
//        notifyDataSetChanged();
//    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



}

