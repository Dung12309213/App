package com.example.applepie.Connector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.applepie.Model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_history.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và cột
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IS_FROM_BOT = "is_from_bot";
    private static final String COLUMN_IS_IMAGE = "is_image";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONTENT + " TEXT,"
            + COLUMN_IS_FROM_BOT + " INTEGER," // 0 for false, 1 for true
            + COLUMN_IS_IMAGE + " INTEGER,"    // 0 for false, 1 for true
            + COLUMN_TIMESTAMP + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    /**
     * Thêm một tin nhắn mới vào cơ sở dữ liệu.
     * @param chatModel Đối tượng ChatModel chứa thông tin tin nhắn.
     * @return ID của hàng mới được thêm vào, hoặc -1 nếu có lỗi.
     */
    public long addMessage(ChatModel chatModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, chatModel.getMessage());
        values.put(COLUMN_IS_FROM_BOT, chatModel.isFromBot() ? 1 : 0);
        values.put(COLUMN_IS_IMAGE, chatModel.isImage() ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, chatModel.getTimestamp());

        long id = db.insert(TABLE_MESSAGES, null, values);
        db.close();
        return id;
    }

    /**
     * Lấy toàn bộ lịch sử tin nhắn từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng ChatModel.
     */
    public List<ChatModel> getAllMessages() {
        List<ChatModel> messageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                boolean isFromBot = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FROM_BOT)) == 1;
                boolean isImage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_IMAGE)) == 1;
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                messageList.add(new ChatModel(content, isFromBot, isImage, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messageList;
    }

    /**
     * Xóa tin nhắn "Leaf AI is typing..." khỏi database.
     */
    public void deleteTypingMessage() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Giả sử "Leaf AI is typing..." là tin nhắn cuối cùng từ bot
        // Bạn có thể cần một cách nhận diện cụ thể hơn nếu có nhiều tin nhắn "typing"
        // Ví dụ: dựa vào timestamp gần nhất và content
        String selection = COLUMN_CONTENT + " = ? AND " + COLUMN_IS_FROM_BOT + " = ?";
        String[] selectionArgs = {"Leaf AI is typing...", "1"};
        int deletedRows = db.delete(TABLE_MESSAGES, selection, selectionArgs);
        Log.d("DatabaseHelper", "Deleted typing messages: " + deletedRows);
        db.close();
    }
}
