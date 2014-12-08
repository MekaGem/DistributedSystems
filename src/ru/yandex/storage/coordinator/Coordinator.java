package ru.yandex.storage.coordinator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Coordinator extends UnicastRemoteObject implements CoordinatorInterface {
    public static final int DEAD_PINGS = 3;

    private long currentTime = 0;
    private boolean dataSavedOnBackup = false;
    private ViewInfo viewInfo = new ViewInfo();
    private Map<String, Long> lastPing = new HashMap<String, Long>();

    public Coordinator() throws RemoteException {
    }

    // this method is to be called by server
    public ViewInfo ping(int view, String serverName) throws RemoteException {
        lastPing.put(serverName, currentTime);

        if (serverName.equals(viewInfo.backup)) { // Backup ping
            if (view == 0 && dataSavedOnBackup) {
                dataSavedOnBackup = false;
                ++viewInfo.view;
            }
        } else if (serverName.equals(viewInfo.primary)) { // Primary ping
            if (view == 0 && dataSavedOnBackup) {
                dataSavedOnBackup = false;
                viewInfo.primary = viewInfo.backup;
                viewInfo.backup = serverName;
                ++viewInfo.view;
            } else if (view == viewInfo.view) {
                dataSavedOnBackup = true;
            }
        } else {
            if (viewInfo.primary.isEmpty()) {
                viewInfo.primary = serverName;
                dataSavedOnBackup = false;
                ++viewInfo.view;
            } else if (viewInfo.backup.isEmpty()) {
                viewInfo.backup = serverName;
                ++viewInfo.view;
            }
        }

        return new ViewInfo(viewInfo);
    }

    // this method is to be called by client
    public String primary() throws RemoteException {
        return viewInfo.primary;
    }

    // this method is to be called automatically as time goes by
    public void tick() {
        Iterator<Map.Entry<String, Long>> iterator = lastPing.entrySet().iterator();
        for (; iterator.hasNext(); ) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() >= DEAD_PINGS) {
                if (!entry.getKey().equals(viewInfo.primary) || dataSavedOnBackup) {
                    iterator.remove();
                }
            }
            // TODO: do something with situation when backup fell during saving data
        }

        boolean stateChanged = false;

        if (!viewInfo.primary.isEmpty() && !lastPing.containsKey(viewInfo.primary)) {
            viewInfo.primary = "";
            stateChanged = true;
        }
        if (!viewInfo.backup.isEmpty() && !lastPing.containsKey(viewInfo.backup)) {
            viewInfo.backup = "";
            stateChanged = true;
        }

        if (viewInfo.primary.isEmpty() || viewInfo.backup.isEmpty()) {
            dataSavedOnBackup = false;
        }


        if (viewInfo.primary.isEmpty()) {
            if (!viewInfo.backup.isEmpty()) {
                viewInfo.primary = viewInfo.backup;
                viewInfo.backup = "";
                stateChanged = true;
            } else if (!lastPing.isEmpty()) {
                viewInfo.primary = lastPing.keySet().iterator().next();
                stateChanged = true;
            }
        }

        if (viewInfo.backup.isEmpty()) {
            for (String serverName : lastPing.keySet()) {
                if (!serverName.equals(viewInfo.primary)) {
                    viewInfo.backup = serverName;
                    stateChanged = true;
                    break;
                }
            }
        }

        if (stateChanged) {
            ++viewInfo.view;
        }
        ++currentTime;
    }
}
