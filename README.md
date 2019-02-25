# Artificial Intelligence International Draughts

Code for the International Draughts assignment for the Artificial Intelligence course (2ID90)

## Project

This project is based on the initial received files from the course. Only two files are changed:

`Player87.java` contains the main part of the algorithm.

`MyDraughtsPlugin.java` is used to select the available players.

## Usage

This project makes use of NetBeans.

Change the argument options (Properties->Run->Arguments) of the AICompetition project to the following:
```
../DraughtsPlugin/dist/
```

After first compiling the AICompetition project and then the DraughtsPlugin project, you can run the AICompetition tool.

## TODO
### Basic
* [ ] Implement alpha-beta
* [ ] Implement an initial evaluation function
* [ ] Implement iterative deepening

### Imrovements
* [ ] Enhance evaluation function
* [ ] Improve search function
* [ ] Improve stop criterion for searching
* [ ] Reuse results in a preveious iteration of iterative deepening