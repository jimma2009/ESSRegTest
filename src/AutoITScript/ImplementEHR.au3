#include <GeneralBasic.au3>

LogMessage("*** Start Implementing eHR.")
CloseMeridian()
LaunchMeridian()
LoginMeridain($CmdLine[1], "Admin")

ImplementEHR()
Close_ImplementHRScreen()
CloseMeridian()
LogMessage("*** End of Implementing eHR.")

