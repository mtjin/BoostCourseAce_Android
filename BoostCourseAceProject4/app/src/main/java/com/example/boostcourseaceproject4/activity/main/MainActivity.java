package com.example.boostcourseaceproject4.activity.main;

import android.os.Bundle;


import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.MenuItem;

import com.android.volley.toolbox.Volley;
import com.example.boostcourseaceproject4.R;
import com.example.boostcourseaceproject4.adapter.MoviePagerAdapter;
import com.example.boostcourseaceproject4.database.AppDatabase;
import com.example.boostcourseaceproject4.fragment.MovieFragment;
import com.example.boostcourseaceproject4.fragment.movie_info.MovieInfoFragment;
import com.example.boostcourseaceproject4.interfaces.MovieFragmentListener;
import com.example.boostcourseaceproject4.model.Movie;
import com.example.boostcourseaceproject4.api.NetworkManager;
import com.example.boostcourseaceproject4.model.MovieList;
import com.example.boostcourseaceproject4.utils.NetworkStatusHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

//메인액티비티 : 영화목록들을 뷰페이저로 보여주는 역할
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MovieFragmentListener, MainContract.View, View.OnClickListener {
    final static String TAG = "MainActivityT";
    //xml
    private ViewPager pager; //뷰페이저
    //예매율순 버튼튼
    private ImageView orderSelectmageView; //선택된 정렬
    private ImageView orderReserveImageVIew; //예매율순
    private ImageView orderQurationImageView; //큐레이션순(평점)
    private ImageView orderOpenImageView;  //최근개봉일순
    private LinearLayout animLinearLayout;
    //value
    private MoviePagerAdapter moviePagerAdapter; //영화 목록 어댑터
    private MovieInfoFragment movieInfoFragment;
    public static Toolbar toolbar; //툴바
    private MainPresenter presenter;
    private Boolean isPageOpen = false; //정렬 애니메이션 열려있는 상태인지
    private int orderType = 1;
    final static String MOVIE_EXTRA = "MOVIE_EXTRA";
    final static String MOVIEID_EXTRA = "MOVIEID_EXTRA";
    //애니메이션
    Animation translateUp; //위로가기 애니메이션
    Animation translateDown; //아래로가기 애니메이션

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this); //프레젠터 생성
        initView(); //뷰초기화
        init(); //값초기화세팅

    }

    //뷰초기화
    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        orderSelectmageView = findViewById(R.id.main_iv_currentorder); //선택된 정렬
        orderSelectmageView.setOnClickListener(this);
        orderReserveImageVIew = findViewById(R.id.main_iv_reservation_order); //예매율순
        orderReserveImageVIew.setOnClickListener(this);
        orderQurationImageView = findViewById(R.id.main_iv_quration_order);//큐레이션순(평점)
        orderQurationImageView.setOnClickListener(this);
        orderOpenImageView = findViewById(R.id.main_iv_open_order); //최근개봉일순
        orderOpenImageView.setOnClickListener(this);
        animLinearLayout = findViewById(R.id.main_linear_anim);
        //애니메이션참조
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);

        toolbar.setTitle("영화 목록");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.main_nav_navigation);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        initAnimation();
    }

    //값들 초기화 세팅
    public void init() {
        //뷰페이저
        pager = findViewById(R.id.main_pager);
        //뷰페이저에서 패래그먼트 옆화면 살짝보이게하기조절
        pager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page(뷰페이저에서 패래그먼트 옆화면 살짝보이게하기조절)
        pager.setPadding(150, 0, 150, 0);
        //캐싱을 해놓을 프래그먼트 개수
        pager.setOffscreenPageLimit(6);
        //어댑터에 추가
        moviePagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(moviePagerAdapter);
        //데이터베이스 오픈
        AppDatabase.openDatabase(getApplicationContext(), "CinemaApp");
        //리퀘스트큐 생성
        if (NetworkManager.requestQueue == null) {
            NetworkManager.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        presenter.requestMovieList(getApplicationContext(), orderType); //서버에 영화 정보 요청
    }

    //정렬버튼 클릭리스너
    private void initAnimation() {
        // 영화정렬 애니메이션
        translateUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                animLinearLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override //바로가기메뉴 선택시 호출
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_0) {
            Toast.makeText(this, "영화목록으로 가기", Toast.LENGTH_LONG).show();
            if (movieInfoFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(movieInfoFragment).commit();
                toolbar.setTitle("영화 목록");
            }
        } else if (id == R.id.nav_1) {
            Toast.makeText(this, "두번째 메뉴 선택됨.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_2) {
            Toast.makeText(this, "세번째 메뉴 선택됨.", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override //영화프래그먼트 상세보기 버튼 클릭시 호출
    public void onDetailButtonClicked(int movieId) {
        movieInfoFragment = new MovieInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIEID_EXTRA, movieId);
        //영화 id 값 전달
        movieInfoFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(R.id.main_container, movieInfoFragment);
        // 해당 transaction 을 Back Stack 에 저장
        transaction.addToBackStack(null);
        // transaction 실행
        transaction.commit();
    }

    @Override
    public void onToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //영화 불러오기 응답
    public void responseMovieList(String response) {
        Gson gson = new Gson();
        //TODO: 리뷰 => json으로 두번 파싱할 필요없이, ResponseMovie를 MovieList가 상속받으면 한번의 파싱으로 해결이 됩니다.
        MovieList movieList = (MovieList) gson.fromJson(response, MovieList.class);
        if (movieList.code == 200) { //코드가 200과 같다면 result라는거안에 데이터가 들어가있다는것을 확신할 수 있음
            List<Movie> movieResultList = movieList.result;
            for (int i = 0; i < movieResultList.size(); i++) {
                Movie movie = movieResultList.get(i);
                //가져온 영화의 상세정보를 가져오기위해 id값을 보내 서버에 영화상세정보 데이터를 요청함
                MovieFragment movieFragment = new MovieFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MOVIE_EXTRA, movie);
                movieFragment.setArguments(bundle);
                moviePagerAdapter.addItem(movieFragment);
                Log.d(TAG, "영화 아이디 : " + movie.getId());
                // requestMovieInfo(movie.getId());
            }
            pager.setAdapter(moviePagerAdapter);
            //데이터베이스에 해당 json을 넣어준다. TODO:: Repository만들어서 데이터베이스에 넣는다고하는데 시간상 나중에 보기로한다.
            AppDatabase.insertMovieJson(1, response);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //디비close
        AppDatabase.closeDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        orderSelectmageView.setVisibility(View.VISIBLE);
    }

    @Override //정렬버튼
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_iv_currentorder:
                boolean status = NetworkStatusHelper.getConnectivityStatus(getApplicationContext()); //인터넷 연결 유무
                if (!status) { //인터넷 연결 안된 경우
                    Toast.makeText(MainActivity.this, "인터넷이 연결되있어야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (isPageOpen) {
                        animLinearLayout.startAnimation(translateUp);
                    } else {
                        animLinearLayout.setVisibility(View.VISIBLE);
                        animLinearLayout.startAnimation(translateDown);
                    }
                    isPageOpen = !isPageOpen;
                }
                break;
            case R.id.main_iv_reservation_order:
                onToastMessage("예매율순을 선택하셨습니다\n예매율이 높은순으로 정렬됩니다.");
                orderSelectmageView.setImageResource(R.drawable.order11);
                moviePagerAdapter.clear();
                orderType = 1;
                presenter.requestMovieList(getApplicationContext(), orderType); //서버에 영화 정보 요청
                // pager.setAdapter(moviePagerAdapter);
                break;
            case R.id.main_iv_quration_order:
                onToastMessage("큐레이션을 선택하셨습니다\n큐레이션은 평점순으로 정렬됩니다");
                orderSelectmageView.setImageResource(R.drawable.order22);
                moviePagerAdapter.clear();
                orderType = 2;
                presenter.requestMovieList(getApplicationContext(), orderType); //서버에 영화 정보 요청
                break;
            case R.id.main_iv_open_order:
                onToastMessage("상영예정을 선택하셨습니다\n최근상영순으로 정렬됩니다.");
                orderSelectmageView.setImageResource(R.drawable.order33);
                moviePagerAdapter.clear();
                orderType = 3;
                presenter.requestMovieList(getApplicationContext(), orderType); //서버에 영화 정보 요청
                break;
        }
    }
}
