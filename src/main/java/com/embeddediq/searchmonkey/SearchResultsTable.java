/*
 * Copyright (C) 2017-18 cottr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.embeddediq.searchmonkey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.IntStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author cottr
 */
public class SearchResultsTable extends javax.swing.JPanel implements ItemListener {

    private final List<SearchResult> rowData;
    private final MyTableModel myModel;
    private final Preferences prefs;
    
    /**
     * Creates new form SearchResults
     */
    public SearchResultsTable() {
        prefs = Preferences.userNodeForPackage(SearchEntry.class);
        initComponents();

        rowData = new ArrayList<>();
        myModel = new MyTableModel();
        jTable1.setModel(myModel);
        SearchMonkeyTableRenderer cellRenderer = new SearchMonkeyTableRenderer();
        jTable1.setDefaultRenderer(Object.class, cellRenderer);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setRowHeight(28);
        jTable1.getColumn(SearchResult.COLUMN_NAMES[SearchResult.FLAGS]).setCellRenderer(new IconTableRenderer(jTable1.getRowHeight()-6));
        jTable1.getTableHeader().setDefaultRenderer(cellRenderer);
        // header = table.getTableHeader();

        
        // Check to see if the Desktop().edit function is supported
        this.jOpen.setVisible(Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
        this.jEdit.setVisible(Desktop.getDesktop().isSupported(Desktop.Action.EDIT));
        this.jBrowse.setVisible(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
        

        Restore();
    }
    
    private void restoreColumnOrder(String name, Object def)
    {
        Gson g = new Gson();
        
        String strdef = prefs.get(name, g.toJson(def));
        int[] indices = g.fromJson(strdef, int[].class);
        if (indices.length != ((int[])def).length)
        {
            indices = ((int[])def);
        }
        
        TableColumnModel columnModel = jTable1.getColumnModel();
        
        TableColumn column[] = new TableColumn[indices.length];

        for (int i = 0; i < column.length; i++) {
            column[i] = columnModel.getColumn(indices[i]);
        }

        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }

        for (int i = 0; i < column.length; i++) {
            columnModel.addColumn(column[i]);
        }
        
        Set<Integer> cols = new HashSet<>();
        for (int i: indices) {
            cols.add(i);
        }
        // Add an entry for each column
        jColumnMenu.removeAll();
        for (int i=0; i<SearchResult.COLUMN_NAMES.length; i++)
        {
            String column2 = SearchResult.COLUMN_NAMES[i];
            JCheckBoxMenuItem item = new JCheckBoxMenuItem();
            item.setName(column2);
            item.setText(column2);
            item.setSelected(cols.contains(i));
            item.addItemListener(this);
            jColumnMenu.add(item);
        }
    }
    private void restoreColumnWidth(String name, Object def)
    {
        Gson g = new Gson();
        
        String strdef = prefs.get(name, g.toJson(def));
        int[] indices = g.fromJson(strdef, int[].class);
        if (indices.length != ((int[])def).length)
        {
            indices = ((int[])def);
        }
        
        for (int i=0; i<SearchResult.COLUMN_NAMES.length; i++)
        {
            try
            {
                jTable1.getColumn(SearchResult.COLUMN_NAMES[i]).setPreferredWidth(indices[i]);
            }
            catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex)
            {
                Logger.getLogger(SearchResultsTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void storeColumnWidth(String name)
    {
        Gson g = new Gson();
        int[] indices = new int[SearchResult.COLUMN_NAMES.length];
        for (int i=0; i<SearchResult.COLUMN_NAMES.length; i++)
        {
            try
            {
                indices[i] = jTable1.getColumn(SearchResult.COLUMN_NAMES[i]).getWidth();
            }
            catch (IllegalArgumentException ex)
            {
                // Logger.getLogger(SearchResultsTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        prefs.put(name, g.toJson(indices));
    }
    private void storeColumnOrder(String name)
    {
        // Read back the current column ordering
        TableColumnModel columnModel = jTable1.getColumnModel();
        int column[] = new int[columnModel.getColumnCount()];
        for (int i = 0; i < column.length; i++) {
            column[i] = columnModel.getColumn(i).getModelIndex();
        }
        
        // Store the current column order
        Gson g = new Gson();
        prefs.put(name, g.toJson(column));
    }

    public void Save()
    {
        storeColumnWidth("ColumnWidth");
        storeColumnOrder("ColumnOrder");
    }
    
    /**
     *
     */
    private void Restore()
    {
        restoreColumnWidth("ColumnWidth", SearchResult.COLUMN_WIDTH);
        restoreColumnOrder("ColumnOrder", IntStream.range(0, SearchResult.COLUMN_WIDTH.length).toArray());
        jTable1.doLayout();
    }
        
    // Use with SearchWorker
    public void clearTable()
    {
        myModel.clear();
    }
    public void insertRows(List<SearchResult> results)
    {
        myModel.addRows(results);
    }
    // Use with SearchWorker
    
    public void addListSelectionListener(ListSelectionListener listener)
    {
        jTable1.getSelectionModel().addListSelectionListener(listener);
    }

    public SearchResult[] getSelectedRows()
    {
        int[] rows = jTable1.getSelectedRows();
        SearchResult[] results = new SearchResult[rows.length];
        for (int i=0; i<rows.length; i++)
        {
            results[i] = rowData.get(jTable1.convertRowIndexToModel(rows[i]));
        }        
        return results;
    }

    public boolean exportToCSV( String pathToExportTo) {

        try( FileWriter csv = new FileWriter( pathToExportTo )) {

            TableModel model = jTable1.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i) + ",");
            }

            csv.write("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    value = value == null ? "" : value;

                    csv.write( StringEscapeUtils.escapeCsv(value.toString()) + ",");
                }
                csv.write("\n");
            }

            return true;
        }
        catch ( IOException ignored ) {

        }
        return false;
    }

    public boolean exportToJSON( String pathToExportTo) {

        try( FileWriter file = new FileWriter( pathToExportTo )) {

            String json = new GsonBuilder()
                .create()
                .toJson( rowData );

            file.write( json );

            return true;
        }
        catch ( IOException ignored ) {

        }
        return false;
    }

    public void resizeAllColumnWidth() {
    final TableColumnModel columnModel = jTable1.getColumnModel();
    for (int column = 0; column < jTable1.getColumnCount(); column++) {
        int width = 15; // Min width
        for (int row = 0; row < jTable1.getRowCount(); row++) {
            TableCellRenderer renderer = jTable1.getCellRenderer(row, column);
            Component comp = jTable1.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width +1 , width);
        }
        if(width > 300) {
            width=300;
        }
        columnModel.getColumn(column).setPreferredWidth(width);
    }
}

    private class MyTableModel extends AbstractTableModel 
    {
        MyTableModel()
        {
            super();
        }
        
        @Override
        public String getColumnName(int col) {
            // if (col == SearchResult.FILE_ICON) return ""; // !!Don't do this as it breaks stuff downstream!!
            return SearchResult.COLUMN_NAMES[col];
        }
        
        @Override
        public Class<?> getColumnClass(int col)
        {
            return SearchResult.COLUMN_CLASSES[col];
        }
        
        public void addRows(List<SearchResult> objects) { 
            for (SearchResult vals: objects) {
                rowData.add(vals);
                
                int rowNr = rowData.size() - 1;
                this.fireTableRowsInserted(rowNr, rowNr);
            }
        }
        
        @Override
        public int getRowCount() { 
            return rowData.size();
        }

        public void clear() {
            rowData.clear();
            fireTableDataChanged();
        }
        
        @Override
        public int getColumnCount() {
            return SearchResult.COLUMN_NAMES.length; // Constant
        }
        
        @Override
        public Object getValueAt(int row, int col) {
            SearchResult val = rowData.get(row);
            return val.get(col);
        }
        @Override
        public boolean isCellEditable(int row, int col)
            { return false; }

    }
    
    class IconTableRenderer extends DefaultTableCellRenderer
    {

        private final JLabel hidden;
        private final JLabel linked;
        public IconTableRenderer(int height) {
            setOpaque(true); //MUST do this for background to show up.
            hidden = new JLabel(getScaledIcon(getClass().getResource("/images/File-Hide-icon.png"), height));
            hidden.setToolTipText("Hidden file");
            linked = new JLabel(getScaledIcon(getClass().getResource("/images/link-icon-614x460.png"), height));
            linked.setToolTipText("Symbolic link");
            this.setLayout(new FlowLayout());
        }

        private Icon getScaledIcon(URL srcImg, int height) {
            ImageIcon image = new ImageIcon(srcImg);
            
            image.setImage(getScaledImage(image.getImage(), height));
            return (Icon)image;
        }

        private Image getScaledImage(Image srcImg, int height){
            int w = height * srcImg.getWidth(this) / srcImg.getHeight(this);
            BufferedImage resizedImg = new BufferedImage(w, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImg.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(srcImg, 0, 0, w, height, null);
            g2.dispose();

            return resizedImg;
        }

        /**
         *
         * @param table
         * @param value
         * @param isSelected
         * @param hasFocus
         * @param row
         * @param column
         * @return
         */
        @Override
        public Component getTableCellRendererComponent(
                                JTable table, Object value,
                                boolean isSelected, boolean hasFocus,
                                int row, int column) {
            JPanel panel = new JPanel();
            int flags = (int)value;
            List<String> flagText = new ArrayList<>();
            if (flags == SearchResult.HIDDEN_FILE)
            {
                panel.add(hidden);
                //setIcon(hidden);
                flagText.add("Hidden file");
            }
            if (flags == SearchResult.SYMBOLIC_LINK){
                panel.add(linked);
                // setIcon(linked);
                flagText.add("Symbolic link");
            }
            // Update tool tips
            String txtToolTip = String.join(", ", flagText); 
            if (txtToolTip.isEmpty())
            {
                txtToolTip = "Normal file";
            }
            panel.setToolTipText(txtToolTip);
            if (isSelected)
            {
                panel.setForeground(table.getSelectionForeground());                
                panel.setBackground(table.getSelectionBackground());                
            } else {
                panel.setForeground(table.getForeground());                
                panel.setBackground(table.getBackground());                
            }
            return panel;
            
        }
    }
    
    public class SearchMonkeyTableRenderer extends DefaultTableCellRenderer { 
        
        private final ResourceBundle rb;
        
        public SearchMonkeyTableRenderer() {
            setOpaque(true); //MUST do this for background to show up.
            this.rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
            hidden = new ImageIcon(getClass().getResource("/images/File-Hide-icon.png"));
            linked = new ImageIcon(getClass().getResource("/images/link-icon-614x460.png"));
        }

        private final Icon hidden;
        private final Icon linked;
        
        private final String[] MAG_NAMES = new String[] {
            "SearchResult.Bytes.String",
            "SearchResult.KBytes.String",
            "SearchResult.MBytes.String",
            "SearchResult.GBytes.String",
            "SearchResult.TBytes.String",
        };
    
        private int getFileOrder(long fsize)
        {
            int order = 0;
            while (fsize > 1024)
            {
                order ++;
                fsize /= 1024;
            }
            if (order > 1) order --;
            return order;
        }
        
        /**
         *
         * @param table
         * @param value
         * @param isSelected
         * @param hasFocus
         * @param row
         * @param column
         * @return
         */
        @Override
        public Component getTableCellRendererComponent(
                                JTable table, Object value,
                                boolean isSelected, boolean hasFocus,
                                int row, int column) {

            setIcon(null);
            
            // Special case for the header
            if (row == -1)
            {
                DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // Convert heading text into the international string

                renderer.setText(rb.getString((String)value));
                return renderer;
            }

            String txtVal = "";
            String txtToolTip = new String();
            int idx = table.convertColumnIndexToModel(column);
            switch (idx) {
                case SearchResult.SIZE: // Handle Size
                    int order = getFileOrder((long)value);
                    if (order > 0) {
                        txtVal = String.format("%.1f %s", ((double)((long)value) / Math.pow(1024, order)), rb.getString(MAG_NAMES[order]));
                    } else {
                        txtVal = String.format("%d %s", (long)value, rb.getString(MAG_NAMES[order]));
                    }
                    break;
                case SearchResult.CREATED: // Handle Date
                case SearchResult.ACCESSED:
                case SearchResult.MODIFIED:
                    FileTime ft = (FileTime)value;
                    LocalDateTime ldt = LocalDateTime.ofInstant(ft.toInstant(), ZoneId.systemDefault());
                    txtVal = ldt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
                    txtToolTip = ldt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM));
                    break;
                case SearchResult.FLAGS: // Handle Flags
                    int flags = (int)value;
                    List<String> flagText = new ArrayList<>();
                    if (flags == SearchResult.HIDDEN_FILE)
                    {
                        //this.add(hidden);
                        //setIcon(hidden);
                        flagText.add("HIDDEN");
                    }
                    if (flags == SearchResult.SYMBOLIC_LINK){
                        //this.add(linked);
                        // setIcon(linked);
                        flagText.add("SYMBOLIC");
                    }
                    txtVal = String.join(", ", flagText); 
                    if (txtVal.isEmpty())
                    {
                        txtToolTip = "Normal file";
                    }
                    break;
                case SearchResult.FILE_ICON: // Handle file icon
                    setIcon(GetIcon((int)value));
                    break;
                case SearchResult.COUNT: // Handle Count
                    long count = (long)value;
                    if (count < 0L)
                    {
                        txtVal = "N/A"; // Not applicable
                        txtToolTip = "Not applicable";
                       break;
                    } else {
                        txtToolTip = String.format("%d Match%s", count, (count > 1 ? "es" : ""));
                    }
                default:
                    if (value != null) {
                        txtVal = value.toString();
                    }
                    break;
            }

            // Create text, and tooltips to match
            setText(txtVal);
            if (txtToolTip.isEmpty()) txtToolTip = txtVal;
            if (!txtToolTip.isEmpty()) setToolTipText(txtToolTip);
            
            // Allow selection
            if (isSelected)
            {
                // selectedBorder is a solid border in the color
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                // unselectedBorder is a solid border in the color
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            // this.set

            return this;
        }
    }
    
    public Icon GetIcon(int lookup)
    {
        return icons[lookup];
    }
    private final Icon[] icons = new Icon[] {
        getScaledIcon(getClass().getResource("/images/icons/icon-archive.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-audio.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-c.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-doc.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-drawing.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-font.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-h.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-htm.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-image.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-java.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-odb.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-odg.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-odp.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-ods.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-odt.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-pdf.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-ppt.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-spreadsheet.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-text-generic.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-unknown.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-video.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-word-processor.png"), 12),
        getScaledIcon(getClass().getResource("/images/icons/icon-xls.png"), 12),
    };
    
    private Icon getScaledIcon(URL srcImg, int height) {
        ImageIcon image = new ImageIcon(srcImg);

        image.setImage(image.getImage());
        //image.setImage(getScaledImage(image.getImage(), height));
        return (Icon)image;
    }
//    private Image getScaledImage(Image srcImg, int height){
//        int w = height * srcImg.getWidth(this) / srcImg.getHeight(this);
//        BufferedImage resizedImg = new BufferedImage(w, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = resizedImg.createGraphics();
//
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.drawImage(srcImg, 0, 0, w, height, null);
//        g2.dispose();
//
//        return resizedImg;
//    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jEdit = new javax.swing.JMenuItem();
        jBrowse = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jColumnMenu = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        jPopupMenu1.setLabel(bundle.getString("SearchResultsTable.jPopupMenu1.label")); // NOI18N
        jPopupMenu1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenu1PopupMenuWillBecomeVisible(evt);
            }
        });

        jOpen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jOpen.setMnemonic('O');
        jOpen.setText(bundle.getString("SearchResultsTable.jOpen.text")); // NOI18N
        jOpen.setToolTipText(bundle.getString("SearchResultsTable.jOpen.toolTipText")); // NOI18N
        jOpen.setEnabled(false);
        jOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOpenActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jOpen);
        jPopupMenu1.add(jSeparator1);

        jEdit.setMnemonic('E');
        jEdit.setText(bundle.getString("SearchResultsTable.jEdit.text")); // NOI18N
        jEdit.setToolTipText(bundle.getString("SearchResultsTable.jEdit.toolTipText")); // NOI18N
        jEdit.setEnabled(false);
        jEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jEdit);

        jBrowse.setMnemonic('B');
        jBrowse.setText(bundle.getString("SearchResultsTable.jBrowse.text")); // NOI18N
        jBrowse.setToolTipText(bundle.getString("SearchResultsTable.jBrowse.toolTipText")); // NOI18N
        jBrowse.setEnabled(false);
        jBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jBrowse);
        jPopupMenu1.add(jSeparator2);

        jColumnMenu.setMnemonic('C');
        jColumnMenu.setText(bundle.getString("SearchResultsTable.jColumnMenu.text")); // NOI18N
        jColumnMenu.setToolTipText(bundle.getString("SearchResultsTable.jColumnMenu.toolTipText")); // NOI18N
        jPopupMenu1.add(jColumnMenu);

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setComponentPopupMenu(jPopupMenu1);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setComponentPopupMenu(jPopupMenu1);
        jTable1.setFillsViewportHeight(true);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void itemStateChanged(ItemEvent ie) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)ie.getItem();
        String menuName = item.getName();
        if (ie.getStateChange() == ItemEvent.SELECTED)
        {
            TableColumnModel tcm = jTable1.getColumnModel();
            int idx;
            for (idx=0; idx<SearchResult.COLUMN_NAMES.length; idx++) {
                if (SearchResult.COLUMN_NAMES[idx].equals(menuName)) break;
            }
            //int idx2 = tcm.getColumn(idx);
            TableColumn col = new TableColumn(idx, SearchResult.COLUMN_WIDTH[idx]);
            //TableColumn col = tcm.getColumn(idx);
            jTable1.addColumn(col);
            jTable1.moveColumn(jTable1.getColumnCount() - 1, idx);
        } else {
            TableColumn col = jTable1.getColumn(menuName);
            jTable1.removeColumn(col);
            
        }
    }

    boolean colsVisible[] = new boolean[9];
    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        boolean enable = true;
        int count = jTable1.getSelectedRowCount();
        if (count > 0)
        {
            jOpen.setEnabled(enable);
            jEdit.setEnabled(enable);
            if (count > 1) {
                jOpen.setText(String.format("Open %d files", count));
                jEdit.setText(String.format("Edit %d files", count));
            }
        }
        
        count = getUniqueFolders().length;
        if (count > 0)
        {
            jBrowse.setEnabled(enable);
            if (count > 1) {
                jBrowse.setText(String.format("Browse %d folders", count));
            }
        }
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    String[] getUniqueFolders()
    {
        Set<String> folders = new HashSet<>();
        int[] rows = this.jTable1.getSelectedRows();
        for (int row: rows)
        {
            SearchResult result = rowData.get(jTable1.convertRowIndexToModel(row));
            folders.add(result.pathName);
        }
        return (String[]) folders.toArray(new String[folders.size()]);
    }
    
    private void jOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOpenActionPerformed
        int[] rows = this.jTable1.getSelectedRows();
        for (int row: rows) {
            Open(row);
        }
    }//GEN-LAST:event_jOpenActionPerformed

    private void Open(int row)
    {
        SearchResult result = rowData.get(jTable1.convertRowIndexToModel(row));
        File file = new File(result.pathName, result.fileName);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Logger.getLogger(SearchResultsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Edit(int row)
    {
        SearchResult result = rowData.get(jTable1.convertRowIndexToModel(row));
        File file = new File(result.pathName, result.fileName);
        try {
            Desktop.getDesktop().edit(file);
        } catch (IOException ex) {
            Logger.getLogger(SearchResultsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Browse(String folder)
    {
        File file = new File(folder);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Logger.getLogger(SearchResultsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        if (evt.getClickCount() > 1) {
            // Double click handler]
            Point point = evt.getPoint();
            int row = jTable1.rowAtPoint(point);
            if (row != -1)
            {
                Open(row);
            }
        }
    }//GEN-LAST:event_jTable1MousePressed

    private void jBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseActionPerformed
        String[] folders = getUniqueFolders();
        // int[] rows = this.jTable1.getSelectedRows();
        for (String folder: folders) {
            Browse(folder);
        }
    }//GEN-LAST:event_jBrowseActionPerformed

    private void jEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditActionPerformed
        int[] rows = this.jTable1.getSelectedRows();
        for (int row: rows) {
            Edit(row);
        }
    }//GEN-LAST:event_jEditActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jBrowse;
    private javax.swing.JMenu jColumnMenu;
    private javax.swing.JMenuItem jEdit;
    private javax.swing.JMenuItem jOpen;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables


}
