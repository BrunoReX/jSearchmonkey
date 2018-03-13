; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Searchmonkey"
#define MyAppVersion "3.2.0"
#define MyAppPublisher "EmbeddedIQ"
#define MyAppURL "http://searchmonkey.EmbeddedIQ.com/"
#define MyAppExeName "Searchmonkey.jar"
#define MyAppShortName "Searchmonkey"
#define MyAppGroup "EmbeddedIQ"
#define JRE_Version "jre-8u161"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{89CAA77F-6F11-476C-BA53-3AEA14214E1E}
AppName={#MyAppGroup} {#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
; AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppGroup}\{#MyAppName}
DisableProgramGroupPage=yes
OutputBaseFilename={#MyAppName}_full_v{#MyAppVersion}
OutputDir=.
Compression=lzma
SolidCompression=yes
LicenseFile="etc\gpl-3.0.txt"
UninstallDisplayIcon={app}\ico.ico
SetupIconFile=..\..\..\resources\images\ico.ico
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "jre"; Description: "Install Oracle prerequisits"; GroupDescription: "Other tasks:"

[Files]
Source: "etc\Java\jre-8u161-windows-i586.exe.bin"; DestDir: "{tmp}"; DestName: "jre-8u161-windows.exe"; Tasks: jre; Flags: deleteafterinstall ignoreversion nocompression;  Check: Is64BitInstallMode
Source: "etc\Java\jre-8u161-windows-x64.exe.bin"; DestDir: "{tmp}"; DestName: "jre-8u161-windows.exe"; Tasks: jre; Flags: deleteafterinstall ignoreversion nocompression; Check: not Is64BitInstallMode
Source: "..\..\..\..\..\target\{#MyAppName}-{#MyAppVersion}-jar-with-dependencies.jar"; DestDir: "{app}"; DestName: "{#MyAppName}.jar"; Flags: ignoreversion nocompression

; Extras
Source: "..\..\..\..\..\README.md"; DestDir: "{app}"; DestName: "Release Notes.txt"; Flags: isreadme
Source: "..\..\..\resources\images\ico.ico"; DestDir: "{app}"

[Icons]
Name: "{commonprograms}\{#MyAppGroup}\{#MyAppName}"; Filename: "{app}\{#MyAppName}.jar"; IconFilename: "{app}\ico.ico"
Name: "{commondesktop}\{#MyAppGroup} {#MyAppName}"; Filename: "{app}\{#MyAppName}.jar"; IconFilename: "{app}\ico.ico"; Tasks: desktopicon

[Run]
Filename: "{tmp}\jre-8u161-windows.exe"; Tasks: jre; Parameters: "/s REMOVEOUTOFDATEJRES=1 AUTO_UPDATE=Disable"; StatusMsg: "Installing Java 8 Runtime..."

