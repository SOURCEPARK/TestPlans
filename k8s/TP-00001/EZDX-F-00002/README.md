# TP0001/EZDX-F-00002

## Scope:

Der Test soll die DBasisfunktionen der EZDX Verteilung prüfen. Alles Setting sind auf den Defaults belassen. Es wird die Übertragung zwischen zwei EZDXC Diensten getestet.

## Beschreibung

Dieser Test stellt eine einfach Struktur bestehend aus einem Hauptserver ROOT, der mit einem Panel und der EzDex Serverinfrastruktur ausgerüstet und zwei Clients ROOT.CROSS und ROOT.VWVG dar. Die Dateien werden von. ROOT.CROSS an ROOT.VWVG übertragen. 
Die Knoten beherbergen die Dienste HASHFS, RIS und EZDXC

## Technik

Panel Port: 30111
Database: 30112

Die Dateien zur Übertragung werden im Ordner .../ezdxc/spool/upload erwartet.

