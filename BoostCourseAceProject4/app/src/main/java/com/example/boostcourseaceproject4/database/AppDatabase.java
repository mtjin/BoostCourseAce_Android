package com.example.boostcourseaceproject4.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.boostcourseaceproject4.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class AppDatabase {
    //insert or replace into  ==> 이 구문을 사용하면 중복된 값을 가지는 행이 있어도 오류가 발생하지 않고, 새로운 값으로 update 될 수 있다.


    private static final String TAG = "databaseHelper";

    private static SQLiteDatabase database;
    private static ArrayList<Comment> commentList = new ArrayList<>();

    // 1단계: 데이터베이스(저장소)를 만들거나 오픈하는 단계
    public static void openDatabase(Context context, String databaseName) {
        if(database == null) {
            try {
                DatabaseHelper helper = new DatabaseHelper(context, databaseName, null, 1); //헬퍼를 생성함
                database = helper.getWritableDatabase();  //데이터베이스에 쓸수 있는 권한을 리턴해줌(갖게됨)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeDatabase() {
        // println("openDatabase 호출됨.");
        try {
            if (database != null) {
                database.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //영화 데이터 삽입
    public static void insertMovieJson(int id, String movieJson) {
        if (database != null) {
            String sql = "insert or replace into movie( id , movieJson) values(? , ?)";
            Object[] params = {id, movieJson};
            database.execSQL(sql, params);
        } else {
            Log.d(TAG, "데이터베이스를 먼저 오픈하세요");
        }
    }

    //영화데이터 조회
    public static String selectMovieJsonData() {
        String tableName = "movie";
        //println("selectMoviesListData() 호출됨.");
        if (database != null) {
            String result = "";
            String sql = "select movieJson from " + tableName;
            Cursor cursor = database.rawQuery(sql, null); //파라미터는 없으니깐 null 값 넣어주면된다.
            cursor.moveToNext(); //이거 해줘야 데이터읽어올 수 있음 (한번더 하면 다음데이터불러온다)
            result += cursor.getString(0);
            cursor.close(); //cursor라는것도 실제 데이터베이스 저장소를 접근하는 것이기 때문에 자원이 한정되있다. 그러므로 웬만하면 마지막에 close를 꼭 해줘야한다.
            return result;
        }
        return null;
    }

    //영화 상세정보 데이터 삽입
    public static void insertMovieInfoJson(int id, String movieInfoJson) {
        if (database != null) {
            String sql = "insert or replace into movieInfo( id , movieInfoJson) values(? , ?)";
            Object[] params = {id, movieInfoJson};
            database.execSQL(sql, params);
        } else {
            Log.d(TAG, "데이터베이스를 먼저 오픈하세요");
        }
    }

    //영화 상세정보 데이터 조회
    public static String selectMovieInfoJsonData(int id) {
        String tableName = "movieInfo";
        if (database != null) {
            String result = "";
            String sql = "select movieInfoJson from " + tableName + " where id = " + id;
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToNext();
            result += cursor.getString(0);
            cursor.close();
            return result;
        }
        return null;
    }

    //TODO: Json으로 받던거 수정 ,   //현재 서버에서 배열로 오는 값을 json 그대로 DB에 저장합니다만, 이 경우 총 개수를 알수 없으므로 배열값 각각을 하나의 record형태로 저장하는것이 좋습니다.
    //댓글 정보 삽입
    public static void insertComment(List<Comment> commentList) {
        if (database != null || commentList != null) {
            for (int i = 0; i < commentList.size(); i++) {
                Comment comment = commentList.get(i);
                String sql = "insert or replace into comment( id , writer , movieId,times,rating,  contents,  recommend) values(? , ?,?,?,?,?,?)";
                Object[] params = {comment.id, comment.writer, comment.movieId, comment.time, comment.rating, comment.contents, comment.recommend};
                database.execSQL(sql, params);
            }
        } else {
            Log.d(TAG, "데이터베이스를 먼저 오픈하세요");
        }
    }

    //댓글 정보  조회
    public static ArrayList<Comment> selectCommentData(int movieId) {
        String tableName = "comment";
        if (database != null) {
            String result = "";
            String sql = "select id ,writer, movieId,times,rating,  contents,  recommend  from " + tableName + " where movieId = " + movieId;
            Cursor cursor = database.rawQuery(sql, null);
            commentList.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                commentList.add(new Comment(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getFloat(4), cursor.getString(5), cursor.getInt(6)));
            }
            cursor.close();
            return commentList;
        }
        return null;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) { //데이터베이스를 처음 생성해주는 경우(기존에 사용자가 데이터베이스를 사용하지 않았던 경우)
            //println("헬퍼 createCreate() 호출됨.");
            String tableName = "movie";
            String tableName2 = "movieInfo";
            String tableName3 = "comment";
            //이 함수에서 데이터베이스는 매개변수인 db를 써야한다.
            //테이블 생성할떄 if not exists라는 조건문을 넣어줄 수 있다. (존재하지않을때 테이블 생성)
            //2단계 : 테이블 생성(영화리스트 테이블)
            String sql = "create table if not exists " + tableName + "(id integer PRIMARY KEY , movieJson text)"; //영화리스트 테이블 생성
            db.execSQL(sql);
            String sql2 = "create table if not exists " + tableName2 + "(id integer PRIMARY KEY , movieInfoJson text)"; //영화상세정보 테이블 생성
            db.execSQL(sql2);
            String sql3 = "create table if not exists " + tableName3 + "(id integer PRIMARY KEY , writer text, movieId integer, times text, rating float, contents text, recommend intefer )"; //댓글 테이블 생성
            db.execSQL(sql3);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //기존 사용자가 디비를 사용하고있어서 그걸 업데이트(수정)해주는경우
            // println("onUpgrade() 호출됨: " + oldVersion + "===> " + newVersion);
        }
    }

}
