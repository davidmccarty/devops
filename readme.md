# DevOps Exercises

## Setup OS Tools

### Install Java (v8,11,17,18 for x64)
https://www.happycoders.eu/java/how-to-switch-multiple-java-versions-windows/
1. Unzip to `c:\Program Files\Java`
2. Delete system env variables for PATH
        C:\ProgramData\Oracle\Java\javapath
        C:\Program Files (x86)\Common Files\Oracle\Java\javapath
3. Add/set variable JAVA_HOME
        C:\Program Files\Java\jdk-18
4. Add PATH variable setting
        %JAVA_HOME%\bin
5. Add PATH variable setting
        c:\Prigram Files\utils\java

Then copy/update scripts to c:\Prigram Files\utils\java

### Install node and npm
https://nodejs.org/en/download/

### Install kubectl, helm, oc
Download binary and copy to c:\Program Fiels\utils
Update path env variable.

### Install Maven
Download and copy to c:\Program Files\utils\maven

### Install Gradle
Download and copy to c:\Program Files\utils\gradle




## Setup mk-dev minikube profile for hosting the application

### Run minikube from I drive
1. Create folder `I:\minikube`
2. Set system env variable è MINIKUBE_HOME` to use the I drive folder

### Create minikube instance mk-dev
```sh
# Setup dev cluster
$ minikube start -p mk-dev --driver hyperv --cpus 2 --memory 4g  --disk-size 20GB --dns-domain mk-dev.local --kubernetes-version 1.20.0
$ minikube profile mk-dev
$ minikube dashboard
# (if it fails verifying proxy then check dashboard pods are started and try again)
```

### Troubleshooting
1. Remember to update hosts file for each restart because the `minikube ip` will always change
2. If start hangs at 'verifying ingress' then maybe one of teh pods didn't start.
   ```sh
   kubectl get pods -n ingress-nginx
   kubectl delete pods -a -n ingress-nginx
   ```
3. For connectivity issues run a busybox pod with curl to help test internal IP and URL etc
   ```sh
   $ kubectl run curl-client -n default --image=radial/busyboxplus:curl -i --tty --rm
   ```

### Update .kube contexts
Update the `.kube/config` file with additional contextx if you want to run kubectl commands from WSL.


## Setup mk-devops minikube profile for hosting the tools
Note: kubernetes version must be <1.21 for gitlab chart to install
```sh
# Setup dev cluster
$ minikube start -p mk-devops --driver hyperv --cpus 4 --memory 12g --disk-size 50GB --dns-domain mk-devops.local --kubernetes-version 1.20.0
$ minikube profile mk-devops
$ minikube addons enable ingress
$ minikube addons enable metrics-server
$ minikube addons enable dashboard
$ minikube addons enable registry

# WARNING - this will change each time you restart minikube so rememeber to update hosts file entries
$ minikube ip
172.18.141.117
```
Create ingress for dashboard and registry
```sh
$ kubectl apply -f minikube\dashboard_ingress.yaml
$ kubectl apply -f minikube\registry_ingress.yaml  # TODO - TLS to be fixed
```
Update the hosts file
```
# c:\Windows\System32\drivers\etc\hosts
172.18.141.117 dashboard.mk-devops.local registry.mk-devops.local
```
### Enable CoreDNS
CoreDNS is a replacement for the default kubernetes internal DNS (kube-dns) used to resolve resource names. From kubernetes 1.23 CoreDNS becomes the default.
1. Install CoreDNS  (to be done https://coredns.io/2017/04/28/coredns-for-minikube/)





## Install Jenkins on minikube mk-devops
Base yaml files copied from
https://devopscube.com/setup-jenkins-on-kubernetes-cluster/

```sh
$ cd minikube\jenkins
# find and replace namespace devops-tools --> jenkins
# then create namespace
$ kubectl apply -f namespace.yaml
# craete ServiceAccount, ClusterRole and ClusterroleBinding
$ kubectl apply -f serviceAccount.yaml
# Change the node selector to mk-devops in volume.yaml then create the PV (using explicit PV name)
$ kubectl apply -f volume.yaml
# Create the deployment
$ kubectl apply -f deployment.yaml
# Create the service (don't use the nodeport service but instead craete a clusterIP service)
$ kubectl apply -f service.yaml
# Create the ingress
$ kubectl apply -n jenkins -f ingress.yaml
```
Update the hosts file
```
# c:\Windows\System32\drivers\etc\hosts
172.23.151.172 jenkins.mk-devops.local
```
Go to the jenkins-server pod and get the initial password from the log
```
*************************************************************
Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:
a7695d1cba894736b668a7fcfd4bcd11
This may also be found at: /var/jenkins_home/secrets/initialAdminPassword
*************************************************************
```
Now open browser at http://jenkins.mk-devops.local and login with the password.
Install recommended plugins.  More will be added later.

When prompted create a new admin user  --> dmccarty/<pwd>


## Install ArgoCD
https://argo-cd.readthedocs.io/en/stable/
```sh
$ kubectl create namespace argocd
$ kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```
Install argocd cli from here  https://argo-cd.readthedocs.io/en/stable/cli_installation/
```ps1
cd 'C:\Program Files\utils\'
$version = (Invoke-RestMethod https://api.github.com/repos/argoproj/argo-cd/releases/latest).tag_name
echo $version
--> v2.3.3
$url = "https://github.com/argoproj/argo-cd/releases/download/" + $version + "/argocd-windows-amd64.exe"
$output = "argocd.exe"
Invoke-WebRequest -Uri $url -OutFile $output

$ argocd version
argocd: v2.3.3+07ac038
  BuildDate: 2022-03-30T01:52:36Z
  GitCommit: 07ac038a8f97a93b401e824550f0505400a8c84e
  GitTreeState: clean
  GoVersion: go1.17.6
  Compiler: gc
  Platform: windows/amd64
time="2022-04-02T09:24:02+02:00" level=fatal msg="Argo CD server address unspecified"
```
Add ingress
- first need to patch the minikube ingress controller to support passthru
```sh
$ edit deployment ingress-nginx-controller -n ingress-nginx
# and add the following argument
# - --enable-ssl-passthrough
```
Create the ingress
```ps1
$ kubectl apply -f argocd\argocd-server_ingress.yaml -n argocd
```
Update the hosts file
```
# c:\Windows\System32\drivers\etc\hosts
172.23.151.172 argocd.mk-devops.local
```
UI is not available at http://argocd.mk-devops.local
and you can login with user `admin` and password from below
```sh
$ kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
# --> user=admin pwd=oNxERlOopRZUgzFM
$ argocd login argocd.mk-devops.local
WARNING: server certificate had error: x509: certificate signed by unknown authority. Proceed insecurely (y/n)? y
Username: admin
Password:
'admin:login' logged in successfully
Context 'argocd.mk-devops.local' updated
```


## Deploy a load test application
https://www.techtarget.com/searchitoperations/tutorial/Kubernetes-performance-testing-tutorial-Load-test-a-cluster

Create namespace and deploy application
```sh
$ kubectl create namespace php-workload
$ kubectl apply -n php-workload -f https://k8s.io/examples/application/php-apache.yaml
```
Create autoscaler for pod
```sh
$ kubectl autoscale deployment php-apache -n php-workload --cpu-percent=80 --min=1 --max=4
$ kubectl get hpa -n php-workload
NAME         REFERENCE               TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
php-apache   Deployment/php-apache   0%/80%    1         4         1          36s
$ kubectl describe hpa php-apache -n php-workload
```
Create deployment to create calls on php-apache pod
```sh
$ kubectl apply -f load-test\infinite-calls_deployment.yaml
```
You should get enough workload to cause the hpa to scale the pods.  If not then scale the `infinite-calls` pod.

## RealWord Application
https://codebase.show/projects/realworld

For each application fork the repo from the github ui to <davidmccarty>, rename it and then clone it to local workspace
```sh
$ git clone https://github.com/davidmccarty/realworld-front-angular.git
$ git clone https://github.com/davidmccarty/realworld-back-springboot.git
```



### Front-End Angular
```sh
# Upgrade npm
$ npm install -g npm
# Install anguar cli
$ npm install -g @angular/cli
$ ng --version
```
Build the project
```sh
$ cd realworld-front-angular
$ npm install
