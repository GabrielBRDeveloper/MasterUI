#!/bin/bash
echo "Deleting redundant files"

rm -r res/layout/example_list.xml
rm -r res/layout/main.xml
rm -r res/layout/text.xml

rm -r src/br/nullexcept/mux/test
rm src/META-INF/MANIFEST.MF

echo "Building..."

ant