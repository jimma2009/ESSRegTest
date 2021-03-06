USE [AutoLoginDBForRestore]
GO
/****** Object:  StoredProcedure [dbo].[getTestTimeSpan]    Script Date: 02/09/2019 12:34:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[getTestTimeSpan] @sn varchar(50), @timeDiffText varchar(50) OUTPUT
as

Declare @startTime DateTime;
Declare @endTime DateTime;
Declare @timeDiff DateTime;


SELECT @startTime=[TestTime] FROM [AutoLoginDBForRestore].[dbo].[TestResult_ESS] where ItemName = 'Configure MCS' and TestSerialNo=@sn;
SELECT @endTime=[TestTime] FROM [AutoLoginDBForRestore].[dbo].[TestResult_ESS] where TestResult='Completed' and TestSerialNo=@sn;

set @timeDiff=(@endTime - @startTime);
set @timeDiffText=FORMAT(@timeDiff, 'hh:mm:ss');





