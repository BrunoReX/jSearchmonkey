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
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cottr
 */
public class TestExpression extends javax.swing.JPanel implements DocumentListener, ChangeListener, ClipboardOwner {

    /**
     * Creates new form RegexHelper
     * @param flags
     * @param name
     */
    public TestExpression(int flags, String name) {
        initComponents();

        // Load reference documents
        regexRef = ResourceBundle.getBundle("com.embeddediq.searchmonkey.regexRef.Bundle", Locale.getDefault());

        // Load langauge bundle
        rb = ResourceBundle.getBundle("com.embeddediq.searchmonkey.shared.Bundle", Locale.getDefault());
        mf = new MessageFormat(rb.getString("TestExpression.Status.String"));
        double[] fileLimits = {0,1,2};
        String [] fileStrings = {
            rb.getString("TestExpression.StatusNoMatches.String"),
            rb.getString("TestExpression.StatusOneMatch.String"),
            rb.getString("TestExpression.StatusMultipleMatches.String")
        };
        ChoiceFormat choiceForm = new ChoiceFormat(fileLimits, fileStrings);
        Format[] formats = {choiceForm, NumberFormat.getInstance()};
        mf.setFormats(formats);
        
        // Load default string
        def1 = rb.getString("TestExpression.LoremIpsum.String");

        
        prefs = Preferences.userNodeForPackage(SearchEntry.class);
        wizardName = name;
        
        Restore(); // Load back previous example content
        
        // Create some styles
        PreviewResultDoc doc = new PreviewResultDoc();
        try {
            doc.insertString(0, jTextPane2.getText(), doc.nameStyle);
        } catch (BadLocationException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTextPane2.setStyledDocument(doc);

        // Update flags
        this.flags = flags;
        jIgnoreContentCase.setSelected((this.flags & Pattern.CASE_INSENSITIVE) != 0);
        jLimitMaxHits.setSelected(true);
        this.jMaxHits.getModel().addChangeListener(this);
        
        // Add document listener to the regex edit tool
        // Or if the reference document is updated
        jTextPane1.getDocument().addDocumentListener(this);
        jTextPane2.getDocument().addDocumentListener(this);
        
        UpdateHelpPage(null);
        // jReferenceList.setSelectedIndex(0); // Select the first item
        this.UpdateReference(0);
    }
    
    private final ResourceBundle rb;
    private final ResourceBundle regexRef;
    private final MessageFormat mf;
    
    private final String def1;
    private String wizardName;
    
    private void Restore() // Load back previous example content
    {
        Gson g = new Gson();
        String json = prefs.get(wizardName, g.toJson(def1));
        String item = g.fromJson(json, String.class);
        jTextPane2.setText(item);
    }
    
    public void Save()
    {
        Gson g = new Gson();
        Object val = jTextPane2.getText();
        String json = g.toJson(val);
        prefs.put(wizardName, json); // Add list of look in folders        
    }
    private final Preferences prefs;
    
    public String getRegex()
    {
        return jTextPane1.getText();
    }
    
    public void setRegex(String val)
    {
        jTextPane1.setText(val);
    }
    
    private int flags;
    private void UpdateRegex()
    {
        int count = 0;
        try
        {
            String txt = jTextPane1.getText();
            
            jTextPane1.setBackground(UIManager.getLookAndFeelDefaults().getColor("TextPane.background"));
            jTextPane3.setText("");
            //if (txt.length() == 0) return;
            //flags |= Pattern.CASE_INSENSITIVE;
            //flags |= Pattern.DOTALL;
            //flags |= Pattern.UNICODE_CASE;
            //flags |= Pattern.UNICODE_CASE;
            
            Pattern compile = Pattern.compile(txt, flags);
            Matcher m = compile.matcher(this.jTextPane2.getText().replaceAll("(?!\\r)\\n", ""));
            PreviewResultDoc doc = (PreviewResultDoc)this.jTextPane2.getDocument();
            doc.setCharacterAttributes(0, doc.getLength(), doc.nameStyle, true);
            if (m.find())
            {
                if (txt.length() != 0)
                {
                    this.jTextPane1.setBackground(Color.GREEN);
                    do{
                        int s = m.start();
                        int e = m.end();
                        doc.setCharacterAttributes(s, e-s, doc.linkStyle, false);
                        count ++;
                        if ((this.limit > 0) && (count == this.limit)) break;
                    } while (m.find());
                }
            }
            else
            {
                this.jTextPane1.setBackground(Color.ORANGE);
                //this.jTextPane2.setSelectionColor(Color.ORANGE);
            }
        }
        catch (IllegalArgumentException ex)
        {
            this.jTextPane1.setBackground(Color.RED);
            this.jTextPane3.setText(ex.getLocalizedMessage());
        }
        // Update the status message
        Object[]args = new Object[]{count, count};
        this.jStatus.setText(mf.format(args));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jCut = new javax.swing.JMenuItem();
        jCopy = new javax.swing.JMenuItem();
        jPaste = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu1 = new javax.swing.JMenu();
        jResetLatin = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane3 = new javax.swing.JTextPane();
        jStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTopMenu = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jIgnoreContentCase = new javax.swing.JCheckBox();
        jLimitMaxHits = new javax.swing.JCheckBox();
        jMaxHits = new javax.swing.JSpinner();
        jReference = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jReferencePage = new javax.swing.JTextPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        jReferenceList = new javax.swing.JList<>();
        jCheat = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jHelpPage1 = new javax.swing.JTextPane();

        jPopupMenu1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenu1PopupMenuWillBecomeVisible(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        jCut.setText(bundle.getString("TestExpression.jCut.text")); // NOI18N
        jCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCutActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jCut);

        jCopy.setText(bundle.getString("TestExpression.jCopy.text")); // NOI18N
        jCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jCopy);

        jPaste.setText(bundle.getString("TestExpression.jPaste.text")); // NOI18N
        jPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jPaste);
        jPopupMenu1.add(jSeparator1);

        jMenu1.setText(bundle.getString("TestExpression.jMenu1.text")); // NOI18N

        jResetLatin.setText(bundle.getString("TestExpression.jResetLatin.text")); // NOI18N
        jResetLatin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetLatinActionPerformed(evt);
            }
        });
        jMenu1.add(jResetLatin);

        jPopupMenu1.add(jMenu1);

        setMinimumSize(new java.awt.Dimension(480, 350));
        setName("jLayeredPane1"); // NOI18N
        setPreferredSize(new java.awt.Dimension(480, 350));
        setLayout(new java.awt.BorderLayout(10, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jScrollPane3.setComponentPopupMenu(jPopupMenu1);

        jTextPane2.setText(bundle.getString("TestExpression.jTextPane2.text")); // NOI18N
        jTextPane2.setToolTipText(bundle.getString("TestExpression.jTextPane2.toolTipText")); // NOI18N
        jTextPane2.setComponentPopupMenu(jPopupMenu1);
        jTextPane2.setInheritsPopupMenu(true);
        jTextPane2.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane3.setViewportView(jTextPane2);

        jLabel3.setBackground(new java.awt.Color(204, 204, 204));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText(bundle.getString("TestExpression.jLabel3.text")); // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jLabel3.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText(bundle.getString("TestExpression.jLabel2.text")); // NOI18N
        jLabel2.setAlignmentX(0.5F);
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabel2.setOpaque(true);

        jScrollPane4.setComponentPopupMenu(jPopupMenu1);

        jTextPane3.setEditable(false);
        jTextPane3.setToolTipText(bundle.getString("TestExpression.jTextPane3.toolTipText")); // NOI18N
        jTextPane3.setInheritsPopupMenu(true);
        jTextPane3.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane4.setViewportView(jTextPane3);

        jStatus.setBackground(new java.awt.Color(204, 204, 204));
        jStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jStatus.setText(bundle.getString("TestExpression.jStatus.text")); // NOI18N
        jStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jStatus.setOpaque(true);

        jTextPane1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(jScrollPane4)
            .addComponent(jStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jStatus)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(260, 32767));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(260, 101));

        jTopMenu.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(bundle.getString("TestExpression.jLabel1.text")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(474, 140));
        jTopMenu.add(jLabel1, java.awt.BorderLayout.NORTH);

        jIgnoreContentCase.setText(bundle.getString("SearchEntryPanel.jIgnoreContentCase.text")); // NOI18N
        jIgnoreContentCase.setToolTipText(bundle.getString("SearchEntryPanel.jIgnoreContentCase.toolTipText")); // NOI18N
        jIgnoreContentCase.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jIgnoreContentCaseStateChanged(evt);
            }
        });

        jLimitMaxHits.setText(bundle.getString("SearchEntryPanel.jLimitMaxHits.text")); // NOI18N
        jLimitMaxHits.setToolTipText(bundle.getString("SearchEntryPanel.jLimitMaxHits.toolTipText")); // NOI18N
        jLimitMaxHits.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLimitMaxHitsItemStateChanged(evt);
            }
        });
        jLimitMaxHits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLimitMaxHitsActionPerformed(evt);
            }
        });

        jMaxHits.setModel(new javax.swing.SpinnerNumberModel(500, 1, null, 1));
        jMaxHits.setToolTipText(bundle.getString("SearchEntryPanel.jMaxHits.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMaxHits, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLimitMaxHits, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jIgnoreContentCase))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jIgnoreContentCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLimitMaxHits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMaxHits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTopMenu.add(jPanel1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(bundle.getString("TestExpression.jTopMenu.TabConstraints.tabTitle"), jTopMenu); // NOI18N

        jReference.setLayout(new java.awt.BorderLayout());

        jReferencePage.setEditable(false);
        jReferencePage.setContentType("text/html"); // NOI18N
        jReferencePage.setText(bundle.getString("TestExpression.jReferencePage.text")); // NOI18N
        jReferencePage.setToolTipText(bundle.getString("TestExpression.jReferencePage.toolTipText")); // NOI18N
        jReferencePage.setAutoscrolls(false);
        jReferencePage.setCaretPosition(1);
        jScrollPane2.setViewportView(jReferencePage);

        jReference.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jReferenceList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Character classes: character set [ABC]", "Character classes: negated set [^ABC]", "Character classes: range [A-Z]", "Character classes: dot .", "Character classes: word \\w", "Character classes: not word \\W", "Character classes: digit \\d", "Character classes: not digit \\D", "Character classes: whitespace \\s", "Character classes: not whitespace \\S", "Anchors: beginning ^", "Anchors: end $", "Anchors: word boundary \\b", "Anchors: not word boundary \\B", "Escaped: reserved characters \\+", "Escaped: octal escape \\0000", "Escaped: hexadecimal escape \\xFF", "Escaped: unicode escape \\uFFFF", "Escaped: extended escape \\u{FFFF}", "Escaped: control character \\cI", "Escaped: tab \\t", "Escaped: line feed \\n", "Escaped: vertical tab \\v", "Escaped: form feed \\f", "Escaped: carriage return \\r", "Escaped: null \\0", "Groups: capturing group (ABC)", "Groups: numeric reference \\1", "Groups: non-capturing group (?:ABC)", "Look-around: lookahead (?=ABC)", "Look-around: look-behind (?!ABC)", "Quantifiers & Alternation: plus +", "Quantifiers & Alternation: star *", "Quantifiers & Alternation: quantifier {1,3}", "Quantifiers & Alternation: optional ?", "Quantifiers & Alternation: lazy ?", "Quantifiers & Alternation: alternation |", "Substitution: match $&", "Substitution: capture group $1", "Substitution: before match $`", "Substitution: after match $'", "Substitution: escaped $ $$", "Substitution: escaped characters \\n", "Flags: ignore case i", "Flags: global search g", "Flags: multiline m", "Flags: unicode u", "Flags: sticky y" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jReferenceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jReferenceList.setToolTipText(bundle.getString("TestExpression.jReferenceList.toolTipText")); // NOI18N
        jReferenceList.setSelectedIndex(0);
        jReferenceList.setVisibleRowCount(6);
        jReferenceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jReferenceListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(jReferenceList);

        jReference.add(jScrollPane6, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab(bundle.getString("TestExpression.jReference.TabConstraints.tabTitle"), jReference); // NOI18N

        jCheat.setLayout(new java.awt.BorderLayout());

        jScrollPane5.setToolTipText(bundle.getString("TestExpression.jScrollPane5.toolTipText")); // NOI18N

        jHelpPage1.setEditable(false);
        jHelpPage1.setContentType("text/html"); // NOI18N
        jHelpPage1.setText(bundle.getString("TestExpression.jHelpPage1.text")); // NOI18N
        jHelpPage1.setToolTipText(bundle.getString("TestExpression.jHelpPage1.toolTipText")); // NOI18N
        jHelpPage1.setAutoscrolls(false);
        jHelpPage1.setCaretPosition(1);
        jScrollPane5.setViewportView(jHelpPage1);

        jCheat.add(jScrollPane5, java.awt.BorderLayout.LINE_END);

        jTabbedPane1.addTab(bundle.getString("TestExpression.jCheat.TabConstraints.tabTitle"), jCheat); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void jResetLatinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetLatinActionPerformed
        jTextPane2.setText(def1);
        UpdateRegex();
    }//GEN-LAST:event_jResetLatinActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        jSeparator1.setVisible(evt.getSource().equals(this.jTextPane2));
        jMenu1.setVisible(evt.getSource().equals(this.jTextPane2));
        
        
        JTextPane item = (JTextPane)this.jPopupMenu1.getInvoker();
        boolean enable_copy = item.getSelectedText() != null;
        jCut.setEnabled(enable_copy);
        jCopy.setEnabled(enable_copy);        
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void jCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCutActionPerformed
        // Copy the text
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        JTextPane item = (JTextPane)jPopupMenu1.getInvoker();
        clipboard.setContents(new StringSelection(item.getSelectedText()), this);
        
        // Remove the text
        try {
            item.getDocument().remove(item.getSelectionStart(), item.getSelectionEnd());
        } catch (BadLocationException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCutActionPerformed

    private void jCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCopyActionPerformed
        // Copy the text
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        JTextPane item = (JTextPane)jPopupMenu1.getInvoker();
        clipboard.setContents(new StringSelection(item.getSelectedText()), this);
    }//GEN-LAST:event_jCopyActionPerformed

    private void jPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasteActionPerformed
        // Paste the text
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if ((contents != null) &&
          contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          try {
            String result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            
            // Paste the data
            JTextPane item = (JTextPane)jPopupMenu1.getInvoker();
            item.getDocument().remove(item.getSelectionStart(), item.getSelectionEnd());
            item.getDocument().insertString(item.getCaretPosition(), result, null);
          }
          catch (UnsupportedFlavorException | IOException | BadLocationException ex){
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
          }
        }

    }//GEN-LAST:event_jPasteActionPerformed

    private void jReferenceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jReferenceListValueChanged
        if (evt.getValueIsAdjusting()) return;
        int idx = ((JList)evt.getSource()).getSelectedIndex();
        if (idx == -1) return;
        UpdateReference(idx);
    }//GEN-LAST:event_jReferenceListValueChanged

    int limit = 0;
    private void jLimitMaxHitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLimitMaxHitsItemStateChanged
        jMaxHits.setEnabled(jLimitMaxHits.isSelected());
        limit = 0;
        if (jLimitMaxHits.isSelected())
        {
            limit = (int)jMaxHits.getValue();
        }
        UpdateRegex();
    }//GEN-LAST:event_jLimitMaxHitsItemStateChanged

    private void jLimitMaxHitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLimitMaxHitsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLimitMaxHitsActionPerformed

    private void jIgnoreContentCaseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jIgnoreContentCaseStateChanged
        if (jIgnoreContentCase.isSelected())
        {
            flags |= Pattern.CASE_INSENSITIVE;
        } else {
            flags &= ~Pattern.CASE_INSENSITIVE;
        }
        UpdateRegex();
    }//GEN-LAST:event_jIgnoreContentCaseStateChanged
    
    private void UpdateReference(int idx)
    {                                            
        try {
            String head = regexRef.getString("TestExpression.RegexRef.HEAD");
            String content = "<body>" + regexRef.getString(String.format("TestExpression.RegexRef.%d", idx + 1)) + "</body>";
            this.jReferencePage.setText("<html>" + head + content + "</html>");
            this.jReferencePage.setCaretPosition(1);
        } catch (IndexOutOfBoundsException ex) {
            // Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void UpdateHelpPage(String resource)
    {
        boolean restoreMenu = (resource == null);
        jReference.setVisible(!restoreMenu);
        jTopMenu.setVisible(restoreMenu);
        this.setSize(250, 200);
        if (restoreMenu) return; // We are done

        try
        {
            URL url = this.getClass().getResource("/help/regexRef.htm");
            Path path = Paths.get(url.toURI());

            List<String> content = Files.readAllLines(path, StandardCharsets.UTF_8);
            this.jReferencePage.setText(String.join("\n", content));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jCheat;
    private javax.swing.JMenuItem jCopy;
    private javax.swing.JMenuItem jCut;
    private javax.swing.JTextPane jHelpPage1;
    private javax.swing.JCheckBox jIgnoreContentCase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox jLimitMaxHits;
    private javax.swing.JSpinner jMaxHits;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuItem jPaste;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPanel jReference;
    private javax.swing.JList<String> jReferenceList;
    private javax.swing.JTextPane jReferencePage;
    private javax.swing.JMenuItem jResetLatin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel jStatus;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane3;
    private javax.swing.JPanel jTopMenu;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent de) {
        SwingUtilities.invokeLater(() -> {
            UpdateRegex();
        });
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        SwingUtilities.invokeLater(() -> {
            UpdateRegex();
        });
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        // UpdateRegex();
    }

    @Override
    public void lostOwnership(Clipboard clpbrd, Transferable t) {
        // Don't really care!
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        this.limit = (int)this.jMaxHits.getValue();
        UpdateRegex();
    }
    
}
