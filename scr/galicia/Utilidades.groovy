package galicia;

def enviaremail (String AUTORIZANTEMAIL,String PROYECTO, String APP_NAME, String BUILD_NUMBER, String BUILD_URL)
{
    mail (to: "${AUTORIZANTEMAIL}@bancogalicia.com.ar",
        subject: "${PROYECTO} - ${APP_NAME} tiene el build numero ${BUILD_NUMBER} listo para autorizar",
        body: "Link de autorizacion ${BUILD_URL}input/",
        from: "typ-devops@bancogalicia.com.ar");
}