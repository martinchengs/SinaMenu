package com.szswj.sinamenu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * Created by WayneSuper on 18/01/2016.
 */
public class SJToggleMenuOverlay extends ImageButton implements DialogInterface.OnKeyListener, View.OnClickListener {

    private Dialog mDialog;
    private boolean mDismissing;
    private int contentLayoutRes;
    private SJToggleMenuLayout mToggleMenu;

    public SJToggleMenuOverlay(Context context) {
        this(context, null);
    }

    public SJToggleMenuOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SJToggleMenuOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SJToggleMenuOverlay, defStyleAttr, 0);
        contentLayoutRes = a.getResourceId(R.styleable.SJToggleMenuOverlay_menuLayout, -1);
        a.recycle();
        initialize();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void initialize() {
        // We create a fake dialog which dims the screen and we display the expandable menu as content
        mDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f;//设置背景透明值
        mDialog.getWindow().setAttributes(lp);

        mToggleMenu = (SJToggleMenuLayout) LayoutInflater.from(getContext()).inflate(contentLayoutRes, null);
        mToggleMenu.setMenuOverlay(this);
        mDialog.setContentView(mToggleMenu);
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
               mToggleMenu.onShowing();
                mDismissing = false;
            }
        });

        // Catch events when keyboard button are clicked. Used to dismiss the menu
        // on 'back' button
        mDialog.setOnKeyListener(this);

        // Clicking this view will expand the button menu
        setOnClickListener(this);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled() && !mDismissing) {
            if (mToggleMenu.isExpanded()) {
                mDismissing = true;
                mToggleMenu.toggle();
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == getId()) {
            show();
        }
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
    /**设置点击监听*/
    public void setOItemClickListener (OnItemClickListener listener) {
        mToggleMenu.setOnItemClickListener(listener);
    }

    public  interface OnItemClickListener {
        void onItemClick(View view);
    }
}
