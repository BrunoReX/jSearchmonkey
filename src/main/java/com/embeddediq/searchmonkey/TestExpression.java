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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author cottr
 */
public class TestExpression extends javax.swing.JPanel implements DocumentListener {

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
        
//        try {
//            String fn = "/help/regex.htm";
//            URL url = getClass().getResource(fn);
//            jTextPane1.setPage(url);
//        } catch (IOException ex) {
//            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // Create some styles
        PreviewResultDoc doc = new PreviewResultDoc();
        try {
            doc.insertString(0, jTextPane2.getText(), doc.nameStyle);
        } catch (BadLocationException ex) {
            Logger.getLogger(TestExpression.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTextPane2.setStyledDocument(doc);

        // Add listener
        // jTextField1.getDocument().addDocumentListener(this);
        // jTextPane2.getDocument().addDocumentListener(this);
        
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
//    private void RestoreDefaults() // Load back previous example content
//    {
//        jTextPane2.setText(def1);
//    }
    
    public void Save()
    {
        Gson g = new Gson();
        Object val = jTextPane2.getText();
        String json = g.toJson(val);
        prefs.put(wizardName, json); // Add list of look in folders        
    }
    private final Preferences prefs;
    
    public JButton getCloseButton()
    {
        return this.jButton2;
    }
    
    public String getRegex()
    {
        return jTextField1.getText();
    }
    
    public void setRegex(String val)
    {
        jTextField1.setText(val);
    }
    
    private int flags;
    private void UpdateRegex()
    {
        int count = 0;
        try
        {
            String txt = jTextField1.getText();
            if (txt.length() == 0) return;
            Pattern compile = Pattern.compile(txt, flags);
            Matcher m = compile.matcher(this.jTextPane2.getText().replaceAll("(?!\\r)\\n", ""));
            PreviewResultDoc doc = (PreviewResultDoc)this.jTextPane2.getDocument();
            doc.setCharacterAttributes(0, doc.getLength(), doc.nameStyle, true);
            if (m.find())
            {
                this.jTextField1.setBackground(Color.GREEN);
                do{
                    int s = m.start();
                    int e = m.end();
                    doc.setCharacterAttributes(s, e-s, doc.linkStyle, false);
                    count ++;
                    //doc.setParagraphAttributes(s, e-s, as, false);
                } while (m.find());
            }
            else
            {
                this.jTextField1.setBackground(Color.ORANGE);
                this.jTextPane2.setSelectionColor(Color.ORANGE);
            }
        }
        catch (IllegalArgumentException ex)
        {
            this.jTextField1.setBackground(Color.RED);
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
        jTextField1 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane3 = new javax.swing.JTextPane();
        jStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setText("Cut");
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Copy");
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setText("Paste");
        jPopupMenu1.add(jMenuItem3);
        jPopupMenu1.add(jSeparator1);

        jMenuItem4.setText("jMenuItem4");
        jPopupMenu1.add(jMenuItem4);

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
                .addContainerGap(217, Short.MAX_VALUE))
            .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                    .addContainerGap(228, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        add(jDesktopPane1, java.awt.BorderLayout.WEST);

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jScrollPane3.setComponentPopupMenu(jPopupMenu1);

        jTextPane2.setText("<Enter sample text here to test regular expression>");
        jTextPane2.setToolTipText("Enter sample text or copy in from a file to test expression");
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

        jTextField1.setText("//g");
        jTextField1.setToolTipText("Enter test regular expresion");
        jTextField1.setComponentPopupMenu(jPopupMenu1);
        jTextField1.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jTextField1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField1InputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });

        jScrollPane4.setComponentPopupMenu(jPopupMenu1);

        jTextPane3.setText("<Enter sample text here to test regular expression>");
        jTextPane3.setToolTipText("Enter sample text or copy in from a file to test expression");
        jTextPane3.setInheritsPopupMenu(true);
        jTextPane3.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane4.setViewportView(jTextPane3);

        jStatus.setBackground(new java.awt.Color(204, 204, 204));
        jStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jStatus.setText("Status");
        jStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jStatus.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
            .addComponent(jScrollPane4)
            .addComponent(jStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, 0)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jStatus)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jButton1.setText("Apply");
        jButton1.setToolTipText("Click apply to test regular expression");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setText("Close");
        jButton2.setToolTipText("Close the dialog");
        jPanel3.add(jButton2);

        jButton3.setText("Reset");
        jButton3.setToolTipText("Reset dialog back to the defaults");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3);

        add(jPanel3, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField1InputMethodTextChanged
        // TODO - add a short delay
        //UpdateRegex();
    }//GEN-LAST:event_jTextField1InputMethodTextChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.jTextField1.setText("sample");
        this.jTextPane2.setText("<Copy and paste your sample text here>");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        UpdateRegex();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel jStatus;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent de) {
        // TODO - add a short delay
        UpdateRegex();
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        // TODO - add a short delay
        UpdateRegex();
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        // TODO - add a short delay
        UpdateRegex();
    }

    class PopupListener2 implements MouseListener
    {
    
        private void showPopup(MouseEvent e)
        {
            if (e.isPopupTrigger()) {
                jPopupMenu1.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
            showPopup(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            // if (me.getButton() == MouseEvent.BUTTON2)
            {
                showPopup(me);
            }
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent me) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
