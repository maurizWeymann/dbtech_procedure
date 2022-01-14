# dbtech_procedure
DBtech Ü4

create package body maut_service as

procedure berechnemaut(
p_mautabschnitt mautabschnitt.abschnitts_id%type,
p_achszahl fahrzeug.achsen%type,
p_kennzeichen fahrzeug.kennzeichen%type)
as
v_dummy varchar2(10);
UNKOWN_VEHICLE exception;
-- pragma exception_init(unkown_vehicle, -20001);
begin
v_dummy := 2;
-- select Kennzeichen into v_dummy
-- from FAHRZEUG
--    where KENNZEICHEN = '%L%'
if v_dummy = 2
then raise UNKOWN_VEHICLE;
end if;
dbms_output.put_line('keine Daten vorhanden');
exception
when UNKOWN_VEHICLE
then dbms_output.put_line('UNKOWN_VEHICLE');
raise UNKOWN_VEHICLE;

end berechnemaut;
end maut_service;

/


