#!/bin/sh


# How to use this script
#
#  set Java env to 1.5
#  make a fresh CDK (git) checkout 10-unsorted
#  navigate to the src/main folder
#  uncomment step1 below
#  from src/main, call this script so it runs step1
#      ( FYI: step1 replaces 1.6 @Test stuff with blanks)
#  when finished, continue below
<< step1
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
#  make fixes if Test remove gave problems
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