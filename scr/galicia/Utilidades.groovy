package galicia;

import com.cloudbees.groovy.cps.NonCPS

import hudson.tasks.junit.TestResultAction

import jenkins.model.Jenkins

import org.apache.commons.io.IOUtils
import org.jenkinsci.plugins.workflow.steps.MissingContextVariableException

def enviaremail (String AUTORIZANTEMAIL,String PROYECTO, String APP_NAME, String BUILD_NUMBER, String BUILD_URL)
{
    mail (to: "${AUTORIZANTEMAIL}@bancogalicia.com.ar",
        subject: "${PROYECTO} - ${APP_NAME} tiene el build numero ${BUILD_NUMBER} listo para autorizar",
        body: "Link de autorizacion ${BUILD_URL}input/",
        from: "typ-devops@bancogalicia.com.ar");
}

def f_problema (int codigo, String mensajeerror, String mensajeok)
{
    if($codigo > 0 ) {
        f_muestra "$mensajeerror"
        exit 1
    }
    else{
        f_muestra "$mensajeok"
    }
}

def f_muestra(String textMessage){
	println "+++++++++++"
    println ${textMessage}
    println "+++++++++++"
}

def detectLANG(String IS)
{ //"redhat-openjdk18-openshift:1.2\npython:3.6\nnodejs:8\ndotnet:2.2\ndotnet:2.0\nnginx:1.12"
    switch (IS){
    case 'redhat-openjdk18-openshift:1.2': return "maven"; break;
    case 'python:3.6': return "maven"; break;
    case 'nodejs:8': return "maven"; break;
    case 'dotnet:2.2': return "maven"; break;
    case 'dotnet:2.0': return "maven"; break;
    case 'nginx:1.12': return "maven"; break;
    default: println "ERROR"; break;                    
    }
}

@NonCPS
static def isPluginActive(pluginId) {
    return Jenkins.instance.pluginManager.plugins.find { p -> p.isActive() && p.getShortName() == pluginId }
}

static boolean hasTestFailures(build){
    //build: https://javadoc.jenkins.io/plugin/workflow-support/org/jenkinsci/plugins/workflow/support/steps/build/RunWrapper.html
    //getRawBuild: https://javadoc.jenkins.io/plugin/workflow-job/org/jenkinsci/plugins/workflow/job/WorkflowRun.html
    //getAction: http://www.hudson-ci.org/javadoc/hudson/tasks/junit/TestResultAction.html
    def action = build?.getRawBuild()?.getAction(TestResultAction.class)
    return action && action.getFailCount() != 0
}

boolean addWarningsNGParser(String id, String name, String regex, String script, String example = ''){
    def classLoader = this.getClass().getClassLoader()
    // usage of class loader to avoid plugin dependency for other use cases of JenkinsUtils class
    def parserConfig = classLoader.loadClass('io.jenkins.plugins.analysis.warnings.groovy.ParserConfiguration', true)?.getInstance()

    if(parserConfig.contains(id)){
        return false
    }else{
        parserConfig.setParsers(
            parserConfig.getParsers().plus(
                classLoader.loadClass('io.jenkins.plugins.analysis.warnings.groovy.GroovyParser', true)?.newInstance(id, name, regex, script, example)
            )
        )
        return true
    }
}

@NonCPS
static String getFullBuildLog(currentBuild) {
    Reader reader = currentBuild.getRawBuild().getLogReader()
    String logContent = IOUtils.toString(reader);
    reader.close();
    reader = null
    return logContent
}

def nodeAvailable() {
    try {
        sh "echo 'Node is available!'"
    } catch (MissingContextVariableException e) {
        echo "No node context available."
        return false
    }
    return true
}

@NonCPS
def getCurrentBuildInstance() {
    return currentBuild
}

@NonCPS
def getRawBuild() {
    return getCurrentBuildInstance().rawBuild
}

def isJobStartedByTimer() {
    return isJobStartedByCause(hudson.triggers.TimerTrigger.TimerTriggerCause.class)
}

def isJobStartedByUser() {
    return isJobStartedByCause(hudson.model.Cause.UserIdCause.class)
}

@NonCPS
def isJobStartedByCause(Class cause) {
    def startedByGivenCause = false
    def detectedCause = getRawBuild().getCause(cause)
    if (null != detectedCause) {
        startedByGivenCause = true
        echo "Found build cause ${detectedCause}"
    }
    return startedByGivenCause
}

@NonCPS
String getIssueCommentTriggerAction() {
    try {
        def triggerCause = getRawBuild().getCause(org.jenkinsci.plugins.pipeline.github.trigger.IssueCommentCause)
        if (triggerCause) {
            //triggerPattern e.g. like '.* /piper ([a-z]*) .*'
            def matcher = triggerCause.comment =~ triggerCause.triggerPattern
            if (matcher) {
                return matcher[0][1]
            }
        }
        return null
    } catch (err) {
        return null
    }
}

def getJobStartedByUserId() {
    return getRawBuild().getCause(hudson.model.Cause.UserIdCause.class)?.getUserId()
}