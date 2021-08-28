#include<File.au3>
#include<ScreenCapture.au3>
#include<Date.au3>

Global $ProjectFilePath="C:\TestAutomationProject\ESSRegTest\src\AutoITScript\AutoITLog"
Global $LogFileName=$ProjectFilePath & "\AutoITLog_" & GetCurrentTimeAndDate() & ".txt"

Func LogMessage($message)
	_FileWriteLog($LogFileName, $message);
EndFunc

Func LoginMeridain($PayrollDBName, $Password)
	WinActive("Sign in to Sage Micropay")
	WinWaitActive("Sign in to Sage MicrOpay","",60);

	if $PayrollDBName="ESS_Auto_Payroll" Then
		;Select Payroll DB
		;Click the first Payroll DB
		ControlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 28)
		Sleep(2000)
		LogMessage("Payroll DB ESS_Auto_Payroll is selected.");
	ElseIf $PayrollDBName="ESS_Auto_Payroll2" Then
		;Click the second Payroll DB
		ControlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 49);
		LogMessage("Delay 70 Seconds.")
		Sleep(2000)
		LogMessage("Payroll DB ESS_Auto_Payroll2 is selected.");
	ElseIf $PayrollDBName="ESS_Auto_Payroll3" Then
		;Click the second Payroll DB
		ControlClick("Sign in to Sage MicrOpay", "", "[Class:TevTreeList; Instance:1]", "left", 1, 85, 70);
		LogMessage("Delay 70 Seconds.")
		Sleep(2000)
		LogMessage("Payroll DB ESS_Auto_Payroll3 is selected.");
	EndIf

	;Input password
	ControlFocus("Sign in to Sage MicrOpay", "","[Class:TevTextEdit; Instance:1]")
	ControlSetText("Sign in to Sage MicrOpay", "","[Class:TevTextEdit; Instance:1]", "")
	ControlSend("Sign in to Sage MicrOpay", "","[Class:TevTextEdit; Instance:1]", $Password)
	logMessage("Password is input.");
	;Click Sing in button
	ControlClick("Sign in to Sage MicrOpay","Sign in", "[Class:TevColorButton; Instance:7]")
	Sleep(15000)
	$handle=WinWait("Sage MicrOpay", "", 120);

	If $handle<>0 Then
		LogMessage("Log on Sage Micropay as Admin user successfully.")
	Else
		LogMessage("Failed log on Sage MicrOpay as Admin user.")
	EndIf
	LogScreenshot()
EndFunc


Func LaunchMeridian()
	Run("C:\Program Files (x86)\Sage MicrOpay\Evolution.exe")
	Sleep(5000)
	LogMessage("Sage Micropay is launched.");
	LogScreenshot()
EndFunc

Func UploadFileInESS($FileFullPathName)
	WinActivate("Open");
	WinWaitActive("Open", "", 10)
	Sleep(2000)
	ControlClick("Open", "", "");
	ControlSend("Open","", "[Class:Edit; Instance:1]", $FileFullPathName)
	Sleep(2000)
	ControlClick("Open", "Open", "[Class:Button; Instance:1]")
EndFunc

Func CloseMeridian()
	WinActive("Sage MicrOpay")
	WinWaitActive("Sage MicrOpay", "", 30)
	Send("!x")
	Sleep(2000)
	LogMessage("Exit Sage MicrOpay is clicked.");
	ControlFocus("Exit Sage MicrOpay", "Yes", "[Class:TButton; Instance:2]")
	ControlClick("Exit Sage MicrOpay", "Yes", "[Class:TButton; Instance:2]")
	Sleep(2000)

	Local $processID=ProcessExists("Evolution.exe");
	If $processID<>0 Then
		ProcessClose($processID)
		LogMessage("Evolution.exe is Terminated.");
	Else
		LogMessage("Evolution.exe is closed.");
	EndIf

	LogMessage("Sage MicrOpay is closed.");
	Sleep(10000)
EndFunc

Func Display_ImplementeHRScreen()
	Display_ImplementHRScreen()
	WinActivate("Implement HR Changes", "Implement eHR")
	ControlFocus ( "Implement HR Changes", "Implement eHR", "[Class:TGroupButton; Instance:1]")
	ControlClick("Implement HR Changes", "Implement eHR", "[Class:TGroupButton; Instance:1]")
	Sleep(20000)

	Local $i=0
	while ControlCommand("Implement HR Changes", "eHR Report", "[CLASS:TButton; INSTANCE:2]", "IsVisible", "")=0
		Sleep(1000)
		$i=$i+1
		if $i>=20 Then
			Break
		EndIf
	WEnd

	while ControlCommand("Implement HR Changes", "eHR Report", "[CLASS:TButton; INSTANCE:2]", "IsEnabled", "")=0
		Sleep(1000)
		$i=$i+1
		if $i>=20 Then
			Break
		EndIf
	WEnd

	LogMessage("Implement eHR checkbox is clicked.")
	LogScreenshot()
EndFunc

Func Display_ImplementHRScreen()
	WinActivate("Sage MicrOpay")
	WinWaitActive("Sage MicrOpay", "", 30)
	Send("!m")
	Sleep(2000);
	Send("{DOWN}{RIGHT}{RIGHT}{DOWN}{DOWN}{DOWN}{ENTER}");
	Sleep(5000)

	If WinWait("Implement HR Changes", "OK", 15)<>0 Then
		LogMessage("Extra Dialogue is shown.")
		LogScreenshot()
		ControlClick("Implement HR Changes", "OK", "[Class:TButton; Instance:1]")
		LogMessage("OK button is clicked.")
		Sleep(2000)
	EndIf

	WinSetState("Implement HR Changes", "", @SW_MAXIMIZE)
	Sleep(2000)
	LogMessage("Implement HR screen is shown.")
	LogScreenshot()

EndFunc

Func Close_ImplementHRScreen()
	WinActivate("Implement HR Changes")
	ControlClick("Implement HR Changes", "Close", "[Class:TButton; Instance:5]");
	Sleep(2000)
	LogMessage("Implement HR Changes screen is closed.")
	LogScreenshot()
EndFunc

Func LogScreenshot()
	$fileName="\Screenshot_" & GetCurrentTimeAndDate() & ".png"
	$fileFullName=$ProjectFilePath & $fileName;
	_ScreenCapture_Capture($fileFullName, 0, 0, -1, -1)
	LogMessage("Screenshot is saved as " & $fileFullName)
EndFunc

Func GetCurrentTimeAndDate()
	$currentTime=_NowTime(5)
	$currentDate=_NowDate()
	$currentTime=StringReplace($currentTime, ":", "")
	$currentDate=StringReplace($currentDate, "/", "")
	$outputString=$currentTime & "_" & $currentDate
	Return $outputString
EndFunc

Func SaveGridInImplementEHRScreen($fileType)
	Display_ImplementeHRScreen()
	ControlClick("Implement HR Changes", "Save Grid", "[Class:TButton; Instance:4]")
	Sleep(2000)
	LogMessage("Save Grid button is clicked.")
	WinWait("Save Grid to File", "", 20)
	WinActivate("Save Grid to File", "")

	$FileName="\Grid_ImplementEHR_" & GetCurrentTimeAndDate() & "." & $fileType
	$FileFullPathName=$ProjectFilePath & $FileName;

	ControlClick("Save Grid to File", "", "[Class:ComboBox; Instance:2]")
	Sleep(2000)
	ControlSend("Save Grid to File", "", "[Class:ComboBox; Instance:2]", "{UP}{UP}{UP}{UP}{UP}{UP}")
	ControlSend("Save Grid to File", "", "[Class:ComboBox; Instance:2]", "{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{DOWN}{ENTER}")
	Sleep(2000)
	ControlFocus("Save Grid to File", "", "[Class:ComboBox; Instance:1]")
	ControlSetText("Save Grid to File", "", "[Class:ComboBox; Instance:1]", "")
	Sleep(2000)
	ControlSend("Save Grid to File", "", "[Class:ComboBox; Instance:1]", $FileFullPathName)
	Sleep(2000)
	LogMessage("File Path " & $FileFullPathName & " is input.")
	LogScreenshot()

	ControlClick("Save Grid to File", "&Save", "[Class:Button; Instance:2]")
	Sleep(2000)
	LogMessage("Save button is clicked.")
	WinWait("Implement HR Changes", "", 10)
	WinActivate("Implement HR Changes")
	ControlClick("Implement HR Changes", "&No", "[CLASS:TButton; INSTANCE:1]")
	Sleep(4000)
	LogMessage("Review Now - No button is clicked.")
	LogMessage("Grid Implement EHR is saved as " & $FileFullPathName)

	Return $FileFullPathName

EndFunc

Func GenerateEHRPReImpReport()
	Display_ImplementeHRScreen()
	ControlClick("Implement HR Changes", "eHR Report", "[Class:TButton; Instance:2]")
	Sleep(2000)
	LogMessage("eHR button is clicked.")
	$waitResult=WinWait("Print Preview", "", 600)

	$FileFullPathName=""

	If $waitResult<>0 Then
	WinActivate("Print Preview", "")
	LogMessage("eHR Print Preveiw screen is shown.")
	LogScreenshot()

	ControlFocus("Print Preview", "", "[Class:TppToolbar; Instance:1]")
	Sleep(2000)
	MouseClick("left", 20, 34, 1)
	Sleep(2000)
	LogMessage("Print button on Toolbar is clicked.")
	LogScreenshot()

	WinWait("Print","",20)
	WinActivate("Print", "")
	ControlClick("Print", "cbxPrintToFile", "[Class:TCheckBox; Instance:3]")
	Sleep(2000)
	LogMessage("Checkbox Print To File is checked.")
	LogScreenshot()


	$FileName="\eHRPreImpReport_" & GetCurrentTimeAndDate()
	$FileFullPathName=$ProjectFilePath & $FileName;

	ControlFocus("Print", "", "[Class:TEdit; Instance:3]")
	ControlSetText("Print", "", "[Class:TEdit; Instance:3]", "")
	Sleep(2000)
	ControlSend("Print", "", "[Class:TEdit; Instance:3]", $FileFullPathName)
	Sleep(2000)
	$FileFullPathName=$FileFullPathName & ".pdf"
	LogMessage("File Path " & $FileFullPathName & " is input.")
	LogScreenshot()

	ControlClick("Print", "OK", "[Class:TButton; Instance:3]")
	Sleep(5000)
	LogMessage("OK button is clicked in Print Screen.")

	WinWait("Print Preview", "", 70)
	WinActivate("Print Preview", "")
	WinClose("Print Preview")
	Sleep(2000)
	LogMessage("Print Preview screen is closed.")

	Else
	WinActivate("Implement HR Changes", "OK")
	LogMessage("No Records in the eHR report.")
	LogScreenshot()
	ControlClick("Implement HR Changes", "OK", "[CLASS:TButton; INSTANCE:1]")
	LogMessage("OK button is clicked.")
	Sleep(2000)
	EndIf


	LogMessage("eHR Pre Implement Report is saved as " & $FileFullPathName)

	Return $FileFullPathName

EndFunc

Func ImplementEHR()
	Display_ImplementeHRScreen()
	MouseClick("left", 621, 77)
	Sleep(2000)
	LogMessage("Checkbox Implement ALl is ticked.")
	LogScreenshot()

	ControlClick("Implement HR Changes", "Implement", "[CLASS:TButton; INSTANCE:3]")
	Sleep(70000);
	LogMessage("Butotn Implement is clicked.")
	LogScreenshot()

EndFunc

Func Display_PrintEmployeeDetailsReport()
	WinActivate("Sage MicrOpay")
	WinWaitActive("Sage MicrOpay", "", 30)
	Send("!r")
	Sleep(2000);
	Send("{DOWN}{RIGHT}{DOWN}{DOWN}{DOWN}{RIGHT}{DOWN}{ENTER}");
	Sleep(5000)

	LogMessage("Print Employee Details screen is shown.")
	LogScreenshot()

EndFunc

Func Print_EmployeeDetailsReport($includeAll)
	Display_PrintEmployeeDetailsReport()
	If $includeAll=1 Then
		WinActivate("Report -- Employee Details")
		ControlFocus("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]")
		ControlClick("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]")
		ControlSend("Report -- Employee Details", "Include All", "[CLASS:TevCheckBox; INSTANCE:3]", "{SPACE}")
		LogMessage("Select All option is checked.")

		LogMessage("Screenshot before click Print butotn.")
		LogScreenshot()

		ControlFocus("Report -- Employee Details", "Print","[CLASS:TButton; INSTANCE:4]")
		ControlClick("Report -- Employee Details", "Print","[CLASS:TButton; INSTANCE:4]")

		$FileName="\EmployeeDetailReport_" & GetCurrentTimeAndDate()
		$FileFullPathName=$ProjectFilePath & $FileName

		Print_File($FileFullPathName)
		Sleep(20000)
		$FileFullPathName=$FileFullPathName & ".pdf"
		LogMessage("Employee Details Report is saved as " & $FileFullPathName)


		ControlFocus("Report -- Employee Details", "Close", "[CLASS:TButton; INSTANCE:2]")
		ControlClick("Report -- Employee Details", "Close", "[CLASS:TButton; INSTANCE:2]")
		Sleep(2000)
		LogMessage("Print Employee Detail Report screen is closed.")
		Return $FileFullPathName
	EndIf

EndFunc


Func Print_File($FileFullPathName)
	WinWait("Print","",120)
	WinActivate("Print", "")
	ControlClick("Print", "cbxPrintToFile", "[Class:TCheckBox; Instance:3]")
	Sleep(2000)
	LogMessage("Checkbox Print To File is checked.")
	LogScreenshot()

	ControlFocus("Print", "", "[Class:TEdit; Instance:3]")
	ControlSetText("Print", "", "[Class:TEdit; Instance:3]", "")
	Sleep(2000)
	ControlSend("Print", "", "[Class:TEdit; Instance:3]", $FileFullPathName)
	Sleep(2000)
	LogMessage("File Path " & $FileFullPathName & " is input.")
	LogScreenshot()

	ControlClick("Print", "OK", "[Class:TButton; Instance:3]")
	Sleep(5000)
	LogMessage("OK button is clicked in Print Screen.")
EndFunc

Func LaunchTPU()
	Run("C:\TPU\ESS TPU.exe")
	Sleep(5000)
	LogMessage("TPU is launched.")
	LogScreenshot()
EndFunc
