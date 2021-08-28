#include <GeneralBasic.au3>

LoginMeridain($CmdLine[1], "Admin");

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

