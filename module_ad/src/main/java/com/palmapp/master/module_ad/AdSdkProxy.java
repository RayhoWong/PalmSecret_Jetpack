package com.palmapp.master.module_ad;

import android.app.Activity;
import android.content.Context;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.nativeAds.AppLovinNativeAd;
import com.applovin.nativeAds.AppLovinNativeAdLoadListener;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.cs.bd.ad.AdSdkApi;
import com.cs.bd.ad.http.bean.BaseModuleDataItemBean;
import com.cs.bd.ad.manager.AdSdkManager;
import com.cs.bd.ad.params.AdSdkParamsBuilder;
import com.cs.bd.ad.params.AdSet;
import com.cs.bd.ad.params.ClientParams;
import com.cs.bd.ad.params.OuterAdLoader;
import com.cs.bd.ad.sdk.AdmobAdConfig;
import com.cs.bd.ad.sdk.SdkAdContext;
import com.cs.bd.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.cs.bd.buychannel.BuyChannelApi;
import com.cs.bd.buychannel.buyChannel.bean.BuyChannelBean;
import com.cs.statistic.StatisticsManager;
import com.google.android.gms.ads.AdSize;
import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.buychannel.BuyChannelProxy;
import com.palmapp.master.baselib.constants.PreConstants;
import com.palmapp.master.baselib.manager.DebugProxy;
import com.palmapp.master.baselib.manager.ThreadExecutorProxy;
import com.palmapp.master.baselib.utils.AppUtil;
import com.palmapp.master.baselib.utils.LogUtil;
import com.palmapp.master.baselib.utils.VersionController;
import com.palmapp.master.module_ad.proxy.AppLovinInterstitialAdProxy;
import com.palmapp.master.module_ad.proxy.AppLovinRewardAdProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangliang on 18-5-19.
 */

public class AdSdkProxy {


    /**
     * 初始化广告sdk数据
     * 有需要进程，需要初始化
     */
    public static void initAdSDK() {
        final String adId = AppUtil.getGoogleAdvertisingId();
        try {
            AppLovinSdk.getInstance(GoCommonEnv.INSTANCE.getApplication()).initializeSdk();
            Context context = GoCommonEnv.INSTANCE.getApplication();
            ClientParams clientParams = new ClientParams(
                    GoCommonEnv.INSTANCE.getOuterChannel(), AppUtil.getInstalledTime(context, context
                    .getPackageName()), !VersionController.INSTANCE.isNewUser());
            clientParams.setUseFrom(BuyChannelApi.getBuyChannelBean(context).isUserBuy() ? "1" : "0");
            AdSdkApi.setEnableLog(DebugProxy.INSTANCE.isOpenLog());
            AdSdkApi.initSDK(context, GoCommonEnv.INSTANCE.getApplicationId(),
                    StatisticsManager.getUserId(context),
                    adId, GoCommonEnv.INSTANCE.getInnerChannel(),
                    clientParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String adId = AppUtil.getGoogleAdvertisingId();
                    AdSdkApi.setGoogleAdvertisingId(GoCommonEnv.INSTANCE.getApplication(), adId);
                }
            }).start();

        } catch (Exception e) {
//            FirebaseSdkProxy.crashLog("初始化ad sdk 异常 " + e.getMessage());
        }
    }

    /**
     * @param activity             全屏广告要用到
     * @param moduleId             广告虚拟id
     * @param adControlInterceptor 拦截器，是否加载广告
     * @param listener             广告请求后回调
     */
    public static void loadAd(Activity activity, int moduleId, AdSdkManager.IAdControlInterceptor adControlInterceptor, final AdSdkManager.ILoadAdvertDataListener listener) {
        Context context = GoCommonEnv.INSTANCE.getApplication();
        LogUtil.d(LogUtil.TAG_XMR, "adController addAdListener method runing");
        if (activity != null) {
            context = new SdkAdContext(GoCommonEnv.INSTANCE.getApplication(), activity) {
                @Override
                public boolean needPassActivity2FbNativeAd() {
                    return false;
                }
            };
        }
        BuyChannelBean bean = BuyChannelProxy.INSTANCE.getBuyChannelBean();
        AdSdkParamsBuilder.Builder builder = new AdSdkParamsBuilder.Builder(context, moduleId, bean.getBuyChannel(), bean.getSecondUserType(), null, listener);
        builder.adControlInterceptor(adControlInterceptor);
        AdSdkParamsBuilder adBuilder = builder.returnAdCount(1).filterAdSourceArray(new AdSet.Builder().add(new AdSet.AdType(BaseModuleDataItemBean.AD_DATA_SOURCE_APPLOVIN, -1)).build())
                .isNeedDownloadIcon(true).isNeedDownloadBanner(true).isRequestData(false).admobAdConfig(new AdmobAdConfig(AdSize.MEDIUM_RECTANGLE))
                .outerAdLoader(new OuterAdLoader() {
                    @Override
                    public void loadAd(final OuterSdkAdSourceListener outerSdkAdSourceListener) {
                        if (getAdSourceType() == BaseModuleDataItemBean.AD_DATA_SOURCE_APPLOVIN) {
                            //applovin
                            if (getAdSourceInfo().getOnlineAdvType() == BaseModuleDataItemBean.ONLINE_AD_SHOW_TYPE_FULL_SCREEN) {
                                final AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(GoCommonEnv.INSTANCE.getApplication()), GoCommonEnv.INSTANCE.getApplication());
                                final AppLovinInterstitialAdProxy proxy = new AppLovinInterstitialAdProxy(interstitialAd);
                                AppLovinSdk.getInstance(GoCommonEnv.INSTANCE.getApplication()).getAdService()
                                        .loadNextAdForZoneId(getAdRequestId(), new AppLovinAdLoadListener() {
                                            @Override
                                            public void adReceived(AppLovinAd ad) {
                                                SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = new SdkAdSourceAdInfoBean();
                                                List<Object> sdkAdSourceAdWrappers = new ArrayList<>();
                                                proxy.setAppLovinAd(ad);
                                                sdkAdSourceAdWrappers.add(proxy);
                                                sdkAdSourceAdInfoBean.addAdViewList(getAdRequestId(), sdkAdSourceAdWrappers);

                                                outerSdkAdSourceListener.onFinish(sdkAdSourceAdInfoBean);
                                            }

                                            @Override
                                            public void failedToReceiveAd(int errorCode) {
                                                outerSdkAdSourceListener.onException(errorCode);
                                            }
                                        });
                                interstitialAd.setAdClickListener(new AppLovinAdClickListener() {
                                    @Override
                                    public void adClicked(AppLovinAd ad) {
                                        outerSdkAdSourceListener.onAdClicked(proxy);
                                    }
                                });
                                interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                    @Override
                                    public void adDisplayed(AppLovinAd ad) {
                                        outerSdkAdSourceListener.onAdShowed(proxy);
                                    }

                                    @Override
                                    public void adHidden(AppLovinAd ad) {
                                        outerSdkAdSourceListener.onAdClosed(proxy);
                                    }
                                });
                            } else if (getAdSourceInfo().getOnlineAdvType() == BaseModuleDataItemBean.ONLINE_AD_SHOW_TYPE_VIDEO) {
                                final AppLovinIncentivizedInterstitial incentivizedInterstitial = AppLovinIncentivizedInterstitial.create(getAdRequestId(), AppLovinSdk.getInstance(GoCommonEnv.INSTANCE.getApplication()));
                                incentivizedInterstitial.preload(new AppLovinAdLoadListener() {
                                    @Override
                                    public void adReceived(AppLovinAd ad) {
                                        SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = new SdkAdSourceAdInfoBean();
                                        List<Object> sdkAdSourceAdWrappers = new ArrayList<>();
                                        AppLovinRewardAdProxy proxy = new AppLovinRewardAdProxy(incentivizedInterstitial);
                                        proxy.setAppLovinAd(ad);
                                        proxy.setOuterSdkAdSourceListener(outerSdkAdSourceListener);
                                        sdkAdSourceAdWrappers.add(proxy);
                                        sdkAdSourceAdInfoBean.addAdViewList(getAdRequestId(), sdkAdSourceAdWrappers);

                                        outerSdkAdSourceListener.onFinish(sdkAdSourceAdInfoBean);
                                    }

                                    @Override
                                    public void failedToReceiveAd(int errorCode) {
                                        outerSdkAdSourceListener.onException(errorCode);
                                    }
                                });
                            } else if (getAdSourceInfo().getOnlineAdvType() == BaseModuleDataItemBean.ONLINE_AD_SHOW_TYPE_BANNER) {
                                ThreadExecutorProxy.INSTANCE.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AppLovinAdView adView = new AppLovinAdView(AppLovinAdSize.BANNER, getAdRequestId(), GoCommonEnv.INSTANCE.getApplication());
                                        adView.setAdLoadListener(new AppLovinAdLoadListener() {
                                            @Override
                                            public void adReceived(AppLovinAd ad) {
                                                SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = new SdkAdSourceAdInfoBean();
                                                List<Object> sdkAdSourceAdWrappers = new ArrayList<>();
                                                sdkAdSourceAdWrappers.add(adView);
                                                sdkAdSourceAdInfoBean.addAdViewList(getAdRequestId(), sdkAdSourceAdWrappers);

                                                outerSdkAdSourceListener.onFinish(sdkAdSourceAdInfoBean);
                                            }

                                            @Override
                                            public void failedToReceiveAd(int errorCode) {
                                                outerSdkAdSourceListener.onException(errorCode);
                                            }
                                        });
                                        adView.setAdClickListener(new AppLovinAdClickListener() {
                                            @Override
                                            public void adClicked(AppLovinAd ad) {
                                                outerSdkAdSourceListener.onAdClicked(adView);
                                            }
                                        });
                                        adView.setAdDisplayListener(new AppLovinAdDisplayListener() {
                                            @Override
                                            public void adDisplayed(AppLovinAd ad) {
                                                outerSdkAdSourceListener.onAdShowed(adView);
                                            }

                                            @Override
                                            public void adHidden(AppLovinAd ad) {
                                                outerSdkAdSourceListener.onAdClosed(adView);
                                            }
                                        });
                                        adView.loadNextAd();
                                    }
                                }, 0);
                            } else {
                                //不是需要的广告源一定要返回失败
                                outerSdkAdSourceListener.onException(-2);
                            }
                        } else {
                            //不是需要的广告源一定要返回失败
                            outerSdkAdSourceListener.onException(-2);
                        }
                    }

                    @Override
                    public long getTimeOut() {
                        return 15000;
                    }
                })
                .build();
        AdSdkApi.loadAdBean(adBuilder);
    }

    /**
     * 永久开启/关停智能预加载服务
     *
     * @param context
     * @param switchOn true：开启；false：关停
     */
    public static void setIntelligentPreloadSwitch(Context context, boolean switchOn) {
        AdSdkApi.configIntelligentPreload(context, switchOn);
    }
}
