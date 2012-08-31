#!/bin/bash
########################################################################
# Build script to build the RMAS Connector documentation site.
# $Id: build.sh,v 1.1 2012-06-13 19:39:09 rob Exp $
########################################################################

set -e
startdir=`pwd`
cd ../..
project_root=`pwd`
tmp_root=${project_root}/tmp

# Prepare tmp dir.
tmp_output=${tmp_root}/RMASConnector/site
if [ -d $tmp_output ]; then
  rm -rf $tmp_output
fi
mkdir -p $tmp_output

# Create the site files. This unfortunately creates directories for 49 languages,
# whilst we only want en-GB.
cd ${tmp_output}
echo publican create_site --site_config rmasconnector.cfg --db_file rmasconnector.db --toc_path .
publican create_site --site_config rmasconnector.cfg --db_file rmasconnector.db --toc_path .

# Install the RMASConnector book to the created site.
cd ${startdir}
cd ../../documentation/RMASConnector
echo publican install_book --site_config ${tmp_output}/rmasconnector.cfg --lang=en-GB
publican install_book --site_config ${tmp_output}/rmasconnector.cfg --lang=en-GB
