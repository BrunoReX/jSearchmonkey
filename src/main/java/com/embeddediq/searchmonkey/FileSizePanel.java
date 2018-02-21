/*
 * Copyright (C) 2018 cottr
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

/**
 *
 * @author cottr
 */
public class FileSizePanel extends javax.swing.JPanel {

    /**
     * Creates new form FileSizePanel
     */
    public FileSizePanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLessThanSpinner = new javax.swing.JSpinner();
        jMoreThanCheck = new javax.swing.JCheckBox();
        jMoreThanSpinner = new javax.swing.JSpinner();
        jLessThanFileSizeScaler = new javax.swing.JComboBox<>();
        jLessThanCheck = new javax.swing.JCheckBox();
        jMoreThanFileSizeScaler = new javax.swing.JComboBox<>();

        setMinimumSize(new java.awt.Dimension(335, 233));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setText("<html><p>Setting these file size constraints size can help to increase the speed of searching because Searchmonkey can simply skip the very large or very small files.</p><br/><p>If one option is checked, then the other constraint will be set to: <em>don't care</em>. You can specify a range, for example <em>between 3 KBytes and 10 Mbytes</em>. However, if you want the opposite, then this too is possible, for example <em>greater than 10 MBytes OR less than 3 Kbytes</em>. Or you can just use one constraint, for example <em>Less than 5 MBytes</em></p></html>");
        add(jLabel1, java.awt.BorderLayout.CENTER);

        jLessThanSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/embeddediq/searchmonkey/Bundle"); // NOI18N
        jLessThanSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jLessThanSpinner.toolTipText")); // NOI18N
        jLessThanSpinner.setEnabled(false);

        jMoreThanCheck.setText(bundle.getString("SearchEntryPanel.jMoreThanCheck.text")); // NOI18N
        jMoreThanCheck.setToolTipText(bundle.getString("SearchEntryPanel.jMoreThanCheck.toolTipText")); // NOI18N
        jMoreThanCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jMoreThanCheckItemStateChanged(evt);
            }
        });

        jMoreThanSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));
        jMoreThanSpinner.setToolTipText(bundle.getString("SearchEntryPanel.jGreaterThanSpinner.toolTipText")); // NOI18N
        jMoreThanSpinner.setEnabled(false);

        jLessThanFileSizeScaler.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bytes", "KBytes", "MBytes", "GBytes", "TBytes" }));
        jLessThanFileSizeScaler.setSelectedIndex(2);
        jLessThanFileSizeScaler.setToolTipText(bundle.getString("SearchEntryPanel.jFileSizeScaler.toolTipText")); // NOI18N
        jLessThanFileSizeScaler.setEnabled(false);

        jLessThanCheck.setText(bundle.getString("SearchEntryPanel.jLessThanCheck.text")); // NOI18N
        jLessThanCheck.setToolTipText(bundle.getString("SearchEntryPanel.jLessThanCheck.toolTipText")); // NOI18N
        jLessThanCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jLessThanCheckItemStateChanged(evt);
            }
        });

        jMoreThanFileSizeScaler.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bytes", "KBytes", "MBytes", "GBytes", "TBytes" }));
        jMoreThanFileSizeScaler.setSelectedIndex(1);
        jMoreThanFileSizeScaler.setToolTipText(bundle.getString("SearchEntryPanel.jFileSizeScaler1.toolTipText")); // NOI18N
        jMoreThanFileSizeScaler.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMoreThanCheck)
                    .addComponent(jLessThanCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLessThanSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(jMoreThanSpinner))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMoreThanFileSizeScaler, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLessThanFileSizeScaler, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jMoreThanFileSizeScaler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMoreThanSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMoreThanCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLessThanCheck)
                    .addComponent(jLessThanSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLessThanFileSizeScaler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void jMoreThanCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jMoreThanCheckItemStateChanged
        boolean sel = jMoreThanCheck.isSelected();
        this.jMoreThanSpinner.setEnabled(sel);
        this.jMoreThanFileSizeScaler.setEnabled(sel);
    }//GEN-LAST:event_jMoreThanCheckItemStateChanged

    private void jLessThanCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jLessThanCheckItemStateChanged
        boolean sel = jLessThanCheck.isSelected();
        this.jLessThanSpinner.setEnabled(sel);
        this.jLessThanFileSizeScaler.setEnabled(sel);
    }//GEN-LAST:event_jLessThanCheckItemStateChanged

    public void set(FileSizeEntry init)
    {
        if (init.useMinSize) {
            int idx = FileSizeEntry.getIndex(init.minSize);
            jMoreThanCheck.setSelected(true);
            // jGreaterThanSpinner.setEnabled(true);
            //this.jFileSizeScaler1.setEnabled(sel);
            this.jMoreThanFileSizeScaler.setSelectedIndex(idx);
            this.jMoreThanSpinner.setValue((double)init.minSize / (double)(2^idx));
        }
        if (init.useMaxSize) {
            int idx = FileSizeEntry.getIndex(init.maxSize);
            jLessThanCheck.setSelected(true);
            this.jLessThanFileSizeScaler.setSelectedIndex(idx);
            this.jLessThanSpinner.setValue((double)init.maxSize / (double)(2^idx));
        }
    }
    
    public FileSizeEntry get()
    {
        FileSizeEntry init = new FileSizeEntry();
        if (jMoreThanCheck.isSelected()) {
            init.useMinSize = true;
            init.minSize = (long)((double)jMoreThanSpinner.getValue() * (double)(2^jMoreThanFileSizeScaler.getSelectedIndex()));
        }
        if (jLessThanCheck.isSelected()) {
            init.useMaxSize = true;
            init.maxSize = (long)((double)jLessThanSpinner.getValue() * (double)(2^jLessThanFileSizeScaler.getSelectedIndex()));
        }

        return init;
    }

//    public String getString()
//    {
//        get().toString();
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox jLessThanCheck;
    private javax.swing.JComboBox<String> jLessThanFileSizeScaler;
    private javax.swing.JSpinner jLessThanSpinner;
    private javax.swing.JCheckBox jMoreThanCheck;
    private javax.swing.JComboBox<String> jMoreThanFileSizeScaler;
    private javax.swing.JSpinner jMoreThanSpinner;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
