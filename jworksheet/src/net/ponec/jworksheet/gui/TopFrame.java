/**
 * Copyright (C) 2007-8, Paul Ponec, contact: http://ponec.net/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/gpl-2.0.txt
 */


package net.ponec.jworksheet.gui;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.core.MessageException;
import net.ponec.jworksheet.bo.Parameters;

/**
 * A TopFrame.
 * @author Pavel Ponec
 */
public class TopFrame extends JFrame {
    
    /** Logger */
    public static final Logger LOGGER = Logger.getLogger(TopFrame.class.getName());
    
    final protected ApplContext applContext;
    
    
    /** Creates a new instance of TopFrame */
    public TopFrame(final ApplContext applContext) {
        this.applContext = applContext;
    }
    
    /** Set Visible the frame */
    public void setVisible() {
        setVisible(true);
    }
    
    /** Show an error message. */
    public int showMessage(String message, Throwable exception) {
        return showMessage(message, JOptionPane.ERROR_MESSAGE, (Icon)null, exception, (Object[])null );
    }
    
    /** Show Message */
    public int showMessage(String message, int messageType, Icon icon, Throwable exception, Object ... params) {
        return showMessage(message, messageType, icon, exception, params, (String[])null);
    }
    
    /** Show Message */
    public int showMessage(String message, int messageType, Icon icon, Throwable exception, Object[] params, String ... aButtons) {
        Object[] buttons = aButtons;
        
        String $message = exception instanceof MessageException
        ? ((MessageException)exception).getMessage(getLocale())
        : message
        ;
        if (params!=null) {
            $message = MessageFormat.format($message, params);
        }
        
        if (exception!=null) {
            Level level
            = messageType==JOptionPane.ERROR_MESSAGE   ? Level.SEVERE
            : messageType==JOptionPane.WARNING_MESSAGE ? Level.WARNING
            : Level.INFO
            ;
            LOGGER.log(level, $message, exception);
        }
        
        String $title = null;
        if ($title == null) {
            $title
            = messageType==JOptionPane.ERROR_MESSAGE    ? "Error"
            : messageType==JOptionPane.WARNING_MESSAGE  ? "Warning"
            : messageType==JOptionPane.QUESTION_MESSAGE ? "Question"
            : "Information"
            ;
        }
        
        if (buttons==null) {
            JButton ok = ApplTools.createCloseButton("OK");
            
            buttons = exception!=null
            ? new Object[] {ok, "More informations"}
            : new Object[] {ok }
            ;
        }
        
        int result = JOptionPane.showOptionDialog
        ( this
        , $message
        , $title
        , JOptionPane.OK_OPTION
        , messageType
        , icon
        , buttons
        , buttons[0]
        );
        
        if (result==1 && exception!=null) {
            // MoreInformations:
            $message = ApplTools.getStackTraceBuf(exception).toString();
            JOptionPane.showMessageDialog(this, $message, "Stack Trace", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return result;
    }
    
    /** Open an URL by a (system) browser. */
    public void browse(String url) {
        String browser = null;
        URI uri = null;
        
        try {
            try {
                uri = new URI(url);
                browser = Parameters.P_SYSTEM_BROWSER_PATH.of(applContext.getParameters());
                if (Parameters.P_SYSTEM_BROWSER_PATH.getDefault().equals(browser)) {
                    browser = "";
                }
                ApplTools.browse(uri, browser);
            } catch (IOException e) {
                browser = new BrowserDialog(applContext, browser).getResult();
                if (ApplTools.isValid(browser)) {
                    Parameters.P_SYSTEM_BROWSER_PATH.setValue(applContext.getParameters(), browser);
                    ApplTools.browse(uri, browser);
                } else {
                    throw e;
                }
            }
        } catch (Throwable e) {
            String msg = "<html>Can't open link: " + url
            + "<br>Try to modify a parameter: " + Parameters.P_SYSTEM_BROWSER_PATH + "</html>"
            ;
            showMessage(msg, e);
        }
        
    }
    
}
