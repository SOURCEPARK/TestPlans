# Testdeskriptoren Repository

Um eine einheitlich Sprache zu verwenden bitte für alle Begriffe das [Glossar](https://github.com/SOURCEPARK/TestPlans.git/Glossar.md) verwenden.

In diesem Repo werden alle Testdeskriptoren und Testpläne gesammelt und der GUI zur weiteren Verarbeitung angeboten. Im Synaptic Ecosystem existieren aktuell drei Testinfrastrukturen auf denen Tests durchgeführt werden:

- vagrant
  In diesem Setup wird Vagrant verwendet, um mit einem VM Provider (in unserem Fall VirtualBox) eine beliebig Anzahl an VMs mit Debian 12 zu starten und für die Verwendung zu konfigurieren.
- docker
  Für bestimmte Tests wird docker verwendet, um die notwendigen Dienste zu starten.
- k8s
  Zum Test bestimmter Eigenschaften wie die Bildung von Dienstclustern oder Servicehives wird eine Testinfrastruktur auf Basis von Kubernetes verwendet.

Welcher Test für welche Plattform funktionieren kann legt der Author des Testplans fest. Er muss den Testdeskriptor erzeugen, der dann von der GUI angezogen und für die Steuerung der Tests ausgewertet wird.

Beispiel für einen Testdeskriptor:

```json
{
    "testdescriptor": {
        "platforms": ["k8s","docker","vagrant"],
        "testplan": "https://gitlab.sourcepark.de/synaptic-dev-team/synaptic-tools-development/synaptic-service-testing/-/tree/master/TP-00001/EZDX-F-00004",
        "maxExecutionTime": "120",
        "T.B.C."
    }
}
```

| Attribut         | Beschreibung                                                 |
| ---------------- | ------------------------------------------------------------ |
| platforms        | beschreibt die Plattformen auf denen der referenzierte Testplan ausgeführt werden kann. |
| testplan         | enthält den git URL zu einem Ordner in dem der Testplan hinterlegt ist. |
| maxExecutionTime | ist die maximale Ausführungszeit die für diesen Testplan auf der Zielumgebung toleriert wird. |



.... 
