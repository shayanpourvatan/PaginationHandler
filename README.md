# PaginationHandler
You can handle pagination with this project, I've put one sample code in HowToUse file in project directory an i will write one sample class and test for this as soon I can, but I will explain here either.

For handling pagination in your project you must use following codes:

       new PaginationHandler.Builder()
                .setRecyclerView(recyclerView)  // set recyclerView that you want to handle pagination
                .setOffsetCount(5)    // set count pre last to load more happened
                .setLoadMoreListener(new PaginationInterface() {      // handle loadMore,
                    @Override
                    public void onLoadMore(final PaginationCompletionInterface pageComplete) {
                       
                    }
                }).build();
                
                
* In first line you've create new object of PaginationHandler by Builder class,
* then you set RecyclerView object ( this line is mandatory and you must put it ), be sure you set adapter and layoutManager to your recyclerView, so first you must init your recyclerView with layoutManager and Adapter, then use this snippet code
* In next line we set offsetCount value, this value is declare where you want to send loadMore request from last, 5 is means when I've reach to 5 last row in recyclerView, send loadMore request, default value is 3
* You must to setLoadMoreListener to put your pagination code into there, after scroll reach to your desire position, onLoadMore will be called,
* PaginationCompleteInterface is an interface that you use it when your job has been completed with loadMore to tell PaginationHandler to be ready for next request
* then use build() method to create your method.

