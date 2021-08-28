
WinActivate("Open");
WinWaitActive("Open", "", 10)
Sleep(2000)
ControlClick("Open", "", "");
ControlSend("Open","", "[Class:Edit; Instance:1]", $CmdLineRaw)
Sleep(2000)
ControlClick("Open", "Open", "[Class:Button; Instance:1]")

