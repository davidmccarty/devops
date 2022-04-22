# ARGOCD

## Tutorial
https://redhat-scholars.github.io/argocd-tutorial/argocd-tutorial/index.html

### Install
Described in minikube readme

### CLI Access
After installing the CMI you can get access to the server using the following 3 steps
```sh
# Get pwd
$ argoPass=$(kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
$ echo $argoPass
# URL was set by ingress to 'argocd.mk-devops.local'
# Now login
$ argocd login --insecure --grpc-web argocd.mk-devops.local  --username admin --password $argoPass
'admin:login' logged in successfully
Context 'argocd.mk-devops.local' updated
```

### Console Access
http://argocd.mk-devops.local

### Deploy Sample App

#### Setup sample app repo
1. Fork the repo at https://github.com/redhat-developer-demos/openshift-gitops-examples.git
2. Mirror it into gitea and then break the mirror, rename to minikube-gitops-examples.git and setup a push mirror back to github.
3. Clone the repo and work in the minikube branch
4. Update the services to use ClusterIP and not NodePort
5. Update all the ingress files to use '<app>.mk-devops.local' as the host
6. Add ingress entries to hosts file

#### Test bgd app manual depoyment
```sh
$ cd apps/bgd/base
$ $ kubectl -n bgd apply -f bgd-ns.yaml
$ kubectl -n bgd apply -f bgd-deployment.yaml
$ kubectl -n bgd apply -f bgd-svc.yaml
$ kubectl -n bgd apply -f bgd-ingress.yaml
```
Open browser to http://bgd.mk-devops.local/
Cleanup
```sh
$ kubectl delete namespace bgd
```

#### Deploy bgd app using argocd
Create argocd application resource file into examples repo
```yaml
# bgd-app.yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: bgd-app
  namespace: argocd
spec:
  destination:
    # Create into ns=bgd on the kubernetes server where argocd is running
    namespace: bgd
    server: https://kubernetes.default.svc
  # Here you’re installing the application in ArgoCD’s default project (.spec.project).
  project: default
  # The manifest repo where the YAML resides and the path to look for.
  source:
    # Note that all the yamls will be applied with no kustomization
    path: apps/bgd/overlays/bgd
    repoURL: http://gitea-http.gitea.svc.mk-devops.local:3000/dmccarty/minikube-gitops-examples.git
    # The branch
    targetRevision: minikube
  # The selfHeal is set to false. Note that you can have Argo CD automatically sync the repo.
  syncPolicy:
    automated:
      prune: true
      selfHeal: false
    syncOptions:
    - CreateNamespace=true
```
Create the CRD instance
```sh
$ kubectl apply -f bgd-app.yaml
application.argoproj.io/bgd-app created
```
Check the application is now visible in argocd console:
- it should be healthy and synched
- confirm resources are created via `kubectl get all -n bgd`
Patch the deployment from blue to green
```sh
$ kubectl -n bgd patch deploy/bgd --type='json' -p='[{"op": "replace", "path": "/spec/template/spec/containers/0/env/0/value", "value":"green"}]'
```
After a new pod starts you can see the color change in the browser http://bgd.mk-devops.local/
Go back to the argocd console and you can see teh app is now out-of-sync. You can now resync it by either of:
- on the console click SYNC and then SYNCHRONIZE
- from the cli
    ```sh
    $ argocd sync app bgd-app
    ```

### Deploy with Kustomize
The bgd app has a second overlay folder for bgdk that includes a kustomize.yaml that patches the namespace and deployment color. so we set this as the source
```yaml
  source:
    # Note that all the yamls will be applied with no kustomization
    path: apps/bgd/overlays/bgdk
    repoURL: http://gitea-http.gitea.svc.mk-devops.local:3000/dmccarty/minikube-gitops-examples.git
```
Deploy the application CRD.
```sh
$ kubectl apply -f bgd-app.yaml
application.argoproj.io/bgdk-app created
```
Check you have a yellow square at http://bgdk.mk-devops.local/

### Synch Waves and Hooks
The example apps todo project has already been annotated with wave numbers e.g.
```yaml
annotations:
    argocd.argoproj.io/sync-wave: "2"
```
and with hooks e.g. job will be run after SYNC and will be deleted if successful
```yaml
annotations:
    argocd.argoproj.io/hook: PostSync
    argocd.argoproj.io/hook-delete-policy: HookSucceeded
```
Create the argo application definition file todo-app.yaml
```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: todo-app
  namespace: argocd
spec:
  destination:
    namespace: todo
    server: https://kubernetes.default.svc
  project: default
  source:
    path: apps/todo
    repoURL: http://gitea-http.gitea.svc.mk-devops.local:3000/dmccarty/minikube-gitops-examples.git
    targetRevision: minikube
  syncPolicy:
    automated:
      prune: true
      selfHeal: false
    syncOptions:
    - CreateNamespace=true
```
and then create the CRD to start the deployment
```sh
$ kubectl apply -f todo-app.yaml
```
Go to the argocd console and watch the deployment progress.
Check the pods in the target namespace
```sh
$ kubectl get pods -n todo
NAME                           READY   STATUS      RESTARTS   AGE
postgresql-756f87f984-76pm9    1/1     Running     0          3m16s
todo-gitops-679d88f6f4-gj4md   1/1     Running     0          3m8s
todo-table-jjztz               0/1     Completed   0          3m12s
$ kubectl -n todo get jobs
```
Open the app at http://todo.mk-devops.local/todo.html
