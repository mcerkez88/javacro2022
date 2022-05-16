#!/bin/bash

#Use  namespace
oc project javacro

rm keycloak.crt
rm kafka_cluster.yaml

export KEYCLOAK_ROUTE=$(oc get route keycloak -n javacro --template='{{ .spec.host }}')

echo $KEYCLOAK_ROUTE

## get OCP root cert
echo "" | openssl s_client -servername $KEYCLOAK_ROUTE -connect $KEYCLOAK_ROUTE:443 -prexit 2>/dev/null | openssl x509 -outform PEM > keycloak.crt

##OCP secret used for kafka broker access to keycloak
oc create secret generic ca-keycloak --from-file=keycloak.crt=keycloak.crt

#prepare templete file with environment specific URLs
sed "s/KEYCLOAK_HOST/$KEYCLOAK_ROUTE/g" kafka_cluster_oauth_template.yaml > kafka_cluster.yaml

#run kafka broker
oc apply -f kafka_cluster.yaml



