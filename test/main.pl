#!/usr/local/bin/perl


################################################################################
#                                                                              #
# Main test API for OrChem                                                     #
#                                                                              #
#  This Perl program offers a command line menu for testing OrChem.            #
#  JUnit alone is not sufficient, because of all the database dependencies.    #
#  Instead, this program offers a menu to create a schema with some compounds  #
#  plus OrChem objects.                                                        #
#  The user can then test the fingerprint loading, and test similarity and/or  #
#  substructure searching through JUnit class invocations.                     #
#                                                                              #
# CONFIGURATION                                                                #
#  Just provide the proper database settings in the unittest.properties file.  #
#  !!! For testing, use some bogus account for which all database objects are  #
#  allowed to be dropped and recreated.                                        #
#                                                                              #
#                                                                              #
#                                                                              #
#  markr@ebi.ac.uk                                                             #
#                                                                              #
#                                                                              #
################################################################################

&printBanner;
&getProperties;
&askIfFilePropertiesAreOkay;
&validateConnDetails;

while ("$menuChoice" ne "0") 
{
   &printBanner;
   &printMenu;	 
   &processMenuChoice;	
}


################################################################################
#                                                                              #
# Sub Units                                                                    #
#                                                                              #
################################################################################

#-------------------------------------------------------------------------------
# sub printBanner : prints screen banner
#-------------------------------------------------------------------------------
sub printBanner 
{
	system("clear");
	print "________________________________________________________________\n\n";
	print " OrChem unit testing                                            \n";
	print "________________________________________________________________\n\n";
}


#-------------------------------------------------------------------------------
# sub printMenu : prints menu options
#-------------------------------------------------------------------------------
sub printMenu
{
   &printConnDetails;

	print " - Option 1: - drop ALL(!!) user database objects \n               create fresh schema\n               load (query) compounds\n";
	print " - Option 2: - loadjava \n";
	print " - Option 3: - fingerprinting \n";
	print " - Option 4: - JUnit test similarity search \n";
	print " - Option 5: - JUnit test substructure search  \n\n\n";
	print " - Option 0: - EXIT \n\n";
	print "________________________________________________________________\n\n";
}

#-------------------------------------------------------------------------------
# sub printConnDetails
#-------------------------------------------------------------------------------
sub printConnDetails
{
  print "   username : $username\n";
  print "   password : $password\n";
  print "   url      : $url\n";
  print "   tns name : $tnsName\n\n";
}


#--------------------------------------------------------------------------------
# sub getProperties  
# reads required unittest.properties file
#--------------------------------------------------------------------------------
sub getProperties 
{
  print "Reading file unittest.properties \n\n";
  open (DBPROPS,"../properties/my_local_copies/unittest.properties")||
  die "Can not find unittest.properties file !";
  while (defined($propLine=<DBPROPS>))
  {
    if(index($propLine,"db",0)!=-1)
    { 
      chomp $propLine;
      $i = index($propLine,"=",0)+1;
      if (index($propLine,"dbUrl",0)!=-1) 
      {
         $url=substr($propLine,$i);
      }
      if (index($propLine,"dbTnsName",0)!=-1)
      {
         $tnsName=substr($propLine,$i);
      }
      if (index($propLine,"dbUser",0)!=-1)
      {
         $username=substr($propLine,$i);
      }
      if (index($propLine,"dbPass",0)!=-1)
      {
         $password=substr($propLine,$i);
      }
    }
  }
  &printConnDetails; 
}

#-------------------------------------------------------------------------------
# sub askIfFilePropertiesAreOkay : asks user (y/n) if the DEFAULT 
#                                  database connection details from file are ok
#
# calls "getConnDetailsFromUser" when user wants to override properties file
#-------------------------------------------------------------------------------

sub askIfFilePropertiesAreOkay 
{
   print "BEFORE YOU PROCEED: realise that step 1 of the test suite DROPS all OrChem objects in this schema! \n";
   print "Do you want to test with these settings ?\n\n";
   
   while ("$userFileProperties" eq "") 
   {
     $userFileProperties = &promptUser("Enter y or n");
     if("$userFileProperties" ne "y" && "$userFileProperties" ne "n")
     {
      $userFileProperties="";
     }
   }
   if("$userFileProperties" eq "n")
   {
     print "\n\n";
     &getConnDetailsFromUser;
   }
}


#-------------------------------------------------------------------------------
# sub getConnDetailsFromUser : gets user database connection details overriding 
#                              connection details from the properties file.
#-------------------------------------------------------------------------------
sub getConnDetailsFromUser 
{
	$username = &promptUser("Enter the username ");
	$password = &promptUser("Enter the password ");
	$tnsName = &promptUser("Enter the (tns) database name ");
	$url      = &promptUser("Enter the url ");
}

#----------------------------------------------------------------------------
# sub processMenuChoice : asks user for a choice from the menu
#
#-------------------------------------------------------------------------------

sub processMenuChoice
{
	$menuChoice="";
   print "Please make a choice from the menu above.\n";
   while ("$menuChoice" eq "") 
   {

      $_ = &promptUser("Enter menu option ");
      if(m/[0-6]/)
      {
         $menuChoice=$_;
      }
   }
   
 	system("clear");
 	print  "Option $menuChoice chosen.\n-----------------\n\n";

   if ("$menuChoice" eq "0") 
   {
      print  "Exiting unit test.\n\n\n";
      exit;
   }


   # choice 1 : drop/recreate the schema 
   if ("$menuChoice" eq "1") 
   {
     $confirm = &promptUser("Are you sure? Enter y to proceed and DROP ALL database objects for $username");
     if("$confirm" eq "y" )
     {
      system ("perl ./step1.pl $username $password $tnsName");
     }
   }

   if ("$menuChoice" eq "2") 
   {
      system ("perl ./step2.pl $username $password $tnsName");
   }

   if ("$menuChoice" eq "3") 
   {
      system ("perl ./step3.pl $username $password $tnsName ");
   }

   if ("$menuChoice" eq "4") 
   {
      system ("perl ./step4.pl $username $password $url $tnsName  $cvsDir");
   }

   if ("$menuChoice" eq "5") 
   {
      system ("cd ../ ; ant test.substr");
   }
   if ("$menuChoice" eq "6")
   {
      system ("perl ./step6.pl $username $password $tnsName");
   }


   $_ = &promptUser("\n\nHit ENTER to return to main menu ..");

}



#-------------------------------------------------------------------------------
# sub promptUser : prompts a user for input
#-------------------------------------------------------------------------------

sub promptUser 
{

   local($promptString,$defaultValue) = @_;
   if ($defaultValue) 
   {
      print $promptString, "[", $defaultValue, "]: ";
   } 
   else 
   {
      print $promptString, ": ";
   }

   $| = 1;               # force a flush after our print
   $_ = <STDIN>;         # get the input from STDIN (presumably the keyboard)

   chomp;

   if ("$defaultValue") 
   {
      return $_ ? $_ : $defaultValue;    # return $_ if it has a value
   } else {
      return $_;
   }
}



#-------------------------------------------------------------------------------
# sub validateConnDetails : validates connection details
#-------------------------------------------------------------------------------

sub validateConnDetails {

if ("$username" eq "" || "$password" eq "" || "$tnsName" eq "" )
  {
  print "Fatal: no empty values allowed. Aborting.\n\n";
  exit;
  }

$username =~ tr/a-z/A-Z/;

}



