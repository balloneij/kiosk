
# Kiosk

![Demonstration gif](demo.gif)

Discovery World's Career Pathways Kiosk developed by an MSOE Senior Design team.

# Setup

## Project

* Clone the repo `git clone git@gitlab.com:msoe.edu/sdl/sd21/discoveryworld/kiosk.git`
* In Intellij: `File > New > Project from Existing Sources`
* Navigate to the `kiosk` folder and select it
* Follow Intellij's defaults for importing the project
* Visit *Troubleshooting* if you had issues

## Checkstyle linter

* Install the Checkstyle plugin `File > Settings > Plugins > Marketplace`
* Search for and install the Checkstyle plugin
* Restart IntelliJ
* Add the team's checkstyle rules `File > Settings > Tools > Checkstyle > Configuration File > '+'`
* Add a description and the local file `checkstyle.xml`
* Press OK
* Select the new checkstyle's checkbox
* Apply changes

# Build

* From the project directory run `mvn package`
* These files will appear in the `target` directory
  * kiosk.java
  * editor.java
* Note: The kiosk and editor require specific assets, so before running the project run the following command 
  \`cp -r assets target`. This will copy all the required assets into the target directory.


## IntelliJ Project Setup

- Go to:
  - `File > Project Structure`
- Under Project:
  - `Project SDK: > Edit > "+" > Download JDK... > Amazon Corretto 1.8 > Download`
- Go back to Project:
  - `Project SDK: > Select corretto-1.8 from the drop down`
  - `Project language level > "8 - Lambdas, type annotations etc."`
- Go to Modules
  - `Module SDK: > Select Project SDK corretto-1.8`
- Go to Libraries
  - Remove JavaFX libraries (if you have them). They are included in the SDK
- Reload Maven by right clicking on a source file
  - `Right click on a source file > Run Maven > Reimport`


## Running

Run the application by double-clicking either the `Editor.bat` or `Kiosk.bat` files.


# Troubleshooting

## Ensure the `src` directory is marked as the source root 

In the Project Explorer, right click the `src` directory.

`Mark Directory As > Sources Root`

## java.lang.NullPointerException
This can sometimes happen if you are missing the `assets` folder. Make sure that folder you found
the `Kiosk.bat` or `Editor.bat` has the `assets` folder and all of it's content.

If you get this error, don't panic. Simply copy the `assets` folder and all it's content into 
the proper location.

## Still not working?

Ask the team and then post what you did to fix it here.
