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
* Modified date not working
* Created date not working

##Known
* In summary, the most recent file is not the most recent.
* Shortcuts (Lnk) files are not being ignored
* File decode timeout (ms) should be moved to content search
* Shortcut links on _About_ page do not work
* Location of the wizards is not centred to the HMI
* Mime types are hard to scroll because the dialog is too small
* Version number in the header does not match the release number
* Filename not restored after start up
* Containing not restored after start up
* Comboboxes can be filled with the same text multiple times

#IMPROVEMENTS

Cleaner HMI:
* Search panel moved to the left-hand side
* Advanced search options simplified by replacing with comboboxes
* Intelligent comboboxes used to enter data with advanced features
* Options tab used to keep search options easy to view and change

Search panel:
* Accessed date: inverted range e.g. not between last week and last month

#TODO
##Work in progress
* Mime Type Option: Support for multiple MIME types e.g. "text/plain, text/html"
* Mime Type Option: Wildcards in MIME types e.g. text/*, etc
* File Size Option: Inverted file range e.g. Greater than X OR less than Y
* Report current search parameters to the search summary
* In file only search, show user friendly message in the hits
* Filename search options: Ignore binary files
* Filename search options: case insensitive search
* Filename search options: Remove limit max file size
* Results table: If number of hits exceeds the limit, then show this e.g. >999
* Results table: Add file type icons, for known types
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

Developer Notes
===============

Searchmonkey was written using maven and on NetBeans 8. If you are using a fresh installation of NetBeans, then be sure to install the Maven plugin before attempting to import this project as there will be a lot of missing dependencies.


