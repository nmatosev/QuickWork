package com.quickwork.utilities;

import com.quickwork.model.Message;

import java.util.Comparator;

public class DateComparator implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
        return m1.getCreatedDate().compareTo(m2.getCreatedDate());
    }
}
