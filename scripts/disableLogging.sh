#!/bin/bash
cp log4j_noLog.properties log4j.properties

jar uf PdpCommunicationManager/target/PdpCommunicationManager-1.0-jar-with-dependencies.jar log4j.properties
