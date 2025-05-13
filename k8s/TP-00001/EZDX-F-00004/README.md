# TP0001/EZDX-F-00004

## Scope:

Der Test prüft die Performance und Verteilungsgeschwindigkeit der EZDXC Instanzen. Geprüft wird der gesamte Pfad

## Beschreibung

Dieser Test stellt eine einfach Struktur bestehend aus einem Hauptserver ROOT, der mit einem Panel und der EzDex Serverinfrastruktur ausgerüstet und zwei Clients ROOT.CROSS und ROOT.VWVG dar. Die Dateien werden von. ROOT.CROSS an ROOT.VWVG übertragen. 
Die Knoten beherbergen die Dienste HASHFS, RIS und EZDXC.

## Technik

Panel Port: 30111
Database: 30112

Die Dateien zur Übertragung werden im Ordner .../ezdxc/spool/upload erwartet.