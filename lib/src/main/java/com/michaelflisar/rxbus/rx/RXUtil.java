package com.michaelflisar.rxbus.rx;

import android.util.Log;

import com.michaelflisar.rxbus.interfaces.IRXBusIsResumedProvider;
import com.michaelflisar.rxbus.interfaces.IRXBusResumedListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Prometheus on 28.04.2016.
 */
public class RXUtil
{
//    private static final Observable.Transformer schedulersTransformer = observable -> observable
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread());

    private static final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable)observable).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyBackgroundWorkForegroundObserveSchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    // default background subscription, foreground observation schedulars
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applySchedulars() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    public static <T> Observable<T> applySchedulars(Observable<T> observable)
    {
        return observable.compose(applyBackgroundWorkForegroundObserveSchedulers());
    }

    public static Observable<Boolean> createResumeStateObservable(IRXBusIsResumedProvider provider)
    {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                IRXBusResumedListener listener = new IRXBusResumedListener() {

                    @Override
                    public void onResumedChanged(boolean resumed) {
                        if (subscriber.isUnsubscribed()) {
                            provider.removeResumedListener(this);
                        } else {
                            subscriber.onNext(resumed);
                        }
                    }
                };

                provider.addResumedListener(listener, false);
            }
        });
    }
}
