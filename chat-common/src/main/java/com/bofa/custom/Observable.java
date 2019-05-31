package com.bofa.custom;

import com.bofa.session.Session;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.custom
 * @date 2019/5/30
 */
public class Observable {

    private boolean changed;

    private CopyOnWriteArrayList<Observer> observers = new CopyOnWriteArrayList<>();

    ReentrantLock mainLock = new ReentrantLock();

    public void addObserver(Observer observer) {
        assert observer != null;
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void notifyObservers() {
        notifyObservers(null);
    }

    public void notifyObservers(Object arg) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

        try {
            mainLock.lock();
            if (!changed) {
                return;
            }
            arrLocal = observers.toArray();
            clearChanged();

            for (int i = arrLocal.length - 1; i >= 0; i--) {
                ((Observer) arrLocal[i]).update(this, arg);
            }
            clearChanged();

        } finally {
            mainLock.unlock();
        }
    }

    public List<Observer> getObservers() {
        return observers;
    }


    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return the number of observers of this object.
     */
    public int countObservers() {
        return observers.size();
    }

    public void deleteObserver(Observer observer) {
        assert observer != null;
        observers.remove(observer);
    }

    public void deleteObserver(Session session){
        Observer temp = null;
        for (Observer obs : observers) {
            if (((Session.SessionObserver) obs).getObserverSession().equals(session)) {
                temp = obs;
            }
        }
        Optional.ofNullable(temp).ifPresent(observers::remove);
    }

    public void deleteObservers() {
        observers.clear();
    }

    public void clearObservers() {
        if (observers == null) {
            return;
        }
        observers.clear();
        observers = null;
    }

    public void setChanged() {
        try {
            mainLock.lock();
            changed = true;
        } finally {
            mainLock.unlock();
        }
    }

    public boolean hasChanged() {
        try {
            mainLock.lock();
            return changed;
        } finally {
            mainLock.unlock();
        }
    }

    public void clearChanged() {
        try {
            mainLock.lock();
            changed = false;
        } finally {
            mainLock.unlock();
        }
    }

}
