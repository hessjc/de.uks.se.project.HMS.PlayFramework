#!/bin/bash
SOURCE="${BASH_SOURCE[0]}"
DIR="bash $( cd -P "$( dirname "$SOURCE" )" && pwd )/bin/homeworkmanagementsystem -DapplyEvolutions.default=true -DapplyDownEvolutions.default=true"
eval $DIR