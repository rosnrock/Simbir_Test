package com.skorbr.simbirtest_v2;

import io.realm.RealmObject;

public class TaskRealmStructure extends RealmObject {
    public int id;
    public String name;
    public String description;
    public long dateStart;
    public long dateFinish;
    public boolean flagOfFirst;
}
