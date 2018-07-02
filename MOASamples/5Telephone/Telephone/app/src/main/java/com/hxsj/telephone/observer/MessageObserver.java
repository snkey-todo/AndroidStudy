package com.hxsj.telephone.observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.hxsj.telephone.log.Logger;


/**
 * Created by Administrator on 13-8-30.
 */
public class MessageObserver implements IObserver {
    public static final String OBSERVER_KEY = "observer_key";
    public static final String FILTER_KEY = "filter_key";
    public static final int MSG_WHAT = 0;
    private static MessageObserver ourInstance = new MessageObserver();
    private Logger log = Logger.getLogger();
    private ArrayList<HashMap<String, Object>> observers = new ArrayList<HashMap<String, Object>>();

    public static MessageObserver getInstance() {
        return ourInstance;
    }

    private MessageObserver() {
    }

    @Override
    public void registerObserver(Observer observer, ObserverFilter filter) {
        if (observer == null) {
            log.e("The observer is null.");
        }
        synchronized (observers) {
            if (!contains(observer, filter)) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(OBSERVER_KEY, observer);
                map.put(FILTER_KEY, filter);
                observers.add(map);
            } else {
                log.d("Observer " + observer + " is already registered.");
            }
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        synchronized (observers) {
            HashMap<String, Object> map = findMapByObserver(observer);
            if (map != null) {
                observers.remove(map);
            } else {
                log.e("Observer " + observer + " was not registered.");
            }
        }
    }

    @Override
	public void notifyDataChanged(Object object, ObserverFilter filter) {
//    	if(filter.getAction().equals("me.nvshen.goddess.bean.tcp.receiver.OFFMSGResponse_PU")){
//    		log.i("notifyDataChanged called action: " + filter.getAction());
//    	}
        for (HashMap<String, Object> map : observers) {
            ObserverFilter filter1 = (ObserverFilter) map.get(FILTER_KEY);
            if (filter1.equals(filter)) {
                Observer observer = (Observer) map.get(OBSERVER_KEY);
                Message msg = h.obtainMessage(MSG_WHAT);
                Bundle b = new Bundle();
                b.putString("action", filter.getAction());
                b.putSerializable("observer", observer);
                b.putSerializable("object", (Serializable) object);
                msg.setData(b);
                h.sendMessage(msg);
            }
        }
    }

    @Override
    public void unregisterAllObserver() {
        synchronized (observers) {
            observers.clear();
        }
    }

    @Override
    public void registerSingleObserver(Observer observer, ObserverFilter filter) {
        if (observer == null) {
            log.e("The observer is null.");
        }
        synchronized (observers) {
            HashMap<String, Object> map = findExistByObserverFilter(filter);
            if (map != null) {
                observers.remove(map);
            }
            HashMap<String, Object> newMap = new HashMap<String, Object>();
            newMap.put(OBSERVER_KEY, observer);
            newMap.put(FILTER_KEY, filter);
            observers.add(newMap);

        }
    }

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT:
                    Bundle b = msg.getData();
                    Observer observer = (Observer) b.getSerializable("observer");
                    Object object = b.getSerializable("object");
                    observer.notifyChanged(object);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @param observer
     * @return
     */
    private boolean contains(Observer observer, ObserverFilter filter) {
        for (HashMap<String, Object> map : observers) {
            Observer b = (Observer) map.get(OBSERVER_KEY);
            ObserverFilter f = (ObserverFilter) map.get(FILTER_KEY);
            if (observer.equals(b) && filter.equals(f)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param observer
     * @return
     */
    private HashMap<String, Object> findMapByObserver(Observer observer) {
        for (HashMap<String, Object> map : observers) {
            Observer b = (Observer) map.get(OBSERVER_KEY);
            if (observer == b) {
                return map;
            }
        }
        return null;
    }

    /**
     * @param observerFilter
     * @return
     */
    private HashMap<String, Object> findExistByObserverFilter(ObserverFilter observerFilter) {
        for (HashMap<String, Object> map : observers) {
            ObserverFilter b = (ObserverFilter) map.get(FILTER_KEY);
            if (observerFilter.getAction().equals(b.getAction())) {
                return map;
            }
        }
        return null;
    }
}
