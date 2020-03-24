
// ####################################################################################################################
// # Pipeline Jenkins                                                                                                 #
// #                                                                                                                  #
// # Función: Pipeline ambiente int                                                                                   #
// #                                                                                                                  #
// # Inputs:                                                                                                          #
// #        1. Url Git                                                                                                #
// #        2. language                                                                                               #
// #                                                                                                                  #
// # Notas:                                                                                                           #
// #        El repositorio debe tener la configuracion de cada microservicio en el repositorio DevOps.                #
// #                                                                                                                  #
// ####################################################################################################################

def call(String CLT_URL,String CLT_CRED,String PROYECTO, String APP_NAME, String ENV, String IS, String GITURL,String GITBRANCH,String LANGUAGE,String TOOL, String HOOKTEAMS, String VERACODE_ID){
        
        def BC= '{"apiVersion":"build.openshift.io/v1","kind":"BuildConfig","metadata":{"labels":{"app":"appn"},"name":"appn2ENV"},"spec":{"failedBuildsHistoryLimit":5,"nodeSelector":{},"output":{},"postCommit":{},"resources":{},"runPolicy":"Serial","source":{"git":{"uri":"https://github.bancogalicia.com.ar/devops-master/lib"},"type":"Git"},"strategy":{"jenkinsPipelineStrategy":{"env":[{"name":"GITURL","value":"sq"},{"name":"GITBRANCH","value":"branch"},{"name":"LANGUAGE","value":"is_tag"},{"name":"TOOL","value":"tol"},{"name":"HOOKTEAMS","value":"hook"},{"name":"VERACODE_ID","value":"veracode"}],"jenkinsfilePath":"pipelines/deployment/2ENV/Jenkinsfile"},"type":"JenkinsPipeline"},"successfulBuildsHistoryLimit":5,"triggers": [{"generic": {"allowEnv": true,"secret": "secret1"},"type": "Generic"}]},"status":{"lastVersion":1}}'
        
        openshift.withCluster(CLT_URL,CLT_CRED) { 
            openshift.withProject(PROYECTO) {
             
                if (!openshift.selector("is/${APP_NAME}").exists()) {
                    openshift.newBuild("--binary=true", "--name=${APP_NAME}", "${IS}");
                } 
                Messages("Creating Application for: ${ENV}","title")				           
	    	    if (!openshift.selector("dc/${APP_NAME}").exists()) {   
                    openshift.newApp("${APP_NAME}:latest", "--allow-missing-imagestream-tags")  			
                    Messages("Create DeploymentConfig - OK","info")   
                    if (!openshift.selector("svc/${APP_NAME}").exists()) {
                        Messages("Creating Service (exposed on port 8080)","info")       
                        def dcSelector = openshift.selector("dc/${APP_NAME}")
                        dcSelector.expose('--port 8080')  
                        Messages("Create Service - OK","info")  
                    }
                    if (!openshift.selector("route/${APP_NAME}").exists()) {
                        Messages("Creating Route...","info")
                        def resultroute = openshift.raw( 'expose', "svc/${APP_NAME}");
                        Messages("Cluster status: ${resultroute.out}","info")
                        def defaultroute = openshift.raw("patch", "route", "${APP_NAME}", "-p", "'{\"spec\":{\"tls\":{\"insecureEdgeTerminationPolicy\": \"Allow\",\"termination\":\"edge\"}}}'");
                        Messages("Cluster status default route: ${defaultroute.out}","info")
                        Messages("Create Route HTTPS - OK","info")
                    }
                    Messages("Deleting Triggers","info")
                   openshift.set("triggers","dc","${APP_NAME}","--remove-all=true")
                   openshift.patch("dc/${APP_NAME}", '\'{"spec": { "triggers": [ { "type": "ImageChange", "imageChangeParams": { "containerNames": [ "${APP_NAME}" ], "from": { "kind":"ImageStreamTag", "namespace": "${PROYECTO}", "name": "${APP_NAME}:latest"},"lastTriggeredImage": "docker-registry.default.svc:5000/${PROYECTO}/${APP_NAME}:latest"}}]}}\'')
                   

                } else {
                    Messages("${ENV} Application already exists for","warn")
                }

                Messages("Creating pipeline for ${ENV}","title")	
                if (!openshift.selector("bc/${APP_NAME}2${ENV}").exists()) {
                    writeFile file: 'bc', text: "${BC}"
                    sh "cat bc | sed -e 's,appn,${APP_NAME},g' -e 's,sq,${GITURL},g' -e 's/is_tag/${LANGUAGE}/g' -e 's,ENV,${ENV},g' -e 's,tol,${TOOL},g' -e 's,branch,${GITBRANCH},g' -e 's,hook,${HOOKTEAMS},g' -e 's,veracode,${VERACODE_ID},g' > bc.json"     
                    openshift.raw("create", "-f", "bc.json");
                    sh "rm bc.json"
                } else {
                    Messages("${ENV} Pipeline already exists","warn")	
                }
            }
        }
}