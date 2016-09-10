package pl.devoxx4kids.devoxx4kids;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.model.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.devoxx4kids.devoxx4kids.estimote.BeaconID;
import pl.devoxx4kids.devoxx4kids.estimote.EstimoteCloudBeaconDetails;
import pl.devoxx4kids.devoxx4kids.estimote.EstimoteCloudBeaconDetailsFactory;
import pl.devoxx4kids.devoxx4kids.estimote.ProximityContentManager;
import pl.devoxx4kids.devoxx4kids.model.BeaconContent;
import pl.devoxx4kids.devoxx4kids.model.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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

    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proximityContentManager = new ProximityContentManager(this, BEACONS,
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                Integer backgroundColor;

                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());

                    String url = serviceContent.get(beaconDetails.getBeaconName());

                    if (url != null) {
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        Glide.with(getApplicationContext()).load(url).into(imageView);
                    }
                } else {
                    backgroundColor = null;
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.beacon));
                }

                findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });

        update = ((Button) findViewById(R.id.update));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }

        sync();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }
        });
    }

    private void sync() {

        if (update!=null) {
            update.setEnabled(false);
        }

        MyApplication application = MyApplication.get(getApplicationContext());
        Service service = application.getService();
        Call<List<BeaconContent>> call = service.getBeaconContents();
        call.enqueue(new Callback<List<BeaconContent>>() {
            @Override
            public void onResponse(Call<List<BeaconContent>> call, Response<List<BeaconContent>> response) {
                Log.d(TAG, "Response " + response.body());
                for (BeaconContent beaconContent : response.body()) {
                    serviceContent.put(beaconContent.id,beaconContent.url);
                }
                if (update!=null) {
                    update.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<BeaconContent>> call, Throwable t) {

                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();

                t.printStackTrace();

                if (update!=null) {
                    update.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }
}
