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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.System.nanoTime;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.Timer;
import javax.swing.text.JTextComponent;

/**
 *
 * @author adam
 */
public class SearchMatchView extends javax.swing.JPanel implements ActionListener {

    /**
     * Creates new form SearchMatchView
     */
    public SearchMatchView() {
        initComponents();

        DefaultCaret caret;
        
        // Disable auto-scrolling on the text window
        caret = (DefaultCaret) jHitsTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        // Disable auto-scrolling on the text window
        caret = (DefaultCaret) jSummaryTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        // Disable auto-scrolling on the text window
        caret = (DefaultCaret) jPreviewTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                
        doc = new PreviewResultDoc();
        jHitsTextPane.setDocument(doc);

        // Create delay
        int delayMs = 250;
        timer = new Timer(delayMs, this);
        timer.setInitialDelay(delayMs);
        timer.setRepeats(false);
        timer.setCoalesce(true);
    }

    PreviewResultDoc doc;

    /*
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals("state"))
        {
            StateValue sv = (StateValue)pce.getNewValue();
            if (sv.equals(StateValue.DONE))
            {
                task = null; // This must be cleared before we can we start the task
                if (busy.get() == 2)
                {
                    timer.restart();
                }
                busy.set(0);
            }
        }
    }
    */

     
    private ContentMatch match;
    private SearchEntry entry;

    public void setContentMatch(SearchEntry entry) { //ContentMatch match) {
        this.entry = entry;
        this.match = new ContentMatch(entry);
        this.jSummaryTextArea.setText("");
    }
    public ContentMatch getContentMatch() {
        return this.match;
    }

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

    
    private static final String[] FSIZE = new String[] {
        "Bytes",
        "KBytes",
        "MBytes",
        "GBytes",
        "TBytes",
        "PBytes",
    };
    
    public void UpdateSummary()
    {
        // ss will be null
        try {
            int end = jSummaryTextArea.getLineEndOffset(0);
            String msg = String.format("Search was cancelled\n\n");
            jSummaryTextArea.replaceRange(msg, 0, end);
        } catch (BadLocationException ex) {
            Logger.getLogger(SearchMatchView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void UpdateSummary(SearchSummary ss, boolean interim)
    {
        jSummaryTextArea.setText(""); // Clear before entering
        if (interim) {
            jSummaryTextArea.append(String.format("Search in progress...\n\n"));
        } else {
            jSummaryTextArea.append(String.format("Search completed in %d seconds.\n\n", (ss.endTime - ss.startTime)/1000000000));
        }
        
        jSummaryTextArea.append(String.format("Matched: %d file%s\n", ss.matchFileCount, ss.matchFileCount != 1 ? "s" : ""));
        if (ss.totalMatchBytes > 0)
        {
            int order = getFileOrder(ss.totalMatchBytes);
            double divider = Math.pow(1024, order);
            
            jSummaryTextArea.append(String.format("Size on disk: %.1f %s\n", ((double)ss.totalMatchBytes / divider), FSIZE[order]));
        }
        if (ss.totalContentMatch > 0)
        {
            jSummaryTextArea.append(String.format("Content found: %d hit%s\n", ss.totalContentMatch, ss.totalContentMatch != 1 ? "s" : ""));
        }
        if (ss.totalFolders > 0)
        {
            jSummaryTextArea.append(String.format("Processed: %d folder%s\n", ss.totalFolders, ss.totalFolders != 1 ? "s" : ""));
        }
        if (ss.skippedFolders > 0)
        {
            jSummaryTextArea.append(String.format("Skipped: %d folder%s\n", ss.skippedFolders, ss.skippedFolders != 1 ? "s" : ""));
        }
        if (ss.totalFiles > 0)
        {
            jSummaryTextArea.append(String.format("Processed: %d file%s\n", ss.totalFiles, ss.totalFiles != 1 ? "s" : ""));
        }
        if (ss.skippedFiles > 0)
        {
            jSummaryTextArea.append(String.format("Skipped: %d file%s\n", ss.skippedFiles, ss.skippedFiles != 1 ? "s" : ""));
        }
        jSummaryTextArea.append("\n");
        if (ss.maxMatchBytes > 0)
        {
            int order = getFileOrder(ss.maxMatchBytes);
            double divider = Math.pow(1024, order);
            
            jSummaryTextArea.append(String.format("Largest file size: %.1f %s\n", ((double)ss.maxMatchBytes / divider), FSIZE[order]));
        }
        if (ss.minMatchBytes >= 0)
        {
            int order = getFileOrder(ss.minMatchBytes);
            double divider = Math.pow(1024, order);
            
            jSummaryTextArea.append(String.format("Smallest file size: %.1f %s\n", ((double)ss.minMatchBytes / divider), FSIZE[order]));
        }
        if (ss.maxContentMatch > 0)
        {
            jSummaryTextArea.append(String.format("Largest matches per file: %d hit%s\n", ss.maxContentMatch, ss.maxContentMatch != 1 ? "s" : ""));
        }
        if (ss.firstModified != null)
        {
            jSummaryTextArea.append(String.format("Oldest modified file: %s\n", ss.firstModified));
        }
        if (ss.lastModified != null)
        {
            jSummaryTextArea.append(String.format("Newest modified file: %s\n", ss.lastModified));
        }
        if (ss.firstCreated != null)
        {
            jSummaryTextArea.append(String.format("Oldest file was created: %s\n", ss.firstCreated));
        }
        if (ss.lastCreated != null)
        {
            jSummaryTextArea.append(String.format("Newest file was created: %s\n", ss.lastCreated));
        }
        if (ss.firstAccessed != null)
        {
            jSummaryTextArea.append(String.format("Oldest file was accessed: %s\n", ss.firstAccessed));
        }
        if (ss.lastAccessed != null)
        {
            jSummaryTextArea.append(String.format("Newest file was accessed: %s\n", ss.lastAccessed));
        }
        
        // We need to make this list thread-safe
        if (ss.skippedFolderList.size() > 0)
        {
            jSummaryTextArea.append("\nThe following folders were skipped:\n");
            for (String item: ss.skippedFolderList) {
                jSummaryTextArea.append(" > " + item + "\n");
            }
        }
        if (ss.skippedFileList.size() > 0)
        {
            jSummaryTextArea.append("\nThe following files were skipped:\n");
            for (String item: ss.skippedFileList) {
                jSummaryTextArea.append(" > " + item + "\n");
            }
        }
    }
            
    public class MatchResult2 {
        public MatchResult2(String title)
        {
            this.title = title;
            isTitle = true;
        }

        public MatchResult2(int line_nr, String title, List<MatchResult> results)
        {
            this.title = title;
            this.line_nr = line_nr;
            this.results = results;
            isTitle = false;
        }
        
        public String title;
        public boolean isTitle = false;
        public int line_nr;
        public List<MatchResult> results;
        public int start; // List of start/end results
        public int end; // List of start/end results
    }

    public class ViewUpdate extends SwingWorker<PreviewResultDoc, MatchResult2> { 
        Path[] paths;
        public ViewUpdate(Path[] paths)
        {
            super();
            this.paths = paths;
        }
        

        @Override
        protected PreviewResultDoc doInBackground() {            
            PreviewResultDoc previewDoc = new PreviewResultDoc();
            for (Path path: paths)
            {
                if (isCancelled()) break; // Check for cancel
                consumePath(path, previewDoc);
            }
            return previewDoc;
        }

        @Override
        protected void done() {
            if (isCancelled()) return; // Ignore cancelled
            try {
                PreviewResultDoc doc2 = get();
                jPreviewTextPane.setDocument(doc2);
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SearchMatchView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        private void consumePath(Path path, PreviewResultDoc previewDoc)
        {
            // ContentMatch cm = new ContentMatch();
            long hitCount = 0L;
            long startTime = nanoTime();
            
            String lines = match.GetContent(path); // ContentMatch.GetContent(path);
            if (lines == null || lines.equals("")) return;
            
            try /*(LineIterator lineIterator = FileUtils.lineIterator(path.toFile())) */ {
                publish(new MatchResult2(path.toString() + "\n"));
                previewDoc.insertString(previewDoc.getLength(), path.toString() + "\n", previewDoc.pathStyle);
                int i = 0;
                //while (lineIterator.hasNext())
                for (String line: lines.split("\n"))
                {
                    if (isCancelled()) break; // Check for cancel
                    // String line = lineIterator.nextLine();
                    previewDoc.insertString(previewDoc.getLength(), line + "\n", previewDoc.nameStyle);
                    i ++;
                    if (entry.containingText != null)
                    {
                        List<MatchResult> results = match.getMatches(line);
                        if (results.size() > 0)
                        {
                            publish(new MatchResult2(i, line, results));
                            for (MatchResult res: results)
                            {
                                int s = previewDoc.getLength() + res.start() - line.length() - 1;
                                int e = res.end() - res.start();
                                previewDoc.setCharacterAttributes(s, e, doc.linkStyle, true);
                            }
                        }
                    }
                }
                // return resultList;
            }
            catch (BadLocationException ex) {
                Logger.getLogger(SearchMatchView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

                
        /**
         *
         * @param results
         */
        @Override
        public void process(List<MatchResult2> results)
        {
            try {
                for (MatchResult2 result: results)
                {
                    if (isCancelled()) break; // Check for cancel
                    if (result.isTitle)
                    {
                        doc.insertString(doc.getLength(), result.title, doc.pathStyle);
                        if (entry.containingText == null || entry.containingText.length() == 0)
                        {
                            doc.insertString(doc.getLength(), "\n\nContent matching was not used. Search with content to show hits within document.\n", doc.nameStyle);
                        }
                    } else {
                        doc.insertString(doc.getLength(), String.format("Line %d:\t", result.line_nr), doc.numberStyle);

                        // Append the match text and format
                        List<MatchResult> resultsx = result.results;
                        String line = result.title;
                        int pos = 0;
                        for (MatchResult res: resultsx)
                        {
                            int s = res.start();
                            int e = res.end();
                            doc.insertString(doc.getLength(), line.substring(pos, s), doc.nameStyle);
                            doc.insertString(doc.getLength(), line.substring(s, e), doc.linkStyle);
                            pos = e;
                        }
                        doc.insertString(doc.getLength(), line.substring(pos) + "\n", doc.nameStyle);
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(SearchMatchView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private ViewUpdate task;

    private Timer timer;

    public void UpdateView(Path[] paths)
    {
        // User has clicked on one or more entries
        
        // Kill any current task
        //if (busy.get() == 1)
        if (task != null && !task.isCancelled())
        {
            try {
                task.cancel(true);
            } catch (CancellationException ex) {
                Logger.getLogger(SearchMatchView.class.getName()).log(Level.SEVERE, null, ex);
            }
            task = null;
            // busy.set(2); // Cancelled, awaiting closure
        }
        
        // And then start (or restart) the delay timer
        this.paths = paths;
        timer.restart();
    }

    // AtomicInteger busy = new AtomicInteger(0);

    private Path[] paths;
    @Override
    public void actionPerformed(ActionEvent ae) {
        // Timer has expired, so let's update the view
        // ports in the background
        // if (busy.get() == 0)
        {
            // Clear the results
            jHitsTextPane.setText("");
            jPreviewTextPane.setText("\n\nProcessing! Please wait...\n");

            // After a short delay, update the hits
            task = new ViewUpdate(paths);
            task.execute();
            // busy.set(1);
        }
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSummaryTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jHitsTextPane = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPreviewTextPane = new javax.swing.JTextPane();

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
        jMenuItem1.setText(bundle.getString("SearchMatchView.jMenuItem1.text")); // NOI18N
        jMenuItem1.setToolTipText(bundle.getString("SearchMatchView.jMenuItem1.toolTipText")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText(bundle.getString("SearchMatchView.jMenuItem2.text")); // NOI18N
        jMenuItem2.setToolTipText(bundle.getString("SearchMatchView.jMenuItem2.toolTipText")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        setLayout(new java.awt.BorderLayout());

        jSummaryTextArea.setEditable(false);
        jSummaryTextArea.setColumns(20);
        jSummaryTextArea.setRows(5);
        jSummaryTextArea.setAutoscrolls(false);
        jSummaryTextArea.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jSummaryTextArea);

        jTabbedPane1.addTab(bundle.getString("SearchMatchView.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        jHitsTextPane.setEditable(false);
        jHitsTextPane.setToolTipText(bundle.getString("SearchMatchView.jHitsTextPane.toolTipText")); // NOI18N
        jHitsTextPane.setAutoscrolls(false);
        jHitsTextPane.setComponentPopupMenu(jPopupMenu1);
        jScrollPane3.setViewportView(jHitsTextPane);

        jTabbedPane1.addTab(bundle.getString("SearchMatchView.jScrollPane3.TabConstraints.tabTitle"), jScrollPane3); // NOI18N

        jPreviewTextPane.setEditable(false);
        jPreviewTextPane.setToolTipText(bundle.getString("SearchMatchView.jPreviewTextPane.toolTipText")); // NOI18N
        jPreviewTextPane.setAutoscrolls(false);
        jPreviewTextPane.setComponentPopupMenu(jPopupMenu1);
        jScrollPane4.setViewportView(jPreviewTextPane);

        jTabbedPane1.addTab(bundle.getString("SearchMatchView.jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private JTextComponent GetSelectedTextBox()
    {
        JComponent parent = (JComponent)this.jTabbedPane1.getSelectedComponent();
        JComponent item = (JComponent)parent.getComponent(0);
        return (JTextComponent)item.getComponent(0);        
    }
    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JTextComponent x = GetSelectedTextBox();
        String txt = x.getSelectedText();
        if (txt.length() != 0)
        {
            StringSelection selection = new StringSelection(txt);

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JTextComponent x = GetSelectedTextBox();
        x.selectAll();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jPopupMenu1PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenu1PopupMenuWillBecomeVisible
        JTextComponent x = GetSelectedTextBox();
        boolean enabled = x.getText().length() != 0;
        this.jMenuItem2.setEnabled(enabled);
        int s1 = x.getSelectionStart();
        int s2 = x.getSelectionEnd();
        enabled &= s2 > s1;
        this.jMenuItem1.setEnabled(enabled);
    }//GEN-LAST:event_jPopupMenu1PopupMenuWillBecomeVisible


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane jHitsTextPane;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JTextPane jPreviewTextPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jSummaryTextArea;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
