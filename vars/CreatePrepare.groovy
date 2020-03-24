def call(String TECH){
switch(TECH) { 
  case "Java":
    	env.IS="redhat-openjdk18-openshift:1.6"
		env.TOOL="maven_3_5_0"
		env.LANGUAGE="maven"
	break;
	case "Python":
		env.IS="python:3.6"
		env.TOOL="any"
		env.LANGUAGE="python"
	break;
	case "React":
		env.IS="nginx:1.12"
		env.TOOL="node8"
		env.LANGUAGE="nodejs"
	break;
	case "Nodejs":
		env.LANGUAGE="nodejs"
        env.VERNODE = input message: 'Select Node Version:', parameters: [choice(name: 'VERNODE', choices: 'node8\nnode10', description: 'Select Nodejs version')]
		galMessages("User Input: ${VERNODE}","info")
        switch(VERNODE) {
		    case "node8":
			    env.IS="nodejs:8"
			    env.TOOL="node8"
			break;
			case "node10":
			    env.IS="nodejs:10"
			    env.TOOL="node10"
		  break;
          default: 
               galMessages("ERROR","error")
          break; 
		}
	break;
	case "Netcore":
	    env.LANGUAGE="netcore"
		env.VERNET = input message: 'Select Netcore Version', parameters: [choice(name: 'VERNET', choices: 'dotnet2.2\ndotnet3.1', description: 'Select Netcore Version')]
		galMessages("User Input: ${VERNET}","info")
        switch(VERNET) {
			case "dotnet2.2":
				env.IS="dotnet:2.2"
				env.TOOL="dotnet2.2"
			break;
			case "dotnet3.1":
				env.IS= "dotnet:3.1"
				env.TOOL="dotnet3.1"
			break;
            default: 
                galMessages("ERROR","error")
            break;
		}
        break;
		default: 
            galMessages("ERROR","error")
        break; 
	}
}