package com.view.calender.horizontal.umar.horizontalcalendarview;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HorizontalCalendarView extends LinearLayout{


    LinearLayout leftButton;
    LinearLayout rightButton;
    ImageView leftImage;
    ImageView rightImage;
    View rootView;
    Context context;
    RecyclerView recyclerView;
    CalAdapter calAdapter;
    ArrayList<DayDateMonthYearModel> currentDayModelList;
    HorizontalPaginationScroller horizontalPaginationScroller;
    Calendar cal;
    Calendar calPrevious;
    DateFormat dateFormat;
    Date date;
    Date datePrevious;
    LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;

    LinearLayout mainBackground;

    public void init(){
        rootView = inflate(context, R.layout.custom_calender_layout, this);
        leftButton =  findViewById(R.id.swipe_left);
        rightButton =  findViewById(R.id.swipe_right);
        recyclerView =  findViewById(R.id.recycler_view);
        mainBackground =  findViewById(R.id.main_background);
        leftImage =  findViewById(R.id.left_image_view);
        rightImage =  findViewById(R.id.right_image_view);
        loadNextPage( );
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "left but", Toast.LENGTH_SHORT).show();
//                if (linearLayoutManager.findFirstVisibleItemPosition())
                recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 3);
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "right but", Toast.LENGTH_SHORT).show();
                recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 3);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleIndex = linearLayoutManager.findLastVisibleItemPosition();
                int firstVisibleIndex = linearLayoutManager.findFirstVisibleItemPosition();
                if (dx > 0) {
                    for(int i = firstVisibleIndex ; i<lastVisibleIndex ; i++) {

                        if (currentDayModelList.get(i).month.compareTo(currentDayModelList.get(i+1).month) != 0) {
                            try {
                                CallBack cb = new CallBack(toCallBack, "updateMonthOnScroll");
                                cb.invoke(currentDayModelList.get(i+1));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (dx < 0) {
                    for(int i = lastVisibleIndex ; i>firstVisibleIndex ; i--) {

                        if (currentDayModelList.get(i).month.compareTo(currentDayModelList.get(i+1).month) != 0) {
                            try {
                                CallBack cb = new CallBack(toCallBack, "updateMonthOnScroll");
                                cb.invoke(currentDayModelList.get(i));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        linearLayoutManager.scrollToPosition(27);
    }

    public void loadNextPage( ){
        if(calAdapter==null){
            currentDayModelList = new ArrayList<>();
            dateFormat = new SimpleDateFormat("MMMM-EEE-yyyy-MM-dd");
            date = new Date();
            //System.out.println("Day 1"+" "+dateFormat.format(date));
            DayDateMonthYearModel currentDayModel=new DayDateMonthYearModel();
            String currentDate=dateFormat.format(date).toString();
            String[] parts = currentDate.split(" ");
            String[] partsDate = currentDate.split("-");
            currentDayModel.month = partsDate[0];
            currentDayModel.date = partsDate[4];
            currentDayModel.day = partsDate[1];
            currentDayModel.year = partsDate[2];
            currentDayModel.monthNumeric = partsDate[3];
            currentDayModel.isToday = true;
            calPrevious = Calendar.getInstance();
            cal = Calendar.getInstance();
            cal.setTime(date);
            calPrevious.setTime(date);


            for (int i = 0; i < 30; i++) {
                calPrevious.add(Calendar.DAY_OF_WEEK, -1);
                String nextDate = dateFormat.format(calPrevious.getTime());
                DayDateMonthYearModel previousDayMode=new DayDateMonthYearModel();
                String[] partsNextDate= nextDate.split("-");
                previousDayMode.month = partsNextDate[0];
                previousDayMode.date = partsNextDate[4];
                previousDayMode.day = partsNextDate[1];
                previousDayMode.year = partsNextDate[2];
                previousDayMode.monthNumeric = partsNextDate[3];
                previousDayMode.isToday= false;
                isLoading = false;
//                calAdapter.addPrevious(currentDayMode);
                currentDayModelList.add(0,previousDayMode);
            }
            currentDayModelList.add(currentDayModel);

            //SimpleDateFormat sdf = new SimpleDateFormat("MMM EEE yyyy-MM-dd");
            for (int i = 0; i < 30; i++) {
                cal.add(Calendar.DAY_OF_WEEK, 1);
                String nextDate = dateFormat.format(cal.getTime());
                DayDateMonthYearModel currentDayMode=new DayDateMonthYearModel();
                String[] partsNextDate= nextDate.split("-");
                currentDayMode.month = partsNextDate[0];
                currentDayMode.date = partsNextDate[4];
                currentDayMode.day = partsNextDate[1];
                currentDayMode.year = partsNextDate[2];
                currentDayMode.monthNumeric = partsNextDate[3];
                currentDayMode.isToday = false;
                currentDayModelList.add(currentDayMode);
                calAdapter = new CalAdapter(context , currentDayModelList);
                linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(calAdapter);
                horizontalPaginationScroller = new HorizontalPaginationScroller(linearLayoutManager) {
                    @Override
                    protected void loadMoreItems() {
                        isLoading = true;
                        loadNextPage();
                    }

                    @Override
                    protected void loadMoreItemsOnLeft() {
//                        isLoading = true;
//                        Toast.makeText(context, "Reached Left", Toast.LENGTH_SHORT).show();
//                        loadPreviousPage( );
                    }

                    @Override
                    public boolean isLoading() {
                        return isLoading;
                    }
                };
                recyclerView.addOnScrollListener(horizontalPaginationScroller);
            }
        }else{
            for (int i = 0; i < 30; i++) {
                cal.add(Calendar.DAY_OF_WEEK, 1);
                String nextDate = dateFormat.format(cal.getTime());
                DayDateMonthYearModel currentDayMode=new DayDateMonthYearModel();
                String[] partsNextDate= nextDate.split("-");
                currentDayMode.month = partsNextDate[0];
                currentDayMode.date = partsNextDate[4];
                currentDayMode.day = partsNextDate[1];
                currentDayMode.year = partsNextDate[2];
                currentDayMode.isToday= false;
                isLoading = false;
                calAdapter.add(currentDayMode);
            }
        }
    }

    public HorizontalCalendarView(Context context) {
        super(context);
        this.context = context;
        init();
    }


    public void setBackgroundColor(int color){
        mainBackground.setBackgroundColor(color);
    }

    public void setControlTint(int color){
        rightImage.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN);
        leftImage.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN);
}

    public void showControls(boolean show){
        if(show){
            leftButton.setVisibility(VISIBLE);
            rightButton.setVisibility(VISIBLE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    7.0f
            );
            LinearLayout.LayoutParams paramTwo = new LinearLayout.LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.5f
            );
            paramTwo.topMargin = 20;
            paramTwo.bottomMargin = 20;
            leftButton.setLayoutParams(paramTwo);
            rightButton.setLayoutParams(paramTwo);
            recyclerView.setLayoutParams(param);

        }else{
            leftButton.setVisibility(GONE);
            rightButton.setVisibility(GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    10.0f
            );
            recyclerView.setLayoutParams(param);
        }
    }

    Object toCallBack ;

    public void setContext( Object toCallBack){
        this.toCallBack = toCallBack;
        calAdapter.setCallback(toCallBack);
        Date date = new Date();
        //System.out.println("Day 1"+" "+dateFormat.format(date));
        DayDateMonthYearModel currentDayModel=new DayDateMonthYearModel();
        String currentDate=dateFormat.format(date).toString();
        String[] parts = currentDate.split(" ");
        String[] partsDate = currentDate.split("-");
        currentDayModel.month = partsDate[0];
        currentDayModel.date = partsDate[4];
        currentDayModel.day = partsDate[1];
        currentDayModel.year = partsDate[2];
        currentDayModel.monthNumeric = partsDate[3];
        currentDayModel.isToday = true;
        try {
            CallBack cb = new CallBack(toCallBack, "updateMonthOnScroll");
            cb.invoke(currentDayModel);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public HorizontalCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void changeAccent(int color ){
        calAdapter.changeAccent(color );
    }
}
