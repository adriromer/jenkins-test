// ####################################################################################################################
// # Pipeline Jenkins                                                                                                 #
// #                                                                                                                  #
// # Función: Parsea la URL de git y devuelve PROYECTO-APP_NAME-URLDEVOPS                                             #
// #                                                                                                                  #
// # Inputs:                                                                                                          #
// #        1. Url Git                                                                                                #
// #                                                                                                                  #
// #                                                                                                                  #
// # Notas:                                                                                                           #
// #        El repositorio debe tener la configuracion de cada microservicio en el repositorio DevOps.                #
// #                                                                                                                  #
// ####################################################################################################################

def call(String git){
      
      def objeto = new String[3]
      println "Parser URL"
      def url ="${git}".replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");  
      def URI=url.split("/")[0].toLowerCase(); 
      objeto[0]= url.split("/")[1].toLowerCase();    
      objeto[1]= url.substring(url.lastIndexOf('/') + 1, url.length()).replaceFirst(".git","").toLowerCase();;   
      objeto[2]="https://"+"${URI}"+"/"+"${objeto[0]}"+"/"+"devops" 

      galMessages("${objeto}","message")

      return objeto          
}