/**
  * Copyright (C) 2007-9, Paul Ponec, contact: http://ponec.net/
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.core.LanguageManager;
import net.ponec.jworksheet.resources.ResourceProvider;

/**
 * Date Dialog
 * @author  Pavel Ponec
 */
public class DateDialog extends TopDialog implements java.awt.event.ActionListener, javax.swing.event.ChangeListener {
    
    private static final Logger LOGGER = Logger.getLogger(DateDialog.class.getName());
    
    /** A spinner format */
    final private String gotoFormat;
    
    /** A spinner format */
    final private String mainFormat;
    
    /** A select daty */
    private Date result = null;
    
    /**
     * Creates new form ReportDialog
     */
    public DateDialog(ApplContext aContext) {
        super(aContext);
        setLocale(aContext.getLanguage());
        
        mainFormat = applContext.getParameters().getDateFormat(Parameters.P_DATE_MAIN_FORMAT, aContext);
        gotoFormat = applContext.getParameters().getDateFormat(Parameters.P_DATE_GOTO_FORMAT, aContext);
        
        initComponents();
        tDate.setValue(applContext.getSelectedDay().getTime());
        
        setTitle("Go to the day");
        setSize(300, 130);
        getRootPane().setDefaultButton(bOK);
        
        
        if (gotoFormat.startsWith("EEEE")) {
            // If format starts EEEE:
            ApplTools.setAlign(tDate, JFormattedTextField.RIGHT);
        }
        
        if (!applContext.getParameters().get(Parameters.P_HIDE_ICONS)) {
            ResourceProvider rp = new ResourceProvider();
            bOK.setIcon    (rp.getIcon(ResourceProvider.IMG_OK));
            bCancel.setIcon(rp.getIcon(ResourceProvider.IMG_CANCEL));
            bToday.setIcon(rp.getIcon(ResourceProvider.IMG_EMPTY));
            bToday.setIconTextGap(0);
        }

        LanguageManager languageManager = applContext.getLanguageManager();
        languageManager.setFirstRunTexts(this);

    }
    
    /** Create a date spinner */
    private JSpinner createDateSpinner() {
        return ApplTools.createSpinnerDate(gotoFormat, getLocale());
    }
    
    /** Return result */
    public Date getResult() {
        setVisible(true);
        return result;
    }

    @Override
    final public void escapeAction(ActionEvent e) {
        bCancelActionPerformed(e);
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
        lDate = new javax.swing.JLabel();
        tDate = createDateSpinner();
        pButtons = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        bToday = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lDate.setText("Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(lDate, gridBagConstraints);

        tDate.setFont(new java.awt.Font("Lucida Sans", 0, 11));
        tDate.setPreferredSize(new java.awt.Dimension(160, 18));
        tDate.addChangeListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(tDate, gridBagConstraints);

        pButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        bOK.setMnemonic('O');
        bOK.setText("OK");
        bOK.addActionListener(this);

        pButtons.add(bOK);

        bCancel.setMnemonic('C');
        bCancel.setText("Cancel");
        bCancel.addActionListener(this);

        pButtons.add(bCancel);

        bToday.setMnemonic('T');
        bToday.setText("Today");
        bToday.addActionListener(this);

        pButtons.add(bToday);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(pButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        getContentPane().add(jPanel2, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == bOK) {
            DateDialog.this.bOKActionPerformed(evt);
        }
        else if (evt.getSource() == bCancel) {
            DateDialog.this.bCancelActionPerformed(evt);
        }
        else if (evt.getSource() == bToday) {
            DateDialog.this.bTodayActionPerformed(evt);
        }
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == tDate) {
            DateDialog.this.tDateStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents
    
    private void tDateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tDateStateChanged
        Date date = (Date) tDate.getValue();
        SimpleDateFormat df = new SimpleDateFormat(mainFormat, applContext.getLanguage());
        setTitle(df.format(date));
    }//GEN-LAST:event_tDateStateChanged
    
    private void bTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTodayActionPerformed
        tDate.setValue(new Date());
    }//GEN-LAST:event_bTodayActionPerformed
    
    private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
        result = (Date) tDate.getValue();
        bCancelActionPerformed(evt);
        
        
    }//GEN-LAST:event_bOKActionPerformed
    
    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_bCancelActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.JButton bToday;
    private javax.swing.ButtonGroup currentDayGroup;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lDate;
    private javax.swing.JPanel pButtons;
    private javax.swing.ButtonGroup reportTypeGroup;
    private javax.swing.JSpinner tDate;
    // End of variables declaration//GEN-END:variables
    
}
