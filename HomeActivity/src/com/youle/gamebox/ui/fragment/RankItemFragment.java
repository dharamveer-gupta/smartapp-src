package com.youle.gamebox.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.ta.mvc.common.TAResponse;
import com.youle.gamebox.ui.R;
import com.youle.gamebox.ui.activity.GameDetailActivity;
import com.youle.gamebox.ui.activity.GameListActivity;
import com.youle.gamebox.ui.adapter.RankGameAdapter;
import com.youle.gamebox.ui.adapter.YouleBaseAdapter;
import com.youle.gamebox.ui.api.AbstractApi;
import com.youle.gamebox.ui.api.RankApi;
import com.youle.gamebox.ui.greendao.GameBean;
import com.youle.gamebox.ui.greendao.JsonEntry;
import com.youle.gamebox.ui.http.JsonHttpListener;
import com.youle.gamebox.ui.http.ZhidianHttpClient;
import com.youle.gamebox.ui.util.ModelConst;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 14-6-4.
 */
public class RankItemFragment extends NextPageFragment implements AdapterView.OnItemClickListener {
    private RankApi.RankType type;
    private AbstractApi api;
    private RankGameAdapter adapter;
    private List<GameBean> gameBeans;

    public RankItemFragment() {
    }

    public RankItemFragment(RankApi.RankType type) {
        this.type = type;
    }

    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    //    @Override
    public AbstractApi getApi() {
        if (api == null) {
            api = new RankApi(type);
        }
        return api;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter != null) {
            getListView().setAdapter(adapter);
        } else if (type == RankApi.RankType.ALL) {
            fillData();
        }
        getListView().setOnItemClickListener(this);

    }


    public void fillData() {
        if (gameBeans == null) {
            loadLoacalData();
            loadData();
        }
    }
    private void loadLoacalData() {
        if (type == RankApi.RankType.ALL) {
            loadCach(ModelConst.RANK_ALL);
        } else if (type == RankApi.RankType.NEW) {
            loadCach(ModelConst.RANK_NEW);
        } else if (type == RankApi.RankType.MONTH) {
            loadCach(ModelConst.RANK_MONTH);
        } else {
            loadCach(ModelConst.RANK_WEEK);
        }
    }

    //    @Override
    public YouleBaseAdapter getAdapter() {
        return adapter;
    }

    //    @Override
    public List pasreJson(String jsonStr) throws JSONException {
        return jsonToList(GameBean.class, jsonStr, "data");
    }

    protected void loadData() {
        api = new RankApi(type);
        ZhidianHttpClient.request(api, new JsonHttpListener(this) {
            @Override
            public void onRequestSuccess(String jsonString) {
                if (type == RankApi.RankType.ALL) {
                    cacheJson(ModelConst.RANK_ALL, jsonString, api);
                } else if (type == RankApi.RankType.NEW) {
                    cacheJson(ModelConst.RANK_NEW, jsonString, api);
                } else if (type == RankApi.RankType.MONTH) {
                    cacheJson(ModelConst.RANK_MONTH, jsonString, api);
                } else {
                    cacheJson(ModelConst.RANK_WEEK, jsonString, api);
                }
            }
        });
    }

    @Override
    public void onSuccess(TAResponse response) {
        JsonEntry entry = (JsonEntry) response.getData();
        if (entry == null) return;
        try {
            JSONObject jsonObject = new JSONObject(entry.getJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initAdapter(entry.getJson());
    }

    @Override
    protected int getViewId() {
        return R.layout.my_game_fragment;
    }

    @Override
    protected String getModelName() {
        if (type == RankApi.RankType.ALL) {
            return "全部排行";
        } else if (type == RankApi.RankType.NEW) {
            return "最新排行";
        } else if (type == RankApi.RankType.MONTH) {
            return "月排行";
        } else {
            return "周排行";
        }
    }

    private void initAdapter(String str) {
        try {
            gameBeans = jsonToList(GameBean.class, str, "data");
            adapter = new RankGameAdapter(getActivity(), gameBeans);
            getListView().setAdapter(adapter);
            boolean pauseOnScroll = true; // or true
            boolean pauseOnFling = true; // or false
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameBean gameBean = gameBeans.get(position);
        Toast.makeText(getActivity(), gameBean.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), GameDetailActivity.class);
        intent.putExtra(GameListActivity.NAME, gameBean.getName());
        intent.putExtra(GameListActivity.ID, gameBean.getId());
        startActivity(intent);
    }
}
