package com.clockbyte.admobadapter.sampleapp.banner;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.clockbyte.admobadapter.bannerads.AdmobBannerAdapterWrapper;
import com.clockbyte.admobadapter.bannerads.BannerAdViewWrappingStrategyBase;
import com.clockbyte.admobadapter.sampleapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity_ListView_Banner extends Activity {

    ListView lvMessages;
    AdmobBannerAdapterWrapper adapterWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_listview);

        // they suggest to initialize things early as possible in Firebase docs
        MobileAds.initialize(getApplicationContext());

        initListViewItems();
    }

    /**
     * Inits an adapter with items, wrapping your adapter with a {@link AdmobBannerAdapterWrapper} and setting the listview to this wrapper
     * FIRST OF ALL Please notice that the following code will work on a real devices but emulator!
     */
    private void initListViewItems() {
        lvMessages = (ListView) findViewById(R.id.lvMessages);

        //creating your adapter, it could be a custom adapter as well
        ArrayAdapter<String> adapter  = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);

        //your test devices' ids
        String[] testDevicesIds = new String[]{getString(R.string.testDeviceID),
                AdRequest.DEVICE_ID_EMULATOR};
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = AdmobBannerAdapterWrapper.builder(this)
                .setLimitOfAds(10)
                .setFirstAdIndex(2)
                .setNoOfDataBetweenAds(10)
                .setTestDeviceIds(testDevicesIds)
                .setAdapter(adapter)
                //Use the following for the default Wrapping behaviour
//                .setAdViewWrappingStrategy(new BannerAdViewWrappingStrategy())
                // Or implement your own custom wrapping behaviour:
                .setAdViewWrappingStrategy(new BannerAdViewWrappingStrategyBase() {
                    @NonNull
                    @Override
                    protected ViewGroup getAdViewWrapper(ViewGroup parent) {
                        return (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.native_express_ad_container,
                                parent, false);
                    }

                    @Override
                    protected void recycleAdViewWrapper(@NonNull ViewGroup wrapper, @NonNull AdView ad) {
                        //get the view which directly will contain ad
                        ViewGroup container = (ViewGroup) wrapper.findViewById(R.id.ad_container);
                        //iterating through all children of the container view and remove the first
                        // occured {@link NativeExpressAdView}. It could be different with
                        // {@param ad}!!!*//*
                        for (int i = 0; i < container.getChildCount(); i++) {
                            View v = container.getChildAt(i);
                            if (v instanceof AdView) {
                                container.removeViewAt(i);
                                break;
                            }
                        }
                    }

                    @Override
                    protected void addAdViewToWrapper(@NonNull ViewGroup wrapper, @NonNull AdView ad) {
                        //get the view which directly will contain ad
                        ViewGroup container = (ViewGroup) wrapper.findViewById(R.id.ad_container);
                        //add the {@param ad} directly to the end of container*//*
                        container.addView(ad);
                    }
                })
                .build();

        lvMessages.setAdapter(adapterWrapper); // setting an AdmobAdapterWrapper to a ListView

        //preparing the collection of data
        final String sItem = "item #";
        ArrayList<String> lst = new ArrayList<>(100);
        for(int i=1;i<=100;i++)
            lst.add(sItem.concat(Integer.toString(i)));

        //adding a collection of data to your adapter and rising the data set changed event
        adapter.addAll(lst);
        adapter.notifyDataSetChanged();
    }

    /*
    * Seems to be a good practice to destroy all the resources you have used earlier :)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapterWrapper.release();
    }

    @Override
    protected void onPause() {
        adapterWrapper.pauseAll();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterWrapper.resumeAll();
    }
}
