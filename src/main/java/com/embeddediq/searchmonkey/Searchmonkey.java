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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author cottr
 */
public class Searchmonkey extends javax.swing.JFrame implements ActionListener, ListSelectionListener, PropertyChangeListener {

    //private final int delay = 250; // Timer delay before updating the contents
    
    private final String[] iconList = new String[] {
        "/images/searchmonkey-16x16.png",
        "/images/searchmonkey-22x22.png",
        "/images/searchmonkey-24x24.png",
        "/images/searchmonkey-32x32.png",
        "/images/searchmonkey-48x48.png",
        "/images/searchmonkey-96x96.png",
        "/images/searchmonkey-300x300.png",
    };
    
    private final Preferences prefs;
   
    /**
     * Creates new form NewMDIApplication
     */
    public Searchmonkey() {
        prefs = Preferences.userNodeForPackage(Searchmonkey.class);
        initComponents();
        
        // Update icon
        ArrayList<Image> imageList = new ArrayList<>();
        for (String fn: iconList)
        {
            imageList.add(new ImageIcon(getClass().getResource(fn)).getImage());
        }
        setIconImages(imageList);
        
        // Stop the toolbar from being floatable
        jToolBar1.setFloatable(false);

        // TODO future stuff
        //this.menuBar.setVisible(false);
        this.contentMenuItem.setVisible(false);
        this.editMenu.setVisible(false);
        this.openMenuItem.setVisible(false);
        this.saveAsMenuItem.setVisible(false);
        this.saveMenuItem.setVisible(false);
        this.jToolBar1.setVisible(false);
        this.jColoumnsMenu.setVisible(false);
        this.pack();
        this.setVisible(true);

        Restore();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void addActionListeners()
    {
        // Add listeners after initialised.
        searchEntryPanel1.addActionListener(this);
        searchResultsTable1.addListSelectionListener(this);
    }
    
    List<SearchEntry> history = new ArrayList<>();
    SearchWorker searchTask;
    public void Start()
    {
        // Get a copy of the search settings taken from the search panel
        SearchEntry entry = searchEntryPanel1.getSearchRequest();
        history.add(entry);
        searchEntryPanel1.Save();

        // SearchEntry entry, SearchResultsTable table
        searchEntryPanel1.Start();
        searchResultsTable1.clearTable();
        searchTask = new SearchWorker(entry, searchResultsTable1);
        searchTask.addPropertyChangeListener(this);
        searchMatchView1.setContentMatch(entry); // Update the content match
        searchTask.execute();
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
    }
    
    public void Stop()
    {
        if (searchTask != null && !searchTask.isCancelled())
        {
            searchTask.cancel(true);
        }
    }
    
    public void Done()
    {
        searchEntryPanel1.Stop();
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
    }
    
    public void UpdateContent(SearchResult[] results)
    {
        Path[] paths = new Path[results.length];
        for (int i=0; i<results.length; i++)
        {
            paths[i] = Paths.get(results[i].pathName, results[i].fileName);
        }
        searchMatchView1.UpdateView(paths);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        searchSummary2 = new com.embeddediq.searchmonkey.SearchSummaryPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        searchEntryPanel1 = new com.embeddediq.searchmonkey.SearchEntryPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        searchResultsTable1 = new com.embeddediq.searchmonkey.SearchResultsTable();
        searchMatchView1 = new com.embeddediq.searchmonkey.SearchMatchView();
        jToolBar1 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        exportAsCsvMenuItem = new javax.swing.JMenuItem();
        exportAsJsonMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        toolMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        showStatusbar = new javax.swing.JCheckBoxMenuItem();
        showToolbar = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jLayoutMenu = new javax.swing.JMenu();
        jLeftToRight = new javax.swing.JRadioButtonMenuItem();
        jRightToLeft = new javax.swing.JRadioButtonMenuItem();
        jColoumnsMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        fileChooser.setCurrentDirectory(null);
        fileChooser.setName(""); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        setTitle(bundle.getString("Searchmonkey.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(500, 500));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(searchSummary2, java.awt.BorderLayout.SOUTH);

        jSplitPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane2PropertyChange(evt);
            }
        });
        jSplitPane2.setLeftComponent(searchEntryPanel1);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setToolTipText(bundle.getString("Searchmonkey.jSplitPane1.toolTipText")); // NOI18N
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });
        jSplitPane1.setTopComponent(searchResultsTable1);
        jSplitPane1.setBottomComponent(searchMatchView1);

        jSplitPane2.setRightComponent(jSplitPane1);

        jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        jButton3.setText(bundle.getString("Searchmonkey.jButton3.text")); // NOI18N
        jButton3.setToolTipText(bundle.getString("Searchmonkey.jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);
        jToolBar1.add(jSeparator1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/system-search.png"))); // NOI18N
        jButton1.setToolTipText(bundle.getString("Searchmonkey.jButton1.toolTipText")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHideActionText(true);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons/process-stop.png"))); // NOI18N
        jButton2.setToolTipText(bundle.getString("Searchmonkey.jButton2.toolTipText")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.setFocusable(false);
        jButton2.setHideActionText(true);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        fileMenu.setMnemonic('f');
        fileMenu.setText(bundle.getString("Searchmonkey.fileMenu.text")); // NOI18N

        openMenuItem.setMnemonic('o');
        openMenuItem.setText(bundle.getString("Searchmonkey.openMenuItem.text")); // NOI18N
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText(bundle.getString("Searchmonkey.saveMenuItem.text")); // NOI18N
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText(bundle.getString("Searchmonkey.saveAsMenuItem.text")); // NOI18N
        fileMenu.add(saveAsMenuItem);

        exportMenu.setText(bundle.getString("Searchmonkey.exportMenu.text")); // NOI18N
        exportMenu.setToolTipText(bundle.getString("Searchmonkey.exportMenu.toolTipText")); // NOI18N

        exportAsCsvMenuItem.setText(bundle.getString("Searchmonkey.exportAsCsvMenuItem.text")); // NOI18N
        exportAsCsvMenuItem.setActionCommand(bundle.getString("Searchmonkey.exportAsCsvMenuItem.actionCommand")); // NOI18N
        exportAsCsvMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAsCsvMenuItem_ActionPerformed(evt);
            }
        });
        exportMenu.add(exportAsCsvMenuItem);

        exportAsJsonMenuItem.setText(bundle.getString("Searchmonkey.exportAsJsonMenuItem.text")); // NOI18N
        exportAsJsonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAsJsonMenuItem_ActionPerformed(evt);
            }
        });
        exportMenu.add(exportAsJsonMenuItem);

        fileMenu.add(exportMenu);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText(bundle.getString("Searchmonkey.exitMenuItem.text")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText(bundle.getString("Searchmonkey.editMenu.text")); // NOI18N

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText(bundle.getString("Searchmonkey.cutMenuItem.text")); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText(bundle.getString("Searchmonkey.copyMenuItem.text")); // NOI18N
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText(bundle.getString("Searchmonkey.pasteMenuItem.text")); // NOI18N
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText(bundle.getString("Searchmonkey.deleteMenuItem.text")); // NOI18N
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        toolMenu.setText(bundle.getString("Searchmonkey.toolMenu.text")); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setMnemonic('T');
        jMenuItem1.setText(bundle.getString("Searchmonkey.jMenuItem1.text")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        toolMenu.add(jMenuItem1);

        menuBar.add(toolMenu);

        viewMenu.setText(bundle.getString("Searchmonkey.viewMenu.text")); // NOI18N

        showStatusbar.setSelected(true);
        showStatusbar.setText(bundle.getString("Searchmonkey.showStatusbar.text")); // NOI18N
        showStatusbar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                showStatusbarStateChanged(evt);
            }
        });
        viewMenu.add(showStatusbar);

        showToolbar.setText(bundle.getString("Searchmonkey.showToolbar.text")); // NOI18N
        showToolbar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                showToolbarStateChanged(evt);
            }
        });
        viewMenu.add(showToolbar);
        viewMenu.add(jSeparator2);

        jLayoutMenu.setText(bundle.getString("Searchmonkey.jLayoutMenu.text")); // NOI18N

        buttonGroup1.add(jLeftToRight);
        jLeftToRight.setSelected(true);
        jLeftToRight.setText(bundle.getString("Searchmonkey.jLeftToRight.text")); // NOI18N
        jLeftToRight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jLeftToRightStateChanged(evt);
            }
        });
        jLayoutMenu.add(jLeftToRight);

        buttonGroup1.add(jRightToLeft);
        jRightToLeft.setText(bundle.getString("Searchmonkey.jRightToLeft.text")); // NOI18N
        jRightToLeft.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRightToLeftStateChanged(evt);
            }
        });
        jLayoutMenu.add(jRightToLeft);

        viewMenu.add(jLayoutMenu);

        jColoumnsMenu.setText(bundle.getString("Searchmonkey.jColoumnsMenu.text")); // NOI18N
        viewMenu.add(jColoumnsMenu);

        menuBar.add(viewMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText(bundle.getString("Searchmonkey.helpMenu.text")); // NOI18N

        contentMenuItem.setMnemonic('c');
        contentMenuItem.setText(bundle.getString("Searchmonkey.contentMenuItem.text")); // NOI18N
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText(bundle.getString("Searchmonkey.aboutMenuItem.text")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        CloseApp();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JOptionPane opt = new JOptionPane(new About(), JOptionPane.PLAIN_MESSAGE);
        // opt.setMessage();
        opt.setOptions(new Object[]{"Ok"});
        opt.setPreferredSize(new Dimension(550, 750));
        JDialog dlg = opt.createDialog((JFrame)this, "About Searchmonkey");
        dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Stop();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        testRegexExpression();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        testRegexExpression();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        Save(FLAG_SAVE_RECT);
    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        Save(FLAG_SAVE_POS);
    }//GEN-LAST:event_formComponentMoved

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY))
        {
            Save(FLAG_SAVE_DIV_RESULTS);
        }
    }//GEN-LAST:event_jSplitPane1PropertyChange

    static private final int FLAG_SAVE_RECT = 0x01;
    static private final int FLAG_SAVE_POS = 0x02;
    static private final int FLAG_SAVE_DIV_MAIN = 0x04; // Divider position between left and right main panel
    static private final int FLAG_SAVE_DIV_RESULTS = 0x08; // Divider position between top and bottom results
    static private final int FLAG_SAVE_SEARCH = 0x10; // Save the search panel options (i.e. before closing)
    static private final int FLAG_SAVE_RESULTS = 0x20; // Save the search results
    static private final int FLAG_SAVE_MENU = 0x40; // Save the menu options
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (evt.getID() == WindowEvent.WINDOW_CLOSING)
        {
            CloseApp();
        }
    }//GEN-LAST:event_formWindowClosing

    public void CloseApp()
    {
        // Todo - check for any 
        this.Stop(); // Stop/cancel search if in progress
        Save(FLAG_SAVE_SEARCH | FLAG_SAVE_RESULTS | FLAG_SAVE_MENU);
        System.exit(0);
    }
    
    private void jSplitPane2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane2PropertyChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY))
        {
            Save(FLAG_SAVE_DIV_MAIN);
        }
    }//GEN-LAST:event_jSplitPane2PropertyChange

    private void showStatusbarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_showStatusbarStateChanged
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)evt.getSource();
        searchSummary2.setVisible(item.getState());
    }//GEN-LAST:event_showStatusbarStateChanged

    private void showToolbarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_showToolbarStateChanged
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)evt.getSource();
        jToolBar1.setVisible(item.getState());
    }//GEN-LAST:event_showToolbarStateChanged

    private void jLeftToRightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jLeftToRightStateChanged
        setLayout(jRightToLeft.isSelected());
    }//GEN-LAST:event_jLeftToRightStateChanged

    private void jRightToLeftStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRightToLeftStateChanged
        setLayout(jRightToLeft.isSelected());
    }//GEN-LAST:event_jRightToLeftStateChanged

    private void exportAsCsvMenuItem_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAsCsvMenuItem_ActionPerformed
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedFile( new File( "export.csv" ) );
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            searchResultsTable1.exportToCSV( fileChooser.getSelectedFile().getAbsolutePath() );
            JOptionPane.showMessageDialog( this, "Results Exported!" );
        }
    }//GEN-LAST:event_exportAsCsvMenuItem_ActionPerformed

    private void exportAsJsonMenuItem_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAsJsonMenuItem_ActionPerformed
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedFile( new File( "export.json" ) );
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            searchResultsTable1.exportToJSON(fileChooser.getSelectedFile().getAbsolutePath() );
            JOptionPane.showMessageDialog( this, "Results Exported!" );
        }
    }//GEN-LAST:event_exportAsJsonMenuItem_ActionPerformed

    private void setLayout(boolean rightToLeft)
    {
        // Todo - do more if we can to help the RTL layout
        if (rightToLeft)
        {
            jSplitPane2.setLeftComponent(this.jSplitPane1);
            jSplitPane2.setRightComponent(this.searchEntryPanel1);
        } else {
            jSplitPane2.setLeftComponent(this.searchEntryPanel1);
            jSplitPane2.setRightComponent(this.jSplitPane1);
        }
    }
    
    private void Save(int flag) throws SecurityException
    {
        if (restoreInProgress) return;
        // In normal mode
        int eState = this.getExtendedState();
        if (eState == Frame.NORMAL)
        {
            Dimension sz = getSize();
            Point pt = getLocationOnScreen();
        
            if ((flag & FLAG_SAVE_RECT) == FLAG_SAVE_RECT)
            {
                prefs.putInt("Height", sz.height);
                prefs.putInt("Width", sz.width);
            }
            if ((flag & FLAG_SAVE_POS) == FLAG_SAVE_POS)
            {
                prefs.putInt("x", pt.x);
                prefs.putInt("y", pt.y);        
            }
        }
        
        // Get results main panel orientation and location
        if ((flag & FLAG_SAVE_DIV_MAIN) == FLAG_SAVE_DIV_MAIN)
        {
            boolean isHoriz = (jSplitPane2.getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
            prefs.putBoolean("div_main_horiz", isHoriz);
            if (isHoriz)
            {
                prefs.putInt("div_main_hpos", jSplitPane2.getDividerLocation());
            } else {
                prefs.putInt("div_main_vpos", jSplitPane2.getDividerLocation());
            }
        }

        // Get results split panel orientation and location
        if ((flag & FLAG_SAVE_DIV_RESULTS) == FLAG_SAVE_DIV_RESULTS)
        {
            boolean isHoriz = (jSplitPane1.getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
            prefs.putBoolean("div_horiz", isHoriz);
            if (isHoriz)
            {
                prefs.putInt("div_hpos", jSplitPane1.getDividerLocation());
            } else {
                prefs.putInt("div_vpos", jSplitPane1.getDividerLocation());
            }
        }
        
        // Save the search panel
        if ((flag & FLAG_SAVE_SEARCH) == FLAG_SAVE_SEARCH)
        {
            searchEntryPanel1.Save();
        }

        // Save the results panel (column order, visibility)
        if ((flag & FLAG_SAVE_RESULTS) == FLAG_SAVE_RESULTS)
        {
            this.searchResultsTable1.Save();
        }

        // Get results main panel orientation and location
        if ((flag & FLAG_SAVE_MENU) == FLAG_SAVE_MENU)
        {
            prefs.putBoolean("viewStatusBar", showStatusbar.isSelected());
            prefs.putBoolean("viewToolBar", showToolbar.isSelected());
            prefs.putBoolean("viewRightToLeft", jRightToLeft.isSelected());
        }

    }
    
    boolean restoreInProgress = false;
    private void Restore() throws SecurityException
    {
        restoreInProgress = true;
        
        showStatusbar.setSelected(prefs.getBoolean("viewStatusBar", true));
        showToolbar.setSelected(prefs.getBoolean("viewToolBar", false));
        jRightToLeft.setSelected(prefs.getBoolean("viewRightToLeft", false));

        
        // Restore last position
        int h = prefs.getInt("Height", -1);
        int w = prefs.getInt("Width", -1);
        int x1 = prefs.getInt("x", -1);
        int y1 = prefs.getInt("y", -1);
        if (x1 != -1 && y1 != -1) {
            setLocation(x1, y1);
        }
        if (h != -1 && w != -1) {
            setSize(w, h);
        }

        boolean isHoriz;
        int pos;

        // Get results split panel orientation and location
        isHoriz = prefs.getBoolean("div_main_horiz", true); // default to horizontal
        if (isHoriz)
        {
            pos = prefs.getInt("div_main_hpos", 250);
       } else {
            pos = prefs.getInt("div_main_vpos", 250);
        }
        jSplitPane2.setOrientation(isHoriz ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setDividerLocation(pos);

        // Get results split panel orientation and location
        isHoriz = prefs.getBoolean("div_horiz", false); // default to vertical
        if (isHoriz)
        {
            pos = prefs.getInt("div_hpos", 250);
        } else {
            pos = prefs.getInt("div_vpos", 250);
        }
        jSplitPane1.setOrientation(isHoriz ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setDividerLocation(pos);

        // Do this later to allow time for the GUI to process the
        // restore commands without saving..
        SwingUtilities.invokeLater(() -> {
            restoreInProgress = false;
        });
        
    }

    private void testRegexExpression()
    {
        JOptionPane optPanel = new JOptionPane();
        // optPanel.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        
        int flags = this.searchEntryPanel1.getRegexFlags(true);
        TestExpression panel = new TestExpression(flags, "Contains");
        optPanel.setMessage(panel);
        JDialog frame = optPanel.createDialog(this, "Test Regular Expression");
        frame.setLocationRelativeTo(this);
        frame.setMinimumSize(new Dimension(800,400));
        frame.setMaximumSize(new Dimension(65535,65535));
        // frame.setSize(800, 400); // From defaults
        frame.setResizable(true);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        
        frame.setVisible(true);       
        panel.Save();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString()); // Speed up the resize time
        // java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
        //PlatformLogger platformLogger = PlatformLogger.getLogger("java.util.prefs");
        //platformLogger.setLevel(PlatformLogger.Level.SEVERE);
        // java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Searchmonkey.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        setDefaultLookAndFeelDecorated(true); // Speed up the resize time
        
        // Fix a bug in the Nimbus look and feel
        UIManager.put("TextPane[Enabled].backgroundPainter",
                (Painter<JComponent>)(Graphics2D g, JComponent comp, int width1, int height1) -> {
            if (comp.isOpaque()) {
                g.setColor(comp.getBackground());
                g.fillRect(0, 0, width1, height1);
            } 
        });         
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Searchmonkey s = new Searchmonkey();
            s.addActionListeners();
            s.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exportAsCsvMenuItem;
    private javax.swing.JMenuItem exportAsJsonMenuItem;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenu jColoumnsMenu;
    private javax.swing.JMenu jLayoutMenu;
    private javax.swing.JRadioButtonMenuItem jLeftToRight;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButtonMenuItem jRightToLeft;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private com.embeddediq.searchmonkey.SearchEntryPanel searchEntryPanel1;
    private com.embeddediq.searchmonkey.SearchMatchView searchMatchView1;
    private com.embeddediq.searchmonkey.SearchResultsTable searchResultsTable1;
    private com.embeddediq.searchmonkey.SearchSummaryPanel searchSummary2;
    private javax.swing.JCheckBoxMenuItem showStatusbar;
    private javax.swing.JCheckBoxMenuItem showToolbar;
    private javax.swing.JMenu toolMenu;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    /**
     * This callback occurs when user clicks start/stop on the entry panel
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Class c = ae.getSource().getClass();
        //if (c.equals(Timer.class))
        //{
//            // Update the contents
//            SearchResult []rows = searchResultsTable1.getSelectedRows();
//            UpdateContent(rows);            
//        }
        //else 
        if (c.equals(SearchEntryPanel.class))
        {
            
            String command = ae.getActionCommand();
            if (command.equals("Start"))
            {
                Start();
            } else if (command.equals("Stop"))
            {
                Stop();
            }
                
        }
    }

    //Timer timer;
    
    /**
     * This callback occurs when user clicks on one (or more) rows on the results table
     * @param lse
     */
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getValueIsAdjusting()) {
            return;
        }

        // Update the contents
        SearchResult []rows = searchResultsTable1.getSelectedRows();
        searchSummary2.SetSelected(String.format("Selected: %d match%s", rows.length, rows.length != 1 ? "es" : ""));
        // timer.start();
        UpdateContent(rows);
    }

    /**
     * This callback occurs when the search starts, stops or has a progress update
     * @param pce
     */
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        String pn = pce.getPropertyName();
        if (pn.equals("state"))
        {
            StateValue sv = (StateValue)pce.getNewValue();
            if (sv.equals(StateValue.STARTED))
            {
                searchSummary2.ShowProgress(true);
                searchSummary2.SetProgress("Searching");
                searchSummary2.SetSearched("");
            }
            else if (sv.equals(StateValue.DONE))
            {
                searchSummary2.ShowProgress(false);
                if (searchTask.isCancelled()) {
                    searchSummary2.SetStatus("Cancelled!");
                    searchMatchView1.UpdateSummary();
                } else {
                    try {
                        SearchSummary ss = searchTask.get();
                        searchSummary2.SetStatus("Done");
                        searchSummary2.SetSearched(String.format("Found: %d match%s (%d seconds)", ss.matchFileCount, ss.matchFileCount != 1 ? "es" : "", (ss.endTime - ss.startTime)/1000000000));
                        searchMatchView1.UpdateSummary(ss, false);
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(Searchmonkey.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                Done();
            }
        }
        else if (pn.equals("match"))
        {
            SearchSummary ss = (SearchSummary)pce.getNewValue();
            searchSummary2.SetSearched(String.format("Found: %d match%s",  ss.matchFileCount,  ss.matchFileCount != 1 ? "es" : ""));
            searchMatchView1.UpdateSummary(ss, true);
        }
    }

}
