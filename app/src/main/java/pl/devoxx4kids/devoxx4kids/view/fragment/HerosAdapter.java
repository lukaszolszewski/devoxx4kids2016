package pl.devoxx4kids.devoxx4kids.view.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import pl.devoxx4kids.devoxx4kids.R;
import pl.devoxx4kids.devoxx4kids.model.Hero;

public class HerosAdapter extends RecyclerView.Adapter<HerosAdapter.ViewHolder> {

    private Realm realm;
    private Context contex;

    public HerosAdapter(Context contex) {
        this.realm = Realm.getDefaultInstance();
        this.contex = contex;
    }

    @Override
    public HerosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hero, parent, false);

        RelativeLayout bg = (RelativeLayout) rootView.findViewById(R.id.bg);
        ImageView image = (ImageView) rootView.findViewById(R.id.image);

        ViewHolder vh = new ViewHolder(rootView,bg,image);
        return vh;
    }

    @Override
    public void onBindViewHolder(HerosAdapter.ViewHolder holder, int position) {
        RealmResults<Hero> heros = realm.where(Hero.class).findAll();
        Hero hero = heros.get(position);
        if (holder.image != null) {
            Glide.with(contex).load(hero.getUrl()).into(holder.image);
        }
        holder.bg.setBackgroundColor(hero.getColor());

    }

    @Override
    public int getItemCount() {
        RealmResults<Hero> heros = realm.where(Hero.class).findAll();
        heros.addChangeListener(new RealmChangeListener<RealmResults<Hero>>() {
            @Override
            public void onChange(RealmResults<Hero> element) {
                notifyDataSetChanged();
            }
        });

        return heros.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout bg;
        ImageView image;

        public ViewHolder(View itemView, RelativeLayout bg, ImageView image) {
            super(itemView);
            this.bg = bg;
            this.image = image;
        }
    }
}
