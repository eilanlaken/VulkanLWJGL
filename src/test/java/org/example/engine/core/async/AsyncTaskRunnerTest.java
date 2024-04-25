package org.example.engine.core.async;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsArrayFloat;
import org.example.engine.core.collections.CollectionsArrayFloatConcurrent;
import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncTaskRunnerTest {

    private static CollectionsArrayFloat           arrayFloat  = new CollectionsArrayFloat();
    private static CollectionsArrayFloat           arrayFloat2 = new CollectionsArrayFloat();
    private static CollectionsArrayFloatConcurrent arrayFloat3 = new CollectionsArrayFloatConcurrent();

    @BeforeEach
    private void setup() {
        arrayFloat.clear();
        arrayFloat.add(1,2,3);
        arrayFloat.add(-1,-2,-3);

        arrayFloat2.clear();
        arrayFloat2.add(3,3,3);

        arrayFloat3.clear();
    }

    @Test
    void test_isPrerequisite_1() {
        AsyncTask t1 = new AsyncTask() {
            @Override
            public void task() {

            }
        };

        AsyncTask t2 = new AsyncTask(t1) {
            @Override
            public void task() {

            }
        };

        Assertions.assertTrue (t2.isPrerequisite(t1));
        Assertions.assertFalse(t1.isPrerequisite(t2));
    }

    @Test
    void test_isPrerequisite_2() {
        AsyncTask t1 = new AsyncTask() {
            @Override
            public void task() {

            }
        };

        AsyncTask t2 = new AsyncTask(t1) {
            @Override
            public void task() {

            }
        };


        AsyncTask t3 = new AsyncTask(t2) {
            @Override
            public void task() {

            }
        };

        Assertions.assertFalse(t1.isPrerequisite(t2));
        Assertions.assertTrue (t2.isPrerequisite(t1));
        Assertions.assertTrue (t3.isPrerequisite(t1));
        Assertions.assertTrue (t3.isPrerequisite(t2));
    }

    @Test
    void test_isPrerequisite_3() {
        AsyncTask t1 = new AsyncTask() {
            @Override
            public void task() {

            }
        };

        AsyncTask t2 = new AsyncTask() {
            @Override
            public void task() {

            }
        };

        AsyncTask t3 = new AsyncTask(t1, t2) {
            @Override
            public void task() {

            }
        };

        Assertions.assertFalse(t1.isPrerequisite(t2));
        Assertions.assertFalse(t2.isPrerequisite(t1));
        Assertions.assertTrue (t3.isPrerequisite(t1));
        Assertions.assertTrue (t3.isPrerequisite(t2));
    }

    @Test
    void test_addPrerequisite_4() {
        AsyncTask t1 = new AsyncTask() {
            @Override
            public void task() {

            }
        };

        AsyncTask t2 = new AsyncTask(t1) {
            @Override
            public void task() {

            }
        };

        Assertions.assertThrows(IllegalArgumentException.class, () -> t1.addPrerequisite(t1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> t1.addPrerequisite(t2));
        Assertions.assertDoesNotThrow(() -> t2.addPrerequisite(t1));
    }

    @Test
    void test_runSync_single_1() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat.size; i++) {
                    arrayFloat.set(i, 1);
                }
            }
        };

        AsyncTask a2 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat.size; i++) {
                    if (arrayFloat.get(i) < 0) arrayFloat.set(i, -10);
                }
            }
        };

        AsyncTaskRunner.runSync(a1);
        AsyncTaskRunner.runSync(a2);

        Assertions.assertEquals(1, arrayFloat.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(3), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(4), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(5), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_single_2() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat.size; i++) {
                    arrayFloat.set(i, 1);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat.size; i++) {
                    if (arrayFloat.get(i) < 0) arrayFloat.set(i, -10);
                }
            }
        };

        AsyncTaskRunner.runSync(a2);

        Assertions.assertEquals(1, arrayFloat.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(3), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(4), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, arrayFloat.get(5), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_single_3() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTaskRunner.runSync(a2);

        Assertions.assertEquals(8, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(8, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(8, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }


    @Test
    void test_runSync_single_4() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTask a3 = new AsyncTask(a2) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTaskRunner.runSync(a3);

        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_many_1() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
                System.out.println("a1: " + arrayFloat2);
            }
        };

        AsyncTask a2 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
                System.out.println("a2: " + arrayFloat2);
            }
        };

        AsyncTask a3 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
                System.out.println("a3: " + arrayFloat2);
            }
        };

        CollectionsArray<AsyncTask> tasks = new CollectionsArray<>();
        tasks.addAll(a1, a2, a3);
        AsyncTaskRunner.runSync(tasks);

        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_many_2() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTask a3 = new AsyncTask(a1, a2) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        CollectionsArray<AsyncTask> tasks = new CollectionsArray<>();
        tasks.addAll(a1, a2, a3);
        AsyncTaskRunner.runSync(tasks);

        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_many_3() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTask a3 = new AsyncTask(a1, a2) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        CollectionsArray<AsyncTask> tasks = new CollectionsArray<>();
        tasks.addAll(a1, a2, a3);
        AsyncTaskRunner.runSync(tasks);

        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_many_4() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTask a3 = new AsyncTask(a1, a2) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        CollectionsArray<AsyncTask> tasks = new CollectionsArray<>();
        tasks.addAll(a2, a3);
        AsyncTaskRunner.runSync(tasks);

        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runSync_many_5() {
        AsyncTask a1 = new AsyncTask() {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) * 2);
                }
            }
        };

        AsyncTask a2 = new AsyncTask(a1) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTask a3 = new AsyncTask(a1, a2) {
            @Override
            public void task() {
                for (int i = 0; i < arrayFloat2.size; i++) {
                    arrayFloat2.set(i, arrayFloat2.get(i) + 2);
                }
            }
        };

        AsyncTaskRunner.runSync(a2, a3);
        Assertions.assertEquals(10, arrayFloat2.get(0),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(1),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10, arrayFloat2.get(2),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void test_runAsync_single_1() throws InterruptedException {
        AsyncTask t1 = new AsyncTask() {
            @Override
            public void task() {
                arrayFloat3.add(1);
                System.out.println("1: " + arrayFloat3);
            }
        };

        AsyncTask t2 = new AsyncTask() {
            @Override
            public void task() {
                arrayFloat3.add(2);
                System.out.println("2: " + arrayFloat3);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread thread1 = AsyncTaskRunner.runAsync(t1);
        Thread thread2 = AsyncTaskRunner.runAsync(t2);

        thread1.join();
        thread2.join();
        System.out.println("after join");
        Assertions.assertEquals(2, arrayFloat3.size);
    }

}