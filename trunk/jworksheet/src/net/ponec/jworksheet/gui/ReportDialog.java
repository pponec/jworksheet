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
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.core.Calculator;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.core.LanguageManager;
import net.ponec.jworksheet.report.MetaReport;
import net.ponec.jworksheet.report.ReportA;
import net.ponec.jworksheet.report.ReportB;
import net.ponec.jworksheet.report.ReportC;
import net.ponec.jworksheet.report.ReportTab;
import net.ponec.jworksheet.resources.ResourceProvider;
import org.ujoframework.UjoProperty;
import org.ujoframework.core.UjoActionImpl;

/**
 * Report Dialog
 * @author  Pavel Ponec
 */
public class ReportDialog extends TopDialog implements java.awt.event.ActionListener, javax.swing.event.ChangeListener, java.awt.event.MouseListener {
    
    public static final Logger LOGGER = Logger.getLogger(ReportDialog.class.getName());
    
    /** A spinner format */
    private String spinnerFormat;
    
    /** General selected date. */
    private long originalDay;
    
    /** An result of report: */
    private Boolean result = false;
    
    /** Enable a synchronization of Date. */
    private boolean dateSynchroEnabled = true;
    
    /** Are workdays sorted? */
    private boolean sorted = false;
    
    /**
     * Creates new form ReportDialog
     */
    public ReportDialog(java.awt.Frame parent, ApplContext aContext) {
        super(aContext);
        originalDay = aContext.getSelectedDay().getCalendar().getTimeInMillis();
        
        setLocale(aContext.getParameters().getLanguage());
        spinnerFormat = Parameters.P_DATE_REPORT_FORMAT.of(applContext.getParameters());
        initComponents();
        selectDayAction(new java.awt.event.ActionEvent(rbDay, -1, ""));
        
        setTitle("Report parameters");
        //setSize(410, 500);
        getRootPane().setDefaultButton(bOK);
        
        currentDayGroup.setSelected(rbDay.getModel(), true);
        
        if (!spinnerFormat.startsWith("EEEE")) {
            // If format starts EEEE:
            ApplTools.setAlign(tDateFrom, JFormattedTextField.LEFT);
            ApplTools.setAlign(tDateTo  , JFormattedTextField.LEFT);
        }
        
        if (!Parameters.P_HIDE_ICONS.of(applContext.getParameters())) {
            ResourceProvider rp = new ResourceProvider();
            bOK.setIcon    (rp.getIcon(ResourceProvider.IMG_OK));
            bReport.setIcon(rp.getIcon(ResourceProvider.IMG_EMPTY));
            bCancel.setIcon(rp.getIcon(ResourceProvider.IMG_CANCEL));
        }
        
        reportList.setModel(createReportModel());
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportList.setSelectedIndex(0);


        LanguageManager languageManager = applContext.getLanguageManager();
        languageManager.setFirstRunTexts(this);
    }
    
    /** Create a List of Reports. */
    private ListModel createReportModel() {
        DefaultListModel model = new DefaultListModel();
        model.addElement(new MetaReport( ReportTab.NAME           , ReportTab.class));
        model.addElement(new MetaReport("Project and Task summary", ReportA.class));
        model.addElement(new MetaReport("Project summary"         , ReportB.class));
        model.addElement(new MetaReport("Detail day reports"      , ReportC.class));
        
        for (MetaReport report : applContext.getMetaReports()) {
            model.addElement(report);
        }
        
        return model;
    }
    
    /** Create a date spinner */
    private JSpinner createDateSpinner() {
        return ApplTools.createSpinnerDate(spinnerFormat, getLocale());
    }
    
    /** Returns a YearMonthDay from a JSpinner component. */
    private YearMonthDay getYearMonthDay(JSpinner spinner) {
        YearMonthDay result = new YearMonthDay();
        Date date = (Date) spinner.getValue();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        result.setYearMonthDay(cal);
        return result;
    }
    
    public Boolean getResult() {
        return result;
    }
    
    @Override
    final public void escapeAction(ActionEvent e) {
        bCancelActionPerformed(e);
    }
    
    /** Returns XSL Parameters: */
    private ArrayList<String[]> getXslParams(MetaReport metaReport) {
        ArrayList<String[]> result = new ArrayList<String[]>(16);
        //
        result.add(new String[] {"DateFrom", getYearMonthDay(tDateFrom).toString()});
        result.add(new String[] {"DateTo"  , getYearMonthDay(tDateTo  ).toString()});
        result.add(new String[] {"BaseUrl" , applContext.getConfigDir().toURI().toString()});
        result.add(new String[] {"JWSName"    , JWorkSheet.APPL_NAME});
        result.add(new String[] {"JWSHomePage", JWorkSheet.APPL_HOMEPAGE});
        result.add(new String[] {"Title"      , metaReport.getTitle()});
        result.add(getXslParamItem(Parameters.P_WORKING_HOURS));
        result.add(getXslParamItem(Parameters.P_COLOR_PRIVATE));
        result.add(getXslParamItem(Parameters.P_COLOR_FINISHED_PROJ));
        result.add(getXslParamItem(Parameters.P_REPORT_CSS));
        
        return result;
    }
    
    /** Returns XSL Parameters: */
    private String[] getXslParamItem(UjoProperty param) {
        final String[] result = new String[]
        { param.getName()
          , applContext.getParameters().readValueString(param, new UjoActionImpl(this))
        } ;
        return result;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        currentDayGroup = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        lDateFrom = new javax.swing.JLabel();
        lDateTo = new javax.swing.JLabel();
        tDateFrom = createDateSpinner();
        tDateTo = createDateSpinner();
        separator1 = new javax.swing.JSeparator();
        lQuickSelection = new javax.swing.JLabel();
        separator2 = new javax.swing.JSeparator();
        separator3 = new javax.swing.JSeparator();
        pButtons = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bReport = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        lReportType = new javax.swing.JLabel();
        bTodayFrom = new javax.swing.JButton();
        bTodayTo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        rbDay = new javax.swing.JRadioButton();
        rbDayPrev = new javax.swing.JRadioButton();
        rbWeek = new javax.swing.JRadioButton();
        rbWeekPrev = new javax.swing.JRadioButton();
        rbMonth = new javax.swing.JRadioButton();
        rbMonthPrev = new javax.swing.JRadioButton();
        rbYear = new javax.swing.JRadioButton();
        rbYearPrev = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        reportList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        lDateFrom.setText("Date from:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanel2.add(lDateFrom, gridBagConstraints);

        lDateTo.setText("Date to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanel2.add(lDateTo, gridBagConstraints);

        tDateFrom.setFont(new java.awt.Font("Lucida Sans", 0, 11));
        tDateFrom.setPreferredSize(new java.awt.Dimension(220, 18));
        tDateFrom.addChangeListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(tDateFrom, gridBagConstraints);

        tDateTo.setFont(new java.awt.Font("Lucida Sans", 0, 11));
        tDateTo.setPreferredSize(new java.awt.Dimension(220, 18));
        tDateTo.addChangeListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(tDateTo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel2.add(separator1, gridBagConstraints);

        lQuickSelection.setText("<html>Quick<br>date selection:</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanel2.add(lQuickSelection, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel2.add(separator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel2.add(separator3, gridBagConstraints);

        pButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        bOK.setMnemonic('O');
        bOK.setText("OK");
        bOK.addActionListener(this);
        pButtons.add(bOK);

        bReport.setMnemonic('R');
        bReport.setText("DReport");
        bReport.setToolTipText("Open report but don't close this dialog");
        bReport.addActionListener(this);
        pButtons.add(bReport);

        bCancel.setMnemonic('C');
        bCancel.setText("Cancel");
        bCancel.addActionListener(this);
        pButtons.add(bCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        jPanel2.add(pButtons, gridBagConstraints);

        lReportType.setDisplayedMnemonic('R');
        lReportType.setLabelFor(reportList);
        lReportType.setText("Report:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanel2.add(lReportType, gridBagConstraints);

        bTodayFrom.setText("GoToday");
        bTodayFrom.setMargin(new java.awt.Insets(1, 14, 1, 14));
        bTodayFrom.setMaximumSize(new java.awt.Dimension(100, 21));
        bTodayFrom.setPreferredSize(new java.awt.Dimension(73, 19));
        bTodayFrom.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(bTodayFrom, gridBagConstraints);

        bTodayTo.setText("GoToday");
        bTodayTo.setMargin(new java.awt.Insets(1, 14, 1, 14));
        bTodayTo.setMaximumSize(new java.awt.Dimension(100, 21));
        bTodayTo.setPreferredSize(new java.awt.Dimension(73, 19));
        bTodayTo.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(bTodayTo, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        currentDayGroup.add(rbDay);
        rbDay.setText("Selected day");
        rbDay.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbDay.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbDay.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(rbDay, gridBagConstraints);

        currentDayGroup.add(rbDayPrev);
        rbDayPrev.setText("Previous day");
        rbDayPrev.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbDayPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbDayPrev.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(rbDayPrev, gridBagConstraints);

        currentDayGroup.add(rbWeek);
        rbWeek.setText("Selected week");
        rbWeek.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbWeek.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbWeek.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbWeek, gridBagConstraints);

        currentDayGroup.add(rbWeekPrev);
        rbWeekPrev.setText("Previous week");
        rbWeekPrev.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbWeekPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbWeekPrev.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbWeekPrev, gridBagConstraints);

        currentDayGroup.add(rbMonth);
        rbMonth.setText("Selected month");
        rbMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonth.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonth.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbMonth, gridBagConstraints);

        currentDayGroup.add(rbMonthPrev);
        rbMonthPrev.setText("Previous month");
        rbMonthPrev.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonthPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonthPrev.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbMonthPrev, gridBagConstraints);

        currentDayGroup.add(rbYear);
        rbYear.setText("Selected year");
        rbYear.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbYear.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbYear.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbYear, gridBagConstraints);

        currentDayGroup.add(rbYearPrev);
        rbYearPrev.setText("Previous year");
        rbYearPrev.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbYearPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbYearPrev.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(rbYearPrev, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jPanel1, gridBagConstraints);

        reportList.addMouseListener(this);
        jScrollPane1.setViewportView(reportList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        getContentPane().add(jPanel2, gridBagConstraints);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-563)/2, (screenSize.height-404)/2, 563, 404);
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == bOK) {
            ReportDialog.this.bOKActionPerformed(evt);
        }
        else if (evt.getSource() == bReport) {
            ReportDialog.this.bOKActionPerformed(evt);
        }
        else if (evt.getSource() == bCancel) {
            ReportDialog.this.bCancelActionPerformed(evt);
        }
        else if (evt.getSource() == bTodayFrom) {
            ReportDialog.this.bTodayFromActionPerformed(evt);
        }
        else if (evt.getSource() == bTodayTo) {
            ReportDialog.this.bTodayToActionPerformed(evt);
        }
        else if (evt.getSource() == rbDay) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbDayPrev) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbWeek) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbWeekPrev) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbMonth) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbMonthPrev) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbYear) {
            ReportDialog.this.selectDayAction(evt);
        }
        else if (evt.getSource() == rbYearPrev) {
            ReportDialog.this.selectDayAction(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == reportList) {
            ReportDialog.this.reportListMouseClicked(evt);
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == tDateFrom) {
            ReportDialog.this.tDateFromStateChanged(evt);
        }
        else if (evt.getSource() == tDateTo) {
            ReportDialog.this.tDateFromStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents
    
    private void reportListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportListMouseClicked
        if (evt.getButton()==MouseEvent.BUTTON1
        &&  evt.getClickCount()==2
        &&  reportList.getSelectedIndex()>=0
        ){
            bOKActionPerformed(null);
        }
    }//GEN-LAST:event_reportListMouseClicked
    
    private void tDateFromStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tDateFromStateChanged
        
        if (dateSynchroEnabled && isVisible()) {
            JSpinner s1 = (JSpinner) evt.getSource();
            JSpinner s2  = s1==tDateFrom ? tDateTo : tDateFrom;
            Date d1 = (Date) s1.getValue();
            Date d2 = (Date) s2.getValue();
            int c = s1==tDateFrom ? d1.compareTo(d2) : d2.compareTo(d1) ;
            if (c>0) {
                dateSynchroEnabled = false;
                s2.setValue(d1);
                dateSynchroEnabled = true;
            }
        }
        
    }//GEN-LAST:event_tDateFromStateChanged
    
    private void bTodayToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTodayToActionPerformed
        tDateTo.setValue(new Date());
    }//GEN-LAST:event_bTodayToActionPerformed
    
    private void bTodayFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTodayFromActionPerformed
        tDateFrom.setValue(new Date());
        
    }//GEN-LAST:event_bTodayFromActionPerformed
    
    private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
        
        bOK.setEnabled(false);
        ApplTools.setCursorWait(true, applContext);
        
        MetaReport metaReport = (MetaReport) reportList.getSelectedValue();
        if (metaReport!=null) try {
            if (!sorted) {
                applContext.getWorkSpace().sortDays();
                sorted = true;
            }
            if (metaReport.getXSL()!=null) {
                result = null;
                applContext.showReport(null, metaReport.getXSL(), getXslParams(metaReport));
                
            } else {
                Calculator cal = (Calculator) metaReport.getTypeClass().newInstance();
                
                cal.init
                ( applContext
                , getYearMonthDay(tDateFrom)
                , getYearMonthDay(tDateTo)
                , metaReport.getTitle()
                );
                String report = cal.print();
                
                applContext.showReport(report);
                result = null;
            }
            
        } catch (Throwable e) {
            applContext.getTopFrame().showMessage("Report failed", e);
        }
        
        bOK.setEnabled(true);
        ApplTools.setCursorWait(false, applContext);
        //bCancelActionPerformed(evt);
        
        if (evt==null
        ||  evt.getSource()==bOK
        /* Parameters.P_CLOSE_REPORT_DIALOG.of(applContext.getParameters())*/
        ){
            bCancelActionPerformed(null);
        }
        
        
    }//GEN-LAST:event_bOKActionPerformed
    
    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_bCancelActionPerformed
    
    private void selectDayAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDayAction
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(originalDay);
        c2.setTimeInMillis(originalDay);
        
        int firstDay = Parameters.P_FIRST_DAY_OF_WEEK.of(applContext.getParameters());
        Object sourceComp = evt.getSource();
        
        if (rbDayPrev==sourceComp) {
            c1.add(Calendar.DAY_OF_YEAR, -1);
        } else if (rbWeekPrev==sourceComp) {
            c1.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (rbMonthPrev==sourceComp) {
            c1.add(Calendar.MONTH, -1);
        } else if (rbYearPrev==sourceComp) {
            c1.add(Calendar.YEAR, -1);
        }
        
        // -------
        
        if (sourceComp==rbDayPrev) {
            c2.setTimeInMillis(c1.getTimeInMillis());
        } else if (sourceComp==rbWeek
        ||         sourceComp==rbWeekPrev
        ){
            int currDay = c2.get(Calendar.DAY_OF_WEEK);
            c1.add(Calendar.DAY_OF_YEAR, firstDay-currDay);
            c2.setTimeInMillis(c1.getTimeInMillis());
            c2.add(Calendar.DAY_OF_YEAR, 6);
        } else if (sourceComp==rbMonth
        ||         sourceComp==rbMonthPrev
        ){
            int currDay = c2.get(Calendar.DAY_OF_MONTH);
            c1.add(Calendar.DAY_OF_YEAR, 1-currDay);
            c2.setTimeInMillis(c1.getTimeInMillis());
            c2.add(Calendar.MONTH, 1);
            c2.add(Calendar.DAY_OF_YEAR, -1);
        } else if (sourceComp==rbYear
        ||         sourceComp==rbYearPrev
        ){
            int currDay  = c2.get(Calendar.DAY_OF_YEAR);
            c1.add(Calendar.DAY_OF_YEAR, 1-currDay);
            c2.setTimeInMillis(c1.getTimeInMillis());
            c2.add(Calendar.YEAR, 1);
            c2.add(Calendar.DAY_OF_YEAR, -1);
        }
        
        dateSynchroEnabled = false;
        tDateFrom.setValue(c1.getTime());
        tDateTo.setValue  (c2.getTime());
        dateSynchroEnabled = true;
        
    }//GEN-LAST:event_selectDayAction
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.JButton bReport;
    private javax.swing.JButton bTodayFrom;
    private javax.swing.JButton bTodayTo;
    private javax.swing.ButtonGroup currentDayGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lDateFrom;
    private javax.swing.JLabel lDateTo;
    private javax.swing.JLabel lQuickSelection;
    private javax.swing.JLabel lReportType;
    private javax.swing.JPanel pButtons;
    private javax.swing.JRadioButton rbDay;
    private javax.swing.JRadioButton rbDayPrev;
    private javax.swing.JRadioButton rbMonth;
    private javax.swing.JRadioButton rbMonthPrev;
    private javax.swing.JRadioButton rbWeek;
    private javax.swing.JRadioButton rbWeekPrev;
    private javax.swing.JRadioButton rbYear;
    private javax.swing.JRadioButton rbYearPrev;
    private javax.swing.JList reportList;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private javax.swing.JSpinner tDateFrom;
    private javax.swing.JSpinner tDateTo;
    // End of variables declaration//GEN-END:variables
    
}
