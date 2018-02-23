/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.embeddediq.searchmonkey;

import com.embeddediq.searchmonkey.RegexWizard.RegexBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;


// Note to self - here is the NIMBUS Default Look and Feel
// https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary

/**
 *
 * @author cottr
 */
public class SearchEntryPanel extends javax.swing.JPanel implements ChangeListener {

    String lastItem = null;
    JSpinner popup_link;
    PopupCalendar cal;
    /**
     * Creates new form SearchEntryPanel
     */
    public SearchEntryPanel() {
        prefs = Preferences.userNodeForPackage(SearchEntry.class);
        
        initComponents();
        
        cal = new PopupCalendar();
        cal.getCalendar().addChangeListener(this);

        jAfter.addMouseListener(new MyMouseAdapter(jAfter, jAfterSpinner));
        jBefore.addMouseListener(new MyMouseAdapter(jBefore, jBeforeSpinner));
        jAfter1.addMouseListener(new MyMouseAdapter(jAfter1, jAfterSpinner1));
        jBefore1.addMouseListener(new MyMouseAdapter(jBefore1, jBeforeSpinner1));
        jAfter2.addMouseListener(new MyMouseAdapter(jAfter2, jAfterSpinner2));
        jBefore2.addMouseListener(new MyMouseAdapter(jBefore2, jBeforeSpinner2));
        
        jPanel3.setVisible(false); // hide the regex view
        jPanel6.setVisible(false); // hide the context word search

        // Restore the settings
        Restore();

        // Check for OS dependent settings:-
        if (IS_OS_WINDOWS)
        {
            jIgnoreHiddenFolders.setVisible(false);
            jIgnoreHiddenFolders.setSelected(false);
        }
        
        // Add a browse button to the jCombobox
        AddComboHandler(jLookIn, "<<BROWSE>>", () -> {
            jLookIn.getModel().setSelectedItem(jLookIn.getItemAt(0));
            // TODO - allow the item name to select which dialog is shown
            String name = jLookIn.getName();
            SelectLookInFolder();
        });

        AddComboHandler(jFilesizeCombo, "<<Others>>", () -> {
            jLookIn.getModel().setSelectedItem(jLookIn.getItemAt(0));
            // TODO - allow the item name to select which dialog is shown
            String name = jLookIn.getName();
            SelectFileSize();
        });
        AddComboHandler(jModifiedCombo, "<<Others>>", () -> {
            jModifiedCombo.getModel().setSelectedItem(jModifiedCombo.getItemAt(0));
            // TODO - allow the item name to select which dialog is shown
            String name = jModifiedCombo.getName();
            // SelectFileSize();
            SelectModifiedDate();
        });
//        
//        AddComboHandler(jAccessedCombo, "<<Others>>", () -> {
//            jAccessedCombo.getModel().setSelectedItem(jAccessedCombo.getItemAt(0));
//            // TODO - allow the item name to select which dialog is shown
//            String name = jAccessedCombo.getName();
////            SelectModifiedDate();
//            SelectAccessedDate();
//        });
        // Creating a custom class for the JComboBox
        jAccessedCombo.setModel(new SeparatorComboBoxModel("Other"));
        jAccessedCombo.setRenderer(new SeparatorComboBoxRenderer());
        jAccessedCombo.addActionListener(new SeparatorComboBoxListener(jAccessedCombo, () -> {
            SelectAccessedDate();
        }));
        
        AddComboHandler(jCreatedCombo, "<<Others>>", () -> {
            jCreatedCombo.getModel().setSelectedItem(jCreatedCombo.getItemAt(0));
            // TODO - allow the item name to select which dialog is shown
            String name = jCreatedCombo.getName();
//            SelectModifiedDate();
            SelectCreatedDate();
        });
        
     
       
        // TODO - future stuff
        // this.jExpertMode.setVisible(false);
        //this.jButton7.setVisible(false);
        //this.jButton8.setVisible(false);
        //this.jButton10.setVisible(false);

        //this.jButton4.setVisible(false);
        //this.jButton5.setVisible(false);
        //this.jButton9.setVisible(false);
        this.jFileSizePanel.setVisible(false);
        this.jModifiedDate.setVisible(false);
        this.jModifiedPanel1.setVisible(false);
        this.jModifiedPanel2.setVisible(false);
    }
    
    class SeparatorComboBoxModel extends DefaultComboBoxModel
    {
        //Color[] cols;
        // Create a basic combobox for the size and date seelction
        SeparatorComboBoxModel(String button)
        {
            //cols = oldColors;
            super(new Object[] {
                "Don't care",
                new JSeparator(JSeparator.HORIZONTAL),
                new JLabel(button)});
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

            if (isSelected)
            {
                this.setBackground((Color)UIManager.getLookAndFeelDefaults().get("ComboBox:\"ComboBox.listRenderer\"[Selected].background"));
                this.setForeground((Color)UIManager.getLookAndFeelDefaults().get("ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground"));
            } else {
                this.setBackground(UIManager.getLookAndFeelDefaults().getColor("ComboBox.background"));
                this.setForeground(UIManager.getLookAndFeelDefaults().getColor("ComboBox.foreground"));
            }

            if (value instanceof Icon) {
               setIcon((Icon)value);
            }
            if (value instanceof JSeparator) {
               return (Component) value;
            }

            String txt;
            if (value instanceof JLabel) {
                txt = ((JLabel)value).getText();
            } else {
                txt = (value == null) ? "" : value.toString();
            }

            this.setText(txt);
            return this;
            // return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
      } 

    }    

    class SeparatorComboBoxListener implements ActionListener {
       JComboBox combobox;
       Object oldItem;
       Runnable callback;

       SeparatorComboBoxListener(JComboBox combobox, Runnable callback) {
          this.combobox = combobox;
          this.callback = callback;
          combobox.setSelectedIndex(0);
          oldItem = combobox.getSelectedItem();
       }

       @Override
       public void actionPerformed(ActionEvent e) {
            Object selectedItem = combobox.getSelectedItem();
            if (selectedItem instanceof JLabel)
            {
               combobox.setSelectedItem(oldItem);
               SwingUtilities.invokeLater(callback);
            }    
            else if (selectedItem instanceof JSeparator)
            {
               combobox.setSelectedItem(oldItem);
            } else {
               oldItem = selectedItem;
            }
       }
    }
    
    private void AddComboHandler(JComboBox item, String browse, Runnable callback)
    {
        // Add a browse button to the jCombobox
        // final String browse = txt; // "<<BROWSE>>";
        item.addItem( browse );
        item.addItemListener((ItemEvent e) -> {
            if ( e.getStateChange() == ItemEvent.SELECTED)
            {
                if (browse.equals( e.getItem())) {
                    // Get last selected..
                    SwingUtilities.invokeLater(callback);
                }
            }
        });
    }
    
    public class PopupCalendar extends JPopupMenu {
        CalendarPopup panel;
        public PopupCalendar (){
            panel = new CalendarPopup();
            
            this.add(panel);
            this.pack();
        }
        
        public CalendarPopup getCalendar()
        {
            return panel;
        }
        
        private JSpinner popup_link;
        public void show(JButton jButton, JSpinner link)
        {
            popup_link = link;
            panel.setDate((Date)link.getValue());
            this.show(jButton, 0, jButton.getHeight());
        }
        public void updateDate()
        {
            if (popup_link == null) return;
            popup_link.setValue(panel.getDate());
        }
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        cal.updateDate();
    }
    
    public class MyMouseAdapter extends MouseAdapter {
        JButton jButton;
        JSpinner link;
        public MyMouseAdapter(JButton jButton, JSpinner link)
        {
           //this.cal = cal;
            this.jButton = jButton;
            this.link = link;
        }
        
        @Override
        public void mousePressed(MouseEvent e)
        {
            cal.show(jButton, link);
        }
    }
   
    int maxCombo = 10;
    private String getSelectedItem(JComboBox jCombo)
    {
        
        //String val = (String)jCombo.getSelectedItem();
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
        longVal = 0L;
        if (jLimitMaxFileSize.isSelected()) {
            longVal = (long)(1024.0 * 1024.0 * (Double)jMaxFileSize.getValue());
        }
        req.maxFileSize = longVal;
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
        longVal = 0L;
        if (jLimitMaxRecurse.isSelected()) {
            longVal = (Long)jMaxRecurse.getValue();
        }
        req.maxRecurse = longVal;

        // Get look in folder
        req.lookIn = new ArrayList<>();
        Object folder = getSelectedItem(jLookIn);
        if (folder.getClass().equals(String.class))
        {
            strItem = (String)folder;
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
        strItem = getSelectedItem(jUseFileRegex.isSelected() ? jFileName1 : jFileName);
        //String prefix = (jUseFileRegex.isSelected() ? SearchEntry.PREFIX_REGEX : SearchEntry.PREFIX_GLOB);
        //req.fileName = FileSystems.getDefault().getPathMatcher(prefix + strItem);
        req.fileNameText = strItem;
        
        // Get containing text
        if (jCheckBox2.isSelected() && jContainingText.getSelectedItem() != null)
        {
            strItem = getSelectedItem(jUseContentRegex.isSelected() ? jContainingText : jContainingText1);
            if (strItem.length() > 0) // Is there a content match to make?
            {
                req.containingText = strItem;
//                int flags = 0;
//                if (!req.flags.useContentRegex) flags |= Pattern.LITERAL;
//                if (req.flags.ignoreContentCase) flags |= Pattern.CASE_INSENSITIVE;
//                //Pattern regex = Pattern.compile(strItem, flags);
//                req.containingTextRegex = Pattern.compile(strItem, flags);
                //req.containingText = new ContentMatch(regex);
            }
        }
        
        // Get min/max size
        if (jLessThanCheck.isSelected()) {
            double scaler = Math.pow(1024,jFileSizeScaler.getSelectedIndex()); // 1024^0 = 1; 1024^1=1K, 1024^2=1M, etc
            req.lessThan = (long)(scaler * (double)jLessThanSpinner.getValue());
        }
        if (jMoreThanCheck.isSelected()) {
            double scaler = Math.pow(1024,jFileSizeScaler1.getSelectedIndex()); // 1024^0 = 1; 1024^1=1K, 1024^2=1M, etc
            req.greaterThan = (long)(scaler * (double)jGreaterThanSpinner.getValue());
        }
        // If max size is not set, then use the built in override
        if ((req.maxFileSize > 0) && (req.lessThan == 0)) {
            req.lessThan = req.maxFileSize;
        }

        // Get modifed before/after date
        if (jModifiedAfterCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jAfterSpinner.getModel()).getDate();
            req.modifiedAfter = FileTime.from(d.toInstant());
        }
        if (jModifiedBeforeCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jBeforeSpinner.getModel()).getDate();
            req.modifiedBefore = FileTime.from(d.toInstant());
        }

        // Get created before/after date
        if (jCreatedAfterCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jAfterSpinner1.getModel()).getDate();
            req.createdAfter = FileTime.from(d.toInstant());
        }
        if (jCreatedBeforeCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jBeforeSpinner1.getModel()).getDate();
            req.createdBefore = FileTime.from(d.toInstant());
        }

        // Get accessed before/after date
        if (jAccessedAfterCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jAfterSpinner2.getModel()).getDate();
            req.accessedAfter = FileTime.from(d.toInstant());
        }
        if (jAccessedBeforeCheck.isSelected()) {
            Date d = ((SpinnerDateModel)jBeforeSpinner2.getModel()).getDate();
            req.accessedBefore = FileTime.from(d.toInstant());
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
    }
    private void Save(String name, JSpinner jSpinner) throws SecurityException
    {
        Gson g = new Gson();
        Object val = jSpinner.getValue();
        String json = g.toJson(val);
        prefs.put(name, json); // Add list of look in folders        
    }
    private final Preferences prefs;
    
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
        List<String> items = g.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
        for (int i = items.size(); --i >= 0;)
        {
            jCombo.insertItemAt(items.get(i), 0);
            // jCombo.addItem(item);
        }
    }
    private void Restore(String name, JSpinner jSpinner, Object def) throws SecurityException
    {
        Gson g = new Gson();
        String json = prefs.get(name, g.toJson(def)); // Add list of look in folders        
        Object val = g.fromJson(json, def.getClass());
        jSpinner.setValue(val);
    }

    public void Save() throws SecurityException
    {
        Save("LookIn", jLookIn);
        Save("FileName", jFileName);
        Save("FileNameRegex", jFileName1);
        prefs.putBoolean("ContainingRegexTextToggle", jCheckBox2.isSelected());
        Save("ContainingRegexText", jContainingText);
        prefs.putBoolean("ContainingTextToggle", jCheckBox3.isSelected());
        Save("ContainingText", jContainingText1);
        
        prefs.putBoolean("LookInSubFolders", jSubFolders.isSelected());
        prefs.putInt("LessThanScaler", jFileSizeScaler.getSelectedIndex());
        prefs.putInt("GreaterThanScaler", jFileSizeScaler1.getSelectedIndex());
        prefs.putBoolean("GreaterThanToggle", jMoreThanCheck.isSelected());
        prefs.putDouble("GreaterThan", (Double)jGreaterThanSpinner.getValue());
        prefs.putBoolean("LessThanToggle", jLessThanCheck.isSelected());
        prefs.putDouble("LessThan", (Double)jLessThanSpinner.getValue());
        
        prefs.putBoolean("AfterToggle", jModifiedAfterCheck.isSelected());
        Save("AfterSpinner", jAfterSpinner);
        prefs.putBoolean("BeforeToggle", jModifiedBeforeCheck.isSelected());
        Save("BeforeSpinner", jBeforeSpinner);
        
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
        prefs.putBoolean("LimitMaxFileSize", jLimitMaxFileSize.isSelected());
        prefs.putDouble("MaxFileSize", (Double)jMaxFileSize.getValue());
        prefs.putBoolean("EnableFileTimeout", jEnableFileTimeout.isSelected());
        prefs.putInt("FileTimeout", (Integer)jFileTimeout.getValue());

        prefs.putBoolean("UseContentRegex", jUseContentRegex.isSelected());
        prefs.putBoolean("IgnoreContentCase", jIgnoreContentCase.isSelected());
        prefs.putBoolean("LimitMaxHits", jLimitMaxHits.isSelected());
        prefs.putLong("MaxHits", (Long)jMaxHits.getValue());
        
        prefs.putBoolean("IgnoreFolderCase", jIgnoreFolderCase.isSelected());
        prefs.putBoolean("IgnoreHiddenFolders", jIgnoreHiddenFolders.isSelected());
        prefs.putBoolean("LimitMaxRecurse", jLimitMaxRecurse.isSelected());
        prefs.putLong("MaxRecurse", (Long)jMaxRecurse.getValue());
        
        // Adanced search settings
        prefs.putBoolean("AfterToggle1", jCreatedAfterCheck.isSelected());
        Save("AfterSpinner1", jAfterSpinner1);
        prefs.putBoolean("BeforeToggle1", jCreatedBeforeCheck.isSelected());
        Save("BeforeSpinner1", jBeforeSpinner1);
        
        prefs.putBoolean("AfterToggle2", jAccessedAfterCheck.isSelected());
        Save("AfterSpinner2", jAfterSpinner2);
        prefs.putBoolean("BeforeToggle2", jAccessedBeforeCheck.isSelected());
        Save("BeforeSpinner2", jBeforeSpinner2);
    }
    private void Restore()
    {
        Date date = new Date();
        boolean enabled;

        // Basic search params
        String home = System.getProperty("user.home");
        Restore("FileName", jFileName, new String[] {"*.txt", "*.[c|h]"});
        Restore("FileNameRegex", jFileName1, new String[] {".*txt", ".*[c|h]"});
        jCheckBox2.setSelected(prefs.getBoolean("ContainingRegexTextToggle", false));
        Restore("ContainingRegexText", jContainingText, new String[] {});
        jCheckBox3.setSelected(prefs.getBoolean("ContainingTextToggle", false));
        Restore("ContainingText", jContainingText1, new String[] {});
        Restore("LookIn", jLookIn, new String[] {home});
        jSubFolders.setSelected(prefs.getBoolean("LookInSubFolders", true));
        
        // File size modifiers
        jFileSizeScaler.setSelectedIndex(prefs.getInt("LessThanScaler", 1)); // Select KBytes by default
        jFileSizeScaler1.setSelectedIndex(prefs.getInt("GreaterThanScaler", 1)); // Select KBytes by default
        enabled = prefs.getBoolean("GreaterThanToggle", false);
        jMoreThanCheck.setSelected(enabled);
        jGreaterThanSpinner.setValue(prefs.getDouble("GreaterThan", 0.0));
        jGreaterThanSpinner.setEnabled(enabled);
        enabled = prefs.getBoolean("LessThanToggle", false);
        jLessThanCheck.setSelected(enabled);
        jLessThanSpinner.setValue(prefs.getDouble("LessThan", 0.0));
        jLessThanSpinner.setEnabled(enabled);
        
        // Last modified date
        enabled = prefs.getBoolean("AfterToggle", false);
        jModifiedAfterCheck.setSelected(enabled);
        Restore("AfterSpinner", jAfterSpinner, date);
        jAfterSpinner.setEnabled(enabled);
        enabled = prefs.getBoolean("BeforeToggle", false);
        jModifiedBeforeCheck.setSelected(enabled);
        Restore("BeforeSpinner", jBeforeSpinner, date);
        jBeforeSpinner.setEnabled(enabled);
        
        // Created date
        enabled = prefs.getBoolean("AfterToggle1", false);
        jCreatedAfterCheck.setSelected(enabled);
        Restore("AfterSpinner1", jAfterSpinner1, date);
        jAfterSpinner1.setEnabled(enabled);
        enabled = prefs.getBoolean("BeforeToggle1", false);
        jCreatedBeforeCheck.setSelected(prefs.getBoolean("BeforeToggle1", false));
        Restore("BeforeSpinner1", jBeforeSpinner1, date);
        jBeforeSpinner1.setEnabled(enabled);
        
        // Last accessed date
        enabled = prefs.getBoolean("AfterToggle2", false);
        jAccessedAfterCheck.setSelected(enabled);
        Restore("AfterSpinner2", jAfterSpinner2, date);
        jAfterSpinner2.setEnabled(enabled);
        enabled = prefs.getBoolean("BeforeToggle2", false);
        jAccessedBeforeCheck.setSelected(enabled);
        Restore("BeforeSpinner2", jBeforeSpinner2, date);
        jBeforeSpinner2.setEnabled(enabled);

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
        jLimitMaxFileSize.setSelected(prefs.getBoolean("LimitMaxFileSize", true));
        jMaxFileSize.setValue(prefs.getDouble("MaxFileSize", 10.0)); // limit to 10 MBytes
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
        jLimitMaxRecurse.setSelected(prefs.getBoolean("LimitMaxRecurse", false));
        jMaxRecurse.setValue(prefs.getLong("MaxRecurse", 5)); // limit to 5 sub directories
        
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
        jModifiedPanel2 = new javax.swing.JPanel();
        jAccessedBeforeCheck = new javax.swing.JCheckBox();
        jAccessedAfterCheck = new javax.swing.JCheckBox();
        jBeforeSpinner2 = new javax.swing.JSpinner();
        jToolBar8 = new javax.swing.JToolBar();
        jBefore2 = new javax.swing.JButton();
        jAfterSpinner2 = new javax.swing.JSpinner();
        jToolBar7 = new javax.swing.JToolBar();
        jAfter2 = new javax.swing.JButton();
        jModifiedPanel1 = new javax.swing.JPanel();
        jCreatedBeforeCheck = new javax.swing.JCheckBox();
        jBeforeSpinner1 = new javax.swing.JSpinner();
        jToolBar9 = new javax.swing.JToolBar();
        jBefore1 = new javax.swing.JButton();
        jCreatedAfterCheck = new javax.swing.JCheckBox();
        jAfterSpinner1 = new javax.swing.JSpinner();
        jToolBar6 = new javax.swing.JToolBar();
        jAfter1 = new javax.swing.JButton();
        jFileSizePanel = new javax.swing.JPanel();
        jMoreThanCheck = new javax.swing.JCheckBox();
        jFileSizeScaler = new javax.swing.JComboBox<>();
        jLessThanCheck = new javax.swing.JCheckBox();
        jLessThanSpinner = new javax.swing.JSpinner();
        jGreaterThanSpinner = new javax.swing.JSpinner();
        jFileSizeScaler1 = new javax.swing.JComboBox<>();
        jModifiedDate = new javax.swing.JPanel();
        jBeforeSpinner = new javax.swing.JSpinner();
        jToolBar4 = new javax.swing.JToolBar();
        jBefore = new javax.swing.JButton();
        jModifiedBeforeCheck = new javax.swing.JCheckBox();
        jAfterSpinner = new javax.swing.JSpinner();
        jToolBar5 = new javax.swing.JToolBar();
        jAfter = new javax.swing.JButton();
        jModifiedAfterCheck = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSearch = new javax.swing.JPanel();
        jBasicSearch = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLookIn = new javax.swing.JComboBox<>();
        jSubFolders = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jFileName = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jFileName1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jContainingText = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jContainingText1 = new javax.swing.JComboBox<>();
        jCheckBox3 = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jFilesizeCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jModifiedCombo = new javax.swing.JComboBox<>();
        jCreatedCombo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jAccessedCombo = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jStartButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0));
        jStopButton = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
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
        jLimitMaxFileSize = new javax.swing.JCheckBox();
        jSkipBinaryFiles = new javax.swing.JCheckBox();
        jMaxFileSize = new javax.swing.JSpinner();
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
        jLimitMaxRecurse = new javax.swing.JCheckBox();
        jMaxRecurse = new javax.swing.JSpinner();
        jManageSkipFolders = new javax.swing.JButton();
        jIgnoreHiddenFolders = new javax.swing.JCheckBox();
        jRestoreAll = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        jFileChooser1.setApproveButtonText(bundle.getString("SearchEntryPanel.jFileChooser1.approveButtonText")); // NOI18N
        jFileChooser1.setApproveButtonToolTipText(bundle.getString("SearchEntryPanel.jFileChooser1.approveButtonToolTipText")); // NOI18N
        jFileChooser1.setDialogTitle(bundle.getString("SearchEntryPanel.jFileChooser1.dialogTitle")); // NOI18N
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooser1.setToolTipText(bundle.getString("SearchEntryPanel.jFileChooser1.toolTipText")); // NOI18N

        jModifiedPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jModifiedPanel2.border.title"))); // NOI18N

        jAccessedBeforeCheck.setText(bundle.getString("SearchEntryPanel.jAccessedBeforeCheck.text")); // NOI18N
        jAccessedBeforeCheck.setToolTipText(bundle.getString("SearchEntryPanel.jAccessedBeforeCheck.toolTipText")); // NOI18N
        jAccessedBeforeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jAccessedBeforeCheckItemStateChanged(evt);
            }
        });

        jAccessedAfterCheck.setText(bundle.getString("SearchEntryPanel.jAccessedAfterCheck.text")); // NOI18N
        jAccessedAfterCheck.setToolTipText(bundle.getString("SearchEntryPanel.jAccessedAfterCheck.toolTipText")); // NOI18N
        jAccessedAfterCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jAccessedAfterCheckItemStateChanged(evt);
            }
        });

        jBeforeSpinner2.setModel(new javax.swing.SpinnerDateModel());
        jBeforeSpinner2.setToolTipText(bundle.getString("SearchEntryPanel.jBeforeSpinner2.toolTipText")); // NOI18N
        jBeforeSpinner2.setEnabled(false);

        jToolBar8.setFloatable(false);
        jToolBar8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar8.setRollover(true);
        jToolBar8.setBorderPainted(false);

        jBefore2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jBefore2.setToolTipText(bundle.getString("SearchEntryPanel.jBefore2.toolTipText")); // NOI18N
        jBefore2.setFocusable(false);
        jBefore2.setHideActionText(true);
        jBefore2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBefore2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBefore2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar8.add(jBefore2);

        jAfterSpinner2.setModel(new javax.swing.SpinnerDateModel());
        jAfterSpinner2.setToolTipText(bundle.getString("SearchEntryPanel.jAfterSpinner2.toolTipText")); // NOI18N
        jAfterSpinner2.setEnabled(false);

        jToolBar7.setFloatable(false);
        jToolBar7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar7.setRollover(true);
        jToolBar7.setBorderPainted(false);

        jAfter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jAfter2.setToolTipText(bundle.getString("SearchEntryPanel.jAfter2.toolTipText")); // NOI18N
        jAfter2.setFocusable(false);
        jAfter2.setHideActionText(true);
        jAfter2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAfter2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jAfter2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar7.add(jAfter2);

        javax.swing.GroupLayout jModifiedPanel2Layout = new javax.swing.GroupLayout(jModifiedPanel2);
        jModifiedPanel2.setLayout(jModifiedPanel2Layout);
        jModifiedPanel2Layout.setHorizontalGroup(
            jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jModifiedPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAccessedBeforeCheck)
                    .addComponent(jAccessedAfterCheck)
                    .addGroup(jModifiedPanel2Layout.createSequentialGroup()
                        .addGroup(jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBeforeSpinner2)
                            .addComponent(jAfterSpinner2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jModifiedPanel2Layout.setVerticalGroup(
            jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jModifiedPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jModifiedPanel2Layout.createSequentialGroup()
                        .addComponent(jAccessedBeforeCheck)
                        .addGap(0, 0, 0)
                        .addComponent(jBeforeSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAccessedAfterCheck)
                .addGap(0, 0, 0)
                .addGroup(jModifiedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAfterSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jModifiedPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jModifiedPanel1.border.title"))); // NOI18N

        jCreatedBeforeCheck.setText(bundle.getString("SearchEntryPanel.jCreatedBeforeCheck.text")); // NOI18N
        jCreatedBeforeCheck.setToolTipText(bundle.getString("SearchEntryPanel.jCreatedBeforeCheck.toolTipText")); // NOI18N
        jCreatedBeforeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCreatedBeforeCheckItemStateChanged(evt);
            }
        });

        jBeforeSpinner1.setModel(new javax.swing.SpinnerDateModel());
        jBeforeSpinner1.setToolTipText(bundle.getString("SearchEntryPanel.jBeforeSpinner1.toolTipText")); // NOI18N
        jBeforeSpinner1.setEnabled(false);

        jToolBar9.setFloatable(false);
        jToolBar9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar9.setRollover(true);
        jToolBar9.setBorderPainted(false);

        jBefore1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jBefore1.setToolTipText(bundle.getString("SearchEntryPanel.jBefore1.toolTipText")); // NOI18N
        jBefore1.setFocusable(false);
        jBefore1.setHideActionText(true);
        jBefore1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBefore1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBefore1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar9.add(jBefore1);

        jCreatedAfterCheck.setText(bundle.getString("SearchEntryPanel.jCreatedAfterCheck.text")); // NOI18N
        jCreatedAfterCheck.setToolTipText(bundle.getString("SearchEntryPanel.jCreatedAfterCheck.toolTipText")); // NOI18N
        jCreatedAfterCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCreatedAfterCheckItemStateChanged(evt);
            }
        });

        jAfterSpinner1.setModel(new javax.swing.SpinnerDateModel());
        jAfterSpinner1.setToolTipText(bundle.getString("SearchEntryPanel.jAfterSpinner1.toolTipText")); // NOI18N
        jAfterSpinner1.setEnabled(false);

        jToolBar6.setFloatable(false);
        jToolBar6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar6.setRollover(true);
        jToolBar6.setBorderPainted(false);

        jAfter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jAfter1.setToolTipText(bundle.getString("SearchEntryPanel.jAfter1.toolTipText")); // NOI18N
        jAfter1.setFocusable(false);
        jAfter1.setHideActionText(true);
        jAfter1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAfter1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jAfter1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar6.add(jAfter1);

        javax.swing.GroupLayout jModifiedPanel1Layout = new javax.swing.GroupLayout(jModifiedPanel1);
        jModifiedPanel1.setLayout(jModifiedPanel1Layout);
        jModifiedPanel1Layout.setHorizontalGroup(
            jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jModifiedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCreatedBeforeCheck)
                    .addComponent(jBeforeSpinner1)
                    .addComponent(jCreatedAfterCheck)
                    .addComponent(jAfterSpinner1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jModifiedPanel1Layout.setVerticalGroup(
            jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jModifiedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jModifiedPanel1Layout.createSequentialGroup()
                        .addComponent(jCreatedBeforeCheck)
                        .addGap(0, 0, 0)
                        .addComponent(jBeforeSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jModifiedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jModifiedPanel1Layout.createSequentialGroup()
                        .addComponent(jCreatedAfterCheck)
                        .addGap(0, 0, 0)
                        .addComponent(jAfterSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jFileSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jFileSizePanel.border.title"))); // NOI18N

        jMoreThanCheck.setText(bundle.getString("SearchEntryPanel.jMoreThanCheck.text")); // NOI18N
        jMoreThanCheck.setToolTipText(bundle.getString("SearchEntryPanel.jMoreThanCheck.toolTipText")); // NOI18N
        jMoreThanCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jMoreThanCheckItemStateChanged(evt);
            }
        });

        jFileSizeScaler.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bytes", "KBytes", "MBytes", "GBytes", "TBytes" }));
        jFileSizeScaler.setSelectedIndex(1);
        jFileSizeScaler.setToolTipText(bundle.getString("SearchEntryPanel.jFileSizeScaler.toolTipText")); // NOI18N
        jFileSizeScaler.setEnabled(false);

        jLessThanCheck.setText(bundle.getString("SearchEntryPanel.jLessThanCheck.text")); // NOI18N
        jLessThanCheck.setToolTipText(bundle.getString("SearchEntryPanel.jLessThanCheck.toolTipText")); // NOI18N
        jLessThanCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLessThanCheckItemStateChanged(evt);
            }
        });

        jLessThanSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
        jLessThanSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jLessThanSpinner.toolTipText")); // NOI18N
        jLessThanSpinner.setEnabled(false);

        jGreaterThanSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
        jGreaterThanSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jGreaterThanSpinner.toolTipText")); // NOI18N
        jGreaterThanSpinner.setEnabled(false);

        jFileSizeScaler1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bytes", "KBytes", "MBytes", "GBytes", "TBytes" }));
        jFileSizeScaler1.setSelectedIndex(1);
        jFileSizeScaler1.setToolTipText(bundle.getString("SearchEntryPanel.jFileSizeScaler1.toolTipText")); // NOI18N
        jFileSizeScaler1.setEnabled(false);

        javax.swing.GroupLayout jFileSizePanelLayout = new javax.swing.GroupLayout(jFileSizePanel);
        jFileSizePanel.setLayout(jFileSizePanelLayout);
        jFileSizePanelLayout.setHorizontalGroup(
            jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLessThanCheck)
                    .addComponent(jLessThanSpinner)
                    .addComponent(jMoreThanCheck)
                    .addComponent(jGreaterThanSpinner))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFileSizeScaler1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileSizeScaler, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jFileSizePanelLayout.setVerticalGroup(
            jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLessThanCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFileSizeScaler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLessThanSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jMoreThanCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jGreaterThanSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileSizeScaler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jModifiedDate.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jModifiedDate.border.title"))); // NOI18N

        jBeforeSpinner.setModel(new javax.swing.SpinnerDateModel());
        jBeforeSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jBeforeSpinner.toolTipText")); // NOI18N
        jBeforeSpinner.setEnabled(false);

        jToolBar4.setFloatable(false);
        jToolBar4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar4.setRollover(true);
        jToolBar4.setBorderPainted(false);

        jBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jBefore.setToolTipText(bundle.getString("SearchEntryPanel.jBefore.toolTipText")); // NOI18N
        jBefore.setBorderPainted(false);
        jBefore.setFocusable(false);
        jBefore.setHideActionText(true);
        jBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBefore.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jBefore);

        jModifiedBeforeCheck.setText(bundle.getString("SearchEntryPanel.jModifiedBeforeCheck.text")); // NOI18N
        jModifiedBeforeCheck.setToolTipText(bundle.getString("SearchEntryPanel.jModifiedBeforeCheck.toolTipText")); // NOI18N
        jModifiedBeforeCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jModifiedBeforeCheckItemStateChanged(evt);
            }
        });

        jAfterSpinner.setModel(new javax.swing.SpinnerDateModel());
        jAfterSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jAfterSpinner.toolTipText")); // NOI18N
        jAfterSpinner.setEnabled(false);

        jToolBar5.setFloatable(false);
        jToolBar5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar5.setRollover(true);
        jToolBar5.setBorderPainted(false);

        jAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jAfter.setToolTipText(bundle.getString("SearchEntryPanel.jAfter.toolTipText")); // NOI18N
        jAfter.setFocusable(false);
        jAfter.setHideActionText(true);
        jAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAfter.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jAfter);

        jModifiedAfterCheck.setText(bundle.getString("SearchEntryPanel.jModifiedAfterCheck.text")); // NOI18N
        jModifiedAfterCheck.setToolTipText(bundle.getString("SearchEntryPanel.jModifiedAfterCheck.toolTipText")); // NOI18N
        jModifiedAfterCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jModifiedAfterCheckItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jModifiedDateLayout = new javax.swing.GroupLayout(jModifiedDate);
        jModifiedDate.setLayout(jModifiedDateLayout);
        jModifiedDateLayout.setHorizontalGroup(
            jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jModifiedDateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jModifiedBeforeCheck)
                    .addComponent(jModifiedAfterCheck)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jModifiedDateLayout.createSequentialGroup()
                        .addGroup(jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jAfterSpinner, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBeforeSpinner))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jModifiedDateLayout.setVerticalGroup(
            jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jModifiedDateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jModifiedBeforeCheck)
                .addGap(0, 0, 0)
                .addGroup(jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBeforeSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jModifiedAfterCheck)
                .addGap(0, 0, 0)
                .addGroup(jModifiedDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAfterSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setAutoscrolls(true);

        jSearch.setLayout(new javax.swing.BoxLayout(jSearch, javax.swing.BoxLayout.Y_AXIS));

        jBasicSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jBasicSearch.border.title"))); // NOI18N

        jLabel2.setLabelFor(jFileName);
        jLabel2.setText(bundle.getString("SearchEntryPanel.jLabel2.text")); // NOI18N

        jLookIn.setEditable(true);
        jLookIn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jLookIn.setToolTipText(bundle.getString("SearchEntryPanel.jLookIn.toolTipText")); // NOI18N
        jLookIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLookInActionPerformed(evt);
            }
        });

        jSubFolders.setSelected(true);
        jSubFolders.setText(bundle.getString("SearchEntryPanel.jSubFolders.text")); // NOI18N
        jSubFolders.setToolTipText(bundle.getString("SearchEntryPanel.jSubFolders.toolTipText")); // NOI18N
        jSubFolders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSubFoldersActionPerformed(evt);
            }
        });

        jFileName.setEditable(true);
        jFileName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jFileName.setToolTipText(bundle.getString("SearchEntryPanel.jFileName.toolTipText")); // NOI18N

        jLabel1.setLabelFor(jFileName);
        jLabel1.setText(bundle.getString("SearchEntryPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jFileName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jFileName1.setEditable(true);
        jFileName1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jFileName1.setToolTipText(bundle.getString("SearchEntryPanel.jFileName1.toolTipText")); // NOI18N

        jLabel3.setLabelFor(jFileName);
        jLabel3.setText(bundle.getString("SearchEntryPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jFileName1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jFileName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jCheckBox2.setText(bundle.getString("SearchEntryPanel.jCheckBox2.text_1")); // NOI18N
        jCheckBox2.setToolTipText(bundle.getString("SearchEntryPanel.jCheckBox2.toolTipText")); // NOI18N
        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox2ItemStateChanged(evt);
            }
        });

        jContainingText.setEditable(true);
        jContainingText.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jContainingText.setToolTipText(bundle.getString("SearchEntryPanel.jContainingText.toolTipText")); // NOI18N
        jContainingText.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jCheckBox2)
                .addGap(18, 18, 18)
                .addComponent(jContainingText, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContainingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jContainingText1.setEditable(true);
        jContainingText1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jContainingText1.setToolTipText(bundle.getString("SearchEntryPanel.jContainingText1.toolTipText")); // NOI18N
        jContainingText1.setEnabled(false);

        jCheckBox3.setText(bundle.getString("SearchEntryPanel.jCheckBox3.text")); // NOI18N
        jCheckBox3.setToolTipText(bundle.getString("SearchEntryPanel.jCheckBox3.toolTipText")); // NOI18N
        jCheckBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox3ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jCheckBox3)
                .addGap(18, 18, 18)
                .addComponent(jContainingText1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox3)
                    .addComponent(jContainingText1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jBasicSearchLayout = new javax.swing.GroupLayout(jBasicSearch);
        jBasicSearch.setLayout(jBasicSearchLayout);
        jBasicSearchLayout.setHorizontalGroup(
            jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBasicSearchLayout.createSequentialGroup()
                .addGroup(jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jBasicSearchLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jBasicSearchLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jLookIn, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jBasicSearchLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jSubFolders)))
                .addContainerGap())
        );
        jBasicSearchLayout.setVerticalGroup(
            jBasicSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBasicSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jLabel4.setText(bundle.getString("SearchEntryPanel.jLabel4.text")); // NOI18N

        jFilesizeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care" }));
        jFilesizeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFilesizeComboActionPerformed(evt);
            }
        });

        jLabel5.setText(bundle.getString("SearchEntryPanel.jLabel5.text")); // NOI18N

        jModifiedCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care", "Last week", "Last month", "Last year", "[Other time frame..]" }));
        jModifiedCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jModifiedComboActionPerformed(evt);
            }
        });

        jCreatedCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Don't care", "Last week", "Last month", "Last year", "[Other time frame..]" }));
        jCreatedCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCreatedComboActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("SearchEntryPanel.jLabel6.text")); // NOI18N

        jLabel7.setText(bundle.getString("SearchEntryPanel.jLabel7.text")); // NOI18N

        jAccessedCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAccessedComboActionPerformed(evt);
            }
        });

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
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAccessedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCreatedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jModifiedCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFilesizeCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 262, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jFilesizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jModifiedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jCreatedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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

        jComboBox1.setEditable(true);
        jSearch.add(jComboBox1);

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
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jExpertMode)
                .addGap(0, 0, 0)
                .addComponent(jDisable3rdParty, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDisableUnicodeDetection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        jLimitMaxFileSize.setSelected(true);
        jLimitMaxFileSize.setText(bundle.getString("SearchEntryPanel.jLimitMaxFileSize.text")); // NOI18N
        jLimitMaxFileSize.setToolTipText(bundle.getString("SearchEntryPanel.jLimitMaxFileSize.toolTipText")); // NOI18N
        jLimitMaxFileSize.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLimitMaxFileSizeItemStateChanged(evt);
            }
        });

        jSkipBinaryFiles.setSelected(true);
        jSkipBinaryFiles.setText(bundle.getString("SearchEntryPanel.jSkipBinaryFiles.text")); // NOI18N
        jSkipBinaryFiles.setToolTipText(bundle.getString("SearchEntryPanel.jSkipBinaryFiles.toolTipText")); // NOI18N

        jMaxFileSize.setModel(new javax.swing.SpinnerNumberModel(64.0f, 0.0f, null, 1.0f));
        jMaxFileSize.setToolTipText(bundle.getString("SearchEntryPanel.jMaxFileSize.toolTipText")); // NOI18N

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
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLimitMaxFileSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jMaxFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jIgnoreFilenameCase)
                    .addComponent(jIgnoreSymbolicLinks)
                    .addComponent(jSkipBinaryFiles)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jEnableFileTimeout)
                        .addGap(6, 6, 6)
                        .addComponent(jFileTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jStrictFilenameSearch))
                .addContainerGap())
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
                    .addComponent(jLimitMaxFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMaxFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jUseContentRegex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUseContentSearch)
                .addGap(3, 3, 3)
                .addComponent(jIgnoreContentCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLimitMaxHits)
                    .addComponent(jMaxHits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SearchEntryPanel.jPanel16.border.title"))); // NOI18N

        jIgnoreFolderCase.setSelected(true);
        jIgnoreFolderCase.setText(bundle.getString("SearchEntryPanel.jIgnoreFolderCase.text")); // NOI18N
        jIgnoreFolderCase.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreFolderCase.toolTipText")); // NOI18N

        jLimitMaxRecurse.setText(bundle.getString("SearchEntryPanel.jLimitMaxRecurse.text")); // NOI18N
        jLimitMaxRecurse.setToolTipText(bundle.getString("SearchEntryPanel.jLimitMaxRecurse.toolTipText")); // NOI18N
        jLimitMaxRecurse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLimitMaxRecurseItemStateChanged(evt);
            }
        });

        jMaxRecurse.setModel(new javax.swing.SpinnerNumberModel(5, 1, 100, 1));
        jMaxRecurse.setToolTipText(bundle.getString("SearchEntryPanel.jMaxRecurse.toolTipText")); // NOI18N
        jMaxRecurse.setEnabled(false);

        jManageSkipFolders.setText(bundle.getString("SearchEntryPanel.jManageSkipFolders.text")); // NOI18N
        jManageSkipFolders.setToolTipText(bundle.getString("SearchEntryPanel.jManageSkipFolders.toolTipText")); // NOI18N
        jManageSkipFolders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jManageSkipFoldersActionPerformed(evt);
            }
        });

        jIgnoreHiddenFolders.setText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFolders.text")); // NOI18N
        jIgnoreHiddenFolders.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreHiddenFolders.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jIgnoreHiddenFolders)
                    .addComponent(jIgnoreFolderCase)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLimitMaxRecurse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jMaxRecurse, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jManageSkipFolders))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jManageSkipFolders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jIgnoreFolderCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jIgnoreHiddenFolders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLimitMaxRecurse)
                    .addComponent(jMaxRecurse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGroup(jOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRestoreAll)))
                    .addGroup(jOptionsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jOptionsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jOptionsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jOptionsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jPanel14, jPanel15, jPanel16});

        jOptionsLayout.setVerticalGroup(
            jOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jOptionsLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
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

    private void SelectLookInFolder()
    {
        jFileChooser1.setApproveButtonText("OK");
        int ret = jFileChooser1.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION)
        {
            File fname = jFileChooser1.getSelectedFile();
            jLookIn.getModel().setSelectedItem(fname.getPath());
        }
    }
    private void SelectFileSize()
    {
        JOptionPane frame = new JOptionPane("Enter file size", JOptionPane.PLAIN_MESSAGE);
        
        FileSizePanel panel = new FileSizePanel();
        panel.set(new FileSizeEntry());
        frame.setMessage(panel);
        frame.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        frame.setMaximumSize(new Dimension(0xFFFF, 0xFFFF));
        frame.setMinimumSize(new Dimension(0, 0));
        frame.setPreferredSize(new Dimension(450, 300));
        JDialog dlg = frame.createDialog((Frame)SwingUtilities.getWindowAncestor(this), "Enter file size");
        dlg.pack();
        dlg.setVisible(true);
        Object ret = frame.getValue();

        if (ret != null && ((Integer)ret).equals(JOptionPane.OK_OPTION))
        {
            FileSizeEntry entry = panel.get();
            jFilesizeCombo.getModel().setSelectedItem(entry.toString());
        }
    }
    
    private void SelectDate(JComboBox jCombo, String msg)
    {
       JOptionPane frame = new JOptionPane(msg, JOptionPane.PLAIN_MESSAGE);
        
        FileDatePanel panel = new FileDatePanel();
        // panel.set(new FileDateEntry());
        frame.setMessage(panel);
        frame.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        frame.setMaximumSize(new Dimension(0xFFFF, 0xFFFF));
        frame.setMinimumSize(new Dimension(0, 0));
        frame.setPreferredSize(new Dimension(450, 300));
        JDialog dlg = frame.createDialog((Frame)SwingUtilities.getWindowAncestor(this), msg);
        dlg.pack();
        dlg.setVisible(true);
        Object ret = frame.getValue();

        if (ret != null && ((Integer)ret).equals(JOptionPane.OK_OPTION))
        {
            //FileDateEntry entry = panel.get();
            //jCombo.getModel().setSelectedItem(entry.toString());
        }
    }
    
    private void SelectModifiedDate()
    {
        SelectDate(jModifiedCombo, "Enter modified date");
    }

    private void SelectAccessedDate()
    {
        SelectDate(jAccessedCombo, "Enter accessed date");
    }

    private void SelectCreatedDate()
    {
        SelectDate(jCreatedCombo, "Enter created date");
    }

    private void jLookInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLookInActionPerformed

    }//GEN-LAST:event_jLookInActionPerformed

    private void jUseFileRegexStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jUseFileRegexStateChanged
        boolean sel = jUseFileRegex.isSelected();
        jPanel3.setVisible(sel);
        jPanel2.setVisible(!sel);
    }//GEN-LAST:event_jUseFileRegexStateChanged

    private void jUseContentRegexStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jUseContentRegexStateChanged
        boolean sel = jUseContentRegex.isSelected();
        jPanel4.setVisible(sel);
        jPanel6.setVisible(!sel);
    }//GEN-LAST:event_jUseContentRegexStateChanged

    private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox2ItemStateChanged
        jContainingText.setEnabled(jCheckBox2.isSelected());
    }//GEN-LAST:event_jCheckBox2ItemStateChanged

    private void jCheckBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox3ItemStateChanged
        jContainingText.setEnabled(jCheckBox3.isSelected());
    }//GEN-LAST:event_jCheckBox3ItemStateChanged

    private void jModifiedAfterCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jModifiedAfterCheckItemStateChanged
        this.jAfterSpinner.setEnabled(jModifiedAfterCheck.isSelected());
    }//GEN-LAST:event_jModifiedAfterCheckItemStateChanged

    private void jModifiedBeforeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jModifiedBeforeCheckItemStateChanged
        this.jBeforeSpinner.setEnabled(jModifiedBeforeCheck.isSelected());
    }//GEN-LAST:event_jModifiedBeforeCheckItemStateChanged

    private void jLessThanCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLessThanCheckItemStateChanged
        boolean sel = jLessThanCheck.isSelected();
        this.jLessThanSpinner.setEnabled(sel);
        this.jFileSizeScaler.setEnabled(sel);
    }//GEN-LAST:event_jLessThanCheckItemStateChanged

    private void jMoreThanCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jMoreThanCheckItemStateChanged
        boolean sel = jMoreThanCheck.isSelected();
        this.jGreaterThanSpinner.setEnabled(sel);
        this.jFileSizeScaler1.setEnabled(sel);
        // TODO add your handling code here:
    }//GEN-LAST:event_jMoreThanCheckItemStateChanged

    private void jCreatedBeforeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCreatedBeforeCheckItemStateChanged
        this.jBeforeSpinner1.setEnabled(jCreatedBeforeCheck.isSelected());
    }//GEN-LAST:event_jCreatedBeforeCheckItemStateChanged

    private void jCreatedAfterCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCreatedAfterCheckItemStateChanged
        this.jAfterSpinner1.setEnabled(jCreatedAfterCheck.isSelected());
    }//GEN-LAST:event_jCreatedAfterCheckItemStateChanged

    private void jAccessedBeforeCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jAccessedBeforeCheckItemStateChanged
        this.jBeforeSpinner2.setEnabled(jAccessedBeforeCheck.isSelected());
    }//GEN-LAST:event_jAccessedBeforeCheckItemStateChanged

    private void jAccessedAfterCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jAccessedAfterCheckItemStateChanged
        this.jAfterSpinner2.setEnabled(jAccessedAfterCheck.isSelected());
    }//GEN-LAST:event_jAccessedAfterCheckItemStateChanged

    private void jRestoreAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRestoreAllActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "Press OK to clear all configuration settings", "Restore all defaults?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return; //Cancel
        try {
            prefs.clear();
            this.Restore();
        } catch (BackingStoreException ex) {
            Logger.getLogger(SearchEntryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRestoreAllActionPerformed

    private void jManageSkipFoldersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jManageSkipFoldersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jManageSkipFoldersActionPerformed

    private void jLimitMaxFileSizeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLimitMaxFileSizeItemStateChanged
        jMaxFileSize.setEnabled(jLimitMaxFileSize.isSelected());
    }//GEN-LAST:event_jLimitMaxFileSizeItemStateChanged

    private void jLimitMaxRecurseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLimitMaxRecurseItemStateChanged
        jMaxRecurse.setEnabled(jLimitMaxRecurse.isSelected());
    }//GEN-LAST:event_jLimitMaxRecurseItemStateChanged

    private void jLimitMaxHitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLimitMaxHitsItemStateChanged
        jMaxHits.setEnabled(jLimitMaxHits.isSelected());
    }//GEN-LAST:event_jLimitMaxHitsItemStateChanged

    private void jEnableFileTimeoutItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jEnableFileTimeoutItemStateChanged
        jFileTimeout.setEnabled(jEnableFileTimeout.isSelected());
    }//GEN-LAST:event_jEnableFileTimeoutItemStateChanged

    private void jSubFoldersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSubFoldersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSubFoldersActionPerformed

    private void jFilesizeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFilesizeComboActionPerformed

        // TODO add your handling code here:
    }//GEN-LAST:event_jFilesizeComboActionPerformed

    private void jModifiedComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jModifiedComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jModifiedComboActionPerformed

    private void jCreatedComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCreatedComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCreatedComboActionPerformed

    private void jAccessedComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAccessedComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jAccessedComboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup ContentSearchType;
    private javax.swing.ButtonGroup FilenameSearchType;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JCheckBox jAccessedAfterCheck;
    private javax.swing.JCheckBox jAccessedBeforeCheck;
    private javax.swing.JComboBox<String> jAccessedCombo;
    private javax.swing.JButton jAfter;
    private javax.swing.JButton jAfter1;
    private javax.swing.JButton jAfter2;
    private javax.swing.JSpinner jAfterSpinner;
    private javax.swing.JSpinner jAfterSpinner1;
    private javax.swing.JSpinner jAfterSpinner2;
    private javax.swing.JPanel jBasicSearch;
    private javax.swing.JButton jBefore;
    private javax.swing.JButton jBefore1;
    private javax.swing.JButton jBefore2;
    private javax.swing.JSpinner jBeforeSpinner;
    private javax.swing.JSpinner jBeforeSpinner1;
    private javax.swing.JSpinner jBeforeSpinner2;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jContainingText;
    private javax.swing.JComboBox<String> jContainingText1;
    private javax.swing.JCheckBox jCreatedAfterCheck;
    private javax.swing.JCheckBox jCreatedBeforeCheck;
    private javax.swing.JComboBox<String> jCreatedCombo;
    private javax.swing.JCheckBox jDisable3rdParty;
    private javax.swing.JCheckBox jDisableUnicodeDetection;
    private javax.swing.JCheckBox jEnableFileTimeout;
    private javax.swing.JCheckBox jExpertMode;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JComboBox<String> jFileName;
    private javax.swing.JComboBox<String> jFileName1;
    private javax.swing.JPanel jFileSizePanel;
    private javax.swing.JComboBox<String> jFileSizeScaler;
    private javax.swing.JComboBox<String> jFileSizeScaler1;
    private javax.swing.JSpinner jFileTimeout;
    private javax.swing.JComboBox<String> jFilesizeCombo;
    private javax.swing.JSpinner jGreaterThanSpinner;
    private javax.swing.JCheckBox jIgnoreContentCase;
    private javax.swing.JCheckBox jIgnoreFilenameCase;
    private javax.swing.JCheckBox jIgnoreFolderCase;
    private javax.swing.JCheckBox jIgnoreHiddenFiles;
    private javax.swing.JCheckBox jIgnoreHiddenFolders;
    private javax.swing.JCheckBox jIgnoreSymbolicLinks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JCheckBox jLessThanCheck;
    private javax.swing.JSpinner jLessThanSpinner;
    private javax.swing.JCheckBox jLimitMaxFileSize;
    private javax.swing.JCheckBox jLimitMaxHits;
    private javax.swing.JCheckBox jLimitMaxRecurse;
    private javax.swing.JComboBox<String> jLookIn;
    private javax.swing.JButton jManageSkipFolders;
    private javax.swing.JSpinner jMaxFileSize;
    private javax.swing.JSpinner jMaxHits;
    private javax.swing.JSpinner jMaxRecurse;
    private javax.swing.JCheckBox jModifiedAfterCheck;
    private javax.swing.JCheckBox jModifiedBeforeCheck;
    private javax.swing.JComboBox<String> jModifiedCombo;
    private javax.swing.JPanel jModifiedDate;
    private javax.swing.JPanel jModifiedPanel1;
    private javax.swing.JPanel jModifiedPanel2;
    private javax.swing.JCheckBox jMoreThanCheck;
    private javax.swing.JPanel jOptions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JButton jRestoreAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jSearch;
    private javax.swing.JCheckBox jSkipBinaryFiles;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JButton jStartButton;
    private javax.swing.JButton jStopButton;
    private javax.swing.JCheckBox jStrictFilenameSearch;
    private javax.swing.JCheckBox jSubFolders;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JToolBar jToolBar9;
    private javax.swing.JRadioButton jUseContentRegex;
    private javax.swing.JRadioButton jUseContentSearch;
    private javax.swing.JRadioButton jUseFileGlobs;
    private javax.swing.JRadioButton jUseFileRegex;
    // End of variables declaration//GEN-END:variables
}
