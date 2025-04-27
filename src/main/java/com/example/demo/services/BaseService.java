package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService<T> {

    protected List<T> items = new ArrayList<>();
    protected long nextId = 0;

    public List<T> getAll() {
        return items;
    }

    public T getById(String id) {
        for (T item : items) {
            if (getId(item).equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void add(T item) {
        if (getId(item) == null || getId(item).isEmpty()) {
            setId(item, String.valueOf(++nextId));
        }
        items.add(item);
    }

    public boolean deleteById(String id) {
        return items.removeIf(item -> getId(item).equals(id));
    }

    protected abstract String getId(T item);

    protected abstract void setId(T item, String id);
}
