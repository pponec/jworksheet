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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.resources.ResourceProvider;

/**
 * Browser Dialog
 * @author  Pavel Ponec
 */
public class BrowserDialog extends TopDialog {
    
    public static final Logger LOGGER = Logger.getLogger(BrowserDialog.class.getName());
    
    /** A select daty */
    private String result = null;
    
    /**
     * Creates new form ReportDialog
     */
    public BrowserDialog(ApplContext aContext, String initValue) {
        super(aContext);
        setLocale(aContext.getParameters().getLanguage());
        
        initComponents();
        tBrowser.setText(initValue);
        
        setTitle("Select an Internet Browser");
        setSize(450, 130);
        getRootPane().setDefaultButton(bOK);
        
        if (!Parameters.P_HIDE_ICONS.of(applContext.getParameters())) {
            ResourceProvider rp = new ResourceProvider();
            bOK.setIcon    (rp.getIcon(ResourceProvider.IMG_OK));
            bCancel.setIcon(rp.getIcon(ResourceProvider.IMG_CANCEL));
        }
        
    }
    
    /** Return result */
    public String getResult() {
        setVisible(true);
        return result;
    }
    
    final public void escapeAction(ActionEvent e) {
        bCancelActionPerformed(e);
    }
    
    /** V�b�r souboru */
    public File selectFile(String title, File selected) {
        
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        
        if (selected!=null) {
            fc.setCurrentDirectory(selected);
        }
        
        int returnVal = fc.showOpenDialog(this);
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        currentDayGroup = new javax.swing.ButtonGroup();
        reportTypeGroup = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        lBrowser = new javax.swing.JLabel();
        pButtons = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        tBrowser = new javax.swing.JTextField();
        bBrowser = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lBrowser.setText("Browser file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(lBrowser, gridBagConstraints);

        pButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        bOK.setMnemonic('O');
        bOK.setText("OK");
        bOK.addActionListener(formListener);

        pButtons.add(bOK);

        bCancel.setMnemonic('C');
        bCancel.setText("Cancel");
        bCancel.addActionListener(formListener);

        pButtons.add(bCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(pButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(tBrowser, gridBagConstraints);

        bBrowser.setText("...");
        bBrowser.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(bBrowser, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        getContentPane().add(jPanel2, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == bOK) {
                BrowserDialog.this.bOKActionPerformed(evt);
            }
            else if (evt.getSource() == bCancel) {
                BrowserDialog.this.bCancelActionPerformed(evt);
            }
            else if (evt.getSource() == bBrowser) {
                BrowserDialog.this.bBrowserActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents
    
    private void bBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBrowserActionPerformed
        
        String value = bBrowser.getText().trim();
        File result = ApplTools.isValid(value) ? new File(value) : null ;
        
        result = selectFile(getTitle(), result);
        if (result!=null) {
            tBrowser.setText(result.getAbsolutePath());
        }
    }//GEN-LAST:event_bBrowserActionPerformed
    
    private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
        
        result = (String) tBrowser.getText();
        bCancelActionPerformed(evt);
        
        
    }//GEN-LAST:event_bOKActionPerformed
    
    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_bCancelActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowser;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.ButtonGroup currentDayGroup;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lBrowser;
    private javax.swing.JPanel pButtons;
    private javax.swing.ButtonGroup reportTypeGroup;
    private javax.swing.JTextField tBrowser;
    // End of variables declaration//GEN-END:variables
    
}
