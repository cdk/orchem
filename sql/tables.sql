    
    create table orchem_parameters
    (  comp_tab_name         varchar2(30) not null
      ,comp_tab_pk_col       varchar2(30) not null
      ,comp_tab_molfile_col  varchar2(30) not null
      ,comp_tab_formula_col  varchar2(30) not null
    )
    cache
    /
    -- example :  insert into orchem_parameters values ('COMPOUNDS','MOLREGNO','MOLFILE', 'MOLFORMULA')
    
    
    create table orchem_fingprint_simsearch
    ( id varchar2(80)         not null -- PK
    , bit_count number(4)     not null 
    , fp raw(128)             not null
    )
    -- PCTFREE 0: could do
    cache
    /
    alter table orchem_fingprint_simsearch  add constraint pk_chem_fp primary key (id)
    /
    
    create table orchem_fingprint_subsearch
    (   
      id  varchar2(80)  not null
     ,bit1  char(1)
     ,bit2  char(1)
     ,bit3  char(1)
     ,bit4  char(1)
     ,bit5  char(1)
     ,bit6  char(1)
     ,bit7  char(1)
     ,bit8  char(1)
     ,bit9  char(1)
     ,bit10  char(1)
     ,bit11  char(1)
     ,bit12  char(1)
     ,bit13  char(1)
     ,bit14  char(1)
     ,bit15  char(1)
     ,bit16  char(1)
     ,bit17  char(1)
     ,bit18  char(1)
     ,bit19  char(1)
     ,bit20  char(1)
     ,bit21  char(1)
     ,bit22  char(1)
     ,bit23  char(1)
     ,bit24  char(1)
     ,bit25  char(1)
     ,bit26  char(1)
     ,bit27  char(1)
     ,bit28  char(1)
     ,bit29  char(1)
     ,bit30  char(1)
     ,bit31  char(1)
     ,bit32  char(1)
     ,bit33  char(1)
     ,bit34  char(1)
     ,bit35  char(1)
     ,bit36  char(1)
     ,bit37  char(1)
     ,bit38  char(1)
     ,bit39  char(1)
     ,bit40  char(1)
     ,bit41  char(1)
     ,bit42  char(1)
     ,bit43  char(1)
     ,bit44  char(1)
     ,bit45  char(1)
     ,bit46  char(1)
     ,bit47  char(1)
     ,bit48  char(1)
     ,bit49  char(1)
     ,bit50  char(1)
     ,bit51  char(1)
     ,bit52  char(1)
     ,bit53  char(1)
     ,bit54  char(1)
     ,bit55  char(1)
     ,bit56  char(1)
     ,bit57  char(1)
     ,bit58  char(1)
     ,bit59  char(1)
     ,bit60  char(1)
     ,bit61  char(1)
     ,bit62  char(1)
     ,bit63  char(1)
     ,bit64  char(1)
     ,bit65  char(1)
     ,bit66  char(1)
     ,bit67  char(1)
     ,bit68  char(1)
     ,bit69  char(1)
     ,bit70  char(1)
     ,bit71  char(1)
     ,bit72  char(1)
     ,bit73  char(1)
     ,bit74  char(1)
     ,bit75  char(1)
     ,bit76  char(1)
     ,bit77  char(1)
     ,bit78  char(1)
     ,bit79  char(1)
     ,bit80  char(1)
     ,bit81  char(1)
     ,bit82  char(1)
     ,bit83  char(1)
     ,bit84  char(1)
     ,bit85  char(1)
     ,bit86  char(1)
     ,bit87  char(1)
     ,bit88  char(1)
     ,bit89  char(1)
     ,bit90  char(1)
     ,bit91  char(1)
     ,bit92  char(1)
     ,bit93  char(1)
     ,bit94  char(1)
     ,bit95  char(1)
     ,bit96  char(1)
     ,bit97  char(1)
     ,bit98  char(1)
     ,bit99  char(1)
     ,bit100  char(1)
     ,bit101  char(1)
     ,bit102  char(1)
     ,bit103  char(1)
     ,bit104  char(1)
     ,bit105  char(1)
     ,bit106  char(1)
     ,bit107  char(1)
     ,bit108  char(1)
     ,bit109  char(1)
     ,bit110  char(1)
     ,bit111  char(1)
     ,bit112  char(1)
     ,bit113  char(1)
     ,bit114  char(1)
     ,bit115  char(1)
     ,bit116  char(1)
     ,bit117  char(1)
     ,bit118  char(1)
     ,bit119  char(1)
     ,bit120  char(1)
     ,bit121  char(1)
     ,bit122  char(1)
     ,bit123  char(1)
     ,bit124  char(1)
     ,bit125  char(1)
     ,bit126  char(1)
     ,bit127  char(1)
     ,bit128  char(1)
     ,bit129  char(1)
     ,bit130  char(1)
     ,bit131  char(1)
     ,bit132  char(1)
     ,bit133  char(1)
     ,bit134  char(1)
     ,bit135  char(1)
     ,bit136  char(1)
     ,bit137  char(1)
     ,bit138  char(1)
     ,bit139  char(1)
     ,bit140  char(1)
     ,bit141  char(1)
     ,bit142  char(1)
     ,bit143  char(1)
     ,bit144  char(1)
     ,bit145  char(1)
     ,bit146  char(1)
     ,bit147  char(1)
     ,bit148  char(1)
     ,bit149  char(1)
     ,bit150  char(1)
     ,bit151  char(1)
     ,bit152  char(1)
     ,bit153  char(1)
     ,bit154  char(1)
     ,bit155  char(1)
     ,bit156  char(1)
     ,bit157  char(1)
     ,bit158  char(1)
     ,bit159  char(1)
     ,bit160  char(1)
     ,bit161  char(1)
     ,bit162  char(1)
     ,bit163  char(1)
     ,bit164  char(1)
     ,bit165  char(1)
     ,bit166  char(1)
     ,bit167  char(1)
     ,bit168  char(1)
     ,bit169  char(1)
     ,bit170  char(1)
     ,bit171  char(1)
     ,bit172  char(1)
     ,bit173  char(1)
     ,bit174  char(1)
     ,bit175  char(1)
     ,bit176  char(1)
     ,bit177  char(1)
     ,bit178  char(1)
     ,bit179  char(1)
     ,bit180  char(1)
     ,bit181  char(1)
     ,bit182  char(1)
     ,bit183  char(1)
     ,bit184  char(1)
     ,bit185  char(1)
     ,bit186  char(1)
     ,bit187  char(1)
     ,bit188  char(1)
     ,bit189  char(1)
     ,bit190  char(1)
     ,bit191  char(1)
     ,bit192  char(1)
     ,bit193  char(1)
     ,bit194  char(1)
     ,bit195  char(1)
     ,bit196  char(1)
     ,bit197  char(1)
     ,bit198  char(1)
     ,bit199  char(1)
     ,bit200  char(1)
     ,bit201  char(1)
     ,bit202  char(1)
     ,bit203  char(1)
     ,bit204  char(1)
     ,bit205  char(1)
     ,bit206  char(1)
     ,bit207  char(1)
     ,bit208  char(1)
     ,bit209  char(1)
     ,bit210  char(1)
     ,bit211  char(1)
     ,bit212  char(1)
     ,bit213  char(1)
     ,bit214  char(1)
     ,bit215  char(1)
     ,bit216  char(1)
     ,bit217  char(1)
     ,bit218  char(1)
     ,bit219  char(1)
     ,bit220  char(1)
     ,bit221  char(1)
     ,bit222  char(1)
     ,bit223  char(1)
     ,bit224  char(1)
     ,bit225  char(1)
     ,bit226  char(1)
     ,bit227  char(1)
     ,bit228  char(1)
     ,bit229  char(1)
     ,bit230  char(1)
     ,bit231  char(1)
     ,bit232  char(1)
     ,bit233  char(1)
     ,bit234  char(1)
     ,bit235  char(1)
     ,bit236  char(1)
     ,bit237  char(1)
     ,bit238  char(1)
     ,bit239  char(1)
     ,bit240  char(1)
     ,bit241  char(1)
     ,bit242  char(1)
     ,bit243  char(1)
     ,bit244  char(1)
     ,bit245  char(1)
     ,bit246  char(1)
     ,bit247  char(1)
     ,bit248  char(1)
     ,bit249  char(1)
     ,bit250  char(1)
     ,bit251  char(1)
     ,bit252  char(1)
     ,bit253  char(1)
     ,bit254  char(1)
     ,bit255  char(1)
     ,bit256  char(1)
     ,bit257  char(1)
     ,bit258  char(1)
     ,bit259  char(1)
     ,bit260  char(1)
     ,bit261  char(1)
     ,bit262  char(1)
     ,bit263  char(1)
     ,bit264  char(1)
     ,bit265  char(1)
     ,bit266  char(1)
     ,bit267  char(1)
     ,bit268  char(1)
     ,bit269  char(1)
     ,bit270  char(1)
     ,bit271  char(1)
     ,bit272  char(1)
     ,bit273  char(1)
     ,bit274  char(1)
     ,bit275  char(1)
     ,bit276  char(1)
     ,bit277  char(1)
     ,bit278  char(1)
     ,bit279  char(1)
     ,bit280  char(1)
     ,bit281  char(1)
     ,bit282  char(1)
     ,bit283  char(1)
     ,bit284  char(1)
     ,bit285  char(1)
     ,bit286  char(1)
     ,bit287  char(1)
     ,bit288  char(1)
     ,bit289  char(1)
     ,bit290  char(1)
     ,bit291  char(1)
     ,bit292  char(1)
     ,bit293  char(1)
     ,bit294  char(1)
     ,bit295  char(1)
     ,bit296  char(1)
     ,bit297  char(1)
     ,bit298  char(1)
     ,bit299  char(1)
     ,bit300  char(1)
     ,bit301  char(1)
     ,bit302  char(1)
     ,bit303  char(1)
     ,bit304  char(1)
     ,bit305  char(1)
     ,bit306  char(1)
     ,bit307  char(1)
     ,bit308  char(1)
     ,bit309  char(1)
     ,bit310  char(1)
     ,bit311  char(1)
     ,bit312  char(1)
     ,bit313  char(1)
     ,bit314  char(1)
     ,bit315  char(1)
     ,bit316  char(1)
     ,bit317  char(1)
     ,bit318  char(1)
     ,bit319  char(1)
     ,bit320  char(1)
     ,bit321  char(1)
     ,bit322  char(1)
     ,bit323  char(1)
     ,bit324  char(1)
     ,bit325  char(1)
     ,bit326  char(1)
     ,bit327  char(1)
     ,bit328  char(1)
     ,bit329  char(1)
     ,bit330  char(1)
     ,bit331  char(1)
     ,bit332  char(1)
     ,bit333  char(1)
     ,bit334  char(1)
     ,bit335  char(1)
     ,bit336  char(1)
     ,bit337  char(1)
     ,bit338  char(1)
     ,bit339  char(1)
     ,bit340  char(1)
     ,bit341  char(1)
     ,bit342  char(1)
     ,bit343  char(1)
     ,bit344  char(1)
     ,bit345  char(1)
     ,bit346  char(1)
     ,bit347  char(1)
     ,bit348  char(1)
     ,bit349  char(1)
     ,bit350  char(1)
     ,bit351  char(1)
     ,bit352  char(1)
     ,bit353  char(1)
     ,bit354  char(1)
     ,bit355  char(1)
     ,bit356  char(1)
     ,bit357  char(1)
     ,bit358  char(1)
     ,bit359  char(1)
     ,bit360  char(1)
     ,bit361  char(1)
     ,bit362  char(1)
     ,bit363  char(1)
     ,bit364  char(1)
     ,bit365  char(1)
     ,bit366  char(1)
     ,bit367  char(1)
     ,bit368  char(1)
     ,bit369  char(1)
     ,bit370  char(1)
     ,bit371  char(1)
     ,bit372  char(1)
     ,bit373  char(1)
     ,bit374  char(1)
     ,bit375  char(1)
     ,bit376  char(1)
     ,bit377  char(1)
     ,bit378  char(1)
     ,bit379  char(1)
     ,bit380  char(1)
     ,bit381  char(1)
     ,bit382  char(1)
     ,bit383  char(1)
     ,bit384  char(1)
     ,bit385  char(1)
     ,bit386  char(1)
     ,bit387  char(1)
     ,bit388  char(1)
     ,bit389  char(1)
     ,bit390  char(1)
     ,bit391  char(1)
     ,bit392  char(1)
     ,bit393  char(1)
     ,bit394  char(1)
     ,bit395  char(1)
     ,bit396  char(1)
     ,bit397  char(1)
     ,bit398  char(1)
     ,bit399  char(1)
     ,bit400  char(1)
     ,bit401  char(1)
     ,bit402  char(1)
     ,bit403  char(1)
     ,bit404  char(1)
     ,bit405  char(1)
     ,bit406  char(1)
     ,bit407  char(1)
     ,bit408  char(1)
     ,bit409  char(1)
     ,bit410  char(1)
     ,bit411  char(1)
     ,bit412  char(1)
     ,bit413  char(1)
     ,bit414  char(1)
     ,bit415  char(1)
     ,bit416  char(1)
     ,bit417  char(1)
     ,bit418  char(1)
     ,bit419  char(1)
     ,bit420  char(1)
     ,bit421  char(1)
     ,bit422  char(1)
     ,bit423  char(1)
     ,bit424  char(1)
     ,bit425  char(1)
     ,bit426  char(1)
     ,bit427  char(1)
     ,bit428  char(1)
     ,bit429  char(1)
     ,bit430  char(1)
     ,bit431  char(1)
     ,bit432  char(1)
     ,bit433  char(1)
     ,bit434  char(1)
     ,bit435  char(1)
     ,bit436  char(1)
     ,bit437  char(1)
     ,bit438  char(1)
     ,bit439  char(1)
     ,bit440  char(1)
     ,bit441  char(1)
     ,bit442  char(1)
     ,bit443  char(1)
     ,bit444  char(1)
     ,bit445  char(1)
     ,bit446  char(1)
     ,bit447  char(1)
     ,bit448  char(1)
     ,bit449  char(1)
     ,bit450  char(1)
     ,bit451  char(1)
     ,bit452  char(1)
     ,bit453  char(1)
     ,bit454  char(1)
     ,bit455  char(1)
     ,bit456  char(1)
     ,bit457  char(1)
     ,bit458  char(1)
     ,bit459  char(1)
     ,bit460  char(1)
     ,bit461  char(1)
     ,bit462  char(1)
     ,bit463  char(1)
     ,bit464  char(1)
     ,bit465  char(1)
     ,bit466  char(1)
     ,bit467  char(1)
     ,bit468  char(1)
     ,bit469  char(1)
     ,bit470  char(1)
     ,bit471  char(1)
     ,bit472  char(1)
     ,bit473  char(1)
     ,bit474  char(1)
     ,bit475  char(1)
     ,bit476  char(1)
     ,bit477  char(1)
     ,bit478  char(1)
     ,bit479  char(1)
     ,bit480  char(1)
     ,bit481  char(1)
     ,bit482  char(1)
     ,bit483  char(1)
     ,bit484  char(1)
     ,bit485  char(1)
     ,bit486  char(1)
     ,bit487  char(1)
     ,bit488  char(1)
     ,bit489  char(1)
     ,bit490  char(1)
     ,bit491  char(1)
     ,bit492  char(1)
     ,bit493  char(1)
     ,bit494  char(1)
     ,bit495  char(1)
     ,bit496  char(1)
     ,bit497  char(1)
     ,bit498  char(1)
     ,bit499  char(1)
     ,bit500  char(1)
     ,bit501  char(1)
     ,bit502  char(1)
     ,bit503  char(1)
     ,bit504  char(1)
     ,bit505  char(1)
     ,bit506  char(1)
     ,bit507  char(1)
     ,bit508  char(1)
     ,bit509  char(1)
     ,bit510  char(1)
     ,bit511  char(1)
     ,bit512  char(1)
     ,single_bond_count      number(6)
     ,double_bond_count      number(6)
     ,triple_bond_count      number(6)
     ,s_count                number(6)
     ,o_count                number(6)
     ,n_count                number(6)
     ,f_count                number(6)
     ,cl_count               number(6)
     ,br_count               number(6)
     ,i_count                number(6)
     ,c_count                number(6)
         ,p_count                number(6)
     )
    /

    alter table orchem_fingprint_subsearch  add constraint pk_orchem_compinfo primary key (id)
    /
    
