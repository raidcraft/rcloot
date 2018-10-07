# RCLoot Admin Dokumentation

Loot-Tabellen beschränken sich nicht nur auf [Items](https://git.faldoria.de/tof/plugins/raidcraft/rcitems) sondern können beliebige Sachen droppen oder spawnen. Eine Loot-Tabelle könnte auch z.B.: einen [Custom Mob](https://git.faldoria.de/tof/plugins/raidcraft/rcmobs) spawnen oder dem Spieler virtuelles Geld geben.

Loot-Tabellen können sich gegenseitig auf andere Loot-Tabellen in einer beliebigen Tiefe referenzieren. **Dabei muss darauf geachtet werden keine Endlosschleife zu erzeugen.**

* [Loot Objekte](#loot-objekte)
    * [Wahrscheinlichkeiten](#wahrscheinlichkeiten)
* [Loot Tabellen](#loot-tabellen)

## Loot Objekte

Jede Loot-Tabelle definiert die Chance ein oder mehrere Loot Objekte zu erhalten. Dabei gibt es verschiedene Arten von Loot Objekten, welche in der Tabelle unten aufgelistet sind.

> Jede Loot-Tabelle ist selber ein Loot Objekt und kann damit in anderen Tabellen referenziert werden.

| Loot Type | Beschreibung | Custom Config |
| ----------- | ------------ | ------ |
| `item` | Droppt beim Looten ein Item. | <ul style="list-style: none;"><li>`item: WOOL/rc200`</li><li>`amount: 1`</li><li>`price: 1g2s5k`</li></ul> |
| `filtered-custom-items` | Eine Loot-Tabelle die mehrere Custom Items basierend auf den Parametern droppt. | <ul style="list-style: none;">TODO</ul> |
| `level-dependent-items` | Eine Erweiterung der filtered-items Tabelle mit der Möglichkeit basierend auf dem Mob Level Items zu droppen. | <ul style="list-style: none;">TODO</ul> |
| `money` | Gibt dem Spieler Geld. | <ul style="list-style: none;"><li>`amount: 1g2s5k`</li><li>`reason: Quest`</li></ul> |
| `random-money` | Gibt dem Spieler einen zufälligen Betrag an Geld. | <ul style="list-style: none;"><li>`min: 1g2s5k`</li><li>`max: 2g`</li><li>`reason: Quest`</li></ul> |
| `table` (default) | Referenziert eine andere Loot-Tabelle oder definiert eine neue Inline Tabelle. | <ul style="list-style: none;"><li>`name: this.other-loottable`</li><li>`entries: [Liste von Loot-Objekten]`</li></ul> |

Jedes Loot Objekt enthält zusätzlich zu den Custom Config Paramtern generelle Config Parameter.

```yml
# Einfacher Schalter um das Loot-Objekt zu deaktivieren,
# ohne es aus der Config löschen zu müssen.
enabled: true
# Wenn true wird das Objekt dem Ergebnis immer hinzugefügt, egal was für eine Wahrscheinlichkeit oder Count konfiguriert ist.
# Wenn die Objekte mit always den Count überschreiten werden alle always Objekte hinzugefügt.
always: false
# Das Objekt wird nur ein einziges Mal dem Ergebnis hinzugefügt.
unique: false
# Die Wahrscheinlichkeit in Abhängigkeit der anderen Wahrscheinlichkeiten.
# Die probability ist keine reine % Angabe.
probability: 1
# Zählt die Wahrscheinlichkeit der Tabelle nicht mit in die Gesamtgewichtung.
# Items können nur durch das always Flag droppen.
# Ist z.B. nützlich um eine extra Tabelle immer droppen zu lassen,
# aber in der referenzierten Tabelle eigene Wahrscheinlichkeiten zu definieren.
exclude-from-random: false
```

### Wahrscheinlichkeiten

Die `probability` Angabe in Loot-Tabellen ist keine Prozent Angabe sondern gibt die relative Wahrscheinlichkeit an, die ein Loot Objekt hat mit ins Ergebnis aufgenommen zu werden. Man könnte es auch als eine Art Gewichtung sehen.

> Ein Item mit der `probability: 2` hat die doppelte Chance gelootet zu werden als ein Item mit der `probability: 1`.

## Loot Tabellen

In einer Loot-Tabelle werden die oben genannten Loot-Typen zusammengefasst und Wahrscheinlichkeiten definiert. Die Loot-Tabelle wird dann einem [Custom Mob]((https://git.faldoria.de/tof/plugins/raidcraft/rcmobs)), Kiste oder anderem [Loot Host](#loot-hosts) zugewiesen.

```yml
# Kann weggelassen werden da es der default ist.
# type: table
# Der count der Tabelle die am höchsten definiert ist zählt.
# Sobald diese Anzahl an Items erreicht ist wird nicht weiter gelootet.
count: 1
# Die entries Sektion kann weitere Loot-Tabellen und Objekte enthalten.
entries:
  # Der Name muss eindeutig sein innerhalb der Config,
  # spielt aber keine weitere Rolle.
  trash:
    # Für alle Loot-Objekt Typen, siehe die Tabelle oben.
    type: level-dependent-items
    # Gewichtung dass etwas aus dieser Tabelle droppt.
    probability: 33
    # Wie viele Items maximal hier droppen.
    count: 1
    # Custom Config des level-dependent-items Objekts
    lower-diff: 3
    upper-diff: 2
    types:
      - TRASH
    qualities:
      - COMMON
  quest-item:
    type: item
    probability: 33
    count: 1
    item: rc1337
  event-table:
    # referenziert eine weitere Loot-Tabelle
    type: table
    probability: 33
    count: 1
    name: "../events/eastereggs"
```