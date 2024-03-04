package org.example.engine.core.async;

import org.lwjgl.glfw.GLFW;

public final class AsyncUtils {

    private static final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;
    private static long nextFrame = 0;
    private static boolean initialised = false;
    private static final RunningAvg sleepDurations = new RunningAvg(10);
    private static final RunningAvg yieldDurations = new RunningAvg(10);

    public static void sync(int fps) {
        if (fps <= 0) return;
        if (!initialised) initialise();

        try {
            for (long t0 = getTime(), t1; (nextFrame - t0) > sleepDurations.avg(); t0 = t1) { // sleep until the average sleep time is greater than the time remaining till nextFrame
                Thread.sleep(1);
                sleepDurations.add((t1 = getTime()) - t0); // update average sleep time
            }
            sleepDurations.dampenForLowResTicker(); // slowly dampen sleep average if too high to avoid yielding too much
            // yield until the average yield time is greater than the time remaining till nextFrame
            for (long t0 = getTime(), t1; (nextFrame - t0) > yieldDurations.avg(); t0 = t1) {
                Thread.yield();
                yieldDurations.add((t1 = getTime()) - t0); // update average yield time
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nextFrame = Math.max(nextFrame + NANOS_IN_SECOND / fps, getTime()); // schedule next frame, drop frame(s) if already too late for next frame
    }

    private static void initialise() {
        initialised = true;
        sleepDurations.init(1000 * 1000);
        yieldDurations.init((int)(-(getTime() - getTime()) * 1.333));
        nextFrame = getTime();
        String osName = System.getProperty("os.name");

        if (osName.startsWith("Win")) {
            // On windows the sleep functions can be highly inaccurate
            Thread timerAccuracyThread = new Thread(new Runnable() {
                public void run () {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            timerAccuracyThread.setName("LWJGL3 Timer");
            timerAccuracyThread.setDaemon(true);
            timerAccuracyThread.start();
        }
    }

    private static long getTime() {
        return (long)(GLFW.glfwGetTime() * NANOS_IN_SECOND);
    }

    private static class RunningAvg {

        private static final long DAMPEN_THRESHOLD = 10 * 1000L * 1000L; // 10ms
        private static final float DAMPEN_FACTOR = 0.9f; // don't change: 0.9f is exactly right!
        private final long[] slots;
        private int offset;

        public RunningAvg(int slotCount) {
            this.slots = new long[slotCount];
            this.offset = 0;
        }

        public void init(long value) {
            while (this.offset < this.slots.length) {
                this.slots[this.offset++] = value;
            }
        }

        public void add(long value) {
            this.slots[this.offset++ % this.slots.length] = value;
            this.offset %= this.slots.length;
        }

        public long avg() {
            long sum = 0;
            for (long slot : this.slots) {
                sum += slot;
            }
            return sum / this.slots.length;
        }

        public void dampenForLowResTicker() {
            if (this.avg() > DAMPEN_THRESHOLD) {
                for (int i = 0; i < this.slots.length; i++) {
                    this.slots[i] *= DAMPEN_FACTOR;
                }
            }
        }

    }

}
