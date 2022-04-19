pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: kube
            image: alpine/k8s:1.22.6
            command:
            - cat
            tty: true
          serviceAccount: jenkins-builder
        '''
    }
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
    stage('Run') {
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
