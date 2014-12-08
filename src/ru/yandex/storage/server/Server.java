package ru.yandex.storage.server;

import ru.yandex.storage.coordinator.CoordinatorInterface;
import ru.yandex.storage.coordinator.ViewInfo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private final String serverName;
    private final ViewInfo viewInfo = new ViewInfo();
    private final Map<String, String> storage = new HashMap<>();
    private long currentTime = 0;
    private CoordinatorInterface service;

    protected Server(String serverName, String coordinatorName) throws RemoteException {
        this.serverName = serverName;
        try {
            service = (CoordinatorInterface) Naming.lookup(coordinatorName);
        } catch (NotBoundException | MalformedURLException e) {
            throw new RemoteException("Server constructor exception", e);
        }
    }

    @Override
    public void put(String key, String value) throws RemoteException {
        checkAccess(true, false);
        try {
            if (!viewInfo.backup.isEmpty()) {
                ServerInterface backupServer = (ServerInterface) Naming.lookup(viewInfo.backup);
                backupServer.putBackup(key, value);
            }
            storage.put(key, value);
        } catch (NotBoundException | MalformedURLException e) {
            throw new RemoteException("RMI exception", e);
        }
    }

    @Override
    public void putBackup(String key, String value) throws RemoteException {
        checkAccess(false, true);
        storage.put(key, value);
    }

    @Override
    public String get(String key) throws RemoteException {
        checkAccess(true, false);
        return storage.getOrDefault(key, "");
    }

    public void tick() throws RemoteException {
        ViewInfo currentView = service.ping(viewInfo.view, serverName);

        if (viewInfo.primary.equals(serverName) && !currentView.backup.equals(viewInfo.backup) && !currentView.backup.isEmpty()) {
            viewInfo.set(currentView);

            try {
                ServerInterface backupServer = (ServerInterface) Naming.lookup(viewInfo.backup);

                Iterator<Map.Entry<String, String>> iterator = storage.entrySet().iterator();
                for (; iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    backupServer.putBackup(entry.getKey(), entry.getValue());
                }
                currentView = service.ping(currentView.view, serverName);
            } catch (NotBoundException | MalformedURLException e) {
                throw new RemoteException("RMI exception", e);
            }
        }
        viewInfo.set(currentView);
        ++currentTime;
    }

    private void checkAccess(boolean primary, boolean backup) {
        // Hack
        boolean hasAccess = (primary && viewInfo.primary.equals(serverName));
        hasAccess |= (backup && viewInfo.backup.equals(serverName));
        if (!hasAccess) {
            throw new IncorrectOperationException("You called a method of an unused server");
        }
    }
}
