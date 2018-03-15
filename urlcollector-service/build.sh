#!/bin/bash

while getopts n:k:r:c: option
do
	case "${option}"
		in
		n) NAMESPACE=${OPTARG};;
		k) API_KEY_FILE=${OPTARG};;
		r) REGION=${OPTARG};;
		c) CLUSTER_NAME=${OPTARG};;
		
	esac
done

if [[ ! -r $API_KEY_FILE  || -z $NAMESPACE ||  -z $REGION || -z $CLUSTER_NAME ]]
		then
			echo "Incorrect usage, See README"
			exit 1
fi


bx plugin install container-service -r Bluemix -f &&
bx login --apikey @$API_KEY_FILE &&
bx cs region-set $REGION &&
$(bx cs cluster-config --export $CLUSTER_NAME) &&

echo "Build docker image"
docker build -t registry.ng.bluemix.net/$NAMESPACE/url-collector-service . &&

echo "Push docker image"
docker push registry.ng.bluemix.net/$NAMESPACE/url-collector-service &&

echo "Create resource on Kubernetes Cluster"
kubectl create -f urlcollector-service.yaml

exit $?