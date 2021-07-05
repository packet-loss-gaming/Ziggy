package gg.packetloss.ziggy.threading;

import gg.packetloss.ziggy.abstraction.ZTask;

import java.util.concurrent.Future;

public class ThreadPoolZTask implements ZTask {
    private final Future<?> task;

    public ThreadPoolZTask(Future<?> task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel(false);
    }
}
