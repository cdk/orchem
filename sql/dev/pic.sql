declare mdl clob; pic blob; 
begin 
   select molfile into mdl from orchem_compound_sample  where id=38; 
   pic:=orchem_convert.MOLFILETOJPEG(mdl,400,250); 
  extract_file(pic);
end;
/
