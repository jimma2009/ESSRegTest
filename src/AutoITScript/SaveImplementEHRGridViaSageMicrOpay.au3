#include <GeneralBasic.au3>

LogMessage("*** Start Saving Implement eHR Grid via Sage MicrOpay.")
CloseMeridian()
LaunchMeridian()
LoginMeridain($CmdLine[1], "Admin")
$ImplementEHRGrid=SaveGridInImplementEHRScreen("csv")
Close_ImplementHRScreen()
CloseMeridian()
LogMessage("Console Writer String: " & $ImplementEHRGrid)
LogMessage("*** End of Saving Implement eHR Grid via Sage MicrOpay.")
ConsoleWrite($ImplementEHRGrid)

