package com.example.applepie.Storage;

import com.example.applepie.Model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class CartStorage {
    private static final List<OrderModel> cartItems = new ArrayList<>();

    public static void addItem(OrderModel order) {
        cartItems.add(order);
    }

    public static List<OrderModel> getItems() {
        return new ArrayList<>(cartItems);
    }

    public static void clear() {
        cartItems.clear();
    }
}