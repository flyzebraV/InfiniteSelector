package com.jdaddressselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * 描述：自定义无限层级选择器
 * 创建人：LWS
 * 创建时间：2017/2/28
 */
public class CustomerSelector implements AdapterView.OnItemClickListener {

    public static final int INDEX_INVALID = -1;
    private final Context context;
    private SelectedListener listener;
    private View view;
    private View indicator;
    private LinearLayout ll_tabLayout;
    private ProgressBar progressBar;

    private ListView listView;


    private int tabIndex = 0;

    //所有Adapter数据
    List<List<ISelectAble>> allDatas = new ArrayList<>();
    //每个ListView Adapter
    private List<SelectAdapter> adapters = new ArrayList<>();
    //记录每个列表选中下标
    private ArrayList<Integer> selectedIndex = new ArrayList<>();
    // 数据接口
    DataProvider dataProvider;
    //标题栏
    private List<TextView> tabs = new ArrayList<>();

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        getNextData(-1, null);
    }

    public CustomerSelector(Context context) {
        this.context = context;
        initAdapters();
        initViews();
    }

    private void initAdapters() {


    }


    private void initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.address_selector, null);

        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        this.listView = (ListView) view.findViewById(R.id.listView);
        this.indicator = view.findViewById(R.id.indicator);
        this.ll_tabLayout = (LinearLayout) view.findViewById(R.id.layout_tab);


        this.listView.setOnItemClickListener(this);

    }

    public View getView() {
        return view;
    }


    /**
     * 点击ListView Item  更新选中状态
     */
    private int alltableIndex;//总展开层级数
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedIndex.set(tabIndex, position);
        ISelectAble selectAble = allDatas.get(tabIndex).get(position);
        // 更新当前级别及子级标签文本
        tabs.get(tabIndex).setText(selectAble.getName());
        //清空数据集
        if (tabIndex+1<allDatas.size()){

            for (int i=tabIndex+1;i<tabs.size();i++){
                ll_tabLayout.removeView(tabs.get(i));
            }

            allDatas.subList(tabIndex+1,allDatas.size()).clear();
            adapters.subList(tabIndex+1,adapters.size()).clear();
            tabs.subList(tabIndex+1,tabs.size()).clear();
            selectedIndex.subList(tabIndex+1,selectedIndex.size()).clear();
            adapters.get(tabIndex).notifyDataSetChanged();



        }
        // 更新已选中项
        this.adapters.get(tabIndex).setSelectedIndex(position);
        this.adapters.get(tabIndex).notifyDataSetChanged();

        updateTabsVisibility(tabIndex);
        //查询下级数据
        getNextData(selectAble.getId(),selectAble);
    }



    /**
     * 根据当前集合选择的id，向用户获取下一级子集的数据
     */
    private void getNextData(final int preId, ISelectAble selectAble) {
        if (dataProvider == null) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        dataProvider.provideData(tabIndex, preId, selectAble,new DataProvider.DataReceiver() {
            @Override
            public void send(List<ISelectAble> data) {

                // 有数据返回
                if (data != null && data.size() != 0) {
                    if (preId != -1) {
                        tabIndex = tabIndex + 1;
                    }
                    //判断展开层级 增加
                    if (tabIndex <= allDatas.size()) {
                        allDatas.add(data);
                        alltableIndex=allDatas.size();
                        adapters.add(new SelectAdapter(allDatas.get(tabIndex)));
                        adapters.get(tabIndex).notifyDataSetChanged();
                        listView.setAdapter(adapters.get(tabIndex));
                        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.simple_text_view, ll_tabLayout, false);
                        textView.setTag(tabIndex);
                        textView.setOnClickListener(new TextViewClickListener());
                        ll_tabLayout.addView(textView);
                        tabs.add(textView);
                        selectedIndex.add(-1);

                    }
                } else {
                    callbackInternal();
                }



                updateTabsVisibility(tabIndex);
                updateProgressVisibility();


            }
        });
    }

    class  TextViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
//            Toast.makeText(context,v.getTag()+"",Toast.LENGTH_SHORT).show();
             //设置tab 下标
            tabIndex =(int) v.getTag();
            //更新adapter
            listView.setAdapter(adapters.get(tabIndex));
            //设置选择位置
            if (selectedIndex.get(tabIndex) != INDEX_INVALID) {
                listView.setSelection(selectedIndex.get(tabIndex));
            }
            updateTabsVisibility(tabIndex);
        }
    }

    private void callbackInternal() {
        if (listener != null) {

            ArrayList<ISelectAble> result = new ArrayList<>(allDatas.size());
            for (int i = 0; i < allDatas.size()-1; i++) {
                ISelectAble resultBean = allDatas.get(i) == null
                        || selectedIndex.get(i) == INDEX_INVALID ? null : allDatas.get(i).get(selectedIndex.get(i));
                result.add(resultBean);
            }
            listener.onAddressSelected(result);
        }
    }

    private void updateTabsVisibility(int index) {
        for (int i = 0; i < tabs.size(); i++) {
            TextView tv = tabs.get(i);
//            tv.setVisibility(allDatas.get(i).size() != 0 ? View.VISIBLE : View.GONE);
            tv.setVisibility(View.VISIBLE );
            tv.setEnabled(index != i);
        }
    }

    private void updateProgressVisibility() {
        ListAdapter adapter = listView.getAdapter();
        int itemCount = adapter.getCount();
        progressBar.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    public SelectedListener getOnAddressSelectedListener() {
        return listener;
    }

    public void setSelectedListener(SelectedListener listener) {
        this.listener = listener;
    }


}
