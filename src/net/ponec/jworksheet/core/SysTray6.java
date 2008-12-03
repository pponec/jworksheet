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

package net.ponec.jworksheet.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.gui.JWorkSheet;
import net.ponec.jworksheet.resources.ResourceProvider;
import static net.ponec.jworksheet.core.SysTray.Action.*;

/**
 * System Tray management for Java version 6.0 .
 * @author Pavel Ponec
 */
public class SysTray6 extends SysTray implements MouseListener, ActionListener {
    
    private ApplContext applContext;
    private JWorkSheet frame;
    private TrayIcon trayIcon;
    private Boolean systemTraySupport = null;
    
    
    private MenuItem miAbout = new MenuItem("About jWorkSheet");
    private MenuItem miOpen  = new MenuItem("Open");
    private MenuItem miEvent = new MenuItem("Create Event");
    private MenuItem miExit  = new MenuItem("Exit");
    
    /**
     * Creates a new instance of SysTrayTest_0
     */
    public SysTray6() {
    }
    
    @Override
    public void init(ApplContext applContext) {
        this.applContext = applContext;
    }
    
    @Override
    public boolean isSupported() {
        if (systemTraySupport==null) {
            frame = applContext.getTopFrame();
            initSysTray();
            applContext.setSystrayTooltip();
        }
        return systemTraySupport;
    }
    
    public void initSysTray() {
        systemTraySupport = SystemTray.isSupported();
        if (isSupported()) {
            
            PopupMenu popup = new PopupMenu();
            miAbout.addActionListener(this);
            miOpen .addActionListener(this);
            miEvent.addActionListener(this);
            miExit .addActionListener(this);
            
            popup.add(miAbout);
            popup.addSeparator();
            popup.add(miOpen);
            popup.add(miEvent);
            popup.addSeparator();
            popup.add(miExit);
            
            Image image = new ResourceProvider().getImage(ResourceProvider.LOGO_TRY);
            trayIcon = new TrayIcon(image, null, popup);
            
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(this);
            
            try {
                SystemTray tray = SystemTray.getSystemTray();
                tray.add(trayIcon);
            } catch (AWTException e) {
                systemTraySupport = false;
                System.err.println("TrayIcon could not be added.");
            }
            
        } else {
            System.out.println("System Tray is not supported");
        }
    }
    
    /** Open The Main Application Window  */
    private void openWindow() {
        
        frame.setVisibleLock(true);
        
        new Thread(new Runnable() {
            public void run() {
                ApplTools.sleep(80);
                frame.setExtendedState(Frame.NORMAL);
                frame.setVisibleLock(false);
            }
        }).start();
    }
    
    // ---------- ICON ACTION ---------------------------------
    
    /** A MouseListener implementation */
    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Tray Icon - Mouse clicked!");
    }
    
    /** A MouseListener implementation */
    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("Tray Icon - Mouse entered!");
    }
    
    /** A MouseListener implementation */
    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("Tray Icon - Mouse exited!");
    }
    
    /** A MouseListener implementation */
    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("Tray Icon - Mouse pressed!");
    }
    
    /** A MouseListener implementation */
    @Override
    public void mouseReleased(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                final boolean doubleClick = e.getClickCount()==2;
                Enum mode = Parameters.P_SYSTRAY_SECOND_CLICK.of(applContext.getParameters());
                if (EVENT==mode) {
                    if (doubleClick) {
                        frame.createWorkEvent(true);
                    }
                    openWindow();
                } else if (HIDE==mode && frame.isVisible()) {
                    frame.setState(Frame.ICONIFIED);
                } else {
                    openWindow();
                }                  
                break;
            case MouseEvent.BUTTON3:
                miOpen.setLabel(frame.isVisible() ? "Hide" : "Open");
                break;
            default:
        }
    }
    
    // -------------- MENU ACTION --------------
    
    /** An ActionListener implementation. */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source  = e.getSource();
        if (source==miExit) {
            try {
                applContext.closeAppl(true);
            } catch (Throwable ex) {
                String msg = ex.getMessage() + '\n' + ex.getClass().getName();
                showMessage(msg, null);
            }
            System.exit(0);
        } else if (source==miOpen) {
            if (frame.isVisible()) {
                frame.setVisible(false);
            } else {
                openWindow();
            }
            
        } else if (source==miEvent) {
            frame.createWorkEvent(true);
            openWindow();
        } else if (source==miAbout) {
            showMessage
            ( "Applicaton: jWorkSheet\nVersion: "+JWorkSheet.APPL_VERSION+"\nLicense: GNU/GPL"
            , TrayIcon.MessageType.INFO
            );
        }
    }
    
    /**
     * Show an Message:
     * @param msg Message
     * @param aType Message type of TrayIcon.MessageType. Default value is an ERROR.
     */
    private void showMessage(String msg, Object aType) {
        TrayIcon.MessageType type = (TrayIcon.MessageType) aType;
        
        if (type==null) {
            type = TrayIcon.MessageType.ERROR;
        }
        
        String title = TrayIcon.MessageType.ERROR.equals(type)
        ? "Error"
        : "Info"
        ;
        
        trayIcon.displayMessage
        ( title
        , msg
        , type
        );
    }
    
    /** Set a tooltip to main icon. */
    @Override
    public void setTooltip(String message) {
        if (trayIcon!=null) {
            trayIcon.setToolTip(ApplTools.isValid(message) ? message : JWorkSheet.APPL_NAME);
        }
    }
    
    
}
