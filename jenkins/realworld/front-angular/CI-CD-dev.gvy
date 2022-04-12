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
                      - cat
                      tty: true
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
    stages {
        stage('Checkout') {
            steps {
                sh 'ls -ltr'
                container('node') {
                    checkout([$class: 'GitSCM',
                        branches: [[name: '*/dev']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [[$class: 'CleanCheckout']],
                        submoduleCfg: [],
                        userRemoteConfigs: [[
                            url: 'https://github.com/davidmccarty/realworld-front-angular.git'
                        ]]
                    ])
                }
                sh 'ls -ltr'
            }
        }
        stage('Build Distribution') {
            steps {
                container('node') {
                    sh 'npm install --global @angular/cli'
                    sh 'yarn install'
                    sh 'ng build'
                    sh 'ls -ltr'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                container('docker') {
                    sh 'docker version'
                    sh 'docker build -t davidmccarty/realworld-front-angular-jenkins:1.0.0 .'
                    sh 'docker push davidmccarty/realworld-front-angular-jenkins:1.0.0'
                }
            }
        }
    }
}
