package com.hxsj.telephone.observer;

import java.io.Serializable;

/**
 * Created by Administrator on 13-8-30.
 */
public interface Observer extends Serializable{
    public void notifyChanged(Object object);
}
