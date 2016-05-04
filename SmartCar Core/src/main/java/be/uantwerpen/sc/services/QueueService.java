package be.uantwerpen.sc.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Niels on 4/05/2016.
 */
@Service
public class QueueService {

    BlockingQueue<String> jobQueue = new ArrayBlockingQueue<>(10);

    public String getJob(){
        try {
            return jobQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertJob(String job){
        try {
            jobQueue.put(job);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<String> getContentQueue(){

        return this.jobQueue;
    }

}
