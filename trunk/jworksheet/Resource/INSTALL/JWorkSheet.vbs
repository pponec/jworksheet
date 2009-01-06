' ===================================================================
' runJ4Win.vbs version 0.94 - general starting script 
' for a Java application on MS Windows 98, ME, 2000, XP, 95+WSH, NT+WSH.
' 
' Copyright (C) 2003-2006 Paul Ponec, e-mail:ppsee2@gmail.com
' Home Page: http://ponec.net/ppsee/runj4/index.htm
' 
' This program is free software; you can redistribute it and/or modify
' it under the terms of the GNU Lesser General Public License as published by
' the Free Software Foundation version 2.1 of the License.
' This program is distributed in the hope that it will be useful,
' but WITHOUT ANY WARRANTY.
' Please read bin/license-lgpl.txt for the full details. A copy of the LGPL may 
' be found at http://www.gnu.org/licenses/lgpl.txt .
'
' A History:
' rel. 0.92  - 2003/11/30 - The first public version under GPL License
' rel. 0.93  - 2007/08/14 - Modified for jWorkSheet.vbs java program
'
' Usage: wscript.exe jWorkSheet.vbs
' ===================================================================

' Modify path to file "javaw.exe" in case you can't run the application.
java = "" ' "C:\Program Files\Java\jre1.5.0_06\bin\javaw.exe" 

' Maximal Memory Alocation:
parameter = "-Xmx30m"

' Java Archive:
jarName = "jWorkSheet.jar"

' User Parameters:
paru = ""

' Java Web Start is enabled:
javaWS = FALSE

' === CORE: ===
Sub error(ByVal enable)
  If enable Then
    Wscript.Echo "WARNING from ""ppsee.vbs"" script:" & vbCrLf & vbCrLf & "File """ & java & """ was not found! " _
    & vbCrLf & "Ensure you have installed a Java Runtime" _
    & vbCrLf & "or try to modify a ""java"" variable in the script." _
    & vbCrLf & "See ""read-me.html"" file for more information."  _
    & vbCrLf & vbCrLf & "Java Runtime download page: http://java.com/download/"
    WScript.Quit(1)
  End If
End Sub

set shell = WScript.CreateObject("Wscript.Shell")
set fso   = WScript.CreateObject("Scripting.FileSystemObject")
javax = java
if Len(javax)=0 Or not fso.FileExists(javax) then
  jreg  = "HKLM\Software\JavaSoft\Java Runtime Environment\"
  on error resume next
  javax = shell.RegRead(jreg & "CurrentVersion")
  error(Err.Number<>0)
  javax = shell.RegRead(jreg & javax & "\JavaHome") & "\bin\"
  if javaWS then
       javax = javax & "javaws.exe"
  else
       javax = javax & "javaw.exe"
  end if  
  error(Err.Number<>0)
  On Error Goto 0
  error(not fso.FileExists(javax))
end if
' HTML Viewer
htmlView = "HKLM\SOFTWARE\Classes\HTTP\shell\open\command\"
htmlView = shell.RegRead(htmlView)
htmlView = Replace (htmlView , """", "'")
' htmlView = Left(htmlView, InstrRev(htmlView, " "))
Set args = Wscript.Arguments
For i = 0 to args.Count-1
   paru = paru & " """ & args(i) & """" 
Next

param = """" & javax & """ "
path = WScript.ScriptFullName
path = Left(path, InstrRev(path, "\"))
if javaWS then
  param = param & " """ & path & "file.jnlp" & """"
else
  param = param & parameter & " -jar """ & path & jarName & """ " & paru
end if
shell.Run(param) 
' === EOF ===


