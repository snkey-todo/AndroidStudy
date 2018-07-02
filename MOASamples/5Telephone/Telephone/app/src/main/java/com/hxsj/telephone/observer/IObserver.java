package com.hxsj.telephone.observer;


public interface IObserver {
	public void registerObserver(Observer observer, ObserverFilter filter);

	public void unregisterObserver(Observer observer);

	public void notifyDataChanged(Object object, ObserverFilter filter);

	public void unregisterAllObserver();

	public void registerSingleObserver(Observer observer, ObserverFilter filter);
}
