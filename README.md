# csumb-kb-server


## Endpoints 
```src/main/java/org/mbari/m3/kbserver/Main.java```
* userLogin
* changeParent
* getMetadata
* createConcept
* addConceptMedia
* addConceptName
* addUserAccount
* deleteConcept
* addLinkRealization
  * userName, jwt, concept, linkName, linkValue, toConcept
* deleteLinkRealization
* updateLinkRealization
  * userName, jwt, concept, oldLinkName, oldLinkValue, oldToConcept, newLinkName, newLinkValue, newToConcept

## Start up

1. Clone [vars-kb](http://github.com/mbari-media-managment/vars-kb)
2. Build the vars-kb as you need it's libaries:
```
cd vars-kb
mvn clean install -Dmaven.test.skip=true
```
3. Start up the derby database.
```
git clone http://github.com/mbari-media-managment/m3-microservices
cd m3-microservices
docker-compose build
docker-compose up
```
4. Build app
```
cd csumb-kb-server
mvn compile
mvn exec:java -Dexec.mainClass=org.mbari.m3.kbserver.Main
```