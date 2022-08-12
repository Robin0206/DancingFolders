import java.util.concurrent.locks.ReentrantLock;

public class Workerpool {
    private final int numOfWorkers;
    private final boolean encrypted;
    private final String[] paths;
    private ReentrantLock lock;
    private boolean[] freeToEn_Decrypt;

    public Workerpool(String[] paths, int numOfWorkers, boolean encrypted) {
        this.numOfWorkers = numOfWorkers;
        this.encrypted = encrypted;
        this.paths = paths;
        this.lock = new ReentrantLock();
        this.freeToEn_Decrypt = new boolean[paths.length];
    }
    public void work(char[] password) throws Exception {
        Worker[] workers = new Worker[numOfWorkers];
        if(encrypted) {
            //initialize workers
            for(int i = 0; i < workers.length; i++) {
                workers[i] = new Worker(password, paths, this, encrypted);
            }
            //start the workers
            for (Worker worker : workers) {
                worker.start();
            }
            //wait for the workers to stop
            for (Worker worker : workers) {
                worker.join();
            }
        }else {
            //initialize workers
            for(int i = 0; i < workers.length; i++) {
                workers[i] = new Worker(password, paths, this, encrypted);
            }
            //start the workers
            for (Worker worker : workers) {
                worker.start();
            }
            //wait for the workers to stop
            for (Worker worker : workers) {
                worker.join();
            }
        }
    }

    public int getFreePlace() {
        lock.lock();

        for(int i = 0; i < freeToEn_Decrypt.length; i++) {//search for free places
            if(freeToEn_Decrypt[i]) {//if a place is free
                freeToEn_Decrypt[i] = false;//mark it as not freeToEn_Decrypt
                lock.unlock();//release the lock
                return i;//return the place
            }
        }
        lock.unlock();//return the lock
        return -1;
    }
}
