

PROMPT drop table orchem_audit_compound_changes (if exists)
drop table orchem_audit_compound_changes
/

PROMPT creating table orchem_audit_compound_changes
create table orchem_audit_compound_changes
( action CHAR(1) NOT NULL, id varchar2(80) NOT NULL, timestamp date NOT NULL)
/


PROMPT creating trigger 'orchem_trg_compound_changes'
CREATE OR REPLACE TRIGGER orchem_trg_compound_changes
 BEFORE DELETE OR INSERT OR UPDATE
 ON &&YOUR_COMPOUND_TABLE
 FOR EACH ROW
DECLARE   
   action char(1);
BEGIN
   IF INSERTING OR UPDATING THEN
       IF INSERTING THEN
          action:='I';
       ELSE 
          action:='U';
       END IF;
      insert into orchem_audit_compound_changes 
      values (action, :NEW.&&YOUR_PRIMARY_KEY,sysdate) ;
   ELSE
      insert into orchem_audit_compound_changes 
      values
      ('D', :OLD.&&YOUR_PRIMARY_KEY,sysdate) ;
   END IF;
END;
/
SHOW ERROR




