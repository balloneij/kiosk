
# Kiosk Processing Java

![Demo gif](demo.gif)

A demo project created by Isaac Ballone to learn how to use Processing through the Java API.

It's pretty straightforward, and easy. Examples on [Processing's website](https://processing.org/examples/) transfer 1:1. All the functions used are exposed through the PApplet class.

# Setup

* Clone the repo `git clone git@gitlab.com:msoe.edu/sdl/sd21/discoveryworld/kiosk.git`
* In Intellij: `File > New > Project from Existing Sources`
* Navigate to the `kiosk` folder and select it
* Follow Intellij's defaults for importing the project
* Visit *Troubleshooting* if you had issues


# Setup through Intellij

## Open the project
Use the iml

## Add dependencies

## Run

Run `Kiosk.main()`.

or

Run the Jar using `java -jar build/kiosk-java-processing.jar`

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
