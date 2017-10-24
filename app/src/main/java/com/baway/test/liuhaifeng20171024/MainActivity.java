package com.baway.test.liuhaifeng20171024;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;




public class MainActivity extends AppCompatActivity {
    private List<Bean.DataBean> list;
    private XRecyclerView recycler;
    private String URL = "http://www.yulin520.com/a2a/impressApi/news/mergeList?sign=C7548DE604BCB8A17592EFB9006F9265&pageSize=20&gender=2&ts=1871746850&page=";
    private int page = 1;//设置首先展示的页面是第一页
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String u = (String) msg.obj;
            Gson gson = new Gson();
            Bean bean = gson.fromJson(u, Bean.class);
            list = bean.getData();
            //设置布局管理器
            recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            //设置适配器
            recycler.setAdapter(new MyAdapter());
            //分页加的
            recycler.setLoadingListener(new XRecyclerView.LoadingListener() {
                @Override
                //下拉刷新
                public void onRefresh() {
                    page = 1;
                    list.clear();
                    jiexi();
                    recycler.refreshComplete();
                }

                @Override
                //上拉加载
                public void onLoadMore() {
                    page++;
                    jiexi();
                    Toast.makeText(MainActivity.this, "加载更多", Toast.LENGTH_LONG).show();
                    recycler.loadMoreComplete();
                }
            });

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断网络
        checkNetwork();
        //找控件
        initData();
        //解析数据
        jiexi();

        if (!checkNetwork()) {
            Toast.makeText(this, "没有网络", Toast.LENGTH_LONG).show();
            Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
            startActivity(intent);
            return;
        }
    }

    private void jiexi() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String string = response.body().string();
                Log.i("aaa", "run: " + string);
                Message message = new Message();
                message.obj = string;
                handler.sendMessage(message);
            }
        });
    }
    //找到控件
    private void initData() {
        recycler = (XRecyclerView) findViewById(R.id.recycler);
    }

    private boolean checkNetwork() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }

    class MyAdapter extends XRecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        //绑定布局
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.name.setText(list.get(position).getTitle());
             holder.old.setText(list.get(position).getUserAge()+"");

            Glide.with(MainActivity.this).load(list.get(position).getImg()).into(holder.image);

           holder.desc.setText(list.get(position).getIntroduction());
            holder.job.setText(list.get(position).getOccupation());
            //添加属性动画，实现渐变效果。
            ObjectAnimator//
                    .ofFloat(holder.image, "alpha",0.0F,1f)//
                    .setDuration(2000)//
                    .start();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends XRecyclerView.ViewHolder {

            public TextView name;
            public TextView old;
            public TextView desc;
            public ImageView image;
            public TextView job;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                old = (TextView) itemView.findViewById(R.id.old);
                desc = (TextView) itemView.findViewById(R.id.desc);
                image = (ImageView) itemView.findViewById(R.id.image);
                job = (TextView) itemView.findViewById(R.id.job);
            }
        }
    }
}
