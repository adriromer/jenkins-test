import galicia.Utilidades;      

def call(String repo,String branch,String cred){


        checkout([ $class: 'GitSCM', branches: [[name: "${branch}"]], doGenerateSubmoduleConfigurations: false, extensions: [[ $class: 'CleanBeforeCheckout', $class: 'SubmoduleOption', disableSubmodules: false,  recursiveSubmodules: true, reference: '', trackingSubmodules: false ]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "${cred}" , url: "${repo}"]] ])
    

}  