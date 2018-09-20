[![pipeline status](https://git.faldoria.de/tof/plugins/raidcraft/rcloot/badges/master/pipeline.svg)](https://git.faldoria.de/tof/plugins/raidcraft/rcloot/commits/master)

# RCLoot

Das Loot Plugin ermöglicht die Erstellung von komplexen Loot-Tabellen um [Items](https://git.faldoria.de/tof/plugins/raidcraft/rcitems), Geld und mehr zu droppen. Außerdem kann man damit Loot-Kisten in der Welt verteilen.

* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)

## Getting Started

* [Project Details](https://git.faldoria.de/tof/plugins/raidcraft/rcloot)
* [Source Code](https://git.faldoria.de/tof/plugins/raidcraft/rcloot/tree/master)
* [Latest Stable Download](https://ci.faldoria.de/view/RaidCraft/job/RCLoot/lastStableBuild)
* [Issue Tracker](https://git.faldoria.de/tof/plugins/raidcraft/rcloot/issues)
* [Developer Documentation](docs/DEVELOPER.md)
* [Admin Documentation](docs/ADMIN.md)

### Prerequisites

Das RCLoot Plugin ist nur von der [RaidCraft API](https://git.faldoria.de/tof/plugins/raidcraft/raidcraft-api) abhängig. Damit es Sinn ergibt sollte jedoch mindestens noch das [RCItems](https://git.faldoria.de/tof/plugins/raidcraft/rcitems) und das [RCMobs](https://git.faldoria.de/tof/plugins/raidcraft/rcmobs) Plugin installiert sein.

### Installation

Beim ersten Start des Servers wird eine `database.yml` und eine `config.yml` angelegt. Am besten den Server direkt nochmal stoppen und die Angaben in der `database.yml` bearbeiten.

Die `config.yml` enthält folgende defaults:

```yml
# Legt einige default Einstellungen für Levelabhängige Loot-Tabellen fest.
# Die Einstellungen können in jeder Loot-Tabelle angepasst und überschrieben werden.
level-table:
  # Wie viel Item Level nach unten das droppende Item abhängig vom Mob Level haben darf.
  lower-diff: 2
  # Wie viel Item Level höher das droppende Item abhängig vom Mob Level haben darf.
  upper-diff: 3
  # Wie viel mindestens droppt.
  min-loot: 1
  # Wie viel maximal droppt.
  max-loot: 1
  # Was für Default Chancen welcher Item Typ hat zu droppen.
  item-types:
    ARMOR: 0.1
    WEAPON: 0.05
    EQUIPMENT: 0.25
    CRAFTING: 0.5
    TRASH: 0.75
  # Was für Default Chancen jede Item Qualität hat zu droppen.
  item-qualities:
    POOR: 0.5
    COMMON: 0.25
    UNCOMMON: 0.1
conversations:
  # Legt die Unterhaltung für die Steuerung des Loot-Objekt Erstellungsprozesses fest.
  create-loot-object: plugins.create-loot-object
```