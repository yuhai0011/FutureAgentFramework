package com.futureagent.lib.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futureagent.lib.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by skywalker on 17/9/07.
 * Email: yuhai833@126.com
 * Description: 日期选择器，兼容Android各版本显示
 */
public class DatePickerDialog extends Dialog {

    private static int START_MONTH = 1, END_MONTH = 12;

    private static int START_HOUR = 0, END_HOUR = 23;
    private static int START_MIN = 0, END_MIN = 59;


    private static String[] MONTH_BIG = {"1", "3", "5", "7", "8", "10", "12"};
    private static String[] MONTH_SMALL = {"4", "6", "9", "11"};

    final List<String> MONTH_LIST_BIG = Arrays.asList(MONTH_BIG);
    final List<String> MONTH_LIST_SMALL = Arrays.asList(MONTH_SMALL);

    private Context mContext;
    private LinearLayout mLayoutAll;
    private LinearLayout mLayoutButtons;
    private TextView mTitle;
    private NumberPicker mNPYear;
    private NumberPicker mNPMonth;
    private NumberPicker mNPDay;
    private NumberPicker mNPHour;
    private NumberPicker mNPMin;
    private Button mBtnConfirm;
    private Button mBtnCancel;

    public DatePickerDialog(Context context, int resStyle) {
        super(context, resStyle);
        mContext = context;
        setContentView(R.layout.date_time_dialog);
        initView();
        setCurrDate();
    }

    private void initView() {
        mLayoutAll = (LinearLayout) findViewById(R.id.layout_background);
        mTitle = (TextView) findViewById(R.id.dialog_title);
        mNPYear = (NumberPicker) findViewById(R.id.num_picker_year);
        mNPMonth = (NumberPicker) findViewById(R.id.num_picker_month);
        mNPDay = (NumberPicker) findViewById(R.id.num_picker_day);
        mNPHour = (NumberPicker) findViewById(R.id.num_picker_hour);
        mNPMin = (NumberPicker) findViewById(R.id.num_picker_min);
        mLayoutButtons = (LinearLayout) findViewById(R.id.dialog_button);
        mBtnConfirm = (Button) findViewById(R.id.button_confirm);
        mBtnCancel = (Button) findViewById(R.id.button_cancel);
        setViewByDayModel();
    }

    private void setViewByDayModel() {
        mLayoutAll.setBackgroundResource(R.drawable.dialog_bg_day);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.dialog_divider_day);
        mLayoutAll.setDividerDrawable(drawable);
        mBtnCancel.setBackgroundResource(R.drawable.dialog_button_bg_day);
        mBtnConfirm.setBackgroundResource(R.drawable.dialog_button_bg_day);
        mLayoutButtons.setDividerDrawable(drawable);
    }

    private void setCurrDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int endYear = calendar.get(Calendar.YEAR);

        mNPYear.setMaxValue(endYear);
        mNPYear.setMinValue(endYear - 5);
        mNPYear.setFocusable(true);
        mNPYear.setFocusableInTouchMode(true);

        mNPMonth.setMaxValue(END_MONTH);
        mNPMonth.setMinValue(START_MONTH);
        mNPMonth.setFocusable(true);
        mNPMonth.setFocusableInTouchMode(true);

        mNPDay.setFocusable(true);
        mNPDay.setFocusableInTouchMode(true);

        mNPHour.setMaxValue(END_HOUR);
        mNPHour.setMinValue(START_HOUR);
        mNPHour.setFocusable(true);
        mNPHour.setFocusableInTouchMode(true);

        mNPMin.setMaxValue(END_MIN);
        mNPMin.setMinValue(START_MIN);
        mNPMin.setFocusable(true);
        mNPMin.setFocusableInTouchMode(true);

        mNPYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newValue) {
                setDayRange(newValue, mNPMonth.getValue());
            }
        });
        mNPMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newValue) {
                setDayRange(mNPYear.getValue(), newValue);
            }
        });
    }

    private void setDayRange(int year, int month) {
        if (MONTH_LIST_BIG.contains(String.valueOf(month))) {
            mNPDay.setMaxValue(31);
            mNPDay.setMinValue(1);
        } else if (MONTH_LIST_SMALL.contains(String.valueOf(month))) {
            mNPDay.setMaxValue(30);
            mNPDay.setMinValue(1);
        } else {
            if (((year) % 4 == 0 && (year) % 100 != 0)
                    || (year) % 400 == 0) {
                mNPDay.setMaxValue(29);
                mNPDay.setMinValue(1);
            } else {
                mNPDay.setMaxValue(28);
                mNPDay.setMinValue(1);
            }
        }
    }

    public void setDialogTitle(String title) {
        mTitle.setText(title);
    }

    public void setDialogContent(final OnDatePickerSetListener listener,
                                 int year, int monthOfYear, int dayOfMonth,
                                 int hour, int min) {

        mNPYear.setValue(year);
        mNPMonth.setValue(monthOfYear + 1);
        mNPDay.setValue(dayOfMonth);
        mNPHour.setValue(hour);
        mNPMin.setValue(min);
        setDayRange(year, monthOfYear + 1);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                listener.onDateSet(null, mNPYear.getValue(), mNPMonth.getValue() - 1, mNPDay.getValue(),
                        mNPHour.getValue(), mNPMin.getValue());
                DatePickerDialog.this.dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DatePickerDialog.this.dismiss();
            }
        });

    }

    /**
     * The listener used to indicate the user has finished selecting a date.
     */
    public interface OnDatePickerSetListener {
        /**
         * @param view the picker associated with the dialog
         * @param year the selected year
         * @param month the selected month (0-11 for compatibility with
         *              {@link Calendar#MONTH})
         * @param dayOfMonth th selected day of the month (1-31, depending on
         *                   month)
         */
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth, int hour, int min);
    }
}
