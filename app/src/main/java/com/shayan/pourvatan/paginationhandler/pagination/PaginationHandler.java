package com.shayan.pourvatan.paginationhandler.pagination;

import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    private FindFirstItemInLayoutManagerInterface findFirstItemInLayoutManagerInterface;
    private int columnCount;
    private
    @Direction.PaginationDirection
    int direction;

    private PaginationState paginationState = PaginationState.IDLE;

    private int offset;

    private enum PaginationState {
        IDLE,
        LOADING,
        DONE // use when you receive all pages.
    }


    private PaginationHandler() {
        throw new AssertionError("Create object via builder class");
    }

    private PaginationHandler(RecyclerView recyclerView,
                              RecyclerView.LayoutManager layoutManager,
                              RecyclerView.Adapter adapter,
                              int offsetCount,
                              PaginationInterface paginationInterface,
                              FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface,
                              FindFirstItemInLayoutManagerInterface findFirstItemInLayoutManagerInterface,
                              int columnCount, @Direction.PaginationDirection int direction) {

        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.layoutManager = layoutManager;
        this.offset = offsetCount;
        this.paginationInterface = paginationInterface;
        this.findLastItemInLayoutManagerInterface = findLastItemInLayoutManagerInterface;
        this.findFirstItemInLayoutManagerInterface = findFirstItemInLayoutManagerInterface;
        this.columnCount = columnCount;
        this.direction = direction;


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
                int firstVisibleItemPosition = adapter.getItemCount();

                if (findLastItemInLayoutManagerInterface != null && direction == Direction.LOAD_FROM_BOTTOM) {

                    lastVisibleItemPosition = findLastItemInLayoutManagerInterface.findLastVisibleItemPosition();

                } else if (findFirstItemInLayoutManagerInterface != null && direction == Direction.LOAD_FROM_TOP) {

                    firstVisibleItemPosition = findFirstItemInLayoutManagerInterface.findFirstVisibleItemPosition();

                } else if (layoutManager instanceof GridLayoutManager) {

                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    lastVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    columnCount = gridLayoutManager.getSpanCount();

                } else if (layoutManager instanceof StaggeredGridLayoutManager) {

                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    int[] lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                    int[] firstPositions = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                    if (lastPositions != null && lastPositions.length > 0) {
                        lastVisibleItemPosition = lastPositions[0];
                    }

                    if (firstPositions != null && firstPositions.length > 0) {
                        firstVisibleItemPosition = firstPositions[0];
                    }


                } else if (layoutManager instanceof LinearLayoutManager) {

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                } else {
                    throw new IllegalArgumentException("this library don't support custom layoutManager, you must setFindLastItemInLayoutManagerInterface to handle other layoutManager ");
                }


                boolean needLoadMore = needLoadMore(firstVisibleItemPosition, lastVisibleItemPosition);

                if (needLoadMore && paginationState == PaginationState.IDLE) {
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

    private boolean needLoadMore(int firstVisibleItemPosition, int lastVisibleItemPosition) {

        switch (direction) {
            case Direction.LOAD_FROM_TOP:
                return firstVisibleItemPosition <= offset;

            case Direction.LOAD_FROM_BOTTOM:
                return adapter.getItemCount() - (lastVisibleItemPosition + columnCount * offset) <= 0;

            default:
                throw new IllegalArgumentException("invalid direction");
        }
    }

    public static class Builder {

        private int offsetCount = 3;
        private int columnCount = 1;
        private RecyclerView recyclerView;
        private PaginationInterface paginationInterface;
        private FindLastItemInLayoutManagerInterface findLastItemInLayoutManagerInterface;
        private FindFirstItemInLayoutManagerInterface findFirstItemInLayoutManagerInterface;
        private
        @Direction.PaginationDirection
        int direction = Direction.LOAD_FROM_BOTTOM;

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

        public Builder setFindFirstItemInLayoutManagerInterface(FindFirstItemInLayoutManagerInterface findFirstItemInLayoutManagerInterface) {
            this.findFirstItemInLayoutManagerInterface = findFirstItemInLayoutManagerInterface;
            return this;
        }

        public Builder setDirection(@Direction.PaginationDirection int direction) {
            this.direction = direction;
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

            return new PaginationHandler(recyclerView,
                    recyclerView.getLayoutManager(),
                    recyclerView.getAdapter(),
                    offsetCount,
                    paginationInterface,
                    findLastItemInLayoutManagerInterface,
                    findFirstItemInLayoutManagerInterface,
                    columnCount,
                    direction);
        }

    }
}
