/*___________________________________________________________________________
  Index creation for OrChem tables
  ___________________________________________________________________________*/

prompt Creating bitmap index on similarity search table
create bitmap index orchem_idx_bmp_simil on orchem_fingprint_simsearch (bit_count)
/

prompt Creating b*tree indexes on substructure search table

create index orchem_btree1 
on
orchem_fingprint_subsearch (
bit1,bit2,bit3,bit4,bit5,bit6,bit7,bit8,bit9,bit10,bit11,bit12,bit13,bit14,bit15,        
bit16,bit17,bit18,bit19,bit20,bit21,bit22,bit23,bit24,bit25,bit26,bit27,bit28,bit29,bit30,bit31
)
parallel 4 nologging
/
create index orchem_btree2
on
orchem_fingprint_subsearch (
bit32,bit33,bit34,bit35,bit36,bit37,bit38,bit39,bit40,bit41,bit42,bit43,bit44,bit45,bit46,bit47,
bit48,bit49,bit50,bit51,bit52,bit53,bit54,bit55,bit56,bit57,bit58,bit59,bit60,bit61,bit62,bit63
)
parallel 4 nologging
/   
create index orchem_btree3
on
orchem_fingprint_subsearch (
bit64,bit65,bit66,bit67,bit68,bit69,bit70,bit71,bit72,bit73,bit74,bit75,bit76,bit77,bit78,bit79,
bit80,bit81,bit82,bit83,bit84,bit85,bit86,bit87,bit88,bit89,bit90,bit91,bit92,bit93,bit94,bit95
)
parallel 4 nologging
/
create index orchem_btree4
on
orchem_fingprint_subsearch (
bit96,bit97,bit98,bit99,bit100,bit101,bit102,bit103,bit104,bit105,bit106,bit107,bit108,bit109,bit110,bit111,
bit112,bit113,bit114,bit115,bit116,bit117,bit118,bit119,bit120,bit121,bit122,bit123,bit124,bit125,bit126,bit127
)
parallel 4 nologging
/
create index orchem_btree5
on
orchem_fingprint_subsearch (
bit128,bit129,bit130,bit131,bit132,bit133,bit134,bit135,bit136,bit137,bit138,bit139,bit140,bit141,bit142,bit143,
bit144,bit145,bit146,bit147,bit148,bit149,bit150,bit151,bit152,bit153,bit154,bit155,bit156,bit157,bit158,bit159
) 
parallel 4 nologging
/
create index orchem_btree6
on
orchem_fingprint_subsearch (
bit160,bit161,bit162,bit163,bit164,bit165,bit166,bit167,bit168,bit169,bit170,bit171,bit172,bit173,bit174,bit175,
bit176,bit177,bit178,bit179,bit180,bit181,bit182,bit183,bit184,bit185,bit186,bit187,bit188,bit189,bit190,bit191
) parallel 4 nologging
/
create index orchem_btree7
on
orchem_fingprint_subsearch (
bit192,bit193,bit194,bit195,bit196,bit197,bit198,bit199,bit200,bit201,bit202,bit203,bit204,bit205,bit206,bit207,
bit208,bit209,bit210,bit211,bit212,bit213,bit214,bit215,bit216,bit217,bit218,bit219,bit220,bit221,bit222,bit223
) parallel 4 nologging
/

create index orchem_btree8
on
orchem_fingprint_subsearch (
bit224,bit225,bit226,bit227,bit228,bit229,bit230,bit231,bit232,bit233,bit234,bit235,bit236,bit237,bit238,bit239,
bit240,bit241,bit242,bit243,bit244,bit245,bit246,bit247,bit248,bit249,bit250,bit251,bit252,bit253,bit254,bit255
) parallel 4 nologging
/
create index orchem_btree9
on
orchem_fingprint_subsearch (
bit256,bit257,bit258,bit259,bit260,bit261,bit262,bit263,bit264,bit265,bit266,bit267,bit268,bit269,bit270,bit271,
bit272,bit273,bit274,bit275,bit276,bit277,bit278,bit279,bit280,bit281,bit282,bit283,bit284,bit285,bit286,bit287
) parallel 4 nologging
/
create index orchem_btree10
on
orchem_fingprint_subsearch (
bit288,bit289,bit290,bit291,bit292,bit293,bit294,bit295,bit296,bit297,bit298,bit299,bit300,bit301,bit302,bit303,
bit304,bit305,bit306,bit307,bit308,bit309,bit310,bit311,bit312,bit313,bit314,bit315,bit316,bit317,bit318,bit319
) parallel 4 nologging
/
create index orchem_btree11
on
orchem_fingprint_subsearch (
bit320,bit321,bit322,bit323,bit324,bit325,bit326,bit327,bit328,bit329,bit330,bit331,bit332,bit333,bit334,bit335,
bit336,bit337,bit338,bit339,bit340,bit341,bit342,bit343,bit344,bit345,bit346,bit347,bit348,bit349,bit350,bit351
) parallel 4 nologging
/
create index orchem_btree12
on
orchem_fingprint_subsearch (
bit352,bit353,bit354,bit355,bit356,bit357,bit358,bit359,bit360,bit361,bit362,bit363,bit364,bit365,bit366,bit367,
bit368,bit369,bit370,bit371,bit372,bit373,bit374,bit375,bit376,bit377,bit378,bit379,bit380,bit381,bit382,bit383
) parallel 4 nologging
/
create index orchem_btree13
on
orchem_fingprint_subsearch (
bit384,bit385,bit386,bit387,bit388,bit389,bit390,bit391,bit392,bit393,bit394,bit395,bit396,bit397,bit398,bit399,
bit400,bit401,bit402,bit403,bit404,bit405,bit406,bit407,bit408,bit409,bit410,bit411,bit412,bit413,bit414,bit415
) parallel 4 nologging
/
create index orchem_btree14
on
orchem_fingprint_subsearch (
bit416,bit417,bit418,bit419,bit420,bit421,bit422,bit423,bit424,bit425,bit426,bit427,bit428,bit429,bit430,bit431,
bit432,bit433,bit434,bit435,bit436,bit437,bit438,bit439,bit440,bit441,bit442,bit443,bit444,bit445,bit446,bit447
) parallel 4 nologging
/
create index orchem_btree15
on
orchem_fingprint_subsearch (
bit448,bit449,bit450,bit451,bit452,bit453,bit454,bit455,bit456,bit457,bit458,bit459,bit460,bit461,bit462,bit463,
bit464,bit465,bit466,bit467,bit468,bit469,bit470,bit471,bit472,bit473,bit474,bit475,bit476,bit477,bit478,bit479
) parallel 4 nologging
/
create index orchem_btree16
on
orchem_fingprint_subsearch (
bit480,bit481,bit482,bit483,bit484,bit485,bit486,bit487,bit488,bit489,bit490,bit491,bit492,bit493,bit494,bit495,
bit496,bit497,bit498,bit499,bit500,bit501,bit502,bit503,bit504,bit505,bit506,bit507,bit508,bit509,bit510,bit511
) parallel 4 nologging
/
create index orchem_btree17
on
orchem_fingprint_subsearch (
bit512,bit513,bit514,bit515,bit516,bit517,bit518,bit519,bit520,bit521,bit522,bit523,bit524,bit525,bit526,bit527,
bit528,bit529,bit530,bit531,bit532,bit533,bit534,bit535,bit536,bit537,bit538,bit539,bit540,bit541,bit542,bit543
) parallel 4 nologging
/
create index orchem_btree18
on
orchem_fingprint_subsearch (
bit544,bit545,bit546,bit547,bit548,bit549,bit550,bit551,bit552,bit553,bit554,bit555,bit556,bit557,bit558,bit559,
bit560,bit561,bit562,bit563,bit564,bit565,bit566,bit567,bit568,bit569,bit570,bit571,bit572,bit573,bit574,bit575
) parallel 4 nologging
/
create index orchem_btree19
on
orchem_fingprint_subsearch (
bit576,bit577,bit578,bit579,bit580,bit581,bit582,bit583,bit584,bit585,bit586,bit587,bit588,bit589,bit590,bit591,
bit592,bit593,bit594,bit595,bit596,bit597,bit598,bit599,bit600,bit601,bit602,bit603,bit604,bit605,bit606,bit607
) parallel 4 nologging
/
create index orchem_btree20
on
orchem_fingprint_subsearch (
bit608,bit609,bit610,bit611,bit612,bit613,bit614,bit615,bit616,bit617,bit618,bit619,bit620,bit621,bit622,bit623,
bit624,bit625,bit626,bit627,bit628,bit629,bit630,bit631,bit632,bit633,bit634,bit635,bit636,bit637,bit638,bit639
) parallel 4 nologging
/
create index orchem_btree21
on
orchem_fingprint_subsearch (
bit640,bit641,bit642,bit643,bit644,bit645,bit646,bit647,bit648,bit649,bit650,bit651,bit652,bit653,bit654,bit655,
bit656,bit657,bit658,bit659,bit660,bit661,bit662,bit663,bit664,bit665,bit666,bit667,bit668,bit669,bit670,bit671
) parallel 4 nologging
/
create index orchem_btree22
on
orchem_fingprint_subsearch (
bit672,bit673,bit674,bit675,bit676,bit677,bit678,bit679,bit680,bit681,bit682,bit683,bit684,bit685,bit686,bit687,
bit688,bit689,bit690,bit691,bit692,bit693,bit694,bit695,bit696,bit697,bit698,bit699,bit700,bit701,bit702,bit703
) parallel 4 nologging
/
create index orchem_btree23
on
orchem_fingprint_subsearch (
bit704,bit705,bit706,bit707,bit708,bit709,bit710,bit711,bit712,bit713,bit714,bit715,bit716,bit717,bit718,bit719,
bit720,bit721,bit722,bit723,bit724,bit725,bit726,bit727,bit728,bit729,bit730,bit731,bit732,bit733,bit734,bit735
) parallel 4 nologging
/
create index orchem_btree24
on
orchem_fingprint_subsearch (
bit736,bit737,bit738,bit739,bit740,bit741,bit742,bit743,bit744,bit745,bit746,bit747,bit748,bit749,bit750,bit751,
bit752,bit753,bit754,bit755,bit756,bit757,bit758,bit759,bit760,bit761,bit762,bit763,bit764,bit765,bit766,bit767
) parallel 4 nologging
/
create index orchem_btree25
on
orchem_fingprint_subsearch (
bit768,bit769,bit770,bit771,bit772,bit773,bit774,bit775
) parallel 4 nologging
/





/*

-- Archived : redundant indices?

create index orchem_btree2a
on
orchem_fingprint_subsearch (
bit16,bit17,bit18,bit19,bit20,bit21,bit22,bit23,bit24,bit25,bit26,bit27,bit28,bit29,bit30,bit31,
bit32,bit33,bit34,bit35,bit36,bit37,bit38,bit39,bit40,bit41,bit42,bit43,bit44,bit45,bit46,bit47
)
parallel 4 nologging
/   

create index orchem_btree3a
on
orchem_fingprint_subsearch (
bit48,bit49,bit50,bit51,bit52,bit53,bit54,bit55,bit56,bit57,bit58,bit59,bit60,bit61,bit62,bit63,
bit64,bit65,bit66,bit67,bit68,bit69,bit70,bit71,bit72,bit73,bit74,bit75,bit76,bit77,bit78,bit79
)
parallel 4 nologging
/

create index orchem_btree4a
on
orchem_fingprint_subsearch (
bit80,bit81,bit82,bit83,bit84,bit85,bit86,bit87,bit88,bit89,bit90,bit91,bit92,bit93,bit94,bit95,
bit96,bit97 ,bit98 ,bit99 ,bit100,bit101,bit102,bit103,bit104,bit105,bit106,bit107,bit108,bit109,bit110,bit111
)
parallel 4 nologging
/

create index orchem_btree5a
on
orchem_fingprint_subsearch (
bit112,bit113,bit114,bit115,bit116,bit117,bit118,bit119,bit120,bit121,bit122,bit123,bit124,bit125,bit126,bit127,
bit128,bit129,bit130,bit131,bit132,bit133,bit134,bit135,bit136,bit137,bit138,bit139,bit140,bit141,bit142,bit143
) 
parallel 4 nologging
/
create index orchem_btree6a
on
orchem_fingprint_subsearch (
bit144,bit145,bit146,bit147,bit148,bit149,bit150,bit151,bit152,bit153,bit154,bit155,bit156,bit157,bit158,bit159,
bit160,bit161,bit162,bit163,bit164,bit165,bit166,bit167,bit168,bit169,bit170,bit171,bit172,bit173,bit174,bit175
) parallel 4 nologging
/
create index orchem_btree7a
on
orchem_fingprint_subsearch (
bit176,bit177,bit178,bit179,bit180,bit181,bit182,bit183,bit184,bit185,bit186,bit187,bit188,bit189,bit190,bit191,
bit192,bit193,bit194,bit195,bit196,bit197,bit198,bit199,bit200,bit201,bit202,bit203,bit204,bit205,bit206,bit207
) parallel 4 nologging
/
create index orchem_btree8a
on
orchem_fingprint_subsearch (
bit208,bit209,bit210,bit211,bit212,bit213,bit214,bit215,bit216,bit217,bit218,bit219,bit220,bit221,bit222,bit223,
bit224,bit225,bit226,bit227,bit228,bit229,bit230,bit231,bit232,bit233,bit234,bit235,bit236,bit237,bit238,bit239
) parallel 4 nologging
/
create index orchem_btree9a
on
orchem_fingprint_subsearch (
bit240,bit241,bit242,bit243,bit244,bit245,bit246,bit247,bit248,bit249,bit250,bit251,bit252,bit253,bit254,bit255,
bit256,bit257,bit258,bit259,bit260,bit261,bit262,bit263,bit264,bit265,bit266,bit267,bit268,bit269,bit270,bit271
) parallel 4 nologging
/
create index orchem_btree10a
on
orchem_fingprint_subsearch (
bit272,bit273,bit274,bit275,bit276,bit277,bit278,bit279,bit280,bit281,bit282,bit283,bit284,bit285,bit286,bit287,
bit288,bit289,bit290,bit291,bit292,bit293,bit294,bit295,bit296,bit297,bit298,bit299,bit300,bit301,bit302,bit303
) parallel 4 nologging
/
create index orchem_btree11a
on
orchem_fingprint_subsearch (
bit304,bit305,bit306,bit307,bit308,bit309,bit310,bit311,bit312,bit313,bit314,bit315,bit316,bit317,bit318,bit319,
bit320,bit321,bit322,bit323,bit324,bit325,bit326,bit327,bit328,bit329,bit330,bit331,bit332,bit333,bit334,bit335
) parallel 4 nologging
/
*/

