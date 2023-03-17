package com.linkitsoft.beepvending.Activities.Dispense;

abstract class RunableEx implements Runnable{
    String data;
    public RunableEx(String data)
    {
        this.data = data;
    }
}
