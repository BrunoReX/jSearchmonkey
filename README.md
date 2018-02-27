# searchmonkey
Searchmonkey - Power searching without the pain. Perform powerful desktop searches without having to index your system using regular expressions. Graphical equivalent to grep.

##Highlights

This version carries with it a major overhaul of the HMI so that it is more consistent with the Searchmonkey family of products.

The key features in this release are:-
* Added content search for most text documents, including:-
.* Adobe PDF
.* Microsoft Office (XML & original file format)
.* Open Office (ODT file format)
.* Zip files containing text files
.* And much, much more besides
* Improved HMI look-and-feel for true next generation feel

#BUGS
##Fixed
* Modified date now working normally
* Created date now working normally
* Dates, when printed in the comboboxes, are now in local date format
* Centred location of the wizards to the HMI
* Title of the window set to Searchmonkey's tagline
* Mime types dialog has been made larger to make it easier to scroll
* Shortcuts (LNK) files are now ignored (user options)
* Filename are restored after start up
* Containing are restored after start up
* Changes to the column number will reset the table column width + positions
* The icon column header is shown in the context menu (but not in the results table)

##Known
* In summary, the most recent file is not the most recent.
* File decode timeout (ms) should be moved to content search
* Shortcut links on _About_ page do not work
* Comboboxes can be filled with the same text multiple times
* Restoring defaults does not reset the table columns + positions

#IMPROVEMENTS

Cleaner HMI:
* Search panel moved to the left-hand side
* Advanced search options simplified by replacing with comboboxes
* Intelligent comboboxes used to enter data with advanced features
* Options tab used to keep search options easy to view and change

Search panel:
* Accessed date: inverted range e.g. not between last week and last month
* File Size Option: Inverted file range e.g. Greater than X OR less than Y
* Filename search options: Removed limit max file size option
* Filename search options: Can now ignore common binary files
* Filename search options: case insensitive search
* Results table: Added file type icons, for known types

#TODO
##Work in progress
* Mime Type Option: Support for multiple MIME types e.g. "text/plain, text/html"
* Mime Type Option: Wildcards in MIME types e.g. text/*, etc
* Report current search parameters to the search summary
* When performing a filename search (no content) show user message in the hits tab
* Results table: If number of hits exceeds the limit, then show this e.g. >999
* Folder search options: Hide/disable ignore case for Windows based systems
* Folder search options: Add list of folders to skip e.g. .svn or .git etc
* General options: Use power search check button
* Filename: regex wizard
* Filename: glob wizard
* Content: regex wizard
* Content: keywords wizard
* Content: keyword searching e.g. hello OR world, etc
* Results table: Add column for guessed file type e.g. UTF-8, etc

## Future roadmap
* Export results as spreadsheet
* Export hits or preview to file
* Save/Restore a complete set of search results (e.g. for comparison)
* Better RTL (right-to-left) support
* Add translations (German, French, Russian)

Developer Notes
===============

Searchmonkey was written using maven and on NetBeans 8. If you are using a fresh installation of NetBeans, then be sure to install the Maven plugin before attempting to import this project as there will be a lot of missing dependencies.

The following compile time warnings are normal:-

```
--- maven-dependency-plugin:3.0.2:analyze-only (analyze) @ searchmonkey ---
Unused declared dependencies found:
   org.apache.tika:tika-parsers:jar:1.17:compile
   org.apache.sis.core:sis-metadata:jar:0.6:compil
```
