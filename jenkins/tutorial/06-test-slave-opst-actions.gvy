pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: alpine
            image: alpine:3.14
            command:
            - cat
            tty: true
          serviceAccount: jenkins-builder
        '''
    }
  }
  stages {
    stage('Build') {
      steps {
        echo 'BUILD'
      }
      post {
        always {
          echo 'Build - post always.'
        }
      }
    }
    stage('Release') {
      steps {
        echo 'RELEASE'
      }
      post {
        success {
          echo 'Release - post success'
        }
      }
    }
  }
  post {
      always {
        echo 'Post Always'
      }
  }
}
