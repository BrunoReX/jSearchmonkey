/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author adam
 */
public class CalendarPanel extends javax.swing.JPanel {

    Calendar calendar;
    /**
     * Creates new form CalendarPopup
     */
    public CalendarPanel() {
        initComponents();

        calendar = GregorianCalendar.getInstance();
        baseCal = GregorianCalendar.getInstance();
        UpdateCalendar();
        
        // This is being overriden by the HMI
        jCalendar.setCellSelectionEnabled(true);

        // Handle row changes
        jCalendar.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            updateDate();
        });

        // Handle column changes
        jCalendar.getColumnModel().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) return;
            updateDate();
        });
        
        jCalendar.addMouseMotionListener(new MyMouseAdapter());   
        jCalendar.setComponentPopupMenu(jPopupMenu1);
    }
    
    // Detect which row and column the mouse is currently hovered over
    private int hoverRow = -1;
    private int hoverColumn = -1;
    public class MyMouseAdapter extends MouseMotionAdapter //extends MouseAdapter
    {

        @Override
        public void mouseMoved(MouseEvent e)
        {
            JTable aTable =  (JTable)e.getSource();
            hoverRow = aTable.rowAtPoint(e.getPoint());
            hoverColumn = aTable.columnAtPoint(e.getPoint());
            aTable.repaint();
        }
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        public StatusColumnCellRenderer()
        {
            super();
        }
                
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            //JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            Calendar now = Calendar.getInstance();
            Calendar c = (Calendar)now.clone();
            c.setTime((Date)value);
            
            this.setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
            //Get the status for the current row.
            if (c.get(Calendar.MONTH) != monthView.get(Calendar.MONTH)) {
                this.setForeground(Color.GRAY);
            } else {
                this.setForeground(Color.BLACK);
            }
            if ((c.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) &&
                    (c.get(Calendar.YEAR) == now.get(Calendar.YEAR))) {
                this.setBackground(Color.GREEN);
            } else {
                this.setBackground(Color.WHITE);
            }
            
            Border border;
            if (isSelected)
            {
                border = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED);
            }
            else {
                border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
            }
            this.setBorder(border);
            
            if (row == hoverRow && col == hoverColumn)
            {
                // Invert colours
                Color tmp = this.getBackground();
                this.setBackground(this.getForeground());
                this.setForeground(tmp);
            }

            //Return the JLabel which renders the cell.
            return this;

        }
    }
    
    Date date;
    public void setDate(Date date)
    {
        // this.date = date;
        calendar.setTime(date);
        this.date = date;
        UpdateCalendar();
        //int count = calendar - baseVal;
        //jTable1.getColumnModel().getSelectionModel().addSelectionInterval(, WIDTH);
        //TODO - select the cell after it has been chosen
    }

    private void setDate(int interval, int amount)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(interval, amount);
        this.setDate(cal.getTime());
    }

    public Date getDate()
    {
        return this.date;
    }
    private Collection<ChangeListener> listeners = new LinkedList<>();
    public void addChangeListener(ChangeListener listener)
    {
        listeners.add(listener);
    }
    
    private void updateDate()
    {
        Calendar my_copy = (Calendar) baseCal.clone();
        int col = jCalendar.getSelectedColumn();
        int row = jCalendar.getSelectedRow();
        if (col != -1 && row != -1)
        {
            my_copy.add(Calendar.DAY_OF_MONTH, col + (row * 7));
            this.date = my_copy.getTime();
            // Implement a change listener
            for(ChangeListener listener: listeners){
                listener.stateChanged(new ChangeEvent(this));
            }        
        }
    }
    
    private Calendar monthView;
    private void UpdateCalendar()
    {
        // Always clear the selection first
        jCalendar.clearSelection();
        jCalendar.getSelectionModel().clearSelection();
        // jCalendar.changeSelection(-1, -1, false, false);

        monthView = (Calendar)calendar.clone();
        int fd = monthView.getFirstDayOfWeek();
        
        // Update days of the week titles
        Map<String, Integer>weeknames = monthView.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT_STANDALONE, getLocale());
        weeknames.entrySet().forEach((entry) -> {
            int col = entry.getValue() - fd;
            if (col < 0) {
                col += 7;
            } //
            String dow = entry.getKey();
            TableColumn column = jCalendar.getTableHeader().getColumnModel().getColumn(col);
            column.setHeaderValue(dow.substring(0,1));
            // TODO - highlight today's date
        });
        
        // Update month/year name
        String m = monthView.getDisplayName(Calendar.MONTH, Calendar.LONG_STANDALONE, getLocale());
        int y = monthView.get(Calendar.YEAR);
        this.jLabel1.setText(String.format("%s %d", m, y));

        // Clear first and last rows
        for (int i = 0; i<7 ; i++)
        {
            jCalendar.getModel().setValueAt(null, 0, i);
            jCalendar.getModel().setValueAt(null, jCalendar.getRowCount()-1, i);
        }

        // Update days of the month
        {
            monthView.set(Calendar.DAY_OF_MONTH, 1);
            int wd = monthView.get(Calendar.DAY_OF_WEEK);
            monthView.add(Calendar.MONTH, 1);
            monthView.add(Calendar.DAY_OF_MONTH, -1);
            int last_day = monthView.get(Calendar.DAY_OF_MONTH); // Get the last day
            int col = wd - fd;
            if (col < 0) {
                col += 7;
            }
            int row = 0;
            for (int i = 0; i<last_day; i++)
            {
                jCalendar.getModel().setValueAt(i+1, row, col);
                col ++;
                if (col >= 7)
                {
                    col = 0;
                    row ++;
                }
            }    
        }
        

        // Set all of the days from the calendar
        monthView.set(Calendar.DAY_OF_MONTH, 1);
        baseCal.setTime(monthView.getTime());
        int cM = baseCal.get(Calendar.MONTH);
        int wd2 = baseCal.get(Calendar.DAY_OF_WEEK);
        int col2 = wd2 - fd;
        if (col2 < 0) {
            col2 += 7;
        }
        baseCal.add(Calendar.DAY_OF_MONTH, -col2); // Go back N days
        Calendar tmp = (Calendar)baseCal.clone();
        for (int row=0; row<jCalendar.getRowCount(); row++)
        {
            for (int col=0; col<7; col++)
            {
                // TODO - change the cell values if from last month
                int val = tmp.get(Calendar.DAY_OF_MONTH);
                jCalendar.getModel().setValueAt(tmp.getTime(), row, col);
                if (date != null && date.equals(tmp.getTime()))
                // if (tmp.equals(calendar))
                {
                    jCalendar.changeSelection(row, col, false, false);
                }
                tmp.add(Calendar.DAY_OF_MONTH, 1); // next
            }
        }

        // Update date renderer
        Enumeration<TableColumn> en = jCalendar.getColumnModel().getColumns();
        while (en.hasMoreElements())
        {
            TableColumn column = en.nextElement();
            column.setCellRenderer(new StatusColumnCellRenderer());
        }
        
    }

    private Calendar baseCal; //  = Calendar.getInstance();
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jCalendar = new javax.swing.JTable();
        jControlPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jNextToolBar = new javax.swing.JToolBar();
        jNext = new javax.swing.JButton();
        jNextYear = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jPrevYear = new javax.swing.JButton();
        jPrev = new javax.swing.JButton();

        jPopupMenu1.setInheritsPopupMenu(true);

        jMenuItem1.setText("Set today");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Set yesterday");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setText("Set last week");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);

        jMenuItem4.setText("Set last month");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        jMenuItem5.setText("Set last quarter");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem5);

        jMenuItem6.setText("Set last year");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem6);

        jMenuItem7.setText("Set last decade");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem7);

        setComponentPopupMenu(jPopupMenu1);
        setMaximumSize(new java.awt.Dimension(180, 150));
        setMinimumSize(new java.awt.Dimension(180, 150));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(180, 150));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jCalendar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "M", "T", "W", "T", "F", "S", "S"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jCalendar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jCalendar.setAutoscrolls(false);
        jCalendar.setColumnSelectionAllowed(true);
        jCalendar.setComponentPopupMenu(jPopupMenu1);
        jCalendar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jCalendar.setFillsViewportHeight(true);
        jCalendar.setIntercellSpacing(new java.awt.Dimension(0, 0));
        jCalendar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jCalendar.getTableHeader().setResizingAllowed(false);
        jCalendar.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jCalendar);
        jCalendar.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jCalendar.getColumnModel().getColumnCount() > 0) {
            jCalendar.getColumnModel().getColumn(0).setResizable(false);
            jCalendar.getColumnModel().getColumn(0).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(1).setResizable(false);
            jCalendar.getColumnModel().getColumn(1).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(2).setResizable(false);
            jCalendar.getColumnModel().getColumn(2).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(3).setResizable(false);
            jCalendar.getColumnModel().getColumn(3).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(4).setResizable(false);
            jCalendar.getColumnModel().getColumn(4).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(5).setResizable(false);
            jCalendar.getColumnModel().getColumn(5).setPreferredWidth(20);
            jCalendar.getColumnModel().getColumn(6).setResizable(false);
            jCalendar.getColumnModel().getColumn(6).setPreferredWidth(20);
        }

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jControlPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("December 2017");
        jControlPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        jNextToolBar.setBorder(null);
        jNextToolBar.setFloatable(false);
        jNextToolBar.setRollover(true);
        jNextToolBar.setBorderPainted(false);

        jNext.setText(">");
        jNext.setDefaultCapable(false);
        jNext.setFocusPainted(false);
        jNext.setFocusable(false);
        jNext.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextActionPerformed(evt);
            }
        });
        jNextToolBar.add(jNext);

        jNextYear.setText(">>");
        jNextYear.setDefaultCapable(false);
        jNextYear.setFocusable(false);
        jNextYear.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jNextYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextYearActionPerformed(evt);
            }
        });
        jNextToolBar.add(jNextYear);

        jControlPanel.add(jNextToolBar, java.awt.BorderLayout.EAST);

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setBorderPainted(false);

        jPrevYear.setText("<<");
        jPrevYear.setBorder(null);
        jPrevYear.setBorderPainted(false);
        jPrevYear.setDefaultCapable(false);
        jPrevYear.setFocusPainted(false);
        jPrevYear.setFocusable(false);
        jPrevYear.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPrevYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrevYearActionPerformed(evt);
            }
        });
        jToolBar1.add(jPrevYear);

        jPrev.setText("<");
        jPrev.setBorder(null);
        jPrev.setBorderPainted(false);
        jPrev.setDefaultCapable(false);
        jPrev.setFocusPainted(false);
        jPrev.setFocusable(false);
        jPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrevActionPerformed(evt);
            }
        });
        jToolBar1.add(jPrev);

        jControlPanel.add(jToolBar1, java.awt.BorderLayout.WEST);

        add(jControlPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void jPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrevActionPerformed
        this.jCalendar.clearSelection();
        calendar.add(Calendar.MONTH, -1);
        UpdateCalendar();
        //updateDate();
    }//GEN-LAST:event_jPrevActionPerformed

    private void jNextYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextYearActionPerformed
        this.jCalendar.clearSelection();
        calendar.add(Calendar.YEAR, 1);
        UpdateCalendar();
        //updateDate();
    }//GEN-LAST:event_jNextYearActionPerformed

    private void jNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextActionPerformed
        this.jCalendar.clearSelection();
        calendar.add(Calendar.MONTH, 1);
        UpdateCalendar();
        //updateDate();
    }//GEN-LAST:event_jNextActionPerformed

    private void jPrevYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrevYearActionPerformed
        this.jCalendar.clearSelection();
        calendar.add(Calendar.YEAR, -1);
        UpdateCalendar();
        //updateDate();
    }//GEN-LAST:event_jPrevYearActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.setDate(Calendar.DAY_OF_MONTH, 0); // Today
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        this.setDate(Calendar.DAY_OF_MONTH, -1); // Yesterday
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.setDate(Calendar.DAY_OF_MONTH, -7); // Last week
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.setDate(Calendar.MONTH, -1); // Last month
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        this.setDate(Calendar.MONTH, -4); // Last quarter
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        this.setDate(Calendar.YEAR, -1); // Last year
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        this.setDate(Calendar.YEAR, -10); // Last decade
    }//GEN-LAST:event_jMenuItem7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable jCalendar;
    private javax.swing.JPanel jControlPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JButton jNext;
    private javax.swing.JToolBar jNextToolBar;
    private javax.swing.JButton jNextYear;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JButton jPrev;
    private javax.swing.JButton jPrevYear;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
