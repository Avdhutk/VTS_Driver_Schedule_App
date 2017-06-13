package com.a3sj.vts.retrofitpostexample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Avdhut K on 08-02-2017.
 */

public class ScheduleListViewAdapter extends BaseAdapter  implements Filterable {
    public ArrayList<HashMap<String, String>> list;
    public  ArrayList<ListItems> myList;
    public ArrayList<String> arrayList1;
    public ArrayList<String> arrayList2;
    public ArrayList<String> arrayList3;
    private List<ApplicationInfo> mListAppInfo;
    Activity activity;
    TextView subTitleFirst;
    TextView subTitleSecond;
    TextView title;
    TextView txtFourth;
    private ScheduleListViewAdapter adapter;
    private Filter filter;

    public ScheduleListViewAdapter(Home activity, ArrayList<String> arrayList1,ArrayList<String> arrayList2,ArrayList<String> arrayList3){
        super();
        this.activity= activity;
        //this.list=list;
        this.myList=myList;
        this.arrayList1=arrayList1;
        this.arrayList2=arrayList2;
        this.arrayList3=arrayList3;
    }
    @Override
    public int getCount() {
        return arrayList1.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList1.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = activity.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.schedule_list_values_activity, null);
            title= (TextView) convertView.findViewById(R.id.listTitle);
            subTitleFirst = (TextView) convertView.findViewById(R.id.firstSubTitleLine);
            subTitleSecond = (TextView) convertView.findViewById(R.id.secondSubTitleLine);
/*            colFirst.setBackgroundColor(Color.GRAY);
            colSecond.setBackgroundColor(Color.GRAY);
            colFirst.setTextColor(Color.BLACK);
            colSecond.setTextColor(Color.BLACK);*/

        }
       // ListItems map = myList.get(position);
        String map1=arrayList1.get(position);
        String map2=arrayList2.get(position);
        String map3=arrayList3.get(position);
       // title.setText(map.getRoute());
        title.setText(map1);
        subTitleFirst.setText(map2);
        subTitleSecond.setText(map3);
        return convertView;

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

}
