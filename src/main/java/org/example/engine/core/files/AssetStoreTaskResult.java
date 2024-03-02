package org.example.engine.core.files;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AssetStoreTaskResult<T> {

    private final Future<T> future;

    AssetStoreTaskResult(Future<T> future) {
        this.future = future;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public T await() {
        try {
            return future.get();
        } catch (InterruptedException ex) {
            return null;
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
