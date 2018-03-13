# Searchmonkey v3.2.0

# 1 Introduction
## 1.1 What is Searchmonkey?

Perform powerful desktop searches without having to index your system using regular expressions. Searchmonkey is the graphical equivalent to grep + find, and is available in Gnome, KDE or JAVA editions.

The key features in this release are:
* Support for most text documents, including:-
	* Adobe PDF
	* Microsoft Office (XML & original file format)
	* Open Office (ODT file format)
	* Zip files containing text files
	* And much, much more besides
* Improved HMI look-and-feel for true next generation feel

This is the release notes for the JAVA edition.

## 1.2 Which version do I need?

Download the file most suited to your environments:

* [Searchmonkey_full_v3.2.0.exe](https://sourceforge.net/projects/searchmonkey/files/jSearchmonkey%20JAVA/v3.2/Searchmonkey_full_v3.2.0.exe/download) - Windows x64 and x86 full installer
	* This windows installer contains everything you need to deploy Searchmonkey on Windows 7, Windows 8, Windows 10, and beyond. It includes a full copy of the Java Runtime Environment (JRE), and works on 64-bit and 32-bit OS. Use this version is unsure
* [Searchmonkey_static_v3.2.0.exe](https://sourceforge.net/projects/searchmonkey/files/jSearchmonkey%20JAVA/v3.2/Searchmonkey_static_v3.2.0.exe/download) - Windows x64 and x86 static installer
	* This windows installer contains everything you need to deploy Searchmonkey on Windows 7, Windows 8, Windows 10, and beyond. It includes a static install of  the Java Runtime Environment (JRE), and works on 64-bit and 32-bit OS. Use this package if you don't want to install any 3rd party dependencies in your uninstall list.
* [Searchmonkey_v3.2.0.exe](https://sourceforge.net/projects/searchmonkey/files/jSearchmonkey%20JAVA/v3.2/Searchmonkey_v3.2.0.exe/download) - Windows x64 and x86 minimal installer
	* This windows installer contains a full copy of Searchmonkey. Use this version if you have already installed Java Runtime Environment (JRE) 1.8 or newer.
* [searchmonkey-3.2.0-jar-with-dependencies.jar](https://sourceforge.net/projects/searchmonkey/files/jSearchmonkey%20JAVA/v3.2/searchmonkey-3.2.0-jar-with-dependencies.jar/download) - x64 and x86 full JAR package
	* This JAR archive contains a full copy of Searchmonkey and is cross-platform. Use this version if you are running Linux or OS.X. To use, install Java Runtime Environment (JRE) 1.8 or newer. To start run from the command line, enter the command e.g. ``` $ java -jar searchmonkey.jar```
* [searchmonkey-3.2.0.jar](https://sourceforge.net/projects/searchmonkey/files/jSearchmonkey%20JAVA/v3.2/searchmonkey-3.2.0.jar/download) - x64 and x86 minimal JAR package
	* This JAR archive contains a full copy of Searchmonkey but does not include any of the depenencies. Use this version if you are wanting to generate a clean package for your Linux distribution. To use, install Java Runtime Environment (JRE) 1.8 or newer and then use your package manager to install all of the dependencies (the full list can be found by compiling the source code). To start run from the command line, enter the command e.g. ``` $ java -jar searchmonkey.jar```

# 2 What's new
## 2.1 Highlights

This version provides a new and improved regular expression test utility:-
 * Adds more control over the look-and-feel
 * Improved regular expression tester
 * Improved built-in help pages
 * General bug fixes

# 2.2 Improvements

* Menu bar:
	* Added view menu to change the look and feel **DONE**
	* Better RTL (right-to-left) support **DONE**
* Test regular expression
	* Improved dialog with built in help to test and evaluate regular expressions **DONE**
	* User can see results in real time from with the test entry box, or the search box **DONE**
	* Fixed the copy/paste buffer **DONE**
* Search summary:
	* Added more summary features including skipped files **DONE**
	* When performing a filename search (no content) a message is shown to the user in the hits tab **DONE**

## 2.3 Fixed Issues
* Search summary:
	* The most recent file is now the most recent **DONE**
	* Fixed the caret-scrolling issue **DONE**
* Search results:
	* Fixing icon for ZIP files **DONE**

# 3 BUGS

## 3.1 Known Issues
* File decode timeout (ms) should be moved to content search
* Shortcut 'links' on _About_ page do not work as expected
* Comboboxes can be filled with the same text multiple times
* Restoring defaults does not reset the table columns + positions
* Hidden columns will be restored on startup (not saved)

## 3.2 Support

Contact support to report any issues that you find with this software.
You can find us on twitter using the handle *jSearchmonkey*

# 4 Roadmap

## 4.1 Work in progress
* Filename matching
	* Filename: regex wizard
	* Filename: glob wizard
* Test wizard:
	* add (?i) and (?d) etc flags to the reference
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
* General:
	* General options: Use power search check button

## 4.2 Ideas in development
* Export results as spreadsheet
* Export hits or preview to file
* Save/Restore a complete set of search results (e.g. for comparison)
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
