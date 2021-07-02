# About This Project
This project's goal is to be a webapp that allows for easy searching of photos
tagged by category and date.

# Dependencies
This project uses Maven to manage most dependencies. There are, of course, other
dependencies. As written currently, it relies on Tomcat 10, and MySQL or MariaDB.

# Info for Tests
## Integration tests
In order for the integration tests to work, the website needs to be deployed so
it is possible to visit it (locally). Other things, like a webdriver for Chrome,
are also required. The configuration for this is in 
src/test/TestConfig.json. That file has a sample configuration for my PC, you 
will NEED to change this.
## TestConfig.json
The fields are:
* testURL: Must be the URL where the test site is. Should be local and end 
in /FamilyPhotos (no trailing slash)
* viewCredential: The value is an object with the keys *username* and *password*.
This is the login that allows a user to view all the photos
* limitedCredential: The value is an object as in above. This is the login that allows
a user to view only certain photos.
* chromeWebDriver: Location of a webdriver for chrome. If empty, the test will
not call System.setProperty("webdriver.chrome.driver", config.chromeWebDriver);
(this should be set unless you are setting that property some other way).