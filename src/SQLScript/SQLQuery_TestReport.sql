/****** Script for SelectTopNRows command from SSMS  ******/
--delete from TestResult_ESS;
SELECT TOP (1000) [Serail No]
      ,[Item No]
	  ,[Item Name]
      ,[Test Result]
      ,[Description]
      ,[Comment]
      ,[testSerialNo]
      ,[Test Time]
      
  FROM [AutoLoginDBForRestore].[dbo].[TestResult_ESS] order by [Test Time]