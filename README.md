# Pokemon
Java implementation of a basic Pokémon engine designed with OOP principles, modular architecture and possibility of future expansion (UI, new Pokémon, advanced combat system and persistence).

## Overview
Pokemon java engine is an object oriented implementation of a simplified Pokémon-inspired battle and trainer management system developed in Java.
The project focuses on applying solid Object-Oriented Programming principles and modular architecture to create a scalable and extensible game engine.

The system supports Pokémon management, trainer interactions, turn-based battles, and healing mechanics through a Pokémon Center.
It is designed with future expansions in mind, including graphical interfaces, persistence systems, advanced battle mechanics, and AI-controlled trainers.

This project was originally developed as a learning exercise in Object-Oriented Programming and has been redesigned to follow more professional software architecture practices.

## Features
-Object-Oriented design with modular architecture

-Turn-based Pokémon battle system

-Trainer and team management

-Pokémon Center healing system

-Attack and damage calculation system

-Interface-based behaviors

-Extensible Pokémon species system

-Prepared for GUI integration (JavaFX/Swing)

-Scalable design for new mechanics

## Architecture
The project follows a modular architecture that separates core systems into independent components:

Model Layer – Core domain objects such as Pokémon, Trainers, Moves, and Items.

Battle Engine – Manages combat logic, turn management, and damage calculation.

Services Layer – Contains application logic that coordinates game systems.

Interfaces Layer – Defines reusable behaviors such as attacking, healing, and evolving.

UI Layer – Handles console interaction (future support for GUI).

Repository Layer – Handles data persistence and storage.

Core Engine – Controls the overall flow of the game.

This separation improves maintainability, testability, and future scalability.

## Project Structure
src/main/java/com/pokemon

app
Main entry point of the application

core
Game engine and game manager

model
Domain entities (Pokemon, Trainer, Move, Item, Ability)

battle
Battle engine and combat logic

services
Game logic services

interfaces
Reusable behaviors

enums
Game constants (types, statuses)

world
Game locations and environment

repository
Persistence layer

ui
User interaction system

utils
Utility classes

## Core Concepts
Pokémon

Each Pokémon is represented as an object containing attributes such as level, health, type, moves, and abilities.

Trainers

Trainers manage a team of Pokémon and participate in battles.

Moves

Moves define attacks used during battle, including power, accuracy, and type.

Battle System

Battles are turn-based and controlled by a battle engine that manages turn order, damage calculations, and win conditions.

Pokémon Center

A service location where Pokémon can be healed.

## Battle System
The battle system consists of several components:

Battle – Controls the lifecycle of a battle

TurnManager – Determines attack order

DamageCalculator – Computes damage based on type effectiveness and stats

BattleSystem – Coordinates the battle process

The architecture allows new mechanics such as status effects, weather systems, and abilities to be added easily.

## Future Improvements
The system is designed to support the following future features:

Pokémon evolution system

Experience and leveling mechanics

Status effects (poison, burn, paralysis)

Weather-based battle effects

Item usage during battle

Wild Pokémon encounters

Pokémon capture mechanics

Trainer AI

Pokédex system

Game save/load functionality

Graphical interface using JavaFX

## Technologies Used
Java

Object-Oriented Programming (OOP)

Java Collections Framework

Modular Architecture Design

## How to Run
1. Clone the repository
2. git clone https://github.com/Marianagmn/Pokemon.git
3. Navigate to the project directory
4. Compile and run the project
java Main