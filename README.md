create or replace PACKAGE BODY maut_service as PROCEDURE berechnemaut (
p_mautabschnitt  mautabschnitt.abschnitts_id%TYPE,
p_achszahl       fahrzeug.achsen%TYPE,
p_kennzeichen    fahrzeug.kennzeichen%TYPE
) AS
-----------------------------------------------------------------------------
-----------------------------------------------------------------------------

    v_unkown_vehicle BOOLEAN := false;
    v_kennzeichen fahrzeug.kennzeichen%TYPE;
    fz_id fahrzeuggerat.fz_id % type;
    
    achszahlString varchar2(5);
    
    berechneteMaut number(8,2);
    
    nextMautID number(10);
    
    offeneBuchung boolean:= false;
    offeneBuchungsID number(10);
    
    v_KennzeichenImAutomatikVerfahrenGefunden boolean;
    v_KennzeichenImManuellenVerfahrenGefunden boolean;

-- FahrzeugTabelle und FahrzeugEintag DEFINITION --
type t_fahrzeug IS TABLE OF FAHRZEUG % ROWTYPE ;
V_FAHRZEUG_TABLE T_FAHRZEUG ;
V_FAHRZEUG FAHRZEUG % ROWTYPE ;
---------------------------------------------------

-- FahrzeuggeratTabelle und FahrzeuggeratEintag DEFINITION --
type t_fahrzeuggerat IS TABLE OF FAHRZEUGGERAT % ROWTYPE ;
V_FAHRZEUGGERAT_TABLE T_FAHRZEUGGERAT ;
V_FAHRZEUGGERAT FAHRZEUGGERAT % ROWTYPE ;
    
---------------------------------------------------

-- BuchungTabelle und BuchungEintag DEFINITION --
type t_buchung IS TABLE OF BUCHUNG % ROWTYPE ;
V_BUCHUNG_TABLE T_BUCHUNG ;
V_BUCHUNG BUCHUNG % ROWTYPE ;
v_buchung_id BUCHUNG.BUCHUNG_ID % TYPE;
---------------------------------------------------

-- MauterhebungTabelle DEFINITION --
type T_MAUTERHEBUNG IS TABLE OF MAUTERHEBUNG % ROWTYPE ;
V_MAUTERHEBUNG_TABLE T_MAUTERHEBUNG ;
V_MAUTERHEBUNG MAUTERHEBUNG % ROWTYPE;
V_MAUT_ID number(10);
    
---------------------------------------------------

-- MautkategorieTabelle DEFINITION --
type T_MAUTKATEGORIE IS TABLE OF MAUTKATEGORIE % ROWTYPE ;
V_MAUTKATEGORIE_TABLE T_MAUTKATEGORIE ;
V_MAUTKATEGORIE MAUTKATEGORIE % ROWTYPE;
---------------------------------------------------

-- MautabschnittTabelle DEFINITION --
type T_MAUTABSCHNITT IS TABLE OF MAUTABSCHNITT % ROWTYPE ;
V_MAUTABSCHNITT_TABLE T_MAUTABSCHNITT ;
V_MAUTABSCHNITT MAUTABSCHNITT % ROWTYPE;
---------------------------------------------------


BEGIN -- berechnemaut() --   
dbms_output.put_line('Start berechne Maut');

    -- CHECK OB KENNZEICHEN IM AUTOMATISCHEN VERFAHREN --
    select * bulk collect into v_fahrzeug_TABLE
        from fahrzeug where kennzeichen = p_kennzeichen;
        
        if v_fahrzeug_TABLE.count = 0 then v_KennzeichenImAutomatikVerfahrenGefunden := false; 
        else --Fahrzeug ist im automatischen Verfahren
            
            select * into v_fahrzeug from fahrzeug where kennzeichen = p_kennzeichen; 
            
                --Prüft ob das Gefunde Fahrzeug ein Fahrzeuggerät hat
                select * BULK COLLECT into V_FAHRZEUGGERAT_TABLE from FAHRZEUGGERAT where FZ_ID = v_fahrzeug.fz_id;
                dbms_output.put_line(V_FAHRZEUGGERAT_TABLE.count || 'Geräte gefunden');
                if V_FAHRZEUGGERAT_TABLE.count = 0 then
                    v_KennzeichenImAutomatikVerfahrenGefunden := false;    
                else
                    v_KennzeichenImAutomatikVerfahrenGefunden := true; 
                end if;

        end if;     

    -- CHECK OB KENNZEICHEN BUCHUNG IM MANUELLEN VERFAHREN FÜR PASSENDEN MAUTABSCHNITT HAT--
    select * bulk collect into V_BUCHUNG_TABLE
        from buchung where kennzeichen = p_kennzeichen and abschnitts_ID = p_mautabschnitt;
        
        if V_BUCHUNG_TABLE.count = 0 then v_KennzeichenImManuellenVerfahrenGefunden := false;
            else --Fahrzeug ist im manuellen Verfahren
                v_KennzeichenImManuellenVerfahrenGefunden := true;
                
        end if;
    
    -- ERSTELLE TABELLE MAUTKATEGORIE
    select KATEGORIE_ID, SSKL_ID, KAT_BEZEICHNUNG, ACHSZAHL, MAUTSATZ_JE_KM
        bulk collect into V_MAUTKATEGORIE_TABLE from MAUTKATEGORIE;
        
    -- ERSTELLE MAUTABSCHNITT
    select * into V_MAUTABSCHNITT from MAUTABSCHNITT where MAUTABSCHNITT.ABSCHNITTS_ID = p_mautabschnitt;
    
    -- ERSTELLE MAX MAUTERHEBUNG
    select max(MAUT_ID) into V_MAUT_ID from MAUTERHEBUNG;

-- AUTOMATISCHES VERFAHREN

    --erstelle Fahrzeugtabelle mit FZ_ID aus Tabelle Fahrzeug wenn status AKTIV
    if v_KennzeichenImAutomatikVerfahrenGefunden = true then dbms_output.put_line('Fahrzeug ist im Automatischen Verfahren');
        select * into V_FAHRZEUGGERAT from FAHRZEUGGERAT 
            where status = 'active' 
            and FAHRZEUGGERAT.FZ_ID = v_fahrzeug.fz_id;  
            
            --erstelle Mauterhebungstabelle mit FZG_ID aus Tabelle Fahrzeuggerät
            if V_FAHRZEUGGERAT.fz_id is not null and p_achszahl = v_fahrzeug.achsen then dbms_output.put_line('Fahrzeug mit aktivem Gerät gefunden');
            
            dbms_output.put_line(v_fahrzeug.achsen || ' Achsen');
                if v_fahrzeug.achsen <= 4 then
                    achszahlString := '= ' || v_fahrzeug.achsen;
                    elsif v_fahrzeug.achsen > 4 then
                    achszahlString := '>= ' || v_fahrzeug.achsen;
                end if;
                    
                
                select * into V_MAUTKATEGORIE from MAUTKATEGORIE
                    where achszahl = achszahlString and v_fahrzeug.sskl_id = mautkategorie.sskl_id;
                    
                    dbms_output.put_line('Mautkategorie_id: '|| V_MAUTKATEGORIE.kategorie_id);
                    dbms_output.put_line('Mautsatz pro Kilometer: '|| V_MAUTKATEGORIE.MAUTSATZ_JE_KM);
               
                --Maut berechnen
                berechneteMaut := V_MAUTKATEGORIE.MAUTSATZ_JE_KM * V_MAUTABSCHNITT.LAENGE / 100000;
                dbms_output.put_line('berechneteMaut: ' ||berechneteMaut);
                dbms_output.put_line('letze maut_id: ' || V_maut_id);
                nextMautID := V_maut_id + 1;
                dbms_output.put_line('nextMautID: ' ||nextMautID);
                insert into mauterhebung values (nextMautID, p_mautabschnitt, v_fahrzeuggerat.FZG_ID, V_MAUTKATEGORIE.kategorie_id, LOCALTIMESTAMP, berechneteMaut);
                
                
            else dbms_output.put_line('Kein Fahrzeug mit aktivem Gerät gefunden');
            RAISE INVALID_VEHICLE_DATA;
                    
            end if;        
    end if;


-- MANUELLES VERFAHREN

    if v_KennzeichenImManuellenVerfahrenGefunden = true then
        dbms_output.put_line('Fahrzeug ist im manuellen Verfahren');
        
     for v_i in 1 .. v_buchung_table.count
        loop
            if v_buchung_table(v_i).b_id = 1  then
            offeneBuchung := true;
            offeneBuchungsID := v_buchung_table(v_i).BUCHUNG_ID;
            end if;
            dbms_output.put_line('BuchungsID: '||v_buchung_table(v_i).b_id);
            dbms_output.put_line('Mautabschnitt: '||v_buchung_table(v_i).ABSCHNITTS_ID);
        end loop;
        
        if offeneBuchung != true then
            raise ALREADY_CRUISED;
        
        else   
        dbms_output.put_line('BuchungsID: '||offeneBuchungsID);
        UPDATE buchung SET b_id = 3, Befahrungsdatum = Localtimestamp 
            where buchung_id = offeneBuchungsID;
            
            
        end if;
 
        
        
        
        dbms_output.put_line('Mautkategorie: '||achszahlString);
        
    end if;








if v_KennzeichenImAutomatikVerfahrenGefunden = false and v_KennzeichenImManuellenVerfahrenGefunden = false
then
RAISE UNKOWN_VEHICLE;
end if;
-----------------------------------------------------------------------------
EXCEPTION
WHEN UNKOWN_VEHICLE         THEN RAISE UNKOWN_VEHICLE ;
WHEN INVALID_VEHICLE_DATA   THEN RAISE INVALID_VEHICLE_DATA ;
WHEN ALREADY_CRUISED        THEN RAISE ALREADY_CRUISED;
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
