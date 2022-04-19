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
            - name: kube
              image: alpine/k8s:1.22.6
              command:
              - cat
              tty: true
            serviceAccount: jenkins-builder
          '''
      }
    }
    environment {
      DOCKER_LOGIN = credentials('docker-davidmccarty')
    }
    parameters {
      string(
        name: 'BRANCH',
        defaultValue: "dev",
        trim: true,
        description: 'Git branch for realworld-front-angular-deploy'
      )
    }
    stages {
      stage('Checkout') {
        steps {
          sh 'ls -ltr'
          container('node') {
            checkout([$class: 'GitSCM',
                          branches: [[name: '*/dev']],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'build-repo']],
                          branches: [[name: "refs/heads/${BRANCH}"]],
                          submoduleCfg: [],
                          userRemoteConfigs: [[
                              url: 'https://github.com/davidmccarty/realworld-front-angular.git'
                          ]]
                      ])
          }
        }
      }
      stage('Angular App Build') {
        steps {
          container('node') {
            sh 'cd build-repo && npm set registry http://npm-verdaccio.verdaccio.svc.mk-devops.local:4873'
            sh 'cd build-repo && yarn config set http://npm-verdaccio.verdaccio.svc.mk-devops.local:4873'
            sh 'npm install --global @angular/cli'
            sh 'cd build-repo && yarn install'
            sh 'cd build-repo && ng build'
          }
        }
      }
      stage('Build Docker Image - docker-in-docker') {
        steps {
          container('docker') {
            sh 'docker login -u $DOCKER_LOGIN_USR -p $DOCKER_LOGIN_PSW'
            sh 'cd build-repo && docker build -f dockerfile-pipeline -t realworld-front-angular-jenkins:1.0.0 .'
            sh 'docker tag realworld-front-angular-jenkins:1.0.0 davidmccarty/realworld-front-angular-jenkins:1.0.0'
            sh 'docker push davidmccarty/realworld-front-angular-jenkins:1.0.0'
          }
        }
      }
      stage('Deploy') {
        steps {
          container('kube') {
            sh 'git clone  https://github.com/davidmccarty/realworld-front-angular-deploy.git'
            sh 'cd realworld-front-angular-deploy && ls -ltr'
            sh 'cd realworld-front-angular-deploy && git status'
            sh "cd realworld-front-angular-deploy && git checkout ${BRANCH}"
            sh "cd realworld-front-angular-deploy && kustomize build overlays/${BRANCH}"
            sh "cd realworld-front-angular-deploy && kubectl apply -k  overlays/${BRANCH}"
          }
        }
      }
    }
}
