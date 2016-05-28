package be.uantwerpen.sc.controllers;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Arthur on 11/05/2016.
 */
public class CLocationPoller implements Runnable
{
    @Autowired
    CCommandSender cCommandSender;

    public CLocationPoller(CCommandSender cCommandSender)
    {
        this.cCommandSender = cCommandSender;
    }

    public void run()
    {
        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                Thread.currentThread().sleep(250);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            cCommandSender.sendCommand("DRIVE DISTANCE");
        }
    }
}
