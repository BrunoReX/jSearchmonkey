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
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton5 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButton6 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
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

        jPopupMenu1.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenu1PopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItem1.setText("Cut");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Copy");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setText("Paste");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);
        jPopupMenu1.add(jSeparator1);

        jMenu1.setText("Reset");

        jMenuItem4.setText("Lorem ipsum..");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jPopupMenu1.add(jMenu1);

        setMaximumSize(new java.awt.Dimension(480, 350));
        setMinimumSize(new java.awt.Dimension(480, 350));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(480, 350));
        setLayout(new java.awt.BorderLayout());

        jDesktopPane1.setBackground(new java.awt.Color(101, 70, 52));
        jDesktopPane1.setPreferredSize(new java.awt.Dimension(170, 324));

        jLabel1.setBackground(new java.awt.Color(102, 102, 102));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("<html><p>Enter a test expression and the syntax will automatically highlight the text within the search panel.</p></html>");

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);
        jToolBar1.add(jSeparator2);

        jButton4.setBackground(new java.awt.Color(0, 0, 204));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Help");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMaximumSize(new java.awt.Dimension(150, 21));
        jButton4.setMinimumSize(new java.awt.Dimension(15, 21));
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);
        jToolBar1.add(jSeparator3);

        jButton5.setBackground(new java.awt.Color(0, 0, 204));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Reference");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMaximumSize(new java.awt.Dimension(150, 21));
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);
        jToolBar1.add(jSeparator4);

        jButton6.setBackground(new java.awt.Color(0, 0, 204));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Cheat sheet");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setMaximumSize(new java.awt.Dimension(150, 21));
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);

        jSeparator5.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator5.setForeground(new java.awt.Color(255, 255, 255));
        jToolBar1.add(jSeparator5);

        jDesktopPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jToolBar1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDesktopPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
            .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                    .addContainerGap(261, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        add(jDesktopPane1, java.awt.BorderLayout.WEST);

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
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
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
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        jTextPane2.setText(def1);
        UpdateRegex();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        jSeparator1.setVisible(evt.getSource().equals(this.jTextPane2));
        jMenu1.setVisible(evt.getSource().equals(this.jTextPane2));
        
        
        JTextPane item = (JTextPane)this.jPopupMenu1.getInvoker();
        boolean enable_copy = item.getSelectedText() != null;
        jMenuItem1.setEnabled(enable_copy);
        jMenuItem2.setEnabled(enable_copy);        
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
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
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Copy the text
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        JTextPane item = (JTextPane)jPopupMenu1.getInvoker();
        clipboard.setContents(new StringSelection(item.getSelectedText()), this);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
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

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
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
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
