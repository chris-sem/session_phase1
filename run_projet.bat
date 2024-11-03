@echo off
rem Nettoie et compile le projet avec Maven
rem mvn clean package

rem Exécute le programme avec le module JavaFX
java --module-path .\javafx-sdk-21.0.4\lib\ --add-modules javafx.controls,javafx.fxml -jar .\target\session_phase1-1.0-SNAPSHOT-jar-with-dependencies.jar