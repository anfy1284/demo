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

    public void add(T item, boolean saveToFile) {
        if (getId(item) == null || getId(item).isEmpty()) {
            setId(item, String.valueOf(++nextId));
        } else {
            // Обновляем nextId, чтобы он соответствовал максимальному ID
            long currentId = Long.parseLong(getId(item));
            if (currentId > nextId) {
                nextId = currentId;
            }
        }
        items.add(item);
        if (saveToFile) {
            onItemAdded(item); // Вызываем метод для действий после добавления
        }
    }

    protected void onItemAdded(T item) {
        // Переопределяется в наследниках, если нужно
    }

    public boolean deleteById(String id) {
        boolean removed = items.removeIf(item -> getId(item).equals(id));
        if (removed) {
            onItemDeleted(id);
        }
        return removed;
    }

    protected void onItemDeleted(String id) {
        // Переопределяется в наследниках, если нужно
    }

    protected abstract String getId(T item);

    protected abstract void setId(T item, String id);
}
