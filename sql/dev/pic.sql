declare mdl clob; pic blob; 
begin 
   select molfile into mdl from orchem_compound_sample  where id=42; 
   pic:=orchem_convert.MOLFILETOJPEG(mdl,800,600); 
  extract_file(pic);
end;
/
