package com.example.mobile.plugin;

import android.widget.LinearLayout;

public interface Plugin {
    String TABLE_NAME = "plugin";
    String ID = "_id";
    String NAME_FIELD = "name_field";
    String VALUE_FIELD = "value_field";
    String CONTACT_ID = "contact_id";

    void save(long contactId);

    void addPlugin(LinearLayout linearLayout);

    default void getPlugin(LinearLayout linearLayout, String... contactId) {
        this.getPlugin(linearLayout, false, contactId);
    }

    void getPlugin(LinearLayout linearLayout, boolean isEnabled, String... contactId);

    void delete(String... contactId);
}
