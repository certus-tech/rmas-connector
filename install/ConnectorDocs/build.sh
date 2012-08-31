#!/bin/bash
set -e
########################################################################
# Build script to build the RMAS Connector documentation site.
########################################################################
project_root=../../
tmp_root=${project_root}tmp/

# Prepare tmp dir.
tmp_output=${tmp_root}/RMASConnector/site
if [ -d $tmp_output ]; then
  # A crude 'clean'
  rm -rf $tmp_output
fi
mkdir -p $tmp_output

# Delete any tmp dirs created by Publican.
rmas_root=${project_root}documentation/RMAS
rmasconnector_root=${project_root}documentation/RMASConnector
if [ -d $rmas_root/publish ]; then
  rm -rf $rmas_root/publish
  rm -rf $rmas_root/tmp
fi
if [ -d $rmasconnector_root/publish ]; then
  rm -rf $rmasconnector_root/publish
  rm -rf $rmasconnector_root/tmp
fi

# Create the site files. This unfortunately creates directories for 49 languages,
# whilst we only want en-US.
cd ${tmp_output}
publican create_site --site_config rmasconnector.cfg --db_file rmasconnector.db --toc_path .

# Install the RMASConnector book to the created site.
cd ../../../documentation/RMAS
cp -R /usr/share/publican/Common_Content/common/ ../../ext/Publican/CommonContent
publican build --embedtoc --formats=html,html-single,pdf --langs=en-US --publish
publican install_book --site_config ${tmp_output}/rmasconnector.cfg --lang=en-US

cd ../RMASConnector
publican build --embedtoc --formats=html,html-single,pdf --langs=en-US --publish
publican install_book --site_config ${tmp_output}/rmasconnector.cfg --lang=en-US

cd ${tmp_output}
for f in `ls -1d *-[A-Z][A-Z]` ; do
  if [ -d $f ]; then
    if [[ ! $f =~ en-.* ]]; then
      rm -rf $f
    fi
  fi
done
rm rmasconnector*

