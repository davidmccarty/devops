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
          - name: docker
            image: docker:20.10.13
            command:
            - cat
            tty: true
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
  environment {
    DOCKER_LOGIN = credentials('docker-davidmccarty')
  }
  parameters {
    string(
      name: 'ALPINE_VERSION',
      defaultValue: "3.14",
      trim: true,
      description: 'Alpine docker image version'
      )
  }
  stages {
    stage('Run') {
      steps {
        container('docker') {
          sh 'docker version'
          sh "docker pull alpine:$params.ALPINE_VERSION"
          sh "docker tag alpine:$params.ALPINE_VERSION davidmccarty/$params.ALPINE_VERSION"
          sh "docker login -u $DOCKER_LOGIN_USR -p $DOCKER_LOGIN_PSW"
          sh "docker push davidmccarty/$params.ALPINE_VERSION"
        }
      }
    }
  }
}
