/*
 * Copyright (C) 2017 cottr
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
import java.util.List;
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

/**
 *
 * @author cottr
 */
public class TestExpression extends javax.swing.JPanel implements DocumentListener, ClipboardOwner {

    /**
     * Creates new form RegexHelper
     * @param flags
     * @param name
     */
    public TestExpression(int flags, String name) {
        initComponents();
        prefs = Preferences.userNodeForPackage(SearchEntry.class);
        wizardName = name;
        
        Restore(); // Load back previous example content
        
        this.flags = flags;

        // Create some styles
        PreviewResultDoc doc = new PreviewResultDoc();
        try {
            doc.insertString(0, jTextPane2.getText(), doc.nameStyle);
        } catch (BadLocationException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTextPane2.setStyledDocument(doc);
        
        // Add document listener to the regex edit tool
        jTextPane1.getDocument().addDocumentListener(this);
        
        //MouseListener popupListener = (MouseListener) new PopupListener2();
        //this.jTextPane2.addMouseListener(popupListener);
    }
    
    
    private final String def1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec orci laoreet mauris venenatis malesuada. Sed vel pretium ex. Aliquam quis metus tristique, cursus augue eu, molestie erat. Praesent eu purus erat. Vestibulum placerat arcu at mi feugiat vulputate. Aenean faucibus libero a lectus iaculis semper. Integer eget ante non eros feugiat volutpat at a tellus. Nulla in sollicitudin tellus, nec tempus odio. Donec sagittis velit sed posuere varius. Duis magna leo, vulputate nec sapien non, efficitur euismod odio. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse congue justo quis sapien dignissim, vel pellentesque est gravida.";
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
        this.jStatus.setText(String.format("Status: Found %d match%s", count, count == 1 ? "" : "es"));
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
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jTopMenu = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jHelpButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jReferenceButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jCheatSheet = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jReference = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jHelpPage = new javax.swing.JTextPane();
        jToolBar2 = new javax.swing.JToolBar();
        jBackButton = new javax.swing.JButton();

        jPopupMenu1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenu1PopupMenuWillBecomeVisible(evt);
            }
        });

        jCut.setText("Cut");
        jCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCutActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jCut);

        jCopy.setText("Copy");
        jCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jCopy);

        jPaste.setText("Paste");
        jPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jPaste);
        jPopupMenu1.add(jSeparator1);

        jMenu1.setText("Reset");

        jResetLatin.setText("Lorem ipsum..");
        jResetLatin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetLatinActionPerformed(evt);
            }
        });
        jMenu1.add(jResetLatin);

        jPopupMenu1.add(jMenu1);

        setMaximumSize(new java.awt.Dimension(480, 350));
        setMinimumSize(new java.awt.Dimension(480, 350));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(480, 350));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jScrollPane3.setComponentPopupMenu(jPopupMenu1);

        jTextPane2.setText("<Enter sample text here to test regular expression>");
        jTextPane2.setToolTipText("Enter sample text or copy in from a file to test expression");
        jTextPane2.setComponentPopupMenu(jPopupMenu1);
        jTextPane2.setInheritsPopupMenu(true);
        jTextPane2.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane3.setViewportView(jTextPane2);

        jLabel3.setBackground(new java.awt.Color(204, 204, 204));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Text");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jLabel3.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(166, 215, 134));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Expression");
        jLabel2.setAlignmentX(0.5F);
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabel2.setOpaque(true);

        jScrollPane4.setComponentPopupMenu(jPopupMenu1);

        jTextPane3.setEditable(false);
        jTextPane3.setToolTipText("");
        jTextPane3.setComponentPopupMenu(jPopupMenu1);
        jTextPane3.setInheritsPopupMenu(true);
        jTextPane3.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane4.setViewportView(jTextPane3);

        jStatus.setBackground(new java.awt.Color(204, 204, 204));
        jStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jStatus.setText("Status");
        jStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jStatus.setOpaque(true);

        jTextPane1.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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

        jTopMenu.setPreferredSize(new java.awt.Dimension(150, 100));
        jTopMenu.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);
        jToolBar1.add(jSeparator2);

        jHelpButton.setBackground(new java.awt.Color(0, 0, 204));
        jHelpButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jHelpButton.setForeground(new java.awt.Color(255, 255, 255));
        jHelpButton.setText("Help");
        jHelpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jHelpButton.setMaximumSize(new java.awt.Dimension(150, 21));
        jHelpButton.setMinimumSize(new java.awt.Dimension(15, 21));
        jHelpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHelpButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(jHelpButton);
        jToolBar1.add(jSeparator3);

        jReferenceButton.setBackground(new java.awt.Color(0, 0, 204));
        jReferenceButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jReferenceButton.setForeground(new java.awt.Color(255, 255, 255));
        jReferenceButton.setText("Reference");
        jReferenceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jReferenceButton.setMaximumSize(new java.awt.Dimension(150, 21));
        jReferenceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jReferenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReferenceButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(jReferenceButton);
        jToolBar1.add(jSeparator4);

        jCheatSheet.setBackground(new java.awt.Color(0, 0, 204));
        jCheatSheet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheatSheet.setForeground(new java.awt.Color(255, 255, 255));
        jCheatSheet.setText("Cheat sheet");
        jCheatSheet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCheatSheet.setMaximumSize(new java.awt.Dimension(150, 21));
        jCheatSheet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheatSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheatSheetActionPerformed(evt);
            }
        });
        jToolBar1.add(jCheatSheet);

        jSeparator5.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator5.setForeground(new java.awt.Color(255, 255, 255));
        jToolBar1.add(jSeparator5);

        jTopMenu.add(jToolBar1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("<html><p>Enter a test expression and the syntax will automatically highlight the text within the search panel.</p></html>");
        jTopMenu.add(jLabel1, java.awt.BorderLayout.SOUTH);

        jReference.setLayout(new java.awt.BorderLayout());

        jHelpPage.setContentType("text/html"); // NOI18N
        jHelpPage.setText("<html>\n    <head>\n        <style type=\"text/css\">\n            table {\n                width: 100%\n            }\n            td, th {\n                background-color: #112233\n            }\n        </style>\n    </head>\n    <body>\n        HTML table test:\n        <div style=\"background-color: black\">\n            <table border=\"0\" cellpadding=\"2\" cellspacing=\"1\">\n                <tr>\n                    <td>cell1</td>\n                    <td>cell2</td>\n                </tr>\n                <tr>\n                    <td>cell3</td>\n                    <td>cell4</td>\n                </tr>\n        </div>\n    </body>\n</html>");
        jHelpPage.setToolTipText("");
        jScrollPane2.setViewportView(jHelpPage);

        jReference.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jToolBar2.setBorder(null);
        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        jBackButton.setText("Back");
        jBackButton.setFocusable(false);
        jBackButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBackButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBackButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(jBackButton);

        jReference.add(jToolBar2, java.awt.BorderLayout.NORTH);

        jLayeredPane1.setLayer(jTopMenu, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jReference, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 170, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTopMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jReference, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTopMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jReference, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jLayeredPane1, java.awt.BorderLayout.WEST);
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

    private void jReferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReferenceButtonActionPerformed
        this.jLayeredPane1.moveToFront(this.jReference);
        try {
            UpdateHelpPage("/help/regexRef.htm");
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.jBackButton.setText("< Reference");
    }//GEN-LAST:event_jReferenceButtonActionPerformed

    private void UpdateHelpPage(String resource) throws URISyntaxException, IOException
    {
        URL url = this.getClass().getResource(resource);
        Path path = Paths.get(url.toURI());

        List<String> content = Files.readAllLines(path, StandardCharsets.UTF_8);
        this.jHelpPage.setText(String.join("\n", content));
    }
    
    private void jBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBackButtonActionPerformed
        this.jLayeredPane1.moveToBack(this.jReference);
    }//GEN-LAST:event_jBackButtonActionPerformed

    private void jHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHelpButtonActionPerformed
        this.jLayeredPane1.moveToFront(this.jReference);
        try {
            UpdateHelpPage("/help/regexHelp.htm");
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.jBackButton.setText("< Help");
    }//GEN-LAST:event_jHelpButtonActionPerformed

    private void jCheatSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheatSheetActionPerformed
        this.jLayeredPane1.moveToFront(this.jReference);
        try {
            UpdateHelpPage("/help/regexCheat.htm");
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.jBackButton.setText("< Cheat Sheet");
    }//GEN-LAST:event_jCheatSheetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBackButton;
    private javax.swing.JButton jCheatSheet;
    private javax.swing.JMenuItem jCopy;
    private javax.swing.JMenuItem jCut;
    private javax.swing.JButton jHelpButton;
    private javax.swing.JTextPane jHelpPage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuItem jPaste;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPanel jReference;
    private javax.swing.JButton jReferenceButton;
    private javax.swing.JMenuItem jResetLatin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JLabel jStatus;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel jTopMenu;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent de) {
        UpdateRegex();
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        UpdateRegex();
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        UpdateRegex();
    }

    @Override
    public void lostOwnership(Clipboard clpbrd, Transferable t) {
        // Don't really care!
    }
    
}
