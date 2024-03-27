#!/bin/bash
echo "Deleting redundant files"
rm -r res/layout/*
rm -r src/br/nullexcept/mux/test
rm src/MATA-INF/MANIFEST.MF

echo "Building..."

ant