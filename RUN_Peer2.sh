#!/bin/bash
clear

function pause(){
   read -p "$*"
}

echo "***************Starting Script********************"
echo
echo "> Generating Vars..."
PEER_ID=2
PROTOCOL_VERSION=1.0
MC_ADDRESS="230.0.0.2"
MC_PORT="3334"
MCB_ADDRESS="230.0.0.3"
MCB_PORT="4445"
MCR_ADDRESS="230.0.0.4"
MCR_PORT="5556"
echo PEER_ID = $PEER_ID
echo PROTOCOL_VERSION = $PROTOCOL_VERSION
echo MC_ADDRESS = $MC_ADDRESS 
echo MC_PORT = $MC_PORT 
echo MCB_ADDRESS = $MCB_ADDRESS
echo MCB_PORT = $MCB_PORT 
echo MCR_ADDRESS = $MCR_ADDRESS 
echo MCR_PORT = $MCR_PORT
echo 
BIN="bin/"
BASEDIR=$(dirname "$0")
echo "> Making Directory bin..."
mkdir -p $BIN
echo 
echo "*********************Compiling********************"
echo 
javac -d $BIN src/channels/*.java src/files/*.java src/interfaces/*.java src/message/*.java src/protocols/*.java src/utils/*.java
echo "> Start Running Peer 2..."
cd Peer2_Storage
java -cp ../$BIN interfaces.Main $PEER_ID $PROTOCOL_VERSION $MC_ADDRESS $MC_PORT $MCB_ADDRESS $MCB_PORT $MCR_ADDRESS $MCR_PORT