package ru.yandex.storage.coordinator;

import java.io.Serializable;

public class ViewInfo implements Serializable {
    public int view;
    public String primary;
    public String backup;

    public ViewInfo(ViewInfo other) {
        view = other.view;
        primary = other.primary;
        backup = other.backup;
    }

    public ViewInfo() {
        view = 0;
        primary = "";
        backup = "";
    }

    public void set(ViewInfo other) {
        view = other.view;
        primary = other.primary;
        backup = other.backup;
    }
}
