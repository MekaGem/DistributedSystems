package ru.yandex.storage.coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinatorInterface extends Remote {
    public ViewInfo ping(int viewNum, String name) throws RemoteException;

    public String primary() throws RemoteException;
}