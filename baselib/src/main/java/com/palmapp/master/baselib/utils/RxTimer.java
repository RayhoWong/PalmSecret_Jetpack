package com.palmapp.master.baselib.utils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * Created by huangweihao on 2019/8/29.
 */
public class RxTimer {
    private static Disposable mDisposable;
    /**
     * 具体的操作
     */
    public interface RxAction{
        void action();
    }
    /**
     *
     * @param milliSeconds 间隔时间 单位：毫秒
     * @param rxAction 具体操作
     */
    public static void timer(final long milliSeconds, final RxAction rxAction){
        Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if(rxAction != null){
                            rxAction.action();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        /**
                         * 取消订阅
                         */
                        if(mDisposable != null && !mDisposable.isDisposed()){
                            mDisposable.dispose();
                        }
                    }
                });
    }
}
