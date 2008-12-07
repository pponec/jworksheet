/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.module;

import net.ponec.jworksheet.core.ApplContextInterface;

/**
 * An sample moduel
 * @author pavel
 */
public class SampleModule implements Runnable {
    
    private ApplContextInterface context;
    
    public SampleModule(ApplContextInterface context) {
        this.context = context;
    }

    public void run() {
        if (context.isStarting()) {
            System.out.println("STARTING jWorkSheet");
        } else {
            System.out.println("FINISHING jWorkSheet");
        }
    }
}
