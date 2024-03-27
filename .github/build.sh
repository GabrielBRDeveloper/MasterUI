#!/bin/bash
echo "Deleting redundant files"
rm -r res/layout/*
rm -r src/br/nullexcept/mux/test
rm src/META-INF/MANIFEST.MF

echo "Building..."

ant