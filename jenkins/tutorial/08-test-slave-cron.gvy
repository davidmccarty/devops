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
  triggers {
    cron '0 * * * *'
  }
  stages {
    stage('Hello') {
      steps {
        parallel (
          'HelloTask' : {
            echo 'Hello'
          },
          'WorldTask' : {
            echo 'World'
          }
        )
      }
    }
    stage('Test') {
      steps {
        retry(2) { // It Retries x number of times mentioned until its successful
          echo 'retry'
        }
        timeout(time: 2, unit: 'SECONDS') { // Time out option will make the step wait for x mins to execute if it takes more than that it will fail
          echo 'timeout'
        }
      }
    }
  }
}
