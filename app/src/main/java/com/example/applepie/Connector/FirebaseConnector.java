package com.example.applepie.Connector;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseConnector {
    private static FirebaseFirestore instance;

    public static FirebaseFirestore getInstance(){
        if (instance==null) {
            instance=FirebaseFirestore.getInstance();

        }
        return instance;
    }
}
