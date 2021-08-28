#include <GeneralBasic.au3>

LogMessage("*** Start Printing Employee Details Report.")
LaunchMeridian()
LoginMeridain($CmdLine[1], "Admin")

$filePath=Print_EmployeeDetailsReport(1)

CloseMeridian()
LogMessage("*** End of Printing Employee Details Report.")
ConsoleWrite($filePath)

