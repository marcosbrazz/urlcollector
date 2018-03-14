#!/bin/bash

if [[ ! -r $2  || -z $1 ]]
		then
			echo "Incorrect usage, See README"
			exit 1
fi

NAMESPACE=$1
API_KEY_FILE=$2

bx login --apikey @$API_KEY_FILE

echo "Build docker image"
docker build -t registry.ng.bluemix.net/$NAMESPACE/compute-interest-api .

echo "Push docker image"
docker push registry.ng.bluemix.net/$NAMESPACE/compute-interest-api

echo "Create resource on Kubernetes Cluster"
kubectl create -f urlcollector-service.yaml

# echo $?  last command exit code

echo "DONE"

exit 0