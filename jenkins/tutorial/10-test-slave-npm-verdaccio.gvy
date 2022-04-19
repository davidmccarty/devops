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
        '''
    }
  }
  stages {
    stage('Run') {
      steps {
        container('node') {
          sh 'npm set registry http://npm-verdaccio.verdaccio.svc.mk-devops.local:4873'
          sh 'npm install pluralize'
        }
      }
    }
  }
}
