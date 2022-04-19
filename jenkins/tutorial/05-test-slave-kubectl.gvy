pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: node
            image: node:16-alpine3.12
            command:
            - cat
            tty: true
          - name: kube
            image: alpine/k8s:1.22.6
            command:
            - cat
            tty: true
          serviceAccount: jenkins-builder
        '''
    }
  }
  stages {
    stage('Run') {
      steps {
        container('node') {
          sh 'npm version'
          sh 'touch hello.txt'
          sh 'ls -ltr'
        }
        container('kube') {
          sh 'ls -ltr'
          sh 'which kubectl'
          sh 'kubectl version'
          sh 'kubectl get namespaces'
          sh 'helm version'
        }
      }
    }
  }
}
