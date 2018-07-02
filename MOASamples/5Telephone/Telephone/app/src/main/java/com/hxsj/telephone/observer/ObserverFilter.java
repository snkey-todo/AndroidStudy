package com.hxsj.telephone.observer;

/**
 * Created by Administrator on 13-8-30.
 */
public class ObserverFilter {
    private String action;

    public ObserverFilter(String action) {
        setAction(action);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof ObserverFilter) {
            ObserverFilter filter = (ObserverFilter) object;
            String actionStr = filter.getAction();
            if (actionStr != null && actionStr.equals(getAction())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (action != null)
            return action.hashCode();
        return 0;
    }
}