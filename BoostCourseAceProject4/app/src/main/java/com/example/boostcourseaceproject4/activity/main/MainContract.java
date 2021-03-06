package com.example.boostcourseaceproject4.activity.main;

import android.content.Context;

import com.example.boostcourseaceproject4.model.Movie;

import java.util.List;


public interface MainContract {
    interface View {
        void onToastMessage(String message); //토스트메세지
        void responseMovieList(String response);// 영화리스트 서버 응답
    }

    interface Presenter{
        void requestMovieList(Context context, int orderType); //영화리스트 서버 요청, orderType은 정렬기준
    }
}
