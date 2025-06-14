package com.example.applepie.Connector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;  // Phiên bản cơ sở dữ liệu

    // Tạo bảng User
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS User (" +
            "id TEXT PRIMARY KEY, " +
            "name TEXT);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);  // Tạo bảng User nếu chưa tồn tại
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {  // Nếu phiên bản cũ < 2, thì thực hiện nâng cấp
            // Thêm cột email vào bảng User
            db.execSQL("ALTER TABLE User ADD COLUMN email TEXT");
        }

        // Nếu bạn có thêm thay đổi trong các phiên bản sau, bạn có thể tiếp tục thực hiện nâng cấp
        if (oldVersion < 3) {
            // Ví dụ thêm cột phone vào bảng
            db.execSQL("ALTER TABLE User ADD COLUMN phone TEXT");
        }
    }
    public void saveUser(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);

        // Thực hiện chèn dữ liệu
        db.insert("User", null, values);
        db.close();
    }

    // Lấy thông tin người dùng từ bảng User
    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM User", null);
    }

    // Xóa thông tin người dùng khi đăng xuất
    public void logoutUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM User");  // Xóa tất cả người dùng trong bảng User
        db.close();
    }
}
