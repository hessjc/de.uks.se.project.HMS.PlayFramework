#!/bin/bash
SOURCE="${BASH_SOURCE[0]}"
DIR="bash $( cd -P "$( dirname "$SOURCE" )" && pwd )/bin/homeworkmanagementsystem -jvm-debug 8000"
eval $DIR