package com.aviptcare.dateselect;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aviptcare.dateselect.adapter.NumericWheelAdapter;
import com.aviptcare.dateselect.listener.OnWheelChangedListener;
import com.aviptcare.dateselect.view.DataChooseView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期选择器
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        selectTime = (TextView) findViewById(R.id.select_time_tv);
        selectTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_time_tv:
                showDateTimePicker();
                break;
            default:
                break;
        }
    }
    private Dialog mDialog;

    private int START_YEAR = 1990, END_YEAR = 2100;

    private TextView dataOk;

    private void showDateTimePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        mDialog = new Dialog(this);
        mDialog.setTitle("请选择日期与时间");

        // 获取dialog布局
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_time_choose_layout, null);
        // 设置dialog的布局,并显示
        mDialog.setContentView(view);
        mDialog.show();

        dataOk = (TextView) view.findViewById(R.id.data_ok);

        // 年
        final DataChooseView wv_year = (DataChooseView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(true);// 可循环滚动
        wv_year.setLabel("年");// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        final DataChooseView wv_month = (DataChooseView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel("月");
        wv_month.setCurrentItem(month);

        // 日
        final DataChooseView wv_day = (DataChooseView) view.findViewById(R.id.day);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wv_day.setLabel("日");
        wv_day.setCurrentItem(day - 1);
        // 时
        final DataChooseView wv_hours = (DataChooseView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCyclic(true);
        wv_hours.setCurrentItem(hour);

        // 分
        final DataChooseView wv_mins = (DataChooseView) view.findViewById(R.id.mins);
        wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wv_mins.setCyclic(true);
        wv_mins.setCurrentItem(minute);

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(DataChooseView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };

        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(DataChooseView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);


        dataOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//选择日期确定
                // 如果是个数,则显示为"02"的样式
                String parten = "00";
                DecimalFormat decimal = new DecimalFormat(parten);

                String chooseTimeStr = (wv_year.getCurrentItem() + START_YEAR)
                        + "-" + decimal.format((wv_month.getCurrentItem() + 1))
                        + "-" + decimal.format((wv_day.getCurrentItem() + 1))
                        + " " + decimal.format(wv_hours.getCurrentItem()) + ":"
                        + decimal.format(wv_mins.getCurrentItem()) + ":00";

                try {
                    //获取当前时间
                    Date currentDate = new Date(System.currentTimeMillis());
                    int currentTime = (int) (currentDate.getTime() / 1000);

                    //获取选择的时间
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    Date chooseDate = sdf.parse(chooseTimeStr);
                    int chooseTime = (int) (chooseDate.getTime() / 1000);

                    //时间比较
                    if (chooseTime <= currentTime) {
                        Toast.makeText(MainActivity.this, "选择的时间应大于当前时间！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                selectTime.setText(chooseTimeStr);
                mDialog.dismiss();
            }
        });
    }
}
