
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

## Setting the build artifacts

* `File > Project Structure > Artifacts > '+' > Jar > From modules with dependencies...`
* Select the Main class
* Press OK and apply changes

## Building

* `Build > Build Artifacts...`
* Select the artifact and build

## Running

Run the Jar using `java -jar out/artifacts/kiosk_jar/kiosk.jar`.

# Troubleshooting

## Ensure the `src` directory is marked as the source root 

In the Project Explorer, right click the `src` directory.

`Mark Directory As > Sources Root`

## Missing dependencies

* `File > Project Structure > Libraries > '+' > New Project Library 'Java'`

* Navigate to the project's directory and shift add all the jar files in `lib`.

* Press OK and add apply changes before exiting.

## Still not working?

Ask the team and then post what you did to fix it here.
