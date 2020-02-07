
# iDempiere Webstore

## Projects:
* org.adempiere.webstore - idempiere webstore
* org.idempiere.webstore.p2 - project to build p2 repository
* org.idempiere.webstore.parent - parent pom project
* org.adempiere.webstore.resourcce - webstore static resources
* org.adempiere.webstore-feature project - webstore feature project
* org.adempiere.webstore.model - webstore model class (fragment to org.adempiere.base)
* org.adempiere.webstore.servlet - webstore servlet class

## Folder layout:
* idempiere
* idempiere-webstore

## Deployment
* at idempiere-webstore, run mvn verify 
* copy deploy-webstore.sh to your idempiere instance's root folder
* at your idempiere instance's root folder (for instance, /opt/idempiere), run ./deploy-webstore.sh <file or url path to org.idempiere.webstore.p2/target/repository>
* for e.g, if your source is at /ws/idempiere-webstore, ./deploy-webstore.sh file:////ws/idempiere-webstore/org.idempiere.webstore.p2/target/repository
* to uninstall, copy remove-webstore.sh to your idempiere instance's root folder. Run ./remove-webstore.sh to uninstall it
* if the bundle doesn't auto start after deployment (with STARTING status), at osgi console, run "sta org.adempiere.webstore" to activate the plugin
