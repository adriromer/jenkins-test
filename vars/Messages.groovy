def call(String,String TYPE){

    switch(TYPE) { 
		case "title": 
            println "\n\n\033[32m[-] ************************************************************************\n[-] ---> $String \n[-] ************************************************************************\033[0m\n"
        break;
        case "message":
            println "\n\033[36m[INFO] ---> [ $String ]\033[0m"
        break;
        case "info":
            println "\n\033[36m[INFO] ---> [ $String ]\033[0m"
        break;
        case "warn":
            println "\n\033[33m[WARN] ---> [ $String ]\033[0m"
        break;
        case "alm":
            println "\n[Alm-Report-$String ]"
        break;
        case "error":
            println "\n\033[31m[ERROR] ---> [ $String ]\033[0m"
        break;
    }

}