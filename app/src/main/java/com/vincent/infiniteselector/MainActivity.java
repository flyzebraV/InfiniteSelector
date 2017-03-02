package com.vincent.infiniteselector;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jdaddressselector.BottomDialog;
import com.jdaddressselector.CustomerBottomDialog;
import com.jdaddressselector.CustomerSelector;
import com.jdaddressselector.DataProvider;
import com.jdaddressselector.ISelectAble;
import com.jdaddressselector.SelectedListener;

import java.nio.channels.Selector;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });


    }


    int index = 0;

    public void showSelectDialog() {
        CustomerSelector selector = new CustomerSelector(this);
        selector.setDataProvider(new DataProvider() {
            @Override
            public void provideData(int currentDeep, int preId, ISelectAble selectAble, DataReceiver receiver) {
                index++;
                receiver.send(getDatas(index));//从网络或者数据库获取数据
                if (currentDeep == 6) {
                    receiver.send(null);//如果是最后层级，返回null代表勾选完成
                }
            }


        });
        final CustomerBottomDialog dialog = new CustomerBottomDialog(this);
        dialog.init(this, selector);
        dialog.show();
        selector.setSelectedListener(new SelectedListener() {
            @Override
            public void onAddressSelected(ArrayList<ISelectAble> selectAbles) {
                String result = "";
                for (ISelectAble selectAble : selectAbles) {
                    result += selectAble.getName() + " ";
                }
                textView.setText(result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                index=0;
            }
        });
    }


    /**
     * 模拟测试数据
     */
    public static ArrayList<ISelectAble> getDatas(final int index) {

        ArrayList<ISelectAble> data = new ArrayList<>();
        for (int j = 0; j < 20; j++) {
            final int finalJ = j;
            data.add(new ISelectAble() {
                @Override
                public String getName() {
                    return "第" + index + "列" + finalJ+ "行";
                }

                @Override
                public int getId() {
                    return finalJ;
                }

                @Override
                public Object getArg() {
                    return this;
                }
            });
        }
        return data;
    }

}
