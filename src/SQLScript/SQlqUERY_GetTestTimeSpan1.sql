USE [AutoLoginDBForRestore];

DECLARE	@return_value int;
DECLARE @timeDiffText varchar(50);

EXEC @return_value = [dbo].[getTestTimeSpan] @sn = 'sn522', @timeDiffText = @timeDiffText OUTPUT;

SELECT	@timeDiffText as 'TimeDiffText';

