

create bitmap index orchem_idx_bmp_simil on orchem_fingprint_simsearch (bit_count)
/

/* B trees */ 

create index orchem_btree1 
on
orchem_fingprint_subsearch (
bit1,bit2,bit3,bit4,bit5,bit6,bit7,bit8,bit9,bit10,bit11,bit12,bit13,bit14,bit15,bit16,bit17,bit18,bit19,bit20,bit21,bit22,bit23,bit24,bit25,bit26,bit27,bit28,bit29,bit30,bit31,bit32
)
parallel 4 nologging
/

create index orchem_btree2
on
orchem_fingprint_subsearch (
bit33,bit34,bit35,bit36,bit37,bit38,bit39,bit40,bit41,bit42,bit43,bit44,bit45,bit46,bit47,bit48,bit49,bit50,bit51,bit52,bit53,bit54,bit55,bit56,bit57,bit58,bit59,bit60,bit61,bit62,bit63,bit64
)
parallel 4 nologging
/   

create index orchem_btree3
on
orchem_fingprint_subsearch (
bit65,bit66,bit67,bit68,bit69,bit70,bit71,bit72,bit73,bit74,bit75,bit76,bit77,bit78,bit79,bit80,bit81,bit82,bit83,bit84,bit85,bit86,bit87,bit88,bit89,bit90,bit91,bit92,bit93,bit94,bit95,bit96
)
parallel 4 nologging
/
   
create index orchem_btree4
on
orchem_fingprint_subsearch (
bit97,bit98,bit99,bit100,bit101,bit102,bit103,bit104,bit105,bit106,bit107,bit108,bit109,bit110,bit111,bit112,bit113,bit114,bit115,bit116,bit117,bit118,bit119,bit120,bit121,bit122,bit123,bit124,bit125,bit126,bit127,bit128
)
parallel 4 nologging
/

create index orchem_btree5
on
orchem_fingprint_subsearch (
bit129,bit130,bit131,bit132,bit133,bit134,bit135,bit136,bit137,bit138,bit139,bit140,bit141,bit142,bit143,bit144,bit145,bit146,bit147,bit148,bit149,bit150,bit151,bit152,bit153,bit154,bit155,bit156,bit157,bit158,bit159,bit160
) 
parallel 4 nologging
/

create index orchem_btree6
on
orchem_fingprint_subsearch (
bit161,bit162,bit163,bit164,bit165,bit166,bit167,bit168,bit169,bit170,bit171,bit172,bit173,bit174,bit175,bit176,bit177,bit178,bit179,bit180,bit181,bit182,bit183,bit184,bit185,bit186,bit187,bit188,bit189,bit190,bit191,bit192
) parallel 4 nologging
/

create index orchem_btree7
on
orchem_fingprint_subsearch (
bit193,bit194,bit195,bit196,bit197,bit198,bit199,bit200,bit201,bit202,bit203,bit204,bit205,bit206,bit207,bit208,bit209,bit210,bit211,bit212,bit213,bit214,bit215,bit216,bit217,bit218,bit219,bit220,bit221,bit222,bit223,bit224
) parallel 4 nologging
/

create index orchem_btree8
on
orchem_fingprint_subsearch (
bit225,bit226,bit227,bit228,bit229,bit230,bit231,bit232,bit233,bit234,bit235,bit236,bit237,bit238,bit239,bit240,bit241,bit242,bit243,bit244,bit245,bit246,bit247,bit248,bit249,bit250,bit251,bit252,bit253,bit254,bit255,bit256
) parallel 4 nologging
/


create index orchem_btree9
on
orchem_fingprint_subsearch (
bit257,bit258,bit259,bit260,bit261,bit262,bit263,bit264,bit265,bit266,bit267,bit268,bit269,bit270,bit271,bit272,bit273,bit274,bit275,bit276,bit277,bit278,bit279,bit280,bit281,bit282,bit283,bit284,bit285,bit286,bit287,bit288
) parallel 4 nologging
/



create index orchem_btree10
on
orchem_fingprint_subsearch (
bit289,bit290,bit291,bit292,bit293,bit294,bit295,bit296,bit297,bit298,bit299,bit300,bit301,bit302,bit303,bit304,bit305,bit306,bit307,bit308,bit309,bit310,bit311,bit312,bit313,bit314,bit315,bit316,bit317,bit318,bit319,bit320
) parallel 4 nologging
/

create index orchem_btree11
on
orchem_fingprint_subsearch (
bit321,bit322,bit323,bit324,bit325,bit326,bit327,bit328,bit329,bit330,bit331,bit332,bit333,bit334,bit335,bit336,bit337,bit338,bit339,bit340,bit341,bit342,bit343,bit344,bit345,bit346,bit347,bit348,bit349,bit350,bit351,bit352
) parallel 4 nologging
/

create index orchem_btree12
on
orchem_fingprint_subsearch (
bit353,bit354,bit355,bit356,bit357,bit358,bit359,bit360,bit361,bit362,bit363,bit364,bit365,bit366,bit367,bit368,bit369,bit370,bit371,bit372,bit373,bit374,bit375,bit376,bit377,bit378,bit379,bit380,bit381,bit382,bit383,bit384
) parallel 4 nologging
/

create index orchem_btree13
on
orchem_fingprint_subsearch (
bit385,bit386,bit387,bit388,bit389,bit390,bit391,bit392,bit393,bit394,bit395,bit396,bit397,bit398,bit399,bit400,bit401,bit402,bit403,bit404,bit405,bit406,bit407,bit408,bit409,bit410,bit411,bit412,bit413,bit414,bit415,bit416
) parallel 4 nologging
/

create index orchem_btree14
on
orchem_fingprint_subsearch (
bit417,bit418,bit419,bit420,bit421,bit422,bit423,bit424,bit425,bit426,bit427,bit428,bit429,bit430,bit431,bit432,bit433,bit434,bit435,bit436,bit437,bit438,bit439,bit440,bit441,bit442,bit443,bit444,bit445,bit446,bit447,bit448
) parallel 4 nologging
/

create index orchem_btree15
on
orchem_fingprint_subsearch (
bit449,bit450,bit451,bit452,bit453,bit454,bit455,bit456,bit457,bit458,bit459,bit460,bit461,bit462,bit463,bit464,bit465,bit466,bit467,bit468,bit469,bit470,bit471,bit472,bit473,bit474,bit475,bit476,bit477,bit478,bit479,bit480
) parallel 4 nologging
/

create index orchem_btree16
on
orchem_fingprint_subsearch (
bit481,bit482,bit483,bit484,bit485,bit486,bit487,bit488,bit489,bit490,bit491,bit492,bit493,bit494,bit495,bit496,bit497,bit498,bit499,bit500,bit501,bit502,bit503,bit504,bit505,bit506,bit507,bit508,bit509,bit510,bit511,bit512
) parallel 4 nologging
/

create index orchem_btree1a 
on
orchem_fingprint_subsearch (
bit1 ,bit2 ,bit3 ,bit4 ,bit5 ,bit6 ,bit7 ,bit8 ,bit9 ,bit10,bit11,bit12,bit13,bit14,bit15,bit16,
bit497,bit498,bit499,bit500,bit501,bit502,bit503,bit504,bit505,bit506,bit507,bit508,bit509,bit510,bit511,bit512
)
parallel 4 nologging
/

create index orchem_btree2a
on
orchem_fingprint_subsearch (
bit17,bit18,bit19,bit20,bit21,bit22,bit23,bit24,bit25,bit26,bit27,bit28,bit29,bit30,bit31,bit32,
bit33,bit34,bit35,bit36,bit37,bit38,bit39,bit40,bit41,bit42,bit43,bit44,bit45,bit46,bit47,bit48
)
parallel 4 nologging
/   

create index orchem_btree3a
on
orchem_fingprint_subsearch (
bit49,bit50,bit51,bit52,bit53,bit54,bit55,bit56,bit57,bit58,bit59,bit60,bit61,bit62,bit63,bit64,
bit65,bit66,bit67,bit68,bit69,bit70,bit71,bit72,bit73,bit74,bit75,bit76,bit77,bit78,bit79,bit80
)
parallel 4 nologging
/
   
create index orchem_btree4a
on
orchem_fingprint_subsearch (
bit81,bit82,bit83,bit84,bit85,bit86,bit87,bit88,bit89,bit90,bit91,bit92,bit93,bit94,bit95,bit96,
bit97 ,bit98 ,bit99 ,bit100,bit101,bit102,bit103,bit104,bit105,bit106,bit107,bit108,bit109,bit110,bit111,bit112
)
parallel 4 nologging
/

create index orchem_btree5a
on
orchem_fingprint_subsearch (
bit113,bit114,bit115,bit116,bit117,bit118,bit119,bit120,bit121,bit122,bit123,bit124,bit125,bit126,bit127,bit128,
bit129,bit130,bit131,bit132,bit133,bit134,bit135,bit136,bit137,bit138,bit139,bit140,bit141,bit142,bit143,bit144
) 
parallel 4 nologging
/

create index orchem_btree6a
on
orchem_fingprint_subsearch (
bit145,bit146,bit147,bit148,bit149,bit150,bit151,bit152,bit153,bit154,bit155,bit156,bit157,bit158,bit159,bit160,
bit161,bit162,bit163,bit164,bit165,bit166,bit167,bit168,bit169,bit170,bit171,bit172,bit173,bit174,bit175,bit176
) parallel 4 nologging
/

create index orchem_btree7a
on
orchem_fingprint_subsearch (
bit177,bit178,bit179,bit180,bit181,bit182,bit183,bit184,bit185,bit186,bit187,bit188,bit189,bit190,bit191,bit192,
bit193,bit194,bit195,bit196,bit197,bit198,bit199,bit200,bit201,bit202,bit203,bit204,bit205,bit206,bit207,bit208
) parallel 4 nologging
/

create index orchem_btree8a
on
orchem_fingprint_subsearch (
bit209,bit210,bit211,bit212,bit213,bit214,bit215,bit216,bit217,bit218,bit219,bit220,bit221,bit222,bit223,bit224,
bit225,bit226,bit227,bit228,bit229,bit230,bit231,bit232,bit233,bit234,bit235,bit236,bit237,bit238,bit239,bit240
) parallel 4 nologging
/


create index orchem_btree9a
on
orchem_fingprint_subsearch (
bit241,bit242,bit243,bit244,bit245,bit246,bit247,bit248,bit249,bit250,bit251,bit252,bit253,bit254,bit255,bit256,
bit257,bit258,bit259,bit260,bit261,bit262,bit263,bit264,bit265,bit266,bit267,bit268,bit269,bit270,bit271,bit272
) parallel 4 nologging
/



create index orchem_btree10a
on
orchem_fingprint_subsearch (
bit273,bit274,bit275,bit276,bit277,bit278,bit279,bit280,bit281,bit282,bit283,bit284,bit285,bit286,bit287,bit288,
bit289,bit290,bit291,bit292,bit293,bit294,bit295,bit296,bit297,bit298,bit299,bit300,bit301,bit302,bit303,bit304
) parallel 4 nologging
/

create index orchem_btree11a
on
orchem_fingprint_subsearch (
bit305,bit306,bit307,bit308,bit309,bit310,bit311,bit312,bit313,bit314,bit315,bit316,bit317,bit318,bit319,bit320,
bit321,bit322,bit323,bit324,bit325,bit326,bit327,bit328,bit329,bit330,bit331,bit332,bit333,bit334,bit335,bit336
) parallel 4 nologging
/

create index orchem_btree12a
on
orchem_fingprint_subsearch (
bit337,bit338,bit339,bit340,bit341,bit342,bit343,bit344,bit345,bit346,bit347,bit348,bit349,bit350,bit351,bit352,
bit353,bit354,bit355,bit356,bit357,bit358,bit359,bit360,bit361,bit362,bit363,bit364,bit365,bit366,bit367,bit368
) parallel 4 nologging
/

create index orchem_btree13a
on
orchem_fingprint_subsearch (
bit369,bit370,bit371,bit372,bit373,bit374,bit375,bit376,bit377,bit378,bit379,bit380,bit381,bit382,bit383,bit384,
bit385,bit386,bit387,bit388,bit389,bit390,bit391,bit392,bit393,bit394,bit395,bit396,bit397,bit398,bit399,bit400
) parallel 4 nologging
/

create index orchem_btree14a
on
orchem_fingprint_subsearch (
bit401,bit402,bit403,bit404,bit405,bit406,bit407,bit408,bit409,bit410,bit411,bit412,bit413,bit414,bit415,bit416,
bit417,bit418,bit419,bit420,bit421,bit422,bit423,bit424,bit425,bit426,bit427,bit428,bit429,bit430,bit431,bit432
) parallel 4 nologging
/

create index orchem_btree15a
on
orchem_fingprint_subsearch (
bit433,bit434,bit435,bit436,bit437,bit438,bit439,bit440,bit441,bit442,bit443,bit444,bit445,bit446,bit447,bit448,
bit449,bit450,bit451,bit452,bit453,bit454,bit455,bit456,bit457,bit458,bit459,bit460,bit461,bit462,bit463,bit464
) parallel 4 nologging
/

create index orchem_btree16a
on
orchem_fingprint_subsearch (
bit465,bit466,bit467,bit468,bit469,bit470,bit471,bit472,bit473,bit474,bit475,bit476,bit477,bit478,bit479,bit480,
bit481,bit482,bit483,bit484,bit485,bit486,bit487,bit488,bit489,bit490,bit491,bit492,bit493,bit494,bit495,bit496
) parallel 4 nologging
/














------------------------


-- _________________________________________________________
-- ____                                                 ____
-- ____            Archived scripts                     ____
-- ____                                                 ____
-- _________________________________________________________

/*
    declare 
       j binary_integer; 
       job varchar2(1000);
    begin 
      for i in 0..15 loop
        job:='begin for i in '||((i*32)+1)||' .. '||((i+1)*32)||' loop execute immediate(''create bitmap index orchem_bmp_idx_fp''||i||'' on orchem_fingprint_subsearch (bit''||i||'') parallel 2 nologging'' ); end loop; end;'  ;
        --dbms_output.put_line(job);
        dbms_job.submit(j,job,sysdate);
      end loop;
      commit; -- must commit to activate dbms job
    end;
*/

  /*
   declare 
      j binary_integer; 
      job varchar2(1000);
   begin 
      for i in 0..15 loop
        job:='begin for i in '||((i*32)+1)||' .. '||((i+1)*32)||' loop execute immediate(''create bitmap index orchem_bmp_idx_fp''||i||'' on orchem_fingprint_subsearch (bit''||i||'') parallel 2 nologging'' ); end loop; end;'  ;
        --dbms_output.put_line(job);
        dbms_job.submit(j,job,sysdate);
      end loop;
      commit; -- must commit to activate dbms job
   end;
   */

    -- Archived: Oracle context index. Performance not adequate.
    /*
    CREATE INDEX SUBSTR_SEARCH_INDX ON orchem_fp_text (text) INDEXTYPE IS CTXSYS.CONTEXT tablespace crossref_ind
    /
    */

    -- Archived: Luciano index. Performance not adequate.
    /*
    create index LUCENE_IDX on orchem_fingprint_subsearch (fp_text) 
    indextype is lucene.LuceneIndex
    parameters('Analyzer:org.apache.lucene.analysis.standard.StandardAnalyzer')
    /
    */
    
    
