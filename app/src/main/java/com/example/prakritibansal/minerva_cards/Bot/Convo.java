package com.example.prakritibansal.minerva_cards.Bot;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static android.content.ContentValues.TAG;

/**
 * Created by prakritibansal on 12/23/17.
 */

public class Convo {
    private static List<Message> currentConversation;
    private static TreeSet<Integer> mCardSet;
    private static TreeSet<Integer> mSentMsg;

    public static void clear() {
        currentConversation = new ArrayList<Message>();
        mCardSet = new TreeSet<Integer>();
        mSentMsg = new TreeSet<Integer>();
    }

    public static void add(final Message message) {
        if (currentConversation == null) {
            clear();
        }

        currentConversation.add(message);
        Log.d(TAG, "message added to the list "+ message);
        if("cd".equals(message.getFrom())){
            mCardSet.add(getCount()-1);
            Log.d(TAG, "message card to the list "+ message);
        }
        if("tx".equals(message.getFrom())){

            mSentMsg.add(getCount()-1);
            Log.d(TAG, "message sent to the list "+ message);
        }

    }

    public static boolean inCardSet(int pos){

        return mCardSet.contains(pos);
    }

    public static boolean inSentSet(int pos){

        return mSentMsg.contains(pos);
    }

    public static Message getMessage(final int pos) {
        if (currentConversation == null) {
            return null;
        }
        return currentConversation.get(pos);
    }

    public static int getCount() {
        return currentConversation == null ? 0 : currentConversation.size();
    }
}
