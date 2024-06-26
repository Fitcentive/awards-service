#!/bin/bash

kubectl delete -n awards deployment/awards-service

# Delete old 1.0 image from gcr
echo "y" | gcloud container images delete gcr.io/fitcentive-dev-03/awards:1.0 --force-delete-tags

# Build and push image to gcr
sbt docker:publish

kubectl apply -f deployment/gke-dev-env/