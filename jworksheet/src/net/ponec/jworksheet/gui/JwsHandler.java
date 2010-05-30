/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ponec.jworksheet.gui;

import net.ponec.jworksheet.core.ApplTools;

/**
 * Signal handler implementation.
 * @author Pavel Ponec
 */
@SuppressWarnings("all")
public class JwsHandler implements sun.misc.SignalHandler {

    private final JWorkSheet jWorkSheet;
    private boolean running = true;

    private JwsHandler(JWorkSheet jWorkSheet) {
        this.jWorkSheet = jWorkSheet;
    }

    public void handle(sun.misc.Signal signal) {
        if (running) {
            running = false;
            jWorkSheet.closeAppl(null);
            System.exit(0);
        }
    }

    /**
     * Method does not work on the Linux Ubuntu.
     * List of failed test signals on Linux: SIGTERM,TERM,SIGKILL,KILL,SIGINT,INT,QUIT .
     * @param jWorkSheet
     * @throws java.lang.Exception
     */
    @SuppressWarnings("all")
    public static void init(final JWorkSheet jWorkSheet) throws Exception {

        JwsHandler h = new JwsHandler(jWorkSheet);
        String signal = ApplTools.isWindowsOS() ? "TERM" : "TERM";
        sun.misc.Signal.handle(new sun.misc.Signal(signal), h);

    }
}