# dbtech_procedure
DBtech Ü4

create or replace PACKAGE BODY maut_service as PROCEDURE berechnemaut (
p_mautabschnitt  mautabschnitt.abschnitts_id%TYPE,
p_achszahl       fahrzeug.achsen%TYPE,
p_kennzeichen    fahrzeug.kennzeichen%TYPE
) AS
-----------------------------------------------------------------------------
-----------------------------------------------------------------------------

    v_unkown_vehicle BOOLEAN := false;
    v_kennzeichen fahrzeug.kennzeichen%TYPE;

-- FahrzeugTabelle und FahrzeugEintag DEFINITION --
type t_fahrzeug IS TABLE OF FAHRZEUG % ROWTYPE ;
V_FAHRZEUG_TABLE T_FAHRZEUG ;
V_SINGLEFAHRZEUG FAHRZEUG % ROWTYPE ;
v_KennzeichenInFahrzeugGefunden boolean;
---------------------------------------------------

-- BuchungTabelle und BuchungEintag DEFINITION --
type t_buchung IS TABLE OF BUCHUNG % ROWTYPE ;
V_BUHCUNG_TABLE T_BUCHUNG ;
V_SINGLEBUCHUNG BUCHUNG % ROWTYPE ;
v_KennzeichenInBuchungGefunden boolean;
---------------------------------------------------


BEGIN -- berechnemaut() --

     BEGIN -- fetch table fahrzeug --
     
         select * 
                bulk collect into v_fahrzeug_table
                from fahrzeug
                    where kennzeichen = p_kennzeichen;
        if v_fahrzeug_table.count = 0
            then v_KennzeichenInFahrzeugGefunden := false;
            else 
                v_KennzeichenInFahrzeugGefunden := true;
        end if;

    END;    --fetch fahrzeug table
    
    BEGIN -- fetch table buchung --
        select *
            bulk collect into V_BUHCUNG_TABLE
            from buchung
                where kennzeichen = p_kennzeichen;
        if V_BUHCUNG_TABLE.count = 0
            then v_KennzeichenInBuchungGefunden := false;
            else
                v_KennzeichenInBuchungGefunden := true;
        end if;

    END; -- fetch table buchung --

if v_KennzeichenInFahrzeugGefunden = false
then
RAISE UNKOWN_VEHICLE;
end if;

dbms_output.put_line(v_fahrzeug_table.count);
-----------------------------------------------------------------------------
EXCEPTION
WHEN UNKOWN_VEHICLE THEN
RAISE UNKOWN_VEHICLE ;
-----------------------------------------------------------------------------
-----------------------------------------------------------------------------
END berechnemaut;
END MAUT_SERVICE ;

--------------------------------------------------------------------
--ERROR SAMPLE
--RAISE UNKOWN_VEHICLE ;
--EXCEPTION
--WHEN UNKOWN_VEHICLE THEN
--RAISE UNKOWN_VEHICLE ;
--------------------------------------------------------------------

