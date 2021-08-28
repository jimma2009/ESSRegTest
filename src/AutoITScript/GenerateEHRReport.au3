#include <GeneralBasic.au3>

LogMessage("*** Start Generating eHR Report.")
CloseMeridian()
LaunchMeridian()
LoginMeridain($CmdLine[1], "Admin")
$eHRFileFullPath=GenerateEHRPReImpReport()

Close_ImplementHRScreen()
CloseMeridian()
LogMessage("Console Writer String: " & $eHRFileFullPath)
LogMessage("*** End of Generating eHR Report.")
ConsoleWrite($eHRFileFullPath)

