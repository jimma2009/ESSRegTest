/****** Script for SelectTopNRows command from SSMS  ******/

update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=56 where TestSerialNo='sn515';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=80 where TestSerialNo='sn516';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=111 where TestSerialNo='sn517';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=78 where TestSerialNo='sn518';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=222 where TestSerialNo='sn519';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=334 where TestSerialNo='sn520';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=13 where TestSerialNo='sn521';
update [AutoLoginDBForRestore].[dbo].[TestRoundConfig] set TotalTestItem=46 where TestSerialNo='sn522';


SELECT TOP (1000) [SerailNo]
      ,[ModuleName]
      ,[TestSerialNo]
      ,[EmailDomainName]
      ,[PayrollDBName]
      ,[CommonDBName]
      ,[ModuleFunctionName]
      ,[TestRoundCode]
      ,[TestRoundDescription]
      ,[Comment]
      ,[TotalTestItem]
      ,[TestTime]
      ,[PayrollDBOrder]
  FROM [AutoLoginDBForRestore].[dbo].[TestRoundConfig] where TestSerialNo='sn506';

