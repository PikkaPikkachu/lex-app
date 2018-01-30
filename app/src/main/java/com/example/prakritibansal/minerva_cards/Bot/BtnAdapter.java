package com.example.prakritibansal.minerva_cards.Bot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.services.lexrts.model.Button;


import com.example.prakritibansal.minerva_cards.TextActivity;
import com.example.prakritibansal.minerva_login.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.prakritibansal.minerva_cards.Login.AppController.TAG;
import com.example.prakritibansal.minerva_cards.TextActivity;

import org.w3c.dom.Text;

/**
 * Created by prakritibansal on 12/29/17.
 */

public class BtnAdapter extends ArrayAdapter<Button>{

    private Context context;
    private List <Button> btnList;
    private static LayoutInflater layoutInflater;


    public BtnAdapter(Context context, ArrayList<Button> btnlist) {
        super(context, 0, btnlist);
        this.context = context;
        this.btnList = btnlist;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = layoutInflater.inflate(R.layout.button_fragment, null);
            holder.data = convertView.findViewById(R.id.button);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }
        Button btn = btnList.get(position);
        if(btn!= null){
            final String text = btn.getValue();
            holder.data.setText(btn.getText());
            holder.data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "-----BUTTON PRESSED-----"+text);
                    TextActivity.convContinuation.continueWithTextInForTextOut(text);
                    view.findViewById(R.id.button).setEnabled(false);
                    TextActivity.lexIsResponding = true;
                    TextActivity.userInp.setVisibility(LinearLayout.GONE);
                    TextActivity.dotsLoading.setVisibility(LinearLayout.VISIBLE);
                }
            });

        }
        return convertView;
    }

    // Helper class to recycle View's
    static class Holder {
        android.widget.Button data;
    }


}
