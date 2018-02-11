/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

        // Disable auto-scrolling on the text window
        DefaultCaret caret = (DefaultCaret) jHitsTextPane.getCaret();
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
        this.jSUmmaryTextArea.setText("");
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
    public void UpdateSummary(SearchSummary ss, boolean interim)
    {
        jSUmmaryTextArea.setText(""); // Clear before entering
        if (interim)
        {
            jSUmmaryTextArea.append(String.format("Search in progress...\n\n"));
        } else {
            jSUmmaryTextArea.append(String.format("Search completed in %d seconds.\n\n", (ss.endTime - ss.startTime)/1000000000));
        }
        
        
        jSUmmaryTextArea.append(String.format("Matched: %d file%s\n", ss.matchFileCount, ss.matchFileCount != 1 ? "s" : ""));
        if (ss.totalMatchBytes > 0)
        {
            int order = getFileOrder(ss.totalMatchBytes);
            double divider = Math.pow(1024, order);
            
            jSUmmaryTextArea.append(String.format("Size on disk: %.1f %s\n", ((double)ss.totalMatchBytes / divider), FSIZE[order]));
        }
        if (ss.totalContentMatch > 0)
        {
            jSUmmaryTextArea.append(String.format("Content found: %d hit%s\n", ss.totalContentMatch, ss.totalContentMatch != 1 ? "s" : ""));
        }
        if (ss.totalFolders > 0)
        {
            jSUmmaryTextArea.append(String.format("Processed: %d folder%s\n", ss.totalFolders, ss.totalFolders != 1 ? "s" : ""));
        }
        if (ss.skippedFolders > 0)
        {
            jSUmmaryTextArea.append(String.format("Skipped: %d folder%s\n", ss.skippedFolders, ss.skippedFolders != 1 ? "s" : ""));
        }
        if (ss.totalFiles > 0)
        {
            jSUmmaryTextArea.append(String.format("Processed: %d file%s\n", ss.totalFiles, ss.totalFiles != 1 ? "s" : ""));
        }
        if (ss.skippedFiles > 0)
        {
            jSUmmaryTextArea.append(String.format("Skipped: %d file%s\n", ss.skippedFiles, ss.skippedFiles != 1 ? "s" : ""));
        }
        if (ss.maxMatchBytes > 0)
        {
            int order = getFileOrder(ss.maxMatchBytes);
            double divider = Math.pow(1024, order);
            
            jSUmmaryTextArea.append(String.format("\nLargest file size: %.1f %s\n", ((double)ss.maxMatchBytes / divider), FSIZE[order]));
        }
        if (ss.maxContentMatch > 0)
        {
            jSUmmaryTextArea.append(String.format("Largest matches per file: %d hit%s\n", ss.maxContentMatch, ss.maxContentMatch != 1 ? "s" : ""));
        }
        if (ss.firstCreated != null)
        {
            jSUmmaryTextArea.append(String.format("Oldest file: %s\n", ss.firstCreated));
        }
        if (ss.lastModified != null)
        {
            jSUmmaryTextArea.append(String.format("Most recent file: %s\n", ss.lastModified));
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
            jPreviewTextPane.setText("");

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
        jSUmmaryTextArea = new javax.swing.JTextArea();
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

        jMenuItem1.setText("Copy");
        jMenuItem1.setToolTipText("");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Select all");
        jMenuItem2.setToolTipText("");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        setLayout(new java.awt.BorderLayout());

        jSUmmaryTextArea.setEditable(false);
        jSUmmaryTextArea.setColumns(20);
        jSUmmaryTextArea.setRows(5);
        jSUmmaryTextArea.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(jSUmmaryTextArea);

        jTabbedPane1.addTab("Summary", jScrollPane1);

        jHitsTextPane.setEditable(false);
        jHitsTextPane.setToolTipText("");
        jHitsTextPane.setComponentPopupMenu(jPopupMenu1);
        jScrollPane3.setViewportView(jHitsTextPane);

        jTabbedPane1.addTab("Hits", jScrollPane3);

        jPreviewTextPane.setEditable(false);
        jPreviewTextPane.setToolTipText("");
        jPreviewTextPane.setComponentPopupMenu(jPopupMenu1);
        jScrollPane4.setViewportView(jPreviewTextPane);

        jTabbedPane1.addTab("Preview", jScrollPane4);

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
    private javax.swing.JTextArea jSUmmaryTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
