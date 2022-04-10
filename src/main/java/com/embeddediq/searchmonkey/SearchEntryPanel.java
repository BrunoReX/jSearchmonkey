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
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

// Note to self - here is the NIMBUS Default Look and Feel
// https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary

/**
 * @author cottr
 */
public class SearchEntryPanel extends javax.swing.JPanel {

    public static final Gson GSON = new Gson();
    String lastItem = null;
    JSpinner popup_link;
    private final ResourceBundle rb;

    /**
     * Creates new form SearchEntryPanel
     */
    public SearchEntryPanel() {
        prefs = Preferences.userNodeForPackage(SearchEntry.class);
        
        initComponents();
        
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
        jFileNameRegexPanel.setVisible( false ); // hide the regex view
        jContainingPlainPanel.setVisible( false ); // hide the context word search

        // Check for OS dependent settings:-
        if (IS_OS_WINDOWS)
        {
            jIgnoreHiddenFolders.setVisible(false);
            jIgnoreHiddenFolders.setSelected(false);
        }

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement(".git");
        model.addElement(".hg");
        model.addElement(".svn");
        jExcludedPaths.setModel( model );
        

        String xx;
        
        // Add a browse button to the jCombobox
        xx = rb.getString(RunDialogMessages.BROWSE.getKey());
        jLookIn.setModel(new SeparatorComboBoxModel(System.getProperty("user.home"), xx));
        jLookIn.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.FOLDER.getKey());
        AdvancedDialog sd_folder = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jLookIn, xx);
        jLookIn.addActionListener(new SeparatorComboBoxListener(jLookIn, sd_folder));

        // Creating a custom class for the JComboBox
        xx = rb.getString(RunDialogMessages.OTHER.getKey());
        jFileTypeCombo.setModel(new SeparatorComboBoxModel(new FileTypeEntry(), xx));
        jFileTypeCombo.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.FILETYPE.getKey());
        AdvancedDialog sd_not_time = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jFileTypeCombo, xx, new FileTypePanel());
        jFileTypeCombo.addActionListener(new SeparatorComboBoxListener(jFileTypeCombo, sd_not_time));
        
        xx = rb.getString(RunDialogMessages.OTHER.getKey());
        jFilesizeCombo.setModel(new SeparatorComboBoxModel(new FileSizeEntry(), xx));
        jFilesizeCombo.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.FILESIZE.getKey());
        AdvancedDialog sd_not_time1 = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jFilesizeCombo, xx, new FileSizePanel());
        jFilesizeCombo.addActionListener(new SeparatorComboBoxListener(jFilesizeCombo, sd_not_time1));

        // Creating a custom class for the JComboBox
        xx = rb.getString(RunDialogMessages.OTHER.getKey());
        jCreatedCombo.setModel(new SeparatorComboBoxModel(new FileDateEntry(), xx));
        jCreatedCombo.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.CREATED.getKey());
        AdvancedDialog sd3 = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jCreatedCombo, xx, new FileDatePanel());
        jCreatedCombo.addActionListener(new SeparatorComboBoxListener(jCreatedCombo, sd3));
        // Creating a custom class for the JComboBox
        xx = rb.getString(RunDialogMessages.OTHER.getKey());
        jModifiedCombo.setModel(new SeparatorComboBoxModel(new FileDateEntry(), xx));
        jModifiedCombo.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.MODIFIED.getKey());
        AdvancedDialog sd1 = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jModifiedCombo, xx, new FileDatePanel());
        jModifiedCombo.addActionListener(new SeparatorComboBoxListener(jModifiedCombo, sd1));
        // Creating a custom class for the JComboBox
        xx = rb.getString(RunDialogMessages.OTHER.getKey());
        jAccessedCombo.setModel(new SeparatorComboBoxModel(new FileDateEntry(), xx));
        jAccessedCombo.setRenderer(new SeparatorComboBoxRenderer());
        xx = rb.getString(RunDialogMessages.ACCESSED.getKey());
        AdvancedDialog sd = new AdvancedDialog((Frame)SwingUtilities.getWindowAncestor(this), jAccessedCombo, xx, new FileDatePanel());
        jAccessedCombo.addActionListener(new SeparatorComboBoxListener(jAccessedCombo, sd));
                
        // Restore the settings
        Restore();
     
       
        // TODO - future stuff
        // this.jExpertMode.setVisible(false);
        //this.jButton7.setVisible(false);
        //this.jButton8.setVisible(false);
        //this.jButton10.setVisible(false);

        //this.jButton4.setVisible(false);
        //this.jButton5.setVisible(false);
        //this.jButton9.setVisible(false);
    }
    
    public int getRegexFlags(boolean useContent)
    {
        int flags = 0;
        if (useContent)
        {
            if (jIgnoreContentCase.isSelected())
                flags |= Pattern.CASE_INSENSITIVE;
        } else {
            if (this.jIgnoreFilenameCase.isSelected())
                flags |= Pattern.CASE_INSENSITIVE;
        }
        //flags |= Pattern.CASE_INSENSITIVE;
        //flags |= Pattern.DOTALL;
        //flags |= Pattern.UNICODE_CASE;
        //flags |= Pattern.UNICODE_CASE;                    
        return flags;
    }
    public void setRegexFlags(boolean useContent, int flags)
    {
        if (useContent)
        {
            jIgnoreContentCase.setSelected((flags & Pattern.CASE_INSENSITIVE) != 0);
        } else {
            jIgnoreFilenameCase.setSelected((flags & Pattern.CASE_INSENSITIVE) != 0);
        }
        //flags |= Pattern.CASE_INSENSITIVE;
        //flags |= Pattern.DOTALL;
        //flags |= Pattern.UNICODE_CASE;
        //flags |= Pattern.UNICODE_CASE;                    
    }
    
    class SeparatorComboBoxModel extends DefaultComboBoxModel
    {
        private final int firstEntry;

        SeparatorComboBoxModel(Object fixedEntry, String button)
        {
            super(new Object[] {
                new JSeparator(JSeparator.HORIZONTAL),
                new JButton(button)});
            if (fixedEntry != null) {
                super.insertElementAt(fixedEntry, 0);
            }
            firstEntry = (fixedEntry != null) ? 1 : 0;
        }

        SeparatorComboBoxModel(String button)
        {
            this(null, button);
        }
        
        // Replace all entries with this new list
        public void setEntries(List<Object> items)
        {
            int count = super.getSize();
            if (count > (2 + firstEntry))
            {
                for (int i=count-3; i>=firstEntry; i--)
                {
                    super.removeElementAt(i);
                }
            }
            for (int i = items.size(); --i >= 0;)
            {
                // Skip the first entry
                super.insertElementAt(items.get(i), firstEntry);
            }
        }
        
        public List<Object> getEntries()
        {
            int count = super.getSize();
            if (count <= (2 + firstEntry)) return new ArrayList<>(); // Empty list
            
            List<Object> items = new ArrayList<>(count - 2 - firstEntry);
            for (int i=firstEntry; i<count-2; i++) {
                items.add(super.getElementAt(i));
            }
            return items;
        }

        // Remove entries that are more than limit
        // Excluding the first entry (Don't care)
        // Excluding the last two entries (Button + separator)
        public void limitEntries(int limit)
        {
            // Ignore first and last two entries
            int count = super.getSize();
            if (count > (limit + 2 + firstEntry))
            {
                for (int i=(count - 3); i>=(firstEntry + limit); i--)
                {
                    super.removeElementAt(i);
                }
            }
        }
        
        // Remove entries that are more than limit
        // Excluding the first entry (Don't care)
        // Excluding the last two entries (Button + separator)
        public void addOrUpdateEntry(Object val)
        {
            int idx = super.getIndexOf(val);
            if ((idx >= 0) && (idx < firstEntry)) return; // Ignore the first entry
            if (idx != -1)
            {
                super.removeElementAt(idx);
            }
            // Skip first entry
            super.insertElementAt(val, firstEntry);
        }

    }
    
    class SeparatorComboBoxRenderer extends DefaultListCellRenderer // // JLabel implements ListCellRenderer // BasicComboBoxRenderer
    {
        public SeparatorComboBoxRenderer() {
            super();
            setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent( JList list,
               Object value, int index, boolean isSelected, boolean cellHasFocus) {

            try
            {
                if (isSelected)
                {
                    setBackground(new Color(UIManager.getLookAndFeelDefaults().getColor("ComboBox:\"ComboBox.listRenderer\"[Selected].background").getRGB()));
                    setForeground(new Color(UIManager.getLookAndFeelDefaults().getColor("ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground").getRGB()));
                } else {
                    setBackground(new Color(UIManager.getLookAndFeelDefaults().getColor("ComboBox:\"ComboBox.listRenderer\".background").getRGB()));
                    setForeground(new Color(UIManager.getLookAndFeelDefaults().getColor("ComboBox.foreground").getRGB()));
                }
            } catch (Exception ioe)
            {
                Logger.getLogger(Searchmonkey.class.getName()).log(Level.WARNING, null, ioe);
            }

            if (value instanceof Icon) {
               setIcon((Icon)value);
            }
            if (value instanceof JSeparator) {
               return (Component) value;
            }

            String txt;
            if (value instanceof JButton) {
                txt = ((JButton)value).getText();
            } else {
                txt = (value == null) ? "" : value.toString();
            }

            setText(txt);
            return this;
      } 
    }
    
    class SeparatorComboBoxListener implements ActionListener {
        JComboBox combobox;
        Object oldItem;
        Object oldValue;
        // Runnable callback;
        AdvancedDialog callback;

        SeparatorComboBoxListener(JComboBox combobox, AdvancedDialog callback) { // Frame parent, JComboBox combobox, String msg) {
            this.combobox = combobox;
            this.callback = callback;
            combobox.setSelectedIndex(0);
            oldItem = combobox.getModel().getSelectedItem();
            oldValue = oldItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object selectedItem = combobox.getModel().getSelectedItem();
            if (selectedItem instanceof JButton)
            {
               combobox.setSelectedItem(oldItem);
               this.callback.setData(oldValue);
               SwingUtilities.invokeLater(callback);
            }    
            else if (selectedItem instanceof JSeparator)
            {
               combobox.setSelectedItem(oldItem);
            }
            else 
            {
                oldValue = selectedItem;
                if (combobox.getSelectedIndex() != -1)
                {
                    oldItem = selectedItem;
                }
            }
        }
    }
    
    int maxCombo = 10;
    private String getSelectedItem(JComboBox jCombo)
    {
        String val = (String)jCombo.getEditor().getItem();
        if (val != null && val.length() > 0)
        {
            DefaultComboBoxModel model = (DefaultComboBoxModel)jCombo.getModel();
            int idx = model.getIndexOf(val);
            jCombo.setSelectedIndex(-1); // Deselect all
            
            if (idx != -1)
            {
                model.removeElementAt(idx);
            }
            jCombo.insertItemAt(val, 0);
            idx = jCombo.getItemCount();
            if (idx > 1 && jCombo.getItemAt(idx - 1).toString().startsWith("<<"))
            {
                idx --;
            }
            if (idx > maxCombo)
            {
                jCombo.removeItemAt(idx - 1);
            }
            jCombo.setSelectedItem(val); // Reselect item
        }
        return val;
    }

    private Object getSelectedItem2(JComboBox jCombo)
    {
        SeparatorComboBoxModel model = (SeparatorComboBoxModel)jCombo.getModel();
        Object val = model.getSelectedItem();
        if (val != null)
        {

            // Update the combobox or refresh the ordering
            jCombo.setSelectedIndex(-1); // Deselect all
            model.addOrUpdateEntry(val);
            model.limitEntries(maxCombo);
            jCombo.setSelectedItem(val); // Reselect item
        }
        return val;
    }
    
    public SearchEntry getSearchRequest() {
        long longVal;
        int intVal;
        SearchEntry req = new SearchEntry();
        String strItem;

        // Set flags from the options tab
        req.flags.usePowerSearch = jExpertMode.isSelected();
        req.flags.disablePlugins = jDisable3rdParty.isSelected();
        req.flags.disableUnicodeDetection = jDisableUnicodeDetection.isSelected();
        
        req.flags.useFilenameRegex = jUseFileRegex.isSelected();
        req.flags.ignoreHiddenFiles = jIgnoreHiddenFiles.isSelected();
        req.flags.ignoreSymbolicLinks = jIgnoreSymbolicLinks.isSelected();
        req.flags.skipBinaryFiles = jSkipBinaryFiles.isSelected();
        req.flags.ignoreFilenameCase = jIgnoreFilenameCase.isSelected();
        intVal = 0;
        if (jEnableFileTimeout.isSelected()) {
            intVal = (int)jFileTimeout.getValue();
        }
        req.FileTimeout = 1000*1000*intVal; // Convert ms -> ns
        
        req.flags.strictFilenameChecks = jStrictFilenameSearch.isSelected();
        req.flags.useContentRegex = jUseContentRegex.isSelected();
        req.flags.ignoreContentCase = jIgnoreContentCase.isSelected();
        longVal = 0L;
        if (jLimitMaxHits.isSelected()) {
            longVal = (Long)jMaxHits.getValue();
        }
        req.maxHits = longVal;
        
        req.flags.ignoreFolderCase = jIgnoreFolderCase.isSelected();
        req.flags.ignoreHiddenFolders = jIgnoreHiddenFolders.isSelected() && jIgnoreHiddenFolders.isVisible(); // unless hidden

        // Get look in folder
        req.lookIn = new ArrayList<>();
        Object folder = getSelectedItem2(jLookIn);
        if (folder instanceof String) // .getClass().equals(File.class))
        {
            strItem = (String) folder;
            req.lookIn.add(Paths.get(strItem));
        }
        else if (folder.getClass().equals(List.class))
        { // Check for list
            for (String item: (List<String>)folder)
            {
                req.lookIn.add(Paths.get(item));
            }
        } else { // Unsupported
            System.out.println("Error! Unsupported class type..");
        }
        req.lookInSubFolders = jSubFolders.isSelected();
        
        // Get filename
        strItem = getSelectedItem( jUseFileRegex.isSelected() ? jFileNameRegexCombo : jFileNameGlobCombo );
        req.fileNameText = strItem;

        // Get containing text
        if ( jContainingRegexCheckBox.isSelected() || jContainingPlainCheckBox.isSelected() ) {
            strItem = getSelectedItem( jUseContentRegex.isSelected() ? jContainingRegexCombo : jContainingPlainCombo );
            if ( strItem.length() > 0 ) // Is there a content match to make?
            {
                req.containingText = strItem;
            }
        }
        
//        // TODO - switch config to using FileSizeEntry directly
        Object fsize = getSelectedItem2(jFilesizeCombo);
        if (fsize != null)
        {
            FileSizeEntry entry = (FileSizeEntry)fsize;
            if (entry.useMinSize)
            {
                req.greaterThan = entry.minSize;
            }
            if (entry.useMaxSize)
            {
                req.lessThan = entry.maxSize;
            }
        }

        Object ftype = getSelectedItem2(jFileTypeCombo);
        if (ftype != null)
        {
            req.mime = new FileTypeEntry(ftype);
        }
        
                
        Object modified = getSelectedItem2(jModifiedCombo);
        if (modified != null)
        {
            FileDateEntry entry = (FileDateEntry)modified;
            if (entry.useAfter)
            {
                req.modifiedAfter = FileTime.from(entry.after.toInstant(ZoneOffset.UTC));
            }
            if (entry.useBefore)
            {
                req.modifiedBefore = FileTime.from(entry.before.toInstant(ZoneOffset.UTC));
            }
        }

        Object accessed = getSelectedItem2(jAccessedCombo);
        if (accessed != null)
        {
            FileDateEntry entry = (FileDateEntry)accessed;
            if (entry.useAfter)
            {
                req.accessedAfter = FileTime.from(entry.after.toInstant(ZoneOffset.UTC));
            }
            if (entry.useBefore)
            {
                req.accessedBefore = FileTime.from(entry.before.toInstant(ZoneOffset.UTC));
            }
        }

        Object created = getSelectedItem2(jCreatedCombo);
        if (created != null)
        {
            FileDateEntry entry = (FileDateEntry)created;
            if (entry.useAfter)
            {
                req.createdAfter = FileTime.from(entry.after.toInstant(ZoneOffset.UTC));
            }
            if (entry.useBefore)
            {
                req.createdBefore = FileTime.from(entry.before.toInstant(ZoneOffset.UTC));
            }
        }

        ListModel<String> excludedPaths = jExcludedPaths.getModel();

        for ( int f = 0; f < excludedPaths.getSize(); f++ ) {
            String excludedPath = excludedPaths.getElementAt( f );
            req.ignoreFolderSet.add( excludedPath );
        }

        return req;
    }
        
    private void Save(String name, JComboBox jCombo) throws SecurityException
    {
        Gson g = new Gson();
        List<String> items = new ArrayList<>();
        for (int i=0; i<jCombo.getItemCount(); i++)
        {
            String item = (String)jCombo.getItemAt(i);
            if (item != null && !item.startsWith("<<")) {
                items.add(item);
            }
        }
        String json = g.toJson(items);
        prefs.put(name, json); // Add list of look in folders        

        // Store the index as well
        int idx = jCombo.getSelectedIndex();
        prefs.putInt(name + ".idx", idx);
    }
    private void Save2(String name, JComboBox jCombo) throws SecurityException
    {
        Gson g = new Gson();
        SeparatorComboBoxModel model = (SeparatorComboBoxModel)jCombo.getModel();
        
        Object item = model.getSelectedItem();
        if (item != null && model.getIndexOf(item) == -1) // If item is not in list, then add
        {
            model.addOrUpdateEntry(model.getSelectedItem()); // Lock in the current item before saving
            jCombo.setSelectedItem(item); // And select it
        }
        List<Object> items = model.getEntries();
        String json = g.toJson(items);
        prefs.put(name, json); // Add list of look in folders        

        // Store the index as well
        int idx = jCombo.getSelectedIndex();
        prefs.putInt(name + ".idx", idx);
    }

    private <T> void Save( String name, JList<T> jCombo ) throws SecurityException {
        DefaultListModel<T> model = (DefaultListModel<T>) jCombo.getModel();

        ArrayList<T> items = new ArrayList<>();
        for(int i = 0; i < model.getSize(); i++){
            items.add( model.get( i ) );
        }

        String json = GSON.toJson( items );
        prefs.put( name, json );

        // Store the index as well
        int idx = jCombo.getSelectedIndex();
        prefs.putInt( name + ".idx", idx );
    }

    //    private void Save(String name, JSpinner jSpinner) throws SecurityException
    //    {
    //        Gson g = new Gson();
    //        Object val = jSpinner.getValue();
    //        String json = g.toJson(val);
    //        prefs.put(name, json); // Add list of look in folders
    //    }
    private final Preferences prefs;

    private <T> void Restore( String name, JList<T> jList, T[] def ) {

        Gson g = new Gson();
        String json = prefs.get( name, GSON.toJson( def ) );
        ArrayList<T> items = g.fromJson( json, new TypeToken<ArrayList<T>>() {}.getType() );
        DefaultListModel<T> model = new DefaultListModel<>();

        for ( int i = 0; i < items.size(); i++ ) {
            model.add( i, items.get( i ) );
        }

        jList.setModel( model );

        if ( model.getSize() > 0 ) {
            int idx = prefs.getInt( name + ".idx", 0 );
            jList.setSelectedIndex( idx ); // Select last item
        }
    }

    private void Restore(String name, JComboBox jCombo, Object def)
    {
        int count = jCombo.getItemCount();
        if ((count > 0) && (jCombo.getItemAt(count - 1).toString().startsWith("<<")))
        {
            count --; // Special case e.g. <<Browse>> or <<Other>>
        }
        for (int i=0; i<count; i++) {
            jCombo.removeItemAt(0);
        }
        Gson g = new Gson();
        String json = prefs.get(name, g.toJson(def));
        List<Object> items = g.fromJson(json, new TypeToken<ArrayList<Object>>() {}.getType());
        for (int i = items.size(); --i >= 0;)
        {
            jCombo.insertItemAt(items.get(i), 0);
        }
     
        if (jCombo.getModel().getSize() > 0)
        {
            int idx = prefs.getInt(name + ".idx", 0);
            jCombo.setSelectedIndex(idx); // Select last item
        }
    }
    
    private void Restore2(String name, JComboBox jCombo, Object[] def)
    {
        // int count = ;
        jCombo.setSelectedIndex(-1); // Select none
        Gson g = new Gson();
        String json = prefs.get(name, g.toJson(def));
        {
            List<Object> items;
            try{
                if (def instanceof FileDateEntry[]) {
                    items = g.fromJson(json, new TypeToken<ArrayList<FileDateEntry>>() {}.getType());
                } else if (def instanceof FileSizeEntry[]) {
                    items = g.fromJson(json, new TypeToken<ArrayList<FileSizeEntry>>() {}.getType());
                } else if (def instanceof FileTypeEntry[]) {
                    items = g.fromJson(json, new TypeToken<ArrayList<FileTypeEntry>>() {}.getType());
                } else { //if (def instanceof String[]) {
                    items = g.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
                }
            } catch (JsonSyntaxException ex)
            {
                items = new ArrayList<>();
            }

            SeparatorComboBoxModel model = (SeparatorComboBoxModel)jCombo.getModel();
            model.setEntries(items);
        }
        
        int idx = prefs.getInt(name + ".idx", 0);
        jCombo.setSelectedIndex(idx); // Select last item
    }    
    
//    private void Restore(String name, JSpinner jSpinner, Object def) throws SecurityException
//    {
//        Gson g = new Gson();
//        String json = prefs.get(name, g.toJson(def)); // Add list of look in folders        
//        Object val = g.fromJson(json, def.getClass());
//        jSpinner.setValue(val);
//    }

    public void Save() throws SecurityException
    {
        Save2( "LookIn", jLookIn );
        Save( "FileName", jFileNameGlobCombo );
        Save( "FileNameRegex", jFileNameRegexCombo );
        prefs.putBoolean( "ContainingRegexTextToggle", jContainingRegexCheckBox.isSelected() );
        Save( "ContainingRegexText", jContainingRegexCombo );
        prefs.putBoolean( "ContainingTextToggle", jContainingPlainCheckBox.isSelected() );
        Save( "ContainingText", jContainingPlainCombo );
        
        prefs.putBoolean("LookInSubFolders", jSubFolders.isSelected());

        Save2("FileTypeCombo", jFileTypeCombo);
        Save2("FileSizeCombo", jFilesizeCombo);

        Save2("FileModifiedCombo", jModifiedCombo);
        Save2("FileCreatedCombo", jCreatedCombo);
        Save2("FileAccessedCombo", jAccessedCombo);
        
        Save( "ExcludedPaths", jExcludedPaths);

        // Search options
        prefs.putBoolean("UsePowerSeach", jExpertMode.isSelected());
        prefs.putBoolean("Disable3rdParty", jDisable3rdParty.isSelected());
        prefs.putBoolean("DisableUnicodeDetection", jDisableUnicodeDetection.isSelected());
        
        prefs.putBoolean("UseFileRegex", jUseFileRegex.isSelected());
        prefs.putBoolean("IgnoreHiddenFiles", jIgnoreHiddenFiles.isSelected());
        prefs.putBoolean("IgnoreSymbolicLinks", jIgnoreSymbolicLinks.isSelected());
        prefs.putBoolean("SkipBinaryFiles", jSkipBinaryFiles.isSelected());
        prefs.putBoolean("StrictFilenameSearch", jStrictFilenameSearch.isSelected());
        prefs.putBoolean("IgnoreFileCase", jIgnoreFilenameCase.isSelected());
        prefs.putBoolean("EnableFileTimeout", jEnableFileTimeout.isSelected());
        prefs.putInt("FileTimeout", (Integer)jFileTimeout.getValue());

        prefs.putBoolean("UseContentRegex", jUseContentRegex.isSelected());
        prefs.putBoolean("IgnoreContentCase", jIgnoreContentCase.isSelected());
        prefs.putBoolean("LimitMaxHits", jLimitMaxHits.isSelected());
        prefs.putLong("MaxHits", (Long)jMaxHits.getValue());
        
        prefs.putBoolean("IgnoreFolderCase", jIgnoreFolderCase.isSelected());
        prefs.putBoolean("IgnoreHiddenFolders", jIgnoreHiddenFolders.isSelected());
    }
    private void Restore()
    {
        boolean enabled;

        // Basic search params
        Restore( "FileName", jFileNameGlobCombo, new String[] { "*.txt", "*.[c|h]" } );
        Restore( "FileNameRegex", jFileNameRegexCombo, new String[] { ".*txt", ".*[c|h]" } );
        jContainingRegexCheckBox.setSelected( prefs.getBoolean( "ContainingRegexTextToggle", false ) );
        Restore( "ContainingRegexText", jContainingRegexCombo, new String[] {} );
        jContainingPlainCheckBox.setSelected( prefs.getBoolean( "ContainingTextToggle", false ) );
        Restore( "ContainingText", jContainingPlainCombo, new String[] {} );
        Restore2("LookIn", jLookIn, new String[] {});
        jSubFolders.setSelected(prefs.getBoolean("LookInSubFolders", true));
        
        Restore2("FileTypeCombo", jFileTypeCombo, new FileTypeEntry[] {});
        Restore2("FileSizeCombo", jFilesizeCombo, new FileSizeEntry[] {});

        Restore2("FileModifiedCombo", jModifiedCombo, new FileDateEntry[] {}); // Empty list
        Restore2("FileCreatedCombo", jCreatedCombo, new FileDateEntry[] {}); // Empty list
        Restore2("FileAccessedCombo", jAccessedCombo, new FileDateEntry[] {}); // Empty list

        Restore( "ExcludedPaths", jExcludedPaths, new String[] { ".git", ".hg", ".svn" } );

        // Search options
        jExpertMode.setSelected(prefs.getBoolean("UsePowerSearch", true));
        jDisable3rdParty.setSelected(prefs.getBoolean("Disable3rdParty", false));
        jDisableUnicodeDetection.setSelected(prefs.getBoolean("DisableUnicodeDetection", false));

        enabled = prefs.getBoolean("UseFileRegex", false);
        jUseFileRegex.setSelected(enabled);
        jUseFileGlobs.setSelected(!enabled);
        jIgnoreHiddenFiles.setSelected(prefs.getBoolean("IgnoreHiddenFiles", false));
        jIgnoreSymbolicLinks.setSelected(prefs.getBoolean("IgnoreSymbolicLinks", false));
        jSkipBinaryFiles.setSelected(prefs.getBoolean("SkipBinaryFiles", true));
        jStrictFilenameSearch.setSelected(prefs.getBoolean("StrictFilenameSearch", false));
        jIgnoreFilenameCase.setSelected(prefs.getBoolean("IgnoreFileCase", true));
        jEnableFileTimeout.setSelected(prefs.getBoolean("EnableFileTimeout", true));
        jFileTimeout.setValue(prefs.getInt("FileTimeout", 5000)); // limit to 5 seconds per file

        enabled = prefs.getBoolean("UseContentRegex", true);
        jUseContentRegex.setSelected(enabled);
        jUseContentSearch.setSelected(!enabled);
        jIgnoreContentCase.setSelected(prefs.getBoolean("IgnoreContentCase", true));
        jLimitMaxHits.setSelected(prefs.getBoolean("LimitMaxHits", true));
        jMaxHits.setValue(prefs.getLong("MaxHits", 999L)); // limit to 999 hits

        jIgnoreFolderCase.setSelected(prefs.getBoolean("IgnoreFolderCase", true));
        jIgnoreHiddenFolders.setSelected(prefs.getBoolean("IgnoreHiddenFolders", false));
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FilenameSearchType = new javax.swing.ButtonGroup();
        ContentSearchType = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSearch = new javax.swing.JPanel();
        jBasicSearch = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLookIn = new javax.swing.JComboBox<>();
        jSubFolders = new javax.swing.JCheckBox();
        jFileNameGlobPanel = new javax.swing.JPanel();
        jFileNameGlobCombo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jFileNameRegexPanel = new javax.swing.JPanel();
        jFileNameRegexCombo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jContainingRegexPanel = new javax.swing.JPanel();
        jContainingRegexCheckBox = new javax.swing.JCheckBox();
        jContainingRegexCombo = new javax.swing.JComboBox<>();
        jContainingPlainPanel = new javax.swing.JPanel();
        jContainingPlainCombo = new javax.swing.JComboBox<>();
        jContainingPlainCheckBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jFileTypeCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jModifiedCombo = new javax.swing.JComboBox<>();
        jCreatedCombo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jAccessedCombo = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jFilesizeCombo = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jStartButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0));
        jStopButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jOptions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jExpertMode = new javax.swing.JCheckBox();
        jDisable3rdParty = new javax.swing.JCheckBox();
        jDisableUnicodeDetection = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel14 = new javax.swing.JPanel();
        jIgnoreSymbolicLinks = new javax.swing.JCheckBox();
        jIgnoreHiddenFiles = new javax.swing.JCheckBox();
        jUseFileRegex = new javax.swing.JRadioButton();
        jUseFileGlobs = new javax.swing.JRadioButton();
        jSkipBinaryFiles = new javax.swing.JCheckBox();
        jIgnoreFilenameCase = new javax.swing.JCheckBox();
        jEnableFileTimeout = new javax.swing.JCheckBox();
        jFileTimeout = new javax.swing.JSpinner();
        jStrictFilenameSearch = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        jUseContentSearch = new javax.swing.JRadioButton();
        jIgnoreContentCase = new javax.swing.JCheckBox();
        jUseContentRegex = new javax.swing.JRadioButton();
        jLimitMaxHits = new javax.swing.JCheckBox();
        jMaxHits = new javax.swing.JSpinner();
        jPanel16 = new javax.swing.JPanel();
        jIgnoreFolderCase = new javax.swing.JCheckBox();
        jIgnoreHiddenFolders = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jAdd = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jExcludedPaths = new javax.swing.JList<>();
        jRemove = new javax.swing.JButton();
        jEdit = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jRestoreAll = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        jFileChooser1.setApproveButtonText(bundle.getString("SearchEntryPanel.jFileChooser1.approveButtonText")); // NOI18N
        jFileChooser1.setApproveButtonToolTipText(bundle.getString("SearchEntryPanel.jFileChooser1.approveButtonToolTipText")); // NOI18N
        jFileChooser1.setDialogTitle(bundle.getString("SearchEntryPanel.jFileChooser1.dialogTitle")); // NOI18N
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooser1.setToolTipText(bundle.getString("SearchEntryPanel.jFileChooser1.toolTipText")); // NOI18N

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setAutoscrolls(true);

        jSearch.setLayout(new javax.swing.BoxLayout(jSearch, javax.swing.BoxLayout.Y_AXIS));

        jBasicSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jBasicSearch.border.title"))); // NOI18N
        jBasicSearch.setPreferredSize(new java.awt.Dimension(240, 195));

        jLabel2.setLabelFor(jFileNameGlobCombo);
        jLabel2.setText(bundle.getString("SearchEntryPanel.jLabel2.text")); // NOI18N

        jLookIn.setEditable(true);
        jLookIn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jLookIn.setToolTipText(bundle.getString("SearchEntryPanel.jLookIn.toolTipText")); // NOI18N

        jSubFolders.setSelected(true);
        jSubFolders.setText(bundle.getString("SearchEntryPanel.jSubFolders.text")); // NOI18N
        jSubFolders.setToolTipText(bundle.getString("SearchEntryPanel.jSubFolders.toolTipText")); // NOI18N

        jFileNameGlobCombo.setEditable(true);
        jFileNameGlobCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jFileNameGlobCombo.setToolTipText(bundle.getString("SearchEntryPanel.jFileNameGlobCombo.toolTipText")); // NOI18N

        jLabel1.setLabelFor(jFileNameGlobCombo);
        jLabel1.setText(bundle.getString("SearchEntryPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jFileNameGlobPanelLayout = new javax.swing.GroupLayout(jFileNameGlobPanel);
        jFileNameGlobPanel.setLayout(jFileNameGlobPanelLayout);
        jFileNameGlobPanelLayout.setHorizontalGroup(
            jFileNameGlobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileNameGlobPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jFileNameGlobCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jFileNameGlobPanelLayout.setVerticalGroup(
            jFileNameGlobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileNameGlobPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jFileNameGlobPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jFileNameGlobCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jFileNameRegexCombo.setEditable(true);
        jFileNameRegexCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jFileNameRegexCombo.setToolTipText(bundle.getString("SearchEntryPanel.jFileNameRegexCombo.toolTipText")); // NOI18N

        jLabel3.setLabelFor(jFileNameGlobCombo);
        jLabel3.setText(bundle.getString("SearchEntryPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jFileNameRegexPanelLayout = new javax.swing.GroupLayout(jFileNameRegexPanel);
        jFileNameRegexPanel.setLayout(jFileNameRegexPanelLayout);
        jFileNameRegexPanelLayout.setHorizontalGroup(
            jFileNameRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileNameRegexPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jFileNameRegexCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFileNameRegexPanelLayout.setVerticalGroup(
            jFileNameRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileNameRegexPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jFileNameRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jFileNameRegexCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jContainingRegexCheckBox.setText(bundle.getString("SearchEntryPanel.jContainingRegexCheckBox.text_1")); // NOI18N
        jContainingRegexCheckBox.setToolTipText(bundle.getString("SearchEntryPanel.jContainingRegexCheckBox.toolTipText")); // NOI18N
        jContainingRegexCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jContainingRegexCheckBoxItemStateChanged(evt);
            }
        });

        jContainingRegexCombo.setEditable(true);
        jContainingRegexCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jContainingRegexCombo.setToolTipText(bundle.getString("SearchEntryPanel.jContainingRegexCombo.toolTipText")); // NOI18N
        jContainingRegexCombo.setEnabled(false);

        javax.swing.GroupLayout jContainingRegexPanelLayout = new javax.swing.GroupLayout(jContainingRegexPanel);
        jContainingRegexPanel.setLayout(jContainingRegexPanelLayout);
        jContainingRegexPanelLayout.setHorizontalGroup(
            jContainingRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jContainingRegexPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jContainingRegexCheckBox)
                .addGap(18, 18, 18)
                .addComponent(jContainingRegexCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jContainingRegexPanelLayout.setVerticalGroup(
            jContainingRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jContainingRegexPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jContainingRegexPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jContainingRegexCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContainingRegexCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jContainingPlainCombo.setEditable(true);
        jContainingPlainCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jContainingPlainCombo.setToolTipText(bundle.getString("SearchEntryPanel.jContainingPlainCombo.toolTipText")); // NOI18N
        jContainingPlainCombo.setEnabled(false);

        jContainingPlainCheckBox.setText(bundle.getString("SearchEntryPanel.jContainingPlainCheckBox.text")); // NOI18N
        jContainingPlainCheckBox.setToolTipText(bundle.getString("SearchEntryPanel.jContainingPlainCheckBox.toolTipText")); // NOI18N
        jContainingPlainCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jContainingPlainCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jContainingPlainPanelLayout = new javax.swing.GroupLayout(jContainingPlainPanel);
        jContainingPlainPanel.setLayout(jContainingPlainPanelLayout);
        jContainingPlainPanelLayout.setHorizontalGroup(
            jContainingPlainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jContainingPlainPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jContainingPlainCheckBox)
                .addGap(18, 18, 18)
                .addComponent(jContainingPlainCombo, 0, 258, Short.MAX_VALUE))
        );
        jContainingPlainPanelLayout.setVerticalGroup(
            jContainingPlainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jContainingPlainPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jContainingPlainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jContainingPlainCheckBox)
                    .addComponent(jContainingPlainCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jBasicSearchLayout = new javax.swing.GroupLayout(jBasicSearch);
        jBasicSearch.setLayout(jBasicSearchLayout);
        jBasicSearchLayout.setHorizontalGroup(
            jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBasicSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jContainingPlainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jContainingRegexPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFileNameGlobPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFileNameRegexPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jBasicSearchLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLookIn, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSubFolders))
                .addContainerGap())
        );
        jBasicSearchLayout.setVerticalGroup(
            jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBasicSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFileNameGlobPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFileNameRegexPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jContainingRegexPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jContainingPlainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLookIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSubFolders)
                .addContainerGap())
        );

        jSearch.add(jBasicSearch);
        jBasicSearch.getAccessibleContext().setAccessibleName(bundle.getString("SearchEntryPanel.jBasicSearch.AccessibleContext.accessibleName")); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel7.border.title"))); // NOI18N
        jPanel7.setPreferredSize(new java.awt.Dimension(240, 189));

        jLabel4.setText(bundle.getString("SearchEntryPanel.jLabel4.text")); // NOI18N

        jFileTypeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care" }));
        jFileTypeCombo.setMinimumSize(new java.awt.Dimension(101, 25));
        jFileTypeCombo.setName(""); // NOI18N
        jFileTypeCombo.setPreferredSize(new java.awt.Dimension(101, 25));

        jLabel5.setText(bundle.getString("SearchEntryPanel.jLabel5.text")); // NOI18N

        jModifiedCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care", "Last week", "Last month", "Last year", "[Other time frame..]" }));
        jModifiedCombo.setMinimumSize(new java.awt.Dimension(101, 25));
        jModifiedCombo.setName(""); // NOI18N
        jModifiedCombo.setPreferredSize(new java.awt.Dimension(101, 25));

        jCreatedCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care", "Last week", "Last month", "Last year", "[Other time frame..]" }));
        jCreatedCombo.setMinimumSize(new java.awt.Dimension(101, 25));
        jCreatedCombo.setName(""); // NOI18N
        jCreatedCombo.setPreferredSize(new java.awt.Dimension(101, 25));

        jLabel6.setText(bundle.getString("SearchEntryPanel.jLabel6.text")); // NOI18N

        jLabel7.setText(bundle.getString("SearchEntryPanel.jLabel7.text")); // NOI18N

        jAccessedCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care" }));
        jAccessedCombo.setMinimumSize(new java.awt.Dimension(101, 25));
        jAccessedCombo.setName(""); // NOI18N
        jAccessedCombo.setPreferredSize(new java.awt.Dimension(101, 25));

        jLabel9.setText(bundle.getString("SearchEntryPanel.jLabel9.text")); // NOI18N

        jFilesizeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care" }));
        jFilesizeCombo.setMinimumSize(new java.awt.Dimension(101, 25));
        jFilesizeCombo.setName(""); // NOI18N
        jFilesizeCombo.setPreferredSize(new java.awt.Dimension(101, 25));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAccessedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCreatedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jModifiedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFileTypeCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFilesizeCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jFileTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(jFilesizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jModifiedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(jCreatedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jAccessedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSearch.add(jPanel7);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jStartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/start-search.png"))); // NOI18N
        jStartButton.setText(bundle.getString("SearchEntryPanel.jStartButton.text")); // NOI18N
        jStartButton.setToolTipText(bundle.getString("SearchEntryPanel.jStartButton.toolTipText")); // NOI18N
        jStartButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jStartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartButtonActionPerformed(evt);
            }
        });
        jPanel5.add(jStartButton);
        jPanel5.add(filler1);

        jStopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop-search.png"))); // NOI18N
        jStopButton.setText(bundle.getString("SearchEntryPanel.jStopButton.text")); // NOI18N
        jStopButton.setToolTipText(bundle.getString("SearchEntryPanel.jStopButton.toolTipText")); // NOI18N
        jStopButton.setEnabled(false);
        jStopButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopButtonActionPerformed(evt);
            }
        });
        jPanel5.add(jStopButton);

        jSearch.add(jPanel5);

        jScrollPane1.setViewportView(jSearch);

        jTabbedPane1.addTab(bundle.getString("SearchEntryPanel.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel1.border.title"))); // NOI18N

        jExpertMode.setSelected(true);
        jExpertMode.setText(bundle.getString("SearchEntryPanel.jExpertMode.text")); // NOI18N
        jExpertMode.setToolTipText(bundle.getString("SearchEntryPanel.jExpertMode.toolTipText")); // NOI18N
        jExpertMode.setEnabled(false);

        jDisable3rdParty.setText(bundle.getString("SearchEntryPanel.jDisable3rdParty.text")); // NOI18N
        jDisable3rdParty.setToolTipText(bundle.getString("SearchEntryPanel.jDisable3rdParty.toolTipText")); // NOI18N

        jDisableUnicodeDetection.setText(bundle.getString("SearchEntryPanel.jDisableUnicodeDetection.text")); // NOI18N
        jDisableUnicodeDetection.setToolTipText(bundle.getString("SearchEntryPanel.jDisableUnicodeDetection.toolTipText")); // NOI18N

        jLabel8.setText(bundle.getString("SearchEntryPanel.jLabel8.text")); // NOI18N

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(10, 0, 99, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jExpertMode)
                    .addComponent(jDisable3rdParty)
                    .addComponent(jDisableUnicodeDetection)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jExpertMode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDisable3rdParty, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDisableUnicodeDetection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel14.border.title"))); // NOI18N

        jIgnoreSymbolicLinks.setText(bundle.getString("SearchEntryPanel.jIgnoreSymbolicLinks.text")); // NOI18N
        jIgnoreSymbolicLinks.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreSymbolicLinks.toolTipText")); // NOI18N

        jIgnoreHiddenFiles.setText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFiles.text")); // NOI18N
        jIgnoreHiddenFiles.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFiles.toolTipText")); // NOI18N

        FilenameSearchType.add(jUseFileRegex);
        jUseFileRegex.setText(bundle.getString("SearchEntryPanel.jUseFileRegex.text")); // NOI18N
        jUseFileRegex.setToolTipText(bundle.getString("SearchEntryPanel.jUseFileRegex.toolTipText")); // NOI18N
        jUseFileRegex.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jUseFileRegexStateChanged(evt);
            }
        });

        FilenameSearchType.add(jUseFileGlobs);
        jUseFileGlobs.setSelected(true);
        jUseFileGlobs.setText(bundle.getString("SearchEntryPanel.jUseFileGlobs.text")); // NOI18N
        jUseFileGlobs.setToolTipText(bundle.getString("SearchEntryPanel.jUseFileGlobs.toolTipText")); // NOI18N

        jSkipBinaryFiles.setSelected(true);
        jSkipBinaryFiles.setText(bundle.getString("SearchEntryPanel.jSkipBinaryFiles.text")); // NOI18N
        jSkipBinaryFiles.setToolTipText(bundle.getString("SearchEntryPanel.jSkipBinaryFiles.toolTipText")); // NOI18N

        jIgnoreFilenameCase.setSelected(true);
        jIgnoreFilenameCase.setText(bundle.getString("SearchEntryPanel.jIgnoreFilenameCase.text")); // NOI18N
        jIgnoreFilenameCase.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreFilenameCase.toolTipText")); // NOI18N

        jEnableFileTimeout.setSelected(true);
        jEnableFileTimeout.setText(bundle.getString("SearchEntryPanel.jEnableFileTimeout.text")); // NOI18N
        jEnableFileTimeout.setToolTipText(bundle.getString("SearchEntryPanel.jEnableFileTimeout.toolTipText")); // NOI18N
        jEnableFileTimeout.setActionCommand(bundle.getString("SearchEntryPanel.jEnableFileTimeout.actionCommand")); // NOI18N
        jEnableFileTimeout.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jEnableFileTimeoutItemStateChanged(evt);
            }
        });

        jFileTimeout.setModel(new javax.swing.SpinnerNumberModel(5000, 250, 300000, 250));
        jFileTimeout.setToolTipText(bundle.getString("SearchEntryPanel.jFileTimeout.toolTipText")); // NOI18N

        jStrictFilenameSearch.setText(bundle.getString("SearchEntryPanel.jStrictFilenameSearch.text")); // NOI18N
        jStrictFilenameSearch.setToolTipText(bundle.getString("SearchEntryPanel.jStrictFilenameSearch.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jUseFileRegex)
                    .addComponent(jUseFileGlobs)
                    .addComponent(jIgnoreHiddenFiles)
                    .addComponent(jIgnoreFilenameCase)
                    .addComponent(jIgnoreSymbolicLinks)
                    .addComponent(jSkipBinaryFiles)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jEnableFileTimeout)
                        .addGap(6, 6, 6)
                        .addComponent(jFileTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jStrictFilenameSearch))
                .addGap(0, 0, 0))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jUseFileRegex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUseFileGlobs)
                .addGap(0, 0, 0)
                .addComponent(jIgnoreHiddenFiles)
                .addGap(0, 0, 0)
                .addComponent(jIgnoreSymbolicLinks)
                .addGap(0, 0, 0)
                .addComponent(jStrictFilenameSearch)
                .addGap(0, 0, 0)
                .addComponent(jSkipBinaryFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jIgnoreFilenameCase)
                .addGap(0, 0, 0)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFileTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEnableFileTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel15.border.title"))); // NOI18N

        ContentSearchType.add(jUseContentSearch);
        jUseContentSearch.setText(bundle.getString("SearchEntryPanel.jUseContentSearch.text")); // NOI18N
        jUseContentSearch.setToolTipText(bundle.getString("SearchEntryPanel.jUseContentSearch.toolTipText")); // NOI18N

        jIgnoreContentCase.setSelected(true);
        jIgnoreContentCase.setText(bundle.getString("SearchEntryPanel.jIgnoreContentCase.text")); // NOI18N
        jIgnoreContentCase.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreContentCase.toolTipText")); // NOI18N

        ContentSearchType.add(jUseContentRegex);
        jUseContentRegex.setSelected(true);
        jUseContentRegex.setText(bundle.getString("SearchEntryPanel.jUseContentRegex.text")); // NOI18N
        jUseContentRegex.setToolTipText(bundle.getString("SearchEntryPanel.jUseContentRegex.toolTipText")); // NOI18N
        jUseContentRegex.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jUseContentRegexStateChanged(evt);
            }
        });

        jLimitMaxHits.setSelected(true);
        jLimitMaxHits.setText(bundle.getString("SearchEntryPanel.jLimitMaxHits.text")); // NOI18N
        jLimitMaxHits.setToolTipText(bundle.getString("SearchEntryPanel.jLimitMaxHits.toolTipText")); // NOI18N
        jLimitMaxHits.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLimitMaxHitsItemStateChanged(evt);
            }
        });

        jMaxHits.setModel(new javax.swing.SpinnerNumberModel(500, 1, null, 1));
        jMaxHits.setToolTipText(bundle.getString("SearchEntryPanel.jMaxHits.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jUseContentRegex)
                    .addComponent(jUseContentSearch)
                    .addComponent(jIgnoreContentCase)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLimitMaxHits)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jMaxHits, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jUseContentRegex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUseContentSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jIgnoreContentCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLimitMaxHits)
                    .addComponent(jMaxHits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel16.border.title"))); // NOI18N

        jIgnoreFolderCase.setSelected(true);
        jIgnoreFolderCase.setText(bundle.getString("SearchEntryPanel.jIgnoreFolderCase.text")); // NOI18N
        jIgnoreFolderCase.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreFolderCase.toolTipText")); // NOI18N

        jIgnoreHiddenFolders.setText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFolders.text")); // NOI18N
        jIgnoreHiddenFolders.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFolders.toolTipText")); // NOI18N

        jAdd.setText(bundle.getString("SearchEntryPanel.jAdd.text")); // NOI18N
        jAdd.setToolTipText(bundle.getString("SearchEntryPanel.jAdd.toolTipText")); // NOI18N
        jAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddActionPerformed(evt);
            }
        });

        jExcludedPaths.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { ".git", ".hg", ".svn" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jExcludedPaths.setToolTipText(bundle.getString("SearchEntryPanel.jExcludedPaths.toolTipText")); // NOI18N
        jExcludedPaths.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jExcludedPathsValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jExcludedPaths);

        jRemove.setText(bundle.getString("SearchEntryPanel.jRemove.text")); // NOI18N
        jRemove.setToolTipText(bundle.getString("SearchEntryPanel.jRemove.toolTipText")); // NOI18N
        jRemove.setEnabled(false);
        jRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRemoveActionPerformed(evt);
            }
        });

        jEdit.setText(bundle.getString("SearchEntryPanel.jEdit.text")); // NOI18N
        jEdit.setToolTipText(bundle.getString("SearchEntryPanel.jEdit.toolTipText")); // NOI18N
        jEdit.setEnabled(false);
        jEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditActionPerformed(evt);
            }
        });

        jLabel10.setText(bundle.getString("SearchEntryPanel.jLabel10.text")); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jAdd, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jEdit, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jRemove, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jAdd, jEdit, jRemove});

        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRemove)
                        .addGap(0, 54, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jIgnoreHiddenFolders)
                    .addComponent(jIgnoreFolderCase))
                .addGap(179, 179, 179))
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jIgnoreFolderCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jIgnoreHiddenFolders)
                .addContainerGap())
        );

        jRestoreAll.setText(bundle.getString("SearchEntryPanel.jRestoreAll.text")); // NOI18N
        jRestoreAll.setToolTipText(bundle.getString("SearchEntryPanel.jRestoreAll.toolTipText")); // NOI18N
        jRestoreAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRestoreAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jOptionsLayout = new javax.swing.GroupLayout(jOptions);
        jOptions.setLayout(jOptionsLayout);
        jOptionsLayout.setHorizontalGroup(
            jOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jOptionsLayout.createSequentialGroup()
                .addGroup(jOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jOptionsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRestoreAll)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jOptionsLayout.setVerticalGroup(
            jOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jOptionsLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRestoreAll))
        );

        jScrollPane2.setViewportView(jOptions);

        jTabbedPane1.addTab(bundle.getString("SearchEntryPanel.jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private final Collection<ActionListener> listeners = new LinkedList<>();
    public void addActionListener(ActionListener listener)
    {
        listeners.add(listener);
        
        // Make the start button the default i.e. Enter
        this.getRootPane().setDefaultButton(this.jStartButton);
        this.getRootPane().registerKeyboardAction((java.awt.event.ActionEvent evt) -> {
            jStopButton.doClick();
        },  KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
    }
    
   
    public void Start()
    {
        Save();
        
        // Call the parent
        jStartButton.setEnabled(false);
        jStopButton.setEnabled(true);
    }
    public void Stop()
    {
        jStopButton.setEnabled(false);
        jStartButton.setEnabled(true);
    }
    
    private void jStartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartButtonActionPerformed

        String filePattern = (String) ( jUseFileRegex.isSelected() ?
            jFileNameRegexCombo.getSelectedItem() :
            jFileNameGlobCombo.getSelectedItem() );

        if ( !validateFilePattern( filePattern, "file path" ) ) {
            return;
        }

        if ( jUseContentRegex.isSelected() && jContainingRegexCheckBox.isSelected() ) {

            String contentPattern = (String) jContainingRegexCombo.getSelectedItem();

            if ( contentPattern != null && contentPattern.length() > 0 ) {
                try {
                    Pattern.compile( contentPattern );
                } catch ( Exception e ) {
                    showWarningMessageDialog(
                        "Invalid regex pattern for content.\n"
                            + "Message:" + e.getMessage() + "\n"
                            + "Please correct before searching.",
                        "Error parsing pattern"
                    );
                    return;
                }
            }
        }

        ListModel<String> excludedPaths = jExcludedPaths.getModel();
        for ( int p = 0; p < excludedPaths.getSize(); p++ ) {
            String excludedPath = excludedPaths.getElementAt( p );
            if ( !validateFilePattern( excludedPath, "excluded path" ) ) {
                return;
            }
        }

        for(ActionListener listener: listeners){
            ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Start");
            listener.actionPerformed(ae);
           //  listener.stateChanged(new ChangeEvent(this));
        } 
        //Start();
    }//GEN-LAST:event_jStartButtonActionPerformed

    private void jStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStopButtonActionPerformed
        for(ActionListener listener: listeners){
            ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop");
            listener.actionPerformed(ae);
           //  listener.stateChanged(new ChangeEvent(this));
        }
        //Stop();
    }//GEN-LAST:event_jStopButtonActionPerformed

    class AdvancedDialog implements Runnable {
        private final JPanel panel;
        private Object data;
        private final String msg;
        private final JComboBox jCombo;
        private final Frame parent;

        public AdvancedDialog(Frame parent, JComboBox jCombo, String msg, JPanel panel)
        {
            this.parent = parent;
            this.jCombo = jCombo;
            this.msg = msg;
            this.panel = panel;
        }
        
        public AdvancedDialog(Frame parent, JComboBox jCombo, String msg)
        {
            this(parent, jCombo, msg, null);
        }
        
        public void setData(Object _data)
        {
            this.data = _data;
            
        }
        
        @Override
        public void run() {
            if (panel != null)
            {
                JOptionPane frame = new JOptionPane(msg, JOptionPane.PLAIN_MESSAGE);
                //FileDatePanel panel = new FileDatePanel();
                if (panel instanceof FileDatePanel)
                {
                    ((FileDatePanel)panel).set((FileDateEntry)data);
                    frame.setPreferredSize(new Dimension(450, 300));
                } else if (panel instanceof FileSizePanel) {
                    ((FileSizePanel)panel).set((FileSizeEntry)data);
                    frame.setPreferredSize(new Dimension(450, 300));
                } else { // if (panel instanceof FileTypePanel) {
                    ((FileTypePanel)panel).set((FileTypeEntry)data);
                    frame.setPreferredSize(new Dimension(550, 600));
            }

                frame.setMessage(panel);
                frame.setOptionType(JOptionPane.OK_CANCEL_OPTION);
                frame.setMaximumSize(new Dimension(0xFFFF, 0xFFFF));
                frame.setMinimumSize(new Dimension(0, 0));
                JDialog dlg = frame.createDialog(parent, msg);
                dlg.pack();
                dlg.setLocationRelativeTo(parent); // Centre on parent
                dlg.setVisible(true);
                Object ret = frame.getValue();

                if (ret != null && ((Integer)ret).equals(JOptionPane.OK_OPTION))
                {
                    Object entry;
                    if (panel instanceof FileDatePanel)
                    {
                        entry = ((FileDatePanel)panel).get();
                    } else if (panel instanceof FileSizePanel) {
                        entry = ((FileSizePanel)panel).get();
                    } else {
                        entry = ((FileTypePanel)panel).get();
                    }
                    jCombo.getModel().setSelectedItem(entry);
                }
            } else {
                String xx = rb.getString(RunDialogMessages.OK.getKey());

                jFileChooser1.setApproveButtonText(xx);
                jFileChooser1.setSelectedFile(new File((String)data));
                int ret = jFileChooser1.showOpenDialog(parent);
                if (ret == JFileChooser.APPROVE_OPTION)
                {
                    File fname = jFileChooser1.getSelectedFile();
                    jLookIn.getModel().setSelectedItem(fname.getPath());
                }
            }
        }
    }

    private void jUseFileRegexStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jUseFileRegexStateChanged
        boolean sel = jUseFileRegex.isSelected();
        jFileNameRegexPanel.setVisible( sel );
        jFileNameGlobPanel.setVisible( !sel );
    }//GEN-LAST:event_jUseFileRegexStateChanged

    private void jUseContentRegexStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jUseContentRegexStateChanged
        boolean sel = jUseContentRegex.isSelected();
        jContainingRegexPanel.setVisible( sel );
        jContainingPlainPanel.setVisible( !sel );
    }//GEN-LAST:event_jUseContentRegexStateChanged

    private void jContainingRegexCheckBoxItemStateChanged(java.awt.event.ItemEvent evt ) {//GEN-FIRST:event_jContainingRegexCheckBoxItemStateChanged
        jContainingRegexCombo.setEnabled( jContainingRegexCheckBox.isSelected() );
    }//GEN-LAST:event_jContainingRegexCheckBoxItemStateChanged

    private void jContainingPlainCheckBoxItemStateChanged(java.awt.event.ItemEvent evt ) {//GEN-FIRST:event_jContainingPlainCheckBoxItemStateChanged
        jContainingPlainCombo.setEnabled( jContainingPlainCheckBox.isSelected() );
    }//GEN-LAST:event_jContainingPlainCheckBoxItemStateChanged

    private void jRestoreAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRestoreAllActionPerformed
        String x1 = rb.getString(RunDialogMessages.CONFIRM_TITLE.getKey());
        String x2 = rb.getString(RunDialogMessages.CONFIRM_DIALOG.getKey());
        int res = JOptionPane.showConfirmDialog(this, x2, x1, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return; //Cancel
        try {
            prefs.clear();
            this.Restore();
        } catch (BackingStoreException ex) {
            Logger.getLogger(SearchEntryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRestoreAllActionPerformed

    private void jLimitMaxHitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLimitMaxHitsItemStateChanged
        jMaxHits.setEnabled(jLimitMaxHits.isSelected());
    }//GEN-LAST:event_jLimitMaxHitsItemStateChanged

    private void jEnableFileTimeoutItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jEnableFileTimeoutItemStateChanged
        jFileTimeout.setEnabled(jEnableFileTimeout.isSelected());
    }//GEN-LAST:event_jEnableFileTimeoutItemStateChanged

    private void jExcludedPathsValueChanged(javax.swing.event.ListSelectionEvent evt ) {//GEN-FIRST:event_jExcludedPathsValueChanged
        if (evt.getValueIsAdjusting()) return;
        
        int idx = jExcludedPaths.getSelectedIndex();
        this.jRemove.setEnabled(idx != -1);
        this.jEdit.setEnabled(idx != -1);
    }//GEN-LAST:event_jExcludedPathsValueChanged

    private void jRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRemoveActionPerformed
        int idx = jExcludedPaths.getSelectedIndex();
        if (idx == -1) return;
        int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove?", "Click OK to permanently delete entry.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (reply == JOptionPane.OK_OPTION)
        {
            DefaultListModel model = (DefaultListModel) ( jExcludedPaths.getModel() );
            model.removeElementAt(idx);
            if (idx >= model.getSize()) idx --;
            if (idx != -1) {
                this.jExcludedPaths.setSelectedIndex( idx );
            }
        }
    }//GEN-LAST:event_jRemoveActionPerformed

    private void jEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditActionPerformed
        int idx = jExcludedPaths.getSelectedIndex();
        if ( idx == -1 )
            return;
        DefaultListModel<String> model = (DefaultListModel<String>) jExcludedPaths.getModel();

        String existingPattern = model.getElementAt( idx );

        existingPattern = tryFileParsePattern( existingPattern, "Enter item to edit" );

        if ( existingPattern == null || existingPattern.length() == 0 )
            return;

        model.setElementAt( existingPattern, idx );
        sort( model );

    }//GEN-LAST:event_jEditActionPerformed

    private String tryFileParsePattern( String reply, String dialogMessage ) {
        while ( true ) {

            reply = JOptionPane.showInputDialog( this, dialogMessage, reply );

            if ( reply == null || reply.length() <= 0 ) {
                return null;
            }

            boolean useFileRegex = jUseFileRegex.isSelected();

            if ( !useFileRegex ) {
                return reply;
            }

            try {
                Pattern.compile( reply );
                break;
            } catch ( Exception e ) {

                int decision = JOptionPane.showConfirmDialog(
                    this,
                    "Invalid regex pattern.\n"
                        + "Message:" + e.getMessage() + "\n"
                        + "Try again?",
                    "Error parsing pattern",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if ( decision == JOptionPane.YES_OPTION ) {
                    continue;
                }

                return null;
            }
        }
        return reply;
    }

    private boolean validateFilePattern( String pattern, String patternType ) {

        if ( pattern == null || pattern.length() == 0 ) {
            return true;
        }

        boolean useFileRegex = jUseFileRegex.isSelected();

        if ( !useFileRegex ) {
            return true;
        }

        try {
            Pattern.compile( pattern );
            return true;
        } catch ( Exception e ) {

            showWarningMessageDialog(
                "Invalid " + patternType + " regex pattern.\n"
                    + "Message:" + e.getMessage() + "\n"
                    + "Please correct before searching.",
                "Error parsing pattern"
            );

            return false;
        }
    }

    private void showWarningMessageDialog( String message, String title ) {
        JOptionPane.showMessageDialog(
            this,
            message,
            title,
            JOptionPane.WARNING_MESSAGE
        );
    }

    private void sort( DefaultListModel<String> dlm ) {
        Object[] dlma = dlm.toArray();    // make an array of the elements in the model
        Arrays.sort( dlma );   // sort the array (this step uses the compareTo method)
        dlm.clear();     // empty the model
        for ( Object x : dlma ) {
            dlm.addElement( (String) x );
        }
    }

    private void jAddActionPerformed( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_jAddActionPerformed
        DefaultListModel<String> model = (DefaultListModel<String>) ( jExcludedPaths.getModel() );

        String newPattern = tryFileParsePattern( "", "Enter item to add" );

        if ( newPattern == null || newPattern.length() == 0 )
            return;

        model.addElement( newPattern );
        sort( model );
    }//GEN-LAST:event_jAddActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup ContentSearchType;
    private javax.swing.ButtonGroup FilenameSearchType;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> jAccessedCombo;
    private javax.swing.JButton jAdd;
    private javax.swing.JPanel jBasicSearch;
    private javax.swing.JCheckBox jContainingPlainCheckBox;
    private javax.swing.JComboBox<String> jContainingPlainCombo;
    private javax.swing.JPanel jContainingPlainPanel;
    private javax.swing.JCheckBox jContainingRegexCheckBox;
    private javax.swing.JComboBox<String> jContainingRegexCombo;
    private javax.swing.JPanel jContainingRegexPanel;
    private javax.swing.JComboBox<String> jCreatedCombo;
    private javax.swing.JCheckBox jDisable3rdParty;
    private javax.swing.JCheckBox jDisableUnicodeDetection;
    private javax.swing.JButton jEdit;
    private javax.swing.JCheckBox jEnableFileTimeout;
    private javax.swing.JList<String> jExcludedPaths;
    private javax.swing.JCheckBox jExpertMode;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JComboBox<String> jFileNameGlobCombo;
    private javax.swing.JPanel jFileNameGlobPanel;
    private javax.swing.JComboBox<String> jFileNameRegexCombo;
    private javax.swing.JPanel jFileNameRegexPanel;
    private javax.swing.JSpinner jFileTimeout;
    private javax.swing.JComboBox<String> jFileTypeCombo;
    private javax.swing.JComboBox<String> jFilesizeCombo;
    private javax.swing.JCheckBox jIgnoreContentCase;
    private javax.swing.JCheckBox jIgnoreFilenameCase;
    private javax.swing.JCheckBox jIgnoreFolderCase;
    private javax.swing.JCheckBox jIgnoreHiddenFiles;
    private javax.swing.JCheckBox jIgnoreHiddenFolders;
    private javax.swing.JCheckBox jIgnoreSymbolicLinks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox jLimitMaxHits;
    private javax.swing.JComboBox<String> jLookIn;
    private javax.swing.JSpinner jMaxHits;
    private javax.swing.JComboBox<String> jModifiedCombo;
    private javax.swing.JPanel jOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JButton jRemove;
    private javax.swing.JButton jRestoreAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel jSearch;
    private javax.swing.JCheckBox jSkipBinaryFiles;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JButton jStartButton;
    private javax.swing.JButton jStopButton;
    private javax.swing.JCheckBox jStrictFilenameSearch;
    private javax.swing.JCheckBox jSubFolders;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton jUseContentRegex;
    private javax.swing.JRadioButton jUseContentSearch;
    private javax.swing.JRadioButton jUseFileGlobs;
    private javax.swing.JRadioButton jUseFileRegex;
    // End of variables declaration//GEN-END:variables
}
