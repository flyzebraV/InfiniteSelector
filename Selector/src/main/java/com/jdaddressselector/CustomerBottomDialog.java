package com.jdaddressselector;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


/**
 * 描述：自定义无限层级选择器Dialog
 * 创建人：LWS
 * 创建时间：2017/2/28
 */
public class CustomerBottomDialog extends Dialog {
    private CustomerSelector selector;

    public CustomerBottomDialog(Context context) {
        super(context, R.style.bottom_dialog);
    }

    public CustomerBottomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public CustomerBottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void init(Context context,CustomerSelector selector) {
        this.selector = selector;
        setContentView(selector.getView());
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = dip2px(context, 456);
        window.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public void setOnAddressSelectedListener(SelectedListener listener) {
        this.selector.setSelectedListener(listener);
    }

    public static CustomerBottomDialog show(Context context) {
        return show(context, null);
    }

    public static CustomerBottomDialog show(Context context, SelectedListener listener) {
        CustomerBottomDialog dialog = new CustomerBottomDialog(context, R.style.bottom_dialog);
        dialog.selector.setSelectedListener(listener);
        dialog.show();

        return dialog;
    }
}
