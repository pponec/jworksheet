/**
 * 
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.ponec.jworksheet.core.ApplTools;
import net.ponec.jworksheet.bo.Parameters;
import net.ponec.jworksheet.bo.Event;
import net.ponec.jworksheet.bo.Project;
import net.ponec.jworksheet.bo.TaskType;
import net.ponec.jworksheet.bo.WorkDay;
import net.ponec.jworksheet.bo.WorkSpace;
import net.ponec.jworksheet.bo.item.Time;
import net.ponec.jworksheet.bo.item.YearMonthDay;
import net.ponec.jworksheet.core.ApplContext;
import net.ponec.jworksheet.gui.component.CheckBoxRenderer;
import net.ponec.jworksheet.gui.models.EventTableModel;
import net.ponec.jworksheet.gui.models.EventTableRenderer;
import net.ponec.jworksheet.gui.component.UjoTable;
import net.ponec.jworksheet.gui.models.CommonTableRenderer;
import net.ponec.jworksheet.gui.models.ParamTableModel;
import net.ponec.jworksheet.gui.models.ProjectTableModel;
import net.ponec.jworksheet.gui.models.TaskCellEditor;
import net.ponec.jworksheet.gui.models.TaskTableModel;
import net.ponec.jworksheet.gui.models.UjoComboBoxModel;
import net.ponec.jworksheet.report.TableReport;
import net.ponec.jworksheet.resources.ResourceProvider;
import org.ujoframework.Ujo;
import org.ujoframework.UjoProperty;
import org.ujoframework.swing.UjoTableModel;
import org.ujoframework.core.ZeroProvider;
import org.ujoframework.swing.UjoPropertyRow;

/**
 * Java Work Sheet
 * @author Pavel Ponec
 */
@SuppressWarnings("unchecked")
public final class JWorkSheet extends TopFrame {
    
    public static final String APPL_VERSION  = "0.85" ;
    public static final String APPL_RELEASED = "2009/03/15";
    
    public static final String APPL_NAME     = "jWorkSheet";
    public static final String APPL_HOMEPAGE = "http://jworksheet.ponec.net/";
    
    private final UjoTable eventTable;
    private final UjoTable projTable;
    private final UjoTable taskTable;
    private final UjoTable paramTable;
    private final UjoTable[] tables;
    
    private boolean visibleLock = false;
    
    private final Color COLOR_OFF  = new Color(0xDC5555);
    private final Color COLOR_WORK = new Color(0x405881);
    
    
    /**
     * Creates new form JWorkSheet
     */
    public JWorkSheet(final ApplContext anApplContext, Throwable error) {
        super(anApplContext);
        
        eventTable = new UjoTable();
        projTable  = new UjoTable();
        taskTable  = new UjoTable();
        paramTable = new UjoTable();
        tables     = new UjoTable[] {eventTable, projTable, taskTable, paramTable};
        
          try {
            if (error!=null) { throw error; }

            final boolean SHOW_LANGUAGE_WINDOW = false;
            applContext.setTopFrame(this, SHOW_LANGUAGE_WINDOW);
            initComponents();

            projTable.enableSorting(applContext);
            taskTable.enableSorting(applContext);
            
            paramTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            projTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            projTable.getModel().addTableModelListener(applContext);
            //projTable.setRenderer(new CommonTableRenderer(applContext));
            //
            paramTable.setRenderer(new CommonTableRenderer(applContext));
            paramTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    String descr = "";
                    int selectedRow = paramTable.getSelectedRow();
                    if (isVisible() && selectedRow>=0) {
                        final String key 
                            = "param."
                            + paramTable.getValueAt
                            ( selectedRow
                            , paramTable.convertColumnIndexToModel(1)
                            ).toString()
                            ;
                        descr = applContext.getLanguageManager().getTextAllways(key);
                    }
                    paramDescr.setText(descr);
                    paramDescr.select(0,0);
                    
                }
            });
            //
            taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            taskTable.getModel().addTableModelListener(applContext);
            //taskTable.setRenderer(new CommonTableRenderer(applContext));
            //
            eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            eventTable.setRenderer(new EventTableRenderer(applContext));
            eventTable.getModel().addTableModelListener(applContext);
            eventTable.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    // Display TOTAL TIME:
                    final EventTableModel model = (EventTableModel) eventTable.getModel();
                    UjoProperty column = e.getColumn()>=0 ? model.getColumn(e.getColumn()) : null ;
                    if (column==null
                    ||  column==Event.P_PERIOD
                    ||  column==Event.P_TIME ){
                        displayTotalTime();
                    }
                    if (e.getLastRow()>=model.getRowCount()-1) {
                        applContext.setSystrayTooltip();
                    }
                }
            });
            
            initHotKeys();
            
            // ======= LOAD DATA =========
            initData();
            initIcons();
            
            // ComboBox Editor:
            setTableEventEditor();
            eventTable.getTableColumn(Event.P_TASK).setCellEditor( new TaskCellEditor() );
            
            eventTable.getTableColumn(Event.P_TIME  ).setMaxWidth(48);
            eventTable.getTableColumn(Event.P_PERIOD).setMaxWidth(48);
            eventTable.getTableColumn(Event.P_PROJ  ).setPreferredWidth(10);
            eventTable.getTableColumn(Event.P_TASK  ).setPreferredWidth(10);
            projTable .getTableColumn(Project.P_ID  ).setMaxWidth(48);
            projTable .getTableColumn(Project.P_PRIVATE).setMaxWidth(58);
            projTable .getTableColumn(Project.P_DEFAULT).setMaxWidth(58);
            projTable .getTableColumn(Project.P_FINISHED ).setMaxWidth(58);
            projTable .showSortedColumn(Parameters.P_SORT_PROJ_COLUMN.of(applContext.getParameters()));
            taskTable .getTableColumn(TaskType.P_ID    ).setMaxWidth(48);
            taskTable .getTableColumn(TaskType.P_DEFAULT).setMaxWidth(58);
            taskTable .getTableColumn(TaskType.P_FINISHED).setMaxWidth(58);
            paramTable.setModel(new ParamTableModel(applContext.getParameters(), applContext));
            paramTable.getTableColumn(UjoPropertyRow.P_DEFAULT).setMinWidth(0);
            paramTable.getTableColumn(UjoPropertyRow.P_DEFAULT).setMaxWidth(0);
            paramTable.getTableColumn(UjoPropertyRow.P_INDEX).setMaxWidth(25);
            paramTable.getTableColumn(UjoPropertyRow.P_NAME ).setPreferredWidth(100);
            paramTable.getTableColumn(UjoPropertyRow.P_VALUE).setPreferredWidth(200);
            
            
            if (ApplTools.isNimbusLAF()) {
                CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
                projTable.getTableColumn(Project.P_PRIVATE).setCellRenderer(checkBoxRenderer);
                projTable.getTableColumn(Project.P_DEFAULT).setCellRenderer(checkBoxRenderer);
                projTable.getTableColumn(Project.P_FINISHED).setCellRenderer(checkBoxRenderer);

                taskTable.getTableColumn(TaskType.P_DEFAULT).setCellRenderer(checkBoxRenderer);
                taskTable.getTableColumn(TaskType.P_FINISHED).setCellRenderer(checkBoxRenderer);
            }
            
            // A Period Editor:
            JTextField text = new JTextField(5);
            text.setHorizontalAlignment(JTextField.RIGHT);
            text.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
            eventTable.getTableColumn(Event.P_PERIOD).setCellEditor(new DefaultCellEditor(text));
            
            projTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    synchonizeTaskTable();
                }
            });
            
            displaySelectedDay();
            displayTotalTime();
            applContext.setInitialized();
            
            // Window close listener:
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // closeAppl(e); // See addShutdownHook
                }
                @Override
                public void windowIconified(WindowEvent e) {
                    if (isSystrayEnabled()) {
                        setVisible(false);
                    }
                    applContext.saveData(false);
                }
                @Override
                public void windowDeiconified(WindowEvent e) {
                    if (!applContext.isSystrayEnabled()
                    &&   applContext.isToday()) {
                        bSortActionPerformed(null);
                    }
                }
            });
            
            // Exit application:
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    closeAppl(null);
                }
            });
            
            // Window sizing:
            ApplTools.windowsSizing(this, Parameters.P_WINDOW_SIZE.of(applContext.getParameters()));
            
            applContext.tableChanged(null); // Save a startup event
            
            if (isSystrayEnabled()) {
                // setVisible(false);
            }
            
        } catch (Throwable e) {
            super.showMessage("Can't open " + APPL_NAME, e);
            System.exit(-1);
        }
    }
    
    /** Set a Project editor of Event Table. */
    private void setTableEventEditor() {
        UjoComboBoxModel m2 = new UjoComboBoxModel(applContext.getWorkSpace().getOpenProjects());
        eventTable.getTableColumn(Event.P_PROJ).setCellEditor( new DefaultCellEditor(new JComboBox(m2)) );
    }
    
    /** Load Data from XML file. */
    private void initData() {
        boolean lock = applContext.createLock(false);
        if (!lock) {
            String msg = "The application {0} is running probably. Do you want to continue?";
            String[] buttons = new String[] {"Exit", "Continue"};
            Icon icon = new ResourceProvider().getIcon(ResourceProvider.LOGO);
            int choice = showMessage(msg, JOptionPane.QUESTION_MESSAGE, icon, null, new Object[] {APPL_NAME}, buttons);
            if (choice==0) {
                setVisible(false);
                System.exit(0);
            }
            applContext.createLock(true);
        }
        
        applContext.loadData();

        if (applContext.isDataRestored()) {
            String msg = "There is the serious error:"
                + "\na personal data of the application can not be found!" 
                + "\nThe jWorkSheet tried to restore data from a backup."
                ;
            final String[] buttons = null;
            Icon icon = new ResourceProvider().getIcon(ResourceProvider.LOGO);
            showMessage(msg, JOptionPane.OK_OPTION, icon, null, new Object[] {APPL_NAME}, buttons);
        }
        
        setTableModels();
        setEventTableModel(true);
        
        // Selection:
        projTable.selectRow(0);
        synchonizeTaskTable();
    }
    
    /** A close window event. */
    public void closeAppl(WindowEvent e) {
        try {
            Parameters.P_SORT_PROJ_COLUMN.setValue(applContext.getParameters(), projTable.getSortedColumn().getName());
            
            applContext.closeAppl(false);
        } catch (Throwable ex) {
            showMessage("Can't save data.", ex);
        }
    }
    
    /** <ul>
     * <li>Value 0 = the current date.</li>
     * <li>Value MIN_VALUE = only refresh date.</li>
     * <li>Value X = Add day count.</li>
     *  </ul>
     */
    private void changeCurrentDate(int skip) {
        eventTable.submitEditMode(true);
        
        YearMonthDay day = applContext.getSelectedDay().cloneDay();
        if (skip==Integer.MIN_VALUE) {
            // none
        } else if (skip==0) {
            day.setToday();
        } else {
            day.addDay(skip);
        }
        applContext.selectWorkDay(day);
        displaySelectedDay();
        setEventTableModel(false);
    }
    
    /** Display selected day. */
    private void displaySelectedDay() {
        lCurrentDay.setText(applContext.getSelectedDayStr());
    }
    
    /**
     * Sent Events to TableModel.
     * @param today Set event to a current day.
     */
    private void setEventTableModel(boolean today) {
        UjoTableModel model = eventTable.getModel();
        WorkDay workDay = applContext.getWorkDay();
        model.setRows(WorkDay.P_EVENTS.getList(workDay));
        cbTimeOff.setSelected(WorkDay.P_DAYOFF.of(workDay));
        cbTimeOffActionPerformed(null);
        
        if (today && model.getRowCount()==0) {
            ((EventTableModel)eventTable.getModel()).insertRowAndDefault( new Event().initTime());
            // Save a startup event:
            applContext.saveData(false);
            
        }
    }
    
    /** Display a total time */
    private void displayTotalTime() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EventTableModel model = (EventTableModel) eventTable.getModel();
                tTimeTotal.setText(model.getTotalTimeHours());
            }
        });
    }
    
    private void setTableModels() {
        List<Project> list2 = WorkSpace.P_PROJS.getList(applContext.getWorkSpace());
        projTable.getModel().setRows(list2);
        //
//        List<TaskType> list3 = WorkSpace.P_TASKS.getList(applContext.getWorkSpace());
//        taskTable.getModel().setRows(list3);
    }
    
    /** Synchronize a task table by a project Table */
    private void synchonizeTaskTable() {
        int row = projTable.getSelectedRow();
        Project project = row>=0 ? (Project) projTable.getModel().getRowNullable(row) : null ;
        ArrayList<TaskType> list = project!=null ? Project.P_TASKS.getList(project) : new ArrayList<TaskType>();
        taskTable.getModel().setRows(list);
        taskTable.selectRow(0);
        taskTable.showSortedColumn(TaskType.P_ID);
    }
    
    /** Show an info dialog "Result to Clibpoard is finished" */
    private void showTableReport(UjoTable table, String title) {
        try {
            TableReport report = new TableReport(applContext);
            String content = report.printTable(table.getStringModelArray(true), title, true);
            applContext.showReport(content);
        } catch (Throwable e) {
            super.showMessage("Can't open report", e);
        }
    }
    
    /** Show or hide button icons by a persistent attribute: arameters.P_HIDE_ICONS. */
    public void initIcons() {
        ResourceProvider imageProvider = new ResourceProvider();
        setIconImage    (imageProvider.getImage(ResourceProvider.LOGO));
        
        Dimension d = new Dimension(80,23);
        bDayPrev.setPreferredSize(d);
        bDayPrev.setMinimumSize(d);
        bDayNext.setPreferredSize(d);
        bDayNext.setMinimumSize(d);
        //
        tabbedPane.setIconAt(0, imageProvider.getIcon(ResourceProvider.IMG_APPLICATION_LIST));
        tabbedPane.setIconAt(1, imageProvider.getIcon(ResourceProvider.IMG_APPLICATION_SPLIT));
        tabbedPane.setIconAt(2, imageProvider.getIcon(ResourceProvider.IMG_WRENCH));
        //
        boolean hideIcon = Parameters.P_HIDE_ICONS.of(applContext.getParameters());
        bDayPrev.setIcon(imageProvider.getIcon(ResourceProvider.IMG_PREV, hideIcon));
        bDayNext.setIcon(imageProvider.getIcon(ResourceProvider.IMG_NEXT, hideIcon));
        bGoto   .setIcon(imageProvider.getIcon(ResourceProvider.IMG_DATE, hideIcon));

        bSort.setVisible(!Parameters.P_AUTOMATIC_SORTING_BY_TIME.of(applContext.getParameters()));
        //setImage(bToday  , ResourceProvider.IMG_DATE, imageProvider);
        //
        setImage(bCreate, ResourceProvider.IMG_ADD, imageProvider);
        setImage(bClone , ResourceProvider.IMG_APPLICATION_DOUBLE, imageProvider);
        setImage(bDelete, ResourceProvider.IMG_DELETE, imageProvider);
        setImage(bSort  , ResourceProvider.IMG_ARROW_DOWN, imageProvider);
        setImage(bReport, ResourceProvider.IMG_REPORT, imageProvider);
        setImage(bAbout , ResourceProvider.IMG_INFORMATION, imageProvider);
        //
        setImage(bProjCreate, ResourceProvider.IMG_ADD, imageProvider);
        setImage(bProjClone , ResourceProvider.IMG_APPLICATION_DOUBLE, imageProvider);
        setImage(bProjDelete, ResourceProvider.IMG_DELETE, imageProvider);
        setImage(bProjSort  , ResourceProvider.IMG_ARROW_DOWN, imageProvider);
        setImage(bProjReport, ResourceProvider.IMG_REPORT, imageProvider);
        //
        setImage(bTaskCreate, ResourceProvider.IMG_ADD, imageProvider);
        setImage(bTaskClone , ResourceProvider.IMG_APPLICATION_DOUBLE, imageProvider);
        setImage(bTaskDelete, ResourceProvider.IMG_DELETE, imageProvider);
        setImage(bTaskSort  , ResourceProvider.IMG_ARROW_DOWN, imageProvider);
        setImage(bTaskReport, ResourceProvider.IMG_REPORT, imageProvider);
        //
        setImage(bParamReport, ResourceProvider.IMG_REPORT, imageProvider);
        setImage(bParamDefault, ResourceProvider.IMG_ARROW_UNDO, imageProvider);
        setImage(bHomePage, ResourceProvider.IMG_HOME_PAGE, imageProvider);
    }
    
    /** Set Icon */
    private void setImage(JButton button, String resource, ResourceProvider imageProvider) {
        if (Parameters.P_HIDE_ICONS.of(applContext.getParameters())) {
            button.setIcon(null);
            button.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        } else {
            button.setIcon(imageProvider.getIcon(resource));
            button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        button.setIconTextGap(8);
    }
    
    /** Get System Systray */
    private boolean isSystrayEnabled() {
        return applContext.isSystrayEnabled();
    }
    
    /** Set new title */
    public void setTitle(String title) {
        String result = APPL_NAME;
        if (title.length()>0) {
            result += ": "  + title;
        }
        super.setTitle(result);
    }

    /** Get a main Tabbed pane */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        pWorkSheet = new javax.swing.JPanel();
        pDays = new javax.swing.JPanel();
        bDayPrev = new javax.swing.JButton();
        lCurrentDay = new javax.swing.JLabel();
        bDayNext = new javax.swing.JButton();
        bToday = new javax.swing.JButton();
        bGoto = new javax.swing.JButton();
        cbTimeOff = new javax.swing.JCheckBox();
        eventSrollPane = new javax.swing.JScrollPane();
        $eventTable = eventTable;
        pButtons = new javax.swing.JPanel();
        bCreate = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bClone = new javax.swing.JButton();
        bSort = new javax.swing.JButton();
        bReport = new javax.swing.JButton();
        bAbout = new javax.swing.JButton();
        pTotal = new javax.swing.JPanel();
        lTimeTotal = new javax.swing.JLabel();
        tTimeTotal = new javax.swing.JTextField();
        pProjects = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pProj = new javax.swing.JPanel();
        pProjButtons = new javax.swing.JPanel();
        bProjCreate = new javax.swing.JButton();
        bProjDelete = new javax.swing.JButton();
        bProjClone = new javax.swing.JButton();
        bProjSort = new javax.swing.JButton();
        bProjReport = new javax.swing.JButton();
        spProjects = new javax.swing.JScrollPane();
        $projTable = projTable;
        pTask = new javax.swing.JPanel();
        spTasks = new javax.swing.JScrollPane();
        $taskTable = taskTable;
        pTaskButtons = new javax.swing.JPanel();
        bTaskCreate = new javax.swing.JButton();
        bTaskDelete = new javax.swing.JButton();
        bTaskClone = new javax.swing.JButton();
        bTaskSort = new javax.swing.JButton();
        bTaskReport = new javax.swing.JButton();
        lTasks = new javax.swing.JLabel();
        pParameters = new javax.swing.JPanel();
        pParamButtons = new javax.swing.JPanel();
        bParamReport = new javax.swing.JButton();
        bParamDefault = new javax.swing.JButton();
        bHomePage = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        splitParam = new javax.swing.JSplitPane();
        spParam = new javax.swing.JScrollPane();
        $paramTable = paramTable;
        pParamDescr = new javax.swing.JPanel();
        spDescr = new javax.swing.JScrollPane();
        paramDescr = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener(formListener);

        pWorkSheet.setLayout(new java.awt.GridBagLayout());

        pDays.setLayout(new java.awt.GridBagLayout());

        bDayPrev.setText("Previous"); // NOI18N
        bDayPrev.setIconTextGap(0);
        bDayPrev.setMargin(new java.awt.Insets(2, 0, 2, 1));
        bDayPrev.setMaximumSize(new java.awt.Dimension(90, 23));
        bDayPrev.setMinimumSize(new java.awt.Dimension(82, 23));
        bDayPrev.setPreferredSize(new java.awt.Dimension(82, 23));
        bDayPrev.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        pDays.add(bDayPrev, gridBagConstraints);

        lCurrentDay.setFont(new java.awt.Font("Tahoma", 1, 12));
        lCurrentDay.setForeground(COLOR_WORK);
        lCurrentDay.setText("Thursday, 21.12.2006"); // NOI18N
        lCurrentDay.setName("~"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pDays.add(lCurrentDay, gridBagConstraints);

        bDayNext.setText("Next"); // NOI18N
        bDayNext.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        bDayNext.setIconTextGap(2);
        bDayNext.setMargin(new java.awt.Insets(2, 1, 2, 0));
        bDayNext.setMaximumSize(new java.awt.Dimension(90, 23));
        bDayNext.setMinimumSize(new java.awt.Dimension(82, 23));
        bDayNext.setPreferredSize(new java.awt.Dimension(82, 23));
        bDayNext.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pDays.add(bDayNext, gridBagConstraints);

        bToday.setText("Today"); // NOI18N
        bToday.setMargin(new java.awt.Insets(2, 0, 2, 0));
        bToday.setMaximumSize(new java.awt.Dimension(90, 23));
        bToday.setMinimumSize(new java.awt.Dimension(66, 23));
        bToday.setPreferredSize(new java.awt.Dimension(66, 23));
        bToday.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pDays.add(bToday, gridBagConstraints);

        bGoto.setText("Go to"); // NOI18N
        bGoto.setIconTextGap(3);
        bGoto.setMargin(new java.awt.Insets(2, 0, 2, 0));
        bGoto.setMaximumSize(new java.awt.Dimension(90, 23));
        bGoto.setMinimumSize(new java.awt.Dimension(80, 23));
        bGoto.setPreferredSize(new java.awt.Dimension(80, 23));
        bGoto.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        pDays.add(bGoto, gridBagConstraints);

        cbTimeOff.setText("Day Off"); // NOI18N
        cbTimeOff.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbTimeOff.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        pDays.add(cbTimeOff, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        pWorkSheet.add(pDays, gridBagConstraints);

        $eventTable.setModel(new net.ponec.jworksheet.gui.models.EventTableModel(applContext));
        eventSrollPane.setViewportView($eventTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pWorkSheet.add(eventSrollPane, gridBagConstraints);

        pButtons.setLayout(new java.awt.GridBagLayout());

        bCreate.setText("New event"); // NOI18N
        bCreate.setMaximumSize(new java.awt.Dimension(65, 65));
        bCreate.setMinimumSize(new java.awt.Dimension(63, 23));
        bCreate.setPreferredSize(new java.awt.Dimension(65, 55));
        bCreate.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pButtons.add(bCreate, gridBagConstraints);

        bDelete.setText("Delete"); // NOI18N
        bDelete.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pButtons.add(bDelete, gridBagConstraints);

        bClone.setText("Copy"); // NOI18N
        bClone.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pButtons.add(bClone, gridBagConstraints);

        bSort.setText("Sort event"); // NOI18N
        bSort.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pButtons.add(bSort, gridBagConstraints);

        bReport.setText("Report"); // NOI18N
        bReport.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pButtons.add(bReport, gridBagConstraints);

        bAbout.setText("About"); // NOI18N
        bAbout.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        pButtons.add(bAbout, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pWorkSheet.add(pButtons, gridBagConstraints);

        pTotal.setLayout(new java.awt.GridBagLayout());

        lTimeTotal.setFont(new java.awt.Font("Tahoma", 1, 12));
        lTimeTotal.setForeground(lCurrentDay.getForeground());
        lTimeTotal.setText("Period Total:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pTotal.add(lTimeTotal, gridBagConstraints);

        tTimeTotal.setEditable(false);
        tTimeTotal.setMinimumSize(new java.awt.Dimension(110, 22));
        tTimeTotal.setPreferredSize(new java.awt.Dimension(100, 22));
        tTimeTotal.addMouseListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pTotal.add(tTimeTotal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        pWorkSheet.add(pTotal, gridBagConstraints);

        tabbedPane.addTab("Events ", pWorkSheet);

        pProjects.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setOneTouchExpandable(true);

        pProj.setLayout(new java.awt.GridBagLayout());

        pProjButtons.setLayout(new java.awt.GridBagLayout());

        bProjCreate.setText("New"); // NOI18N
        bProjCreate.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pProjButtons.add(bProjCreate, gridBagConstraints);

        bProjDelete.setText("Delete"); // NOI18N
        bProjDelete.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pProjButtons.add(bProjDelete, gridBagConstraints);

        bProjClone.setText("Copy"); // NOI18N
        bProjClone.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pProjButtons.add(bProjClone, gridBagConstraints);

        bProjSort.setText("Sort"); // NOI18N
        bProjSort.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pProjButtons.add(bProjSort, gridBagConstraints);

        bProjReport.setText("Report"); // NOI18N
        bProjReport.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pProjButtons.add(bProjReport, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pProj.add(pProjButtons, gridBagConstraints);

        $projTable.setModel(new ProjectTableModel(applContext));
        spProjects.setViewportView($projTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pProj.add(spProjects, gridBagConstraints);

        jSplitPane1.setLeftComponent(pProj);

        pTask.setLayout(new java.awt.GridBagLayout());

        $taskTable.setModel(new TaskTableModel(applContext));
        spTasks.setViewportView($taskTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pTask.add(spTasks, gridBagConstraints);

        pTaskButtons.setLayout(new java.awt.GridBagLayout());

        bTaskCreate.setText("New task"); // NOI18N
        bTaskCreate.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        pTaskButtons.add(bTaskCreate, gridBagConstraints);

        bTaskDelete.setText("Delete task"); // NOI18N
        bTaskDelete.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        pTaskButtons.add(bTaskDelete, gridBagConstraints);

        bTaskClone.setText("Copy task"); // NOI18N
        bTaskClone.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        pTaskButtons.add(bTaskClone, gridBagConstraints);

        bTaskSort.setText("Sort task"); // NOI18N
        bTaskSort.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        pTaskButtons.add(bTaskSort, gridBagConstraints);

        bTaskReport.setText("Report task"); // NOI18N
        bTaskReport.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pTaskButtons.add(bTaskReport, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pTask.add(pTaskButtons, gridBagConstraints);

        lTasks.setFont(new java.awt.Font("Tahoma", 1, 11));
        lTasks.setForeground(lCurrentDay.getForeground());
        lTasks.setText("These are tasks of the selected project:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        pTask.add(lTasks, gridBagConstraints);

        jSplitPane1.setRightComponent(pTask);

        pProjects.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Projects ", pProjects);

        pParameters.setLayout(new java.awt.GridBagLayout());

        pParamButtons.setLayout(new java.awt.GridBagLayout());

        bParamReport.setText("Report"); // NOI18N
        bParamReport.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pParamButtons.add(bParamReport, gridBagConstraints);

        bParamDefault.setText("Default"); // NOI18N
        bParamDefault.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pParamButtons.add(bParamDefault, gridBagConstraints);

        bHomePage.setText("<html>Home Page</html>"); // NOI18N
        bHomePage.setName(".Button.HomePage"); // NOI18N
        bHomePage.addActionListener(formListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pParamButtons.add(bHomePage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pParameters.add(pParamButtons, gridBagConstraints);

        jLabel1.setText("These are expert options:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 7, 5, 0);
        pParameters.add(jLabel1, gridBagConstraints);

        splitParam.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitParam.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitParam.setResizeWeight(1.0);
        splitParam.setOneTouchExpandable(true);

        spParam.setViewportView($paramTable);

        splitParam.setLeftComponent(spParam);

        pParamDescr.setMinimumSize(new java.awt.Dimension(100, 60));
        pParamDescr.setLayout(new java.awt.GridBagLayout());

        spDescr.setBorder(null);

        paramDescr.setBackground(getBackground());
        paramDescr.setBorder(null);
        paramDescr.setEditable(false);
        spDescr.setViewportView(paramDescr);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pParamDescr.add(spDescr, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setForeground(lCurrentDay.getForeground());
        jLabel2.setText("Parameter description:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 1, 0);
        pParamDescr.add(jLabel2, gridBagConstraints);

        splitParam.setRightComponent(pParamDescr);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pParameters.add(splitParam, gridBagConstraints);

        tabbedPane.addTab("Parameters ", pParameters);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.ChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == bDayPrev) {
                JWorkSheet.this.bDayNextActionPerformed(evt);
            }
            else if (evt.getSource() == bDayNext) {
                JWorkSheet.this.bDayNextActionPerformed(evt);
            }
            else if (evt.getSource() == bToday) {
                JWorkSheet.this.bTodayActionPerformed(evt);
            }
            else if (evt.getSource() == bGoto) {
                JWorkSheet.this.bGotoActionPerformed(evt);
            }
            else if (evt.getSource() == cbTimeOff) {
                JWorkSheet.this.cbTimeOffActionPerformed(evt);
            }
            else if (evt.getSource() == bCreate) {
                JWorkSheet.this.bCreateActionPerformed(evt);
            }
            else if (evt.getSource() == bDelete) {
                JWorkSheet.this.bDeleteActionPerformed(evt);
            }
            else if (evt.getSource() == bClone) {
                JWorkSheet.this.bCloneActionPerformed(evt);
            }
            else if (evt.getSource() == bSort) {
                JWorkSheet.this.bSortActionPerformed(evt);
            }
            else if (evt.getSource() == bReport) {
                JWorkSheet.this.bReportActionPerformed(evt);
            }
            else if (evt.getSource() == bAbout) {
                JWorkSheet.this.bAboutActionPerformed(evt);
            }
            else if (evt.getSource() == bProjCreate) {
                JWorkSheet.this.bProjCreateActionPerformed(evt);
            }
            else if (evt.getSource() == bProjDelete) {
                JWorkSheet.this.bProjDeleteActionPerformed(evt);
            }
            else if (evt.getSource() == bProjClone) {
                JWorkSheet.this.bProjCloneActionPerformed(evt);
            }
            else if (evt.getSource() == bProjSort) {
                JWorkSheet.this.bProjSortActionPerformed(evt);
            }
            else if (evt.getSource() == bProjReport) {
                JWorkSheet.this.bProjReportActionPerformed(evt);
            }
            else if (evt.getSource() == bTaskCreate) {
                JWorkSheet.this.bTaskCreateActionPerformed(evt);
            }
            else if (evt.getSource() == bTaskDelete) {
                JWorkSheet.this.bTaskDeleteActionPerformed(evt);
            }
            else if (evt.getSource() == bTaskClone) {
                JWorkSheet.this.bTaskCloneActionPerformed(evt);
            }
            else if (evt.getSource() == bTaskSort) {
                JWorkSheet.this.bTaskSortActionPerformed(evt);
            }
            else if (evt.getSource() == bTaskReport) {
                JWorkSheet.this.bTaskReportActionPerformed(evt);
            }
            else if (evt.getSource() == bParamReport) {
                JWorkSheet.this.bParamReportActionPerformed(evt);
            }
            else if (evt.getSource() == bParamDefault) {
                JWorkSheet.this.bParamDefaultActionPerformed(evt);
            }
            else if (evt.getSource() == bHomePage) {
                JWorkSheet.this.bHomePageActionPerformed(evt);
            }
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == tTimeTotal) {
                JWorkSheet.this.tTimeTotalMouseClicked(evt);
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
            if (evt.getSource() == tabbedPane) {
                JWorkSheet.this.tabbedPaneStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents
    
    private void cbTimeOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTimeOffActionPerformed
        
        WorkDay.P_DAYOFF.setValue(applContext.getWorkDay(), cbTimeOff.isSelected());
        lCurrentDay.setForeground(cbTimeOff.isSelected() ? COLOR_OFF : COLOR_WORK );
        
    }//GEN-LAST:event_cbTimeOffActionPerformed
    
    private void bHomePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bHomePageActionPerformed
        paramTable.submitEditMode(true);
        browse(APPL_HOMEPAGE);
    }//GEN-LAST:event_bHomePageActionPerformed
    
    private void bGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGotoActionPerformed
        
        Date date = new DateDialog(applContext).getResult();
        if (date!=null) {
            final YearMonthDay yearMonthDay = new YearMonthDay(date);
            applContext.selectWorkDay(yearMonthDay);
            changeCurrentDate(Integer.MIN_VALUE);
        }
    }//GEN-LAST:event_bGotoActionPerformed
    
        private void bParamDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bParamDefaultActionPerformed
            UjoTableModel model = paramTable.getModel();
            for (int i=model.getRowCount()-1; i>=0; i--) {
                UjoPropertyRow ujo = (UjoPropertyRow) model.getRowNullable(i);
                UjoProperty param = ujo.getProperty();
                ujo.writeValue(UjoPropertyRow.P_VALUE, param.getDefault());
            }
            model.fireTableAllRowUpdated();
        }//GEN-LAST:event_bParamDefaultActionPerformed
        
        private void bParamReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bParamReportActionPerformed
        paramTable.submitEditMode(true);
        //ApplTools.setClipboard("Parameters:\n" + paramTable.getStringModel());
        showTableReport(paramTable, "Parameter Report");
        }//GEN-LAST:event_bParamReportActionPerformed
    
    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        
        // Submit & validation:
        if (tables!=null) try {
            for (UjoTable table : tables) {
                table.submitEditMode(true);
            }
        } catch (RuntimeException e) {
            showMessage(e.getMessage(), e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
        }
        
        // Accesories
        if (applContext.getWorkSpace()!=null
        &&  tabbedPane.getSelectedComponent()==pWorkSheet
        ){
            setTableEventEditor();
            displaySelectedDay();
        }
        
        // Parameters
        if (tabbedPane.getSelectedComponent()==pParameters
        &&  paramTable.getSelectedRow()<0
        ){
            paramTable.selectRow(0);
        }
        
        
    }//GEN-LAST:event_tabbedPaneStateChanged
    
    private void bAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAboutActionPerformed

        final Package ujo  = Ujo.class.getPackage();

        eventTable.submitEditMode(true);
        ApplTools.aboutApplication
        ( this
        , APPL_NAME
        , APPL_VERSION
        , "GNU/GPL 2"
        , "<a href=\"" + APPL_HOMEPAGE + "\">" + APPL_HOMEPAGE + "</a>"
        , APPL_RELEASED
        , "NetBeans 6.1"
        , "Mark James' icons"
        + "<br>&nbsp;(Creative Commons Attribution License)"
        + "<br>&nbsp;<a href=\"http://famfamfam.com\">http://famfamfam.com</a>"
        , ujo.getSpecificationTitle() + " " + ujo.getSpecificationVersion()
        + "<br>&nbsp;(Apache License, Version 2.0)"
        + "<br>&nbsp;<a href=\"http://ujoframework.org\">http://ujoframework.org</a>"
        , new ResourceProvider().getIcon(ResourceProvider.LOGO)
        , ApplTools.createCloseButton(applContext.getLanguageManager().getText("OK"))
        , applContext.getLanguageManager()
        );
    }//GEN-LAST:event_bAboutActionPerformed
    
    private void bTaskReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTaskReportActionPerformed
        //ApplTools.setClipboard(taskTable.getStringModel());
        showTableReport(taskTable, "Task Report");
    }//GEN-LAST:event_bTaskReportActionPerformed
    
    private void bProjReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bProjReportActionPerformed
        //ApplTools.setClipboard(projTable.getStringModel());
        showTableReport(projTable, "Project Report");
    }//GEN-LAST:event_bProjReportActionPerformed
    
    private void bReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReportActionPerformed
        eventTable.submitEditMode(true);
        
        if (true) {
            bSortActionPerformed(evt);
            applContext.saveData(true);
            ReportDialog reportDialog = new ReportDialog(this, applContext);
            reportDialog.setVisible(true);
            Boolean result = reportDialog.getResult();
//            if (result!=null && result) {
//                showTableReport();
//            }
        } else if (false) {
            // EXPRORT:
            // bSortActionPerformed(evt);
            // ApplTools.setClipboard(lCurrentDay.getText() + '\n' + eventTable.getStringModel());
        } else {
            // bSortActionPerformed(evt);
            // String statistics = lCurrentDay.getText() + '\n' + ((EventTableModel)eventTable.getModel()).calculateGroupStatistics();
            // ApplTools.setClipboard(statistics);
            // showTableReport();
            
        }
        
    }//GEN-LAST:event_bReportActionPerformed
    
    private void bTaskSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTaskSortActionPerformed
        taskTable.submitEditMode(true);
        taskTable.getModel().sort(true, TaskType.P_ID,  TaskType.P_DESCR);
        taskTable.showSortedColumn(TaskType.P_ID);
    }//GEN-LAST:event_bTaskSortActionPerformed
    
    private void bTaskCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTaskCloneActionPerformed
        taskTable.submitEditMode(true);
        
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex>=0) {
            taskTable.getModel().cloneRow(rowIndex, 2, this);
        }
        taskTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bTaskCloneActionPerformed
    
    private void bTaskDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTaskDeleteActionPerformed
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex>=0) {
            taskTable.getModel().deleteRow(rowIndex);
        }
        taskTable.selectRow(rowIndex);
    }//GEN-LAST:event_bTaskDeleteActionPerformed
    
    private void bTaskCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTaskCreateActionPerformed
        TaskType task = new TaskType();
        taskTable.getModel().addRow(task);
        taskTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bTaskCreateActionPerformed
    
    private void bProjSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bProjSortActionPerformed
        projTable.submitEditMode(true);
        projTable.getModel().sort(true, Project.P_ID,  Project.P_DESCR);
        projTable.showSortedColumn(Project.P_ID);
    }//GEN-LAST:event_bProjSortActionPerformed
    
    private void bProjCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bProjCloneActionPerformed
        projTable.submitEditMode(true);
        int rowIndex = projTable.getSelectedRow();
        if (rowIndex>=0) {
            projTable.getModel().cloneRow(rowIndex, 3, this); // CLONE ???
        }
        projTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bProjCloneActionPerformed
    
    private void bProjDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bProjDeleteActionPerformed
        int rowIndex = projTable.getSelectedRow();
        if (rowIndex>=0) {
            projTable.getModel().deleteRow(rowIndex);
        }
        projTable.selectRow(rowIndex);
    }//GEN-LAST:event_bProjDeleteActionPerformed
    
    private void bProjCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bProjCreateActionPerformed
        taskTable.submitEditMode(true);
        
        Project proj = new Project();
        projTable.getModel().addRow(proj);
        projTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bProjCreateActionPerformed
    
    private void bSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSortActionPerformed
        eventTable.submitEditMode(true);
        int selectedIndex = eventTable.getSelectedRow();
        EventTableModel model = (EventTableModel) eventTable.getModel();
        Event selectedRow = model.getRowNullable(selectedIndex);
        
        // Sort:
        final boolean lastRow = selectedIndex==model.getRowCount()-1;
        model.sort( lastRow );
        
        // Restore selection:
        if (selectedRow!=null) {
            eventTable.selectRow(selectedRow);
        }
    }//GEN-LAST:event_bSortActionPerformed
    
    private void bTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTodayActionPerformed
        changeCurrentDate(0);
    }//GEN-LAST:event_bTodayActionPerformed
    
    private void bDayNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDayNextActionPerformed
        int step = evt.getSource()==bDayNext ? +1 : -1 ;
        changeCurrentDate(step);
    }//GEN-LAST:event_bDayNextActionPerformed
    
    private void bCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCloneActionPerformed
        eventTable.submitEditMode(true);
        int rowIndex = eventTable.getSelectedRow();
        if (rowIndex>=0) {
            UjoTableModel model = eventTable.getModel();
            int lastRow = model.getRowCount();
            Time  time1 = (Time) model.getValueAt(lastRow-1, Event.P_TIME);
            Time  time2 = applContext.isToday() ? new Time(true) : ((Event)model.getRowLast()).getTimeFinished();
            Short period = time2.substract(time1);
            //
            model.cloneRow(rowIndex, 2, this);
            model.setValueAt( period                , lastRow-1, Event.P_PERIOD);
            model.setValueAt( time2                 , lastRow  , Event.P_TIME);
            model.setValueAt(ZeroProvider.ZERO_SHORT, lastRow  , Event.P_PERIOD);
            
        }
        eventTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bCloneActionPerformed
    
    private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
        eventTable.submitEditMode(true);
        int rowIndex = eventTable.getSelectedRow();
        if (rowIndex>=0) {
            eventTable.getModel().deleteRow(rowIndex);
        }
        eventTable.selectRow(rowIndex);        
        bSortActionPerformed(evt);
    }//GEN-LAST:event_bDeleteActionPerformed
    
    private void bCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCreateActionPerformed
        Event newEvent = new Event().initTime();
        EventTableModel model = (EventTableModel) eventTable.getModel();
        
        if (!applContext.isToday()){
            if (eventTable.getRowCount()>0) {
                Event lastEvent = model.getRowLast();
                Time newTime = lastEvent.getTimeFinished();
                Event.P_TIME.setValue( newEvent, newTime );
            } else {
                Time newTime = new Time(60*8); // 8:00
                Event.P_TIME.setValue( newEvent, newTime );
            }
        }
        
        model.insertRowAndDefault(newEvent);
        eventTable.selectRow(Integer.MAX_VALUE);
    }//GEN-LAST:event_bCreateActionPerformed

    private void tTimeTotalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tTimeTotalMouseClicked
        bSortActionPerformed(null);
    }//GEN-LAST:event_tTimeTotalMouseClicked
    
    /** Create new work Event */
    public void createWorkEvent(boolean currentDay) {
        if (currentDay) {
            changeCurrentDate(0);
        }
        tabbedPane.setSelectedIndex(0);
        bCreateActionPerformed(null);
    }
    
    /** Init HotKeys */
    private void initHotKeys() {
        
        final String NEW_EVENT = "NewEvent";
        ApplTools.registerAction
        ( KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)
        , NEW_EVENT
        , getRootPane()
        , new AbstractAction(NEW_EVENT) { public void actionPerformed(ActionEvent e) {
            createWorkEvent(true);
        }});
        
        final String SORT_EVENT = "SortEvent";
        ApplTools.registerAction
        ( KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)
        , SORT_EVENT
        , getRootPane()
        , new AbstractAction(SORT_EVENT) { public void actionPerformed(ActionEvent e) {
            tabbedPane.setSelectedIndex(0);
            bSortActionPerformed(e);
        }});
        
    }
    
    /** Enable/Disable method setVisible(). 
     * <br> Unlock recalculate the current day.
     */
    public void setVisibleLock(boolean lock) {
        this.visibleLock = lock;
        if (lock) {
            super.setVisible(true);
        } else if (applContext.isToday()) {
             bSortActionPerformed(null);
        }
    }
    
    @Override
    public void setVisible(boolean b) {
        if (!visibleLock) {
            super.setVisible(b);
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable $eventTable;
    private javax.swing.JTable $paramTable;
    private javax.swing.JTable $projTable;
    private javax.swing.JTable $taskTable;
    private javax.swing.JButton bAbout;
    private javax.swing.JButton bClone;
    private javax.swing.JButton bCreate;
    private javax.swing.JButton bDayNext;
    private javax.swing.JButton bDayPrev;
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bGoto;
    private javax.swing.JButton bHomePage;
    private javax.swing.JButton bParamDefault;
    private javax.swing.JButton bParamReport;
    private javax.swing.JButton bProjClone;
    private javax.swing.JButton bProjCreate;
    private javax.swing.JButton bProjDelete;
    private javax.swing.JButton bProjReport;
    private javax.swing.JButton bProjSort;
    private javax.swing.JButton bReport;
    private javax.swing.JButton bSort;
    private javax.swing.JButton bTaskClone;
    private javax.swing.JButton bTaskCreate;
    private javax.swing.JButton bTaskDelete;
    private javax.swing.JButton bTaskReport;
    private javax.swing.JButton bTaskSort;
    private javax.swing.JButton bToday;
    private javax.swing.JCheckBox cbTimeOff;
    private javax.swing.JScrollPane eventSrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lCurrentDay;
    private javax.swing.JLabel lTasks;
    private javax.swing.JLabel lTimeTotal;
    private javax.swing.JPanel pButtons;
    private javax.swing.JPanel pDays;
    private javax.swing.JPanel pParamButtons;
    private javax.swing.JPanel pParamDescr;
    private javax.swing.JPanel pParameters;
    private javax.swing.JPanel pProj;
    private javax.swing.JPanel pProjButtons;
    private javax.swing.JPanel pProjects;
    private javax.swing.JPanel pTask;
    private javax.swing.JPanel pTaskButtons;
    private javax.swing.JPanel pTotal;
    private javax.swing.JPanel pWorkSheet;
    private javax.swing.JTextPane paramDescr;
    private javax.swing.JScrollPane spDescr;
    private javax.swing.JScrollPane spParam;
    private javax.swing.JScrollPane spProjects;
    private javax.swing.JScrollPane spTasks;
    private javax.swing.JSplitPane splitParam;
    private javax.swing.JTextField tTimeTotal;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables



    /** Main */
    public static void main(String args[]) {
        
        ApplContext applContext = new ApplContext();
        Throwable error = null;
        
        try {
            applContext.setUserConfigDir(args.length>0 ? args[0] : null);
            applContext.loadData();
            ApplTools.initLookAndFeel(Parameters.P_NIMBUS_LAF.of(applContext.getParameters()));
        } catch (Throwable e) {
            error = e;
        }

        new JWorkSheet(applContext, error).setVisible();
    }    
}
