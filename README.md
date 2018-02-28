# Searchmonkey v3.1.0

# 1 Introduction
## 1.1 What is Searchmonkey?

Perform powerful desktop searches without having to index your system using regular expressions. Searchmonkey is the graphical equivalent to grep + find, and is available in Gnome, KDE or JAVA editions.

This is the release notes for the JAVA edition.

## 1.2 Which version do I need?

Download the file most suited to your environments:

* [Searchmonkey_full_v3.1.0.exe](Searchmonkey_full_v3.1.0.exe) - Windows x64 and x86 full installer
	* This windows installer contains everything you need to deploy Searchmonkey on Windows 7, Windows 8, Windows 10, and beyond. It includes a full copy of the Java Runtime Environment (JRE), and works on 64-bit and 32-bit OS. Use this version is unsure
* [Searchmonkey_static_v3.1.0.exe](Searchmonkey_static_v3.1.0.exe) - Windows x64 and x86 static installer
	* This windows installer contains everything you need to deploy Searchmonkey on Windows 7, Windows 8, Windows 10, and beyond. It includes a static install of  the Java Runtime Environment (JRE), and works on 64-bit and 32-bit OS. Use this package if you don't want to install any 3rd party dependencies in your uninstall list.
* [Searchmonkey_v3.1.0.exe](Searchmonkey_v3.1.0.exe) - Windows x64 and x86 minimal installer
	* This windows installer contains a full copy of Searchmonkey. Use this version if you have already installed Java Runtime Environment (JRE) 1.8 or newer.
* [searchmonkey-3.1.0-jar-with-dependencies.jar](searchmonkey-3.1.0-jar-with-dependencies.jar) - x64 and x86 full JAR package
	* This JAR archive contains a full copy of Searchmonkey and is cross-platform. Use this version if you are running Linux or OS.X. To use, install Java Runtime Environment (JRE) 1.8 or newer. To start run from the command line, enter the command e.g. ``` $ java -jar searchmonkey.jar```
* [searchmonkey-3.1.0.jar](searchmonkey-3.1.0.jar) - x64 and x86 minimal JAR package
	* This JAR archive contains a full copy of Searchmonkey but does not include any of the depenencies. Use this version if you are wanting to generate a clean package for your Linux distribution. To use, install Java Runtime Environment (JRE) 1.8 or newer and then use your package manager to install all of the dependencies (the full list can be found by compiling the source code). To start run from the command line, enter the command e.g. ``` $ java -jar searchmonkey.jar```

# 2 What's new
## 2.1 Highlights

This version carries with it a major overhaul of the HMI so that it is more consistent with the Searchmonkey family of products.

The key features in this release are:

* Added content search for most text documents, including:-
	* Adobe PDF
	* Microsoft Office (XML & original file format)
	* Open Office (ODT file format)
	* Zip files containing text files
	* And much, much more besides
* Improved HMI look-and-feel for true next generation feel

# 2.2 Improvements

* Cleaner HMI:
	* Search panel moved to the left-hand side **DONE**
	* Advanced search options simplified by replacing with comboboxes **DONE**
	* Intelligent comboboxes used to enter data with advanced features **DONE**
	* Options tab used to keep search options easy to view and change **DONE**
* Search panel:
	* Accessed date: inverted range e.g. not between last week and last month **DONE**
	* File Size Option: Inverted file range e.g. Greater than X OR less than Y **DONE**
	* Filename search options: Removed limit max file size option **DONE**
	* Filename search options: Can now ignore common binary files **DONE**
	* Filename search options: case insensitive search **DONE**
	* Results table: Added file type icons, for known types **DONE**

## 2.3 Fixed Issues
* Search entry:
	* Modified date now working normally **DONE**
	* Created date now working normally **DONE**
	* Dates, when printed in the comboboxes, are now in local date format **DONE**
	* Filename are restored after start up **DONE**
	* Containing are restored after start up **DONE**
* Dialogs and wizards:
	* Centred location of the wizards to the HMI **DONE**
	* Mime types dialog has been made larger to make it easier to scroll **DONE**
* Options:
	* Shortcuts (LNK) files are now ignored (user options) **DONE**
* Search results:
	* The icon column header is shown in the context menu (but not in the results table) **DONE**
	* Changes to the column number will reset the table column width + positions **DONE**
* General:
	* Title of the window set to Searchmonkey's tagline **DONE**

# 3 BUGS

## 3.1 Known Issues
* In summary, the most recent file is not the most recent.
* File decode timeout (ms) should be moved to content search
* Shortcut links on _About_ page do not work
* Comboboxes can be filled with the same text multiple times
* Restoring defaults does not reset the table columns + positions
* Hidden columns will be restored on startup (not saved)

## 3.2 Support

Contact support to report any issues that you find with this software.

# 4 Roadmap

## 4.1 Work in progress
* Filename matching
	* Filename: regex wizard
	* Filename: glob wizard
* Content matching:
	* Content: regex wizard
	* Content: keywords wizard
	* Content: keyword searching e.g. hello OR world, etc
* Folder searching:
	* Folder search options: Hide/disable ignore case for Windows based systems
	* Folder search options: Add list of folders to skip e.g. .svn or .git etc
* Mime Type Options: 
	* Support for multiple MIME types e.g. "text/plain, text/html"
	* Wildcards in MIME types e.g. text/*, etc
* Results table:
	* Results table: Add column for guessed file type e.g. UTF-8, etc
	* Results table: If number of hits exceeds the limit, then show this e.g. >999
* Result summary:
	* Report current search parameters to the search summary
	* When performing a filename search (no content) show user message in the hits tab
* General:
	* General options: Use power search check button

## 4.2 Ideas in development
* Export results as spreadsheet
* Export hits or preview to file
* Save/Restore a complete set of search results (e.g. for comparison)
* Better RTL (right-to-left) support
* Add translations (German, French, Russian)
* Add command-line support e.g. searchmonkey <folder> [Options]

----------------------------------------------------------------------------------

# 4 Developers

# 4.1 Compile time notes

Searchmonkey was written using maven and on NetBeans 8. If you are using a fresh installation of NetBeans, then be sure to install the Maven plugin before attempting to import this project as there will be a lot of missing dependencies.

The following compile time warnings are normal:-

> --- maven-dependency-plugin:3.0.2:analyze-only (analyze) @ searchmonkey ---
> Unused declared dependencies found:
> org.apache.tika:tika-parsers:jar:1.17:compile
> org.apache.sis.core:sis-metadata:jar:0.6:compile
