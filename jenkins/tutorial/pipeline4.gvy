pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          volumes:
          - name: docker-socket
            emptyDir: {}
          containers:
          - name: node
            image: node:16-alpine3.12
            command:
            - sleep
            - 99d
          - name: docker
            image: docker:20.10.13
            command:
            - sleep
            - 99d
            volumeMounts:
            - name: docker-socket
              mountPath: /var/run
          - name: docker-daemon
            image: docker:20.10.13-dind
            securityContext:
              privileged: true
            volumeMounts:
            - name: docker-socket
              mountPath: /var/run
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
        container('docker') {
          sh 'docker version'
          sh 'ls -ltr'
        }
      }
    }
  }
}
