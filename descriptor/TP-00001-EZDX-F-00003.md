# TP0001/EZDX-F-00003

## Scope:

Der Test prüft die Verteilung der Last auf die verschiedenen FATFS Instanzen. Alles Setting sind auf den Defaults belassen. Es wird die Übertragung von mehreren EZDXC Clients an ein EZDXC Ziel getestet. Die Übertragung wird mit mehreren FATSFS Servern durchgeführt.

## Beschreibung

Die Umgebung besteht aus drei FASTFS Servern, die nicht auf dem Rootsystem installiert sind. EZDXS Server ist  mit dem PANEL auf dem Rootsystem installiert. Vier EZDXC Codes sind ebenfalls installiert. Die Hierarchie ist einstufig, also sind alle Nodes direkt an das Roostsystem angebunden.

## Technik

Panel Port: 30121
Database: 30122

Die Dateien zur Übertragung werden im Ordner .../ezdxc/spool/upload erwartet.