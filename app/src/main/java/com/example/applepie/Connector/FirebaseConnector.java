package com.example.applepie.Connector;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConnector {
    private static FirebaseFirestore instance;

    public static FirebaseFirestore getInstance(){
        if (instance==null) {
            instance=FirebaseFirestore.getInstance();

        }
        return instance;
    }
}
