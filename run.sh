#!/bin/bash
clear

function pause(){
   read -p "$*"
}
 
 
echo "---------Starting Script---------"
echo
echo "---------Generating Vars---------"
MC_ADDRESS="230.0.0.1"
MC_PORT="3333"
MCB_ADDRESS="230.0.0.2"
MCB_PORT="4444"
MCR_ADDRESS="230.0.0.3"
MCR_PORT="5555"
BIN="bin/"
BASEDIR=$(dirname "$0")
echo "-------Making Directory bin------"
mkdir -p $BIN
echo "-------------Compiling-----------"
javac -d $BIN src/channels/*.java src/filefunc/*.java src/interfaces/*.java src/message/*.java src/protocols/*.java
echo "-------------Running-------------"
cd storage
java -cp ../$BIN interfaces.Main $MC_ADDRESS $MC_PORT $MCB_ADDRESS $MCB_PORT $MCR_ADDRESS $MCR_PORT
echo "---------Finished Script---------"