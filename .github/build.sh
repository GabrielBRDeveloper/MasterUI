#!/bin/bash
echo "Deleting redundant files"

rm -r core/res/layout/example_list.xml
rm -r core/res/layout/main.xml
rm -r core/res/layout/text.xml

rm -r texel/src/br/nullexcept/mux/test
rm core/src/META-INF/MANIFEST.MF

echo "Building..."

ant