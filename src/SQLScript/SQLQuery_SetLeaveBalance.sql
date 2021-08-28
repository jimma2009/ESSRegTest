

update _iptblEmployeeLeave set dPostEntDate=SYSDATETIME(), dCurrentAccrualDate=SYSDATETIME(), dPostProDate=SYSDATETIME(), fPostEntHours=14, fPostProHours=0 where iEmployeeID in (select idEmployee from _iptblEmployee where cFirstName='Carmin' and cSurname='CUMMINGS');


select idEmployee from _iptblEmployee where cFirstName='Carmin' and cSurname='CUMMINGS';

select * from _iptblEmployeeLeave where iEmployeeID=12;


select bFullyFinalised, * from _iptblFinaliseLeaveApplication 
where iEmployeeID in (select idEmployee from _iptblEmployee where cEmpCode = 'EMP12') 
and iLeaveTypeID = 2;


update _iptblFinaliseLeaveApplication set bFullyFinalised = 1

select bFullyFinalised, * from _iptblFinaliseLeaveApplication 