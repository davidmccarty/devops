# Install

Refer https://devopscube.com/setup-jenkins-on-kubernetes-cluster/ for step by step process to use these manifests.

**Note:**  These files have been modified for local testing in minikube.


# Setup

Create user with admin privilege

## Disable local builds
1. From jenkins home page click 'build executor status'
2. Edit settings for master node (the only one at this point)
3. Set
   - number executors = 0
   - labels = master
   - Usaeg = only build jobs with matching labels

## Install plugins
1. Go to 'manage' and 'plugins' and 'available'
2. Use the search and install plugins for 'kubernetes', 'docker', 'nodeJS'
3. Restart jenkins by checking the box at bottom of the plugins install page

## Add a builder slave node
1. Create a minikube namespace 'jenkins-build' and add a service account 'jenkins-builder' and rolebinding (and auto generated secret)
   ```sh
   $ kubectl create namespace jenkins-build
   $ kubectl create serviceaccount jenkins-builder -n jenkins-build
   $ kubectl create rolebinding jenkins-admin-binding --clusterrole=admin --serviceaccount=jenkins-build:jenkins-builder -n jenkins-build
   # display the token
   $ kubectl describe secret jenkins-builder -n jenkins-build
   ```
2. Got to 'Manage Nodes and Clouds' --> 'Configure Clouds'
3. Add a new cloud, type is kubernetes and name is 'minikube-devops-slave' then open 'kubernetes cloud details'
4. Because we are inside the cluster we can set the url to https://kubernetes.default.svc
5. set the namespace to 'jenkins-build'
6. Set the jenkins URL for the slave to connect back to the master
   http://jenkins-service.jenkins.svc.mk-devops.local:8080
7. setup a jenkins credential
    - type = secret text
    - copy token into the secret value
    - set ID = jenkins-builder-mk-devops
    - add description
8. Click the 'test connection' button
9. Test should be ok because all the TLS is taken care of by minikube because we are inside the cluster.
   When we setup a remote agent we will have to setup TLS certificate (or choose option to ignore it).
   Also the channel back to the master from teh slave is open, but for a remote slave this will also heve to be setup.

## Create test pipeline
1. New item called 'test-slave-build' of type 'pipeline'
2. Use the pipeline1 at `tutorial/pipeline1.gvy` and paste it into the script section of the pipeline definition
from https://gist.github.com/darinpope/67c297b3ccc04c17991b22e1422df45a
3. Run pipeline and view logs
4. Repeat with pipeline2 and pipeline3
