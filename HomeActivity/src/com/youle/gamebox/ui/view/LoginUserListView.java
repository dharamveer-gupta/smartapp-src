package com.youle.gamebox.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.youle.gamebox.ui.R;
import com.youle.gamebox.ui.adapter.LoginListitemAdapter;
import com.youle.gamebox.ui.bean.LogAccount;

import java.util.List;

/**
 * Created by Administrator on 2014/5/26.
 */
public class LoginUserListView extends LinearLayout{
    @InjectView(R.id.login_listitem)
    ListView mLoginListitem;
    Context mContext ;
    LogUserOnclickItem logUserOnclickItem;
    public LoginUserListView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public void setLogUserOnclickItem(LogUserOnclickItem logUserOnclickItem){
        this.logUserOnclickItem = logUserOnclickItem;
    }

    public LoginUserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LoginUserListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.login_layout_listitem,this);
        ButterKnife.inject(this);
    }

    public void setData(final List<LogAccount> logUserList){
        if(logUserList ==null)return;
        LoginListitemAdapter loginListitemAdapter = new LoginListitemAdapter(mContext,logUserList);
        loginListitemAdapter.setOnclickItem(logUserOnclickItem);
        mLoginListitem.setAdapter(loginListitemAdapter);
        mLoginListitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(logUserOnclickItem!=null){
                    logUserOnclickItem.onUserSelect(logUserList.get(position));
                }

            }
        });

    }

    public interface LogUserOnclickItem{
      public void onUserSelect(LogAccount logUser);
       public void onUserDelete(LogAccount user);
    }
}
