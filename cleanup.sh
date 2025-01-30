#!/bin/bash
FOLDER="OutputFiles/"
if [ -d "$FOLDER" ] && [ -z "$(ls -A "$FOLDER")" ]; then
    echo "...no files to be removed..."
else
    rm -f OutputFiles/*
    echo "...load and listing files removed..."
fi