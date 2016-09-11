package pl.devoxx4kids.devoxx4kids.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.devoxx4kids.devoxx4kids.R;
import pl.devoxx4kids.devoxx4kids.model.Hero;

public class ListFragment extends Fragment{

    private Realm realm;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmResults<Hero> heros;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(
                R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.herolist);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();

        mAdapter = new HerosAdapter(realm, getContext());
        mRecyclerView.setAdapter(mAdapter);

        heros = realm.where(Hero.class).findAll();
        heros.addChangeListener(new RealmChangeListener<RealmResults<Hero>>() {
            @Override
            public void onChange(RealmResults<Hero> element) {
                mAdapter.notifyDataSetChanged();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
