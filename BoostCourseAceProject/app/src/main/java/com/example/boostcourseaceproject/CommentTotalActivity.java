package com.example.boostcourseaceproject;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.boostcourseaceproject.databinding.ActivityCommentTotalBinding;

import java.util.ArrayList;

public class CommentTotalActivity extends AppCompatActivity {
    //TAG
    final static String TAG = "CommentTotalActivityT";
    //binding
    ActivityCommentTotalBinding binding;
    //requestCode
    final static int WRITE_REQUEST = 11;
    //putExtra key
    final static String commentExtra = "commentExtra";
    final static String commentListExtra = "commentListExtra";
    //value
    private ArrayList<Comment> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_total);
        binding.setActivity(this);
        //인텐트처리
        processIntent();
    }

    //인텐트 수신 처리
    private void processIntent(){
        Intent intent = getIntent();
        mCommentList = (ArrayList<Comment>) intent.getSerializableExtra(commentListExtra);
        mCommentAdapter = new CommentAdapter(mCommentList, getApplicationContext());
        binding.commenttotalLivCommentlist.setAdapter(mCommentAdapter);//리스트뷰 어댑터연결
    }


    //작성하기클릭
    public void writeOnClick(View view){
        Toast.makeText(getApplicationContext(), "작성하기", Toast.LENGTH_SHORT).show();
        Intent writeIntent = new Intent(CommentTotalActivity.this, CommentWriteActivity.class);
        startActivityForResult(writeIntent, WRITE_REQUEST);
    }

    public void backOnClick(View view){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(commentListExtra, mCommentList);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(commentListExtra, mCommentList);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST && resultCode == RESULT_OK) { //작성하기결과
            Comment comment = (Comment) data.getSerializableExtra(commentExtra);
            if (comment != null) {
                mCommentList.add(comment);
                mCommentAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "작성하기 RESULT 실패");
            }
        }

    }
}
