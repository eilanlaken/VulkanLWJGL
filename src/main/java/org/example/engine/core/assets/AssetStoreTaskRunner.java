package org.example.engine.core.assets;

import org.example.engine.core.memory.Resource;

import java.util.concurrent.*;

class AssetStoreTaskRunner implements Resource {

    private final ExecutorService service;

    public AssetStoreTaskRunner(int maxConcurrent) {
        service = Executors.newFixedThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread (Runnable r) {
                Thread thread = new Thread(r, "Asset Store Loading Thread");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

//    public <T> AssetStoreTaskResult<T> runTask(final AssetStoreTask task) {
//        if (service.isShutdown()) throw new IllegalStateException("Cannot run tasks on an executor that has been shutdown (disposed)");
//
//        return new AssetStoreTaskResult(service.submit((Callable<T>) () -> {
//            return task.call();
//        }));
//    }

    public void runTask(final AssetStoreTask task) {
        if (service.isShutdown()) throw new IllegalStateException("Cannot run tasks on an executor that has been shutdown (disposed)");
        service.submit(task);
    }

    @Override
    public void free() {
        service.shutdown();
        try {
            if (!service.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }
}
