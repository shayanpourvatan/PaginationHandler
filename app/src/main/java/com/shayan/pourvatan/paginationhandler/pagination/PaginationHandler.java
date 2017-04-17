package com.shayan.pourvatan.paginationhandler.pagination;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by shayanpourvatan on 4/16/17.
 */

public class PaginationHandler {

    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener onScrollListener;
    private PaginationInterface paginationInterface;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface;
    private int columnCount;

    private PaginationState paginationState = PaginationState.IDLE;

    private int offset;

    private enum PaginationState {
        IDLE,
        LOADING,
        DONE // use when you receive all pages.
    }

    public PaginationHandler(RecyclerView recyclerView,
                             RecyclerView.LayoutManager layoutManager,
                             RecyclerView.Adapter adapter,
                             int offsetCount,
                             PaginationInterface paginationInterface,
                             FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface,
                             int columnCount) {

        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.layoutManager = layoutManager;
        this.offset = offsetCount;
        this.paginationInterface = paginationInterface;
        this.findLastItemInLayoutManagerInterface = findLastItemInLayoutManagerInterface;
        this.columnCount = columnCount;


        initOnScrollListener();

    }

    private void initOnScrollListener() {
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                // you must set interface to handle pagination
                if (paginationInterface == null) {
                    return;
                }

                // safety check
                if (adapter == null || layoutManager == null) {
                    return;
                }

                // pagination is on progress or is finished.
                if (paginationState != PaginationState.IDLE) {
                    return;
                }


                int lastVisibleItemPosition = 0;

                if (findLastItemInLayoutManagerInterface != null) {

                    lastVisibleItemPosition = findLastItemInLayoutManagerInterface.findLastVisibleItemPosition();

                } else if (layoutManager instanceof GridLayoutManager) {

                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    lastVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    columnCount = gridLayoutManager.getSpanCount();

                } else if (layoutManager instanceof StaggeredGridLayoutManager) {

                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    int[] lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                    if (lastPositions != null && lastPositions.length > 0) {
                        lastVisibleItemPosition = lastPositions[0];
                    }

                } else if (layoutManager instanceof LinearLayoutManager) {

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                } else {
                    throw new IllegalArgumentException("this library don't support custom layoutManager, you must setFindLastItemInLayoutManagerInterface to handle other layoutManager ");
                }

                boolean needLoadMore = adapter.getItemCount() - (lastVisibleItemPosition + columnCount * offset) < 0;

                if (needLoadMore) {
                    paginationState = PaginationState.LOADING;
                    paginationInterface.onLoadMore(new PaginationCompletionInterface() {
                        @Override
                        public void handledDataComplete(boolean isLast) {
                            if (isLast) {
                                paginationState = PaginationState.DONE;
                            } else {
                                paginationState = PaginationState.IDLE;
                            }
                        }
                    });
                }

            }
        };

        recyclerView.addOnScrollListener(onScrollListener);
    }

    private PaginationHandler() {
        throw new AssertionError("Create object via builder class");
    }

    public static class Builder {

        private int offsetCount = 3;
        private int columnCount = 1;
        private RecyclerView recyclerView;
        private PaginationInterface paginationInterface;
        private FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface;

        public Builder setOffsetCount(int offsetCount) {
            this.offsetCount = offsetCount;
            return this;
        }

        public Builder setRecyclerView(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            return this;
        }

        public Builder setLoadMoreListener(PaginationInterface paginationInterface) {
            this.paginationInterface = paginationInterface;
            return this;
        }

        public Builder setFindLastItemInLayoutManagerInterface(FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface) {
            this.findLastItemInLayoutManagerInterface = findLastItemInLayoutManagerInterface;
            return this;
        }

        public Builder setColumncount(int columnCount) {
            this.columnCount = columnCount;
            return this;
        }

        public PaginationHandler build() {

            if (this.recyclerView == null) {
                throw new AssertionError("recyclerView must be initialized in PaginationHandler");
            }

            if (this.recyclerView.getLayoutManager() == null) {
                throw new AssertionError("layoutManager must be set in recyclerView");
            }

            if (this.recyclerView.getAdapter() == null) {
                throw new AssertionError("adapter must be initialized in PaginationHandler");
            }

            return new PaginationHandler(recyclerView, recyclerView.getLayoutManager(), recyclerView.getAdapter(), offsetCount, paginationInterface, findLastItemInLayoutManagerInterface, columnCount);
        }

    }
}
