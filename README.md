
# iDempiere Webstore

## Projects:
* org.adempiere.webstore - iDempiere webstore
* org.idempiere.webstore.parent - parent pom project

## Development/Build Folder layout:
* <workspace>
       |
       --- idempiere
       |
       --- idempiere-webstore

## Deployment
* at idempiere-webstore, run mvn verify 
* use felix web console to install idempiere-webstore/org.adempiere.webstore/target/org.adempiere.webstore_<version>.<timestamp>.jar (turn on "start bundle", and set "start level" to 4).
* to uninstall, at felix web console, looks for the iDempiere Web Store bundle and uninstall it

