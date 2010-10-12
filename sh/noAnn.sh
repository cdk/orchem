#!/bin/sh


# How to use this script
#  copy over the CDK directory to some /tmp/ dir
#  edit build file to refer to 1.5 in lines 
#    -259,7 
#      debug="${debug}" deprecation="${deprecation}" source="1.5"/>
#    -375,7 +375,7 @@
#      debug="${debug}" deprecation="${deprecation}" target="1.5" source="1.5">
#  set Java env to 1.5
#  navigate to the src/main folder
#  uncomment step1 below
#  from src/main, call this script so it runs step1
#      ( FYI: step1 replaces 1.6 @Test stuff with blanks)
#  when finished, continue below
<< step1
find . -name "*.java" -type f | while read filename
do
  echo file found $filename
  sed 's/@Override*//g' < $filename > ${filename}.modified
  mv ${filename}.modified $filename    # omit this line if you want to keep both versions
done

find . -name "*.java" -type f | while read filename
do
  echo file found $filename
  sed 's/@TestMethod.*//g' < $filename > ${filename}.modified
  mv ${filename}.modified $filename    # omit this line if you want to keep both versions
done

find . -name "*.java" -type f | while read filename
do
  echo file found $filename
  sed 's/@TestClass.*//g' < $filename > ${filename}.modified
  mv ${filename}.modified $filename    # omit this line if you want to keep both versions
done

find . -name "*.java" -type f | while read filename
do
  echo file found $filename
  sed 's/@Test//g' < $filename > ${filename}.modified
  mv ${filename}.modified $filename    # omit this line if you want to keep both versions
done
step1



#  comment step1 again, now uncomment step2
#  navigate to cdk main directory
#  run: ant clean dist-all
#  make fixes if Test annotation removal gave problems, run again ant dist-all
#    known trouble makers StereoTool, AtomTypeCharges, AtomicNumberDifferenceDescriptor.java, MannholdLogPDescriptor.java   
#  go to dist/jar dir
#  call this script, with step2 uncommented.
#<< step2
rm *-sources.jar
find . -name "*.jar" -type f | while read filename
do
  unzip -o $filename 
done
rm *.jar
rm *.javafiles
rm -rf META-INF
rm -rf ./org/openscience/cdk/controller/
rm -rf ./org/openscience/cdk/renderer/
rm -rf ./org/openscience/cdk/modeling/
#rm -rf ./org/openscience/cdk/qsar
find . -name "*Test.class" -exec rm -rf {} \;
find . -name "*Tests.class" -exec rm -rf {} \;
find . -name "*Test\$*.class" -exec rm -rf {} \;
rm -rf data/
jar -cvf cdk.jar *
#step2
