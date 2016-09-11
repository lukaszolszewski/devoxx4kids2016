package pl.devoxx4kids.devoxx4kids.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.model.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import pl.devoxx4kids.devoxx4kids.MyApplication;
import pl.devoxx4kids.devoxx4kids.R;
import pl.devoxx4kids.devoxx4kids.estimote.BeaconID;
import pl.devoxx4kids.devoxx4kids.estimote.EstimoteCloudBeaconDetails;
import pl.devoxx4kids.devoxx4kids.estimote.EstimoteCloudBeaconDetailsFactory;
import pl.devoxx4kids.devoxx4kids.estimote.ProximityContentManager;
import pl.devoxx4kids.devoxx4kids.model.BeaconContent;
import pl.devoxx4kids.devoxx4kids.model.Hero;
import pl.devoxx4kids.devoxx4kids.model.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {

    private static final String TAG = "ScannerFragment";

    private static final String UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final List<BeaconID> BEACONS = Arrays.asList(
            new BeaconID(UUID, 43984, 27941), //Lemon Tart
            new BeaconID(UUID, 48486, 56847), //Icy Marshmallow
            new BeaconID(UUID, 46134, 11150), //Sweet Beetroot
            new BeaconID(UUID, 61323, 35137), //Mint Cocktail
            new BeaconID(UUID, 11019, 56882), //Blueberry Pie
            new BeaconID(UUID, 6911, 52460)   //Candy Floss
    );
    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();

    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE, android.graphics.Color.rgb(98, 84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL, android.graphics.Color.rgb(155, 186, 160));
        BACKGROUND_COLORS.put(Color.LEMON_TART, android.graphics.Color.rgb(255, 244, 79));
        BACKGROUND_COLORS.put(Color.SWEET_BEETROOT, android.graphics.Color.rgb(143, 43, 89));
        BACKGROUND_COLORS.put(Color.CANDY_FLOSS, android.graphics.Color.rgb(255, 186, 210));
    }

    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private static Map<String, String> serviceContent = new HashMap<>();

    private ProximityContentManager proximityContentManager;

    private Realm realm;

    private Button update;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(
                R.layout.fragment_scanner, container, false);

        proximityContentManager = new ProximityContentManager(getContext(), BEACONS,
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                final Integer backgroundColor;

                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());
                    final String url = serviceContent.get(beaconDetails.getBeaconName());

                    if (url != null) {
                        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                        Glide.with(getContext()).load(url).into(imageView);

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sync();
                                try {
                                    realm.beginTransaction();
                                    Hero hero = new Hero();
                                    hero.setUrl(url);
                                    hero.setColor(backgroundColor);
                                    realm.copyToRealm(hero);
                                    realm.commitTransaction();

                                    Toast toast = Toast.makeText(getContext(), getString(R.string.done), Toast.LENGTH_SHORT);
                                    toast.show();

                                } catch (Exception e) {
                                    realm.cancelTransaction();
                                    e.printStackTrace();
                                }

                            }
                        });


                    }
                } else {
                    backgroundColor = null;
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
                    imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            R.drawable.devoxx));
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sync();
                        }});
                }

                rootView.findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });

        realm = Realm.getDefaultInstance();
        update = ((Button) rootView.findViewById(R.id.get));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }});
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SystemRequirementsChecker.checkWithDefaultDialogs(getActivity())) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }

        sync();
    }

    private void sync() {

        if (update!=null) {
            update.setEnabled(false);
        }

        MyApplication application = MyApplication.get(getContext());
        Service service = application.getService();
        Call<List<BeaconContent>> call = service.getBeaconContents();
        call.enqueue(new Callback<List<BeaconContent>>() {
            @Override
            public void onResponse(Call<List<BeaconContent>> call, Response<List<BeaconContent>> response) {
                for (BeaconContent beaconContent : response.body()) {
                    serviceContent.put(beaconContent.id,beaconContent.url);
                }
                if (update!=null) {
                    update.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<BeaconContent>> call, Throwable t) {

                Toast toast = Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();

                t.printStackTrace();

                if (update!=null) {
                    update.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        proximityContentManager.stopContentUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }
}
