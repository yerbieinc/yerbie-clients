#!/bin/bash

# Invokes autorest to generate the java rest client from the open api specification
autorest --input-file=../yerbie.yaml --java --use=@autorest/java@4.0.7 --output-folder=. --namespace=yerbie.autogenerated --generate-client-interfaces
