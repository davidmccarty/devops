# JFrog Container Registry

## Install JFrog Container Registry on minikube mk-devops
1. Install helm chart but disable nginx and ingress
    ```sh
    # Add helm repo
    $ helm repo add jfrog https://charts.jfrog.io
    $ helm repo update
    # create namespace
    $ kubectl create namespace artifactory-registry
    # Install
    $ helm upgrade --install jfrog-container-registry --set artifactory.postgresql.postgresqlPassword=passw0rd --set artifactory.nginx.enabled=false --set artifactory.ingress.enabled=false --namespace artifactory-registry jfrog/artifactory-jcr

    Release "jfrog-container-registry" does not exist. Installing it now.
    NAME: jfrog-container-registry
    LAST DEPLOYED: Wed Apr 13 17:44:13 2022
    NAMESPACE: artifactory-registry
    STATUS: deployed
    REVISION: 1
    TEST SUITE: None
    NOTES:
    Congratulations. You have just deployed JFrog Container Registry!
    ```
2. Create ingress for minikube mk-devops
    ```yaml
    kind: Ingress
    apiVersion: networking.k8s.io/v1
    metadata:
    name: artifactory-registry
    namespace: artifactory-registry
    labels:
        app: artifactory
    spec:
    ingressClassName: nginx
    rules:
        - host: jfrog.mk-devops.local
        http:
            paths:
            - path: /
                pathType: Prefix
                backend:
                service:
                    name: jfrog-container-registry-artifactory
                    port:
                    number: 8082
    ```
3. Update hosts file with ingress endpoint -  jfrog.mk-devops.local
4. Open browser at http://jfrog.mk-devops.local and login with admin/password
5. Accept license and change pwd = Passw0rd
6. Set base URL = http://jfrog.mk-devops.local
7. Skip setting proxy server
8. Click finish

## Setup JFrog repos
Local repos can be accessed using URL http://jfrog.mk-devops.local/artifactory/<repo-name>/<artifact-path>
1. Create a local docker registry canned 'jfrog-docker'
2. Test by starting a container with docker client
    ```sh
    $ kubectl run docker-client -n default --image=docker:20.10.13 -i --tty --rm
    ```
3. Try and login from the container to teh jfrog registry via the service
    ```sh
    $ docker login http://jfrog-container-registry-artifactory.artifactory-registry.svc.mk-devops.local:8082/artifactory/jfrog-docker
    ```
