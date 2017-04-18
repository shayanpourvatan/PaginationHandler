package com.shayan.pourvatan.paginationhandler;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shayan.pourvatan.paginationhandler.pagination.Direction;
import com.shayan.pourvatan.paginationhandler.pagination.FindFirstItemInLayoutManagerInterface;
import com.shayan.pourvatan.paginationhandler.pagination.FindLastItemInLayoutManagerInterface;
import com.shayan.pourvatan.paginationhandler.pagination.PaginationCompletionInterface;
import com.shayan.pourvatan.paginationhandler.pagination.PaginationHandler;
import com.shayan.pourvatan.paginationhandler.pagination.PaginationInterface;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Data> adapterList = new ArrayList<>();
    LinearLayoutManager layoutManager;

    HomeAdapter adapter;

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        for (int i = 0; i < 20; i++) {
            adapterList.add(new Data("new data " + i));
        }

        adapter = new HomeAdapter(this, adapterList);
        recyclerView.setAdapter(adapter);


        new PaginationHandler.Builder()
                .setRecyclerView(recyclerView)  // set recyclerView that you want to handle pagination
                .setOffsetCount(5)    // set count pre last to load more happened


                ///////////////////////////////// Custom LayoutManager need two following method ////////////////////////////

                /// if your layout manager is one of LinearLayoutManager, GridLayoutManager or StaggeredGridLayoutManager or child of those, you don't need this/
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // default value is 1
                .setColumncount(1)

                // this method will return lastVisibleItem method in your layoutManager.
                .setFindLastItemInLayoutManagerInterface(new FindLastItemInLayoutManagerInterface() {
                    @Override
                    public int findLastVisibleItemPosition() {
                        return layoutManager.findLastVisibleItemPosition();
                    }
                })
                // if your direction is LOAD_FROM_TOP you must set it instead of above method in custom layoutManager.
                .setFindFirstItemInLayoutManagerInterface(new FindFirstItemInLayoutManagerInterface() {
                    @Override
                    public int findFirstVisibleItemPosition() {
                        return layoutManager.findFirstVisibleItemPosition();
                    }
                })
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////


                // default is it, if you want to loadMore happened when you reach on top of list, you must change it to LOAD_FROM_TOP
                .setDirection(Direction.LOAD_FROM_BOTTOM)

                .setLoadMoreListener(new PaginationInterface() {      // handle loadMore,
                    @Override
                    public void onLoadMore(final PaginationCompletionInterface pageComplete) {
                        //      @Caution => remember to call pageComplete after adding items into list, or get failed response.

                        // send your request in any way you want from DB or network
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                }


                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int i = 0; i < 40; i++) {
                                            adapterList.add(new Data("test" + (40 * page + i)));
                                        }

                                        adapter.setData(adapterList);
                                        pageComplete.handledDataComplete(++page >= 5);
                                    }
                                });

                            }
                        }).start();
                    }
                }).build();


    }
}
