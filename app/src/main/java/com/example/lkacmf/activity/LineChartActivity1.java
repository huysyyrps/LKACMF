
package com.example.lkacmf.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lkacmf.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Example of a heavily customized {@link LineChart} with limit lines, custom line shapes, etc.
 *
 * @version 3.1.0
 * OnSeekBarChangeListener,
 * @since 1.7.4
 */
public class LineChartActivity1 extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);
        chart = findViewById(R.id.chart1);
        chart.setDrawGridBackground(false);//是否显示表格颜色
        chart.setDrawBorders(false);// 是否在折线图上添加边框
        chart.setScaleEnabled(true);// 是否可以缩放
        chart.setPinchZoom(false); // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        chart.setScaleEnabled(true);  // X轴上的缩放,默认true
        chart.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        chart.setDragEnabled(true);
        ;// 是否可以拖拽
        chart.setTouchEnabled(true); // 设置是否可以触摸
        chart.setDescription(null);// 数据描述
//        //设置是否可以通过双击屏幕放大图表。默认是true
//        chart.isDoubleTapToZoomEnabled = false;

//        linechar.isDoubleTapToZoomEnabled = false
        chart.getViewPortHandler().setMaximumScaleX(30.0f);//限制X轴放大限制
        chart.getViewPortHandler().setMaximumScaleY(3.0f);
        //硬件加速
        chart.setHardwareAccelerationEnabled(true);
        // 当前统计图表中最多在x轴坐标线上显示的总量
//        linechar.setVisibleXRangeMaximum(300f);
//        linechar.moveViewToX(300f);


        //X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(0f);//轴线宽度
        xAxis.setEnabled(true);//是否显示X轴
//        xAxis.granularity = 1F//设置 后 value是从0开始的，每次加1
        xAxis.setAvoidFirstLastClipping(true); //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        setData(45, 180);
        chart.animateX(1500);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(values, "DataSet 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        // create a data object with the data sets
        LineData data = new LineData(dataSets);
        // set data
        chart.setData(data);
    }

}
