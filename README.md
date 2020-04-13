# OPLA-Tool

![OtimizesUEM](https://raw.githubusercontent.com/SBSE-UEM/OPLA-Tool/master/logo-grupo-pesquisa.png)

## Description

This project was created from the project 
```sh
https://github.com/SBSE-UEM/OPLA-Tool-Spyke
```

## Requirements
Before to compile the code, you need to install the following softwares on your PC:
- Java Development Kit (Version >= 6)
- Git - http://git-scm.com
- Maven - http://maven.apache.org (Version >= 3.5)

## How to Build
This section show the step-by-step that you should follow to build the OPLA-Tool. 

- Create a directory to build OPLA-Tool:
```sh
mkdir opla-tool
```
- Access the folder:
```sh
cd opla-tool
```
- Download all projects:
```sh
git clone https://github.com/SBSE-UEM/OPLA-Tool.git
```
- Compile
```sh
mvn clean install Obs: If it is the first run, execute **mvn clean** first to install local dependencies
```
- Open OPLA-Tool:
```sh
java -jar modules/opla-gui/target/opla-gui-1.0.0-SNAPSHOT-jar-with-dependencies
```

## How to open the PLAs
```sh
Download and Install the Eclipse Papyrus Luna RS2: https://www.eclipse.org/papyrus/download.html
Import the PLAs: https://www.youtube.com/watch?v=9mmPUagHjM8
```


## How to import into eclipse IDE
```sh
mvn eclipse:clean
```

Import into eclipse IDE using Maven Project Type

```html
File > Import > Maven > Exists Maven Project > Select the directory created for build OPLA-Tool
```
## How to contribute to this project

Make Fork this project and create a Pull Request with your changes.
https://github.com/SBSE-UEM/OPLA-Tool/blob/master/Contributing.pdf
