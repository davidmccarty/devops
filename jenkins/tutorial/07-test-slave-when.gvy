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
  parameters {
    booleanParam(
      defaultValue: true,
      description: '',
      name: 'WHEN'
    )
  }
  stages {
    stage('When') {
      when {
        expression {
          return params.WHEN
        }
      }
      steps {
        container('alpine'){
          echo 'WHEN is true'
        }
      }
    }
    stage('When not') {
      when {
        not {
          expression {
            return params.WHEN
          }
        }
      }
      steps {
        container('alpine'){
          echo 'WHEN is false'
        }
      }
    }
    stage('Always') {
      steps {
        echo 'ALWAYS'
      }
    }
  }
}
