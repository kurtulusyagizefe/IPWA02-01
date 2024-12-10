# LikeHeroToZero

## Projektbeschreibung

**LikeHeroToZero** ist eine Nachhaltigkeits-Webanwendung, die von einer PR-Agentur entwickelt wurde, um Umweltschutzorganisationen wie NABU und BUND zu unterstützen. Die Anwendung zielt darauf ab, der Öffentlichkeit weltweite CO2-Emissionsdaten zugänglich zu machen und Wissenschaftlern eine benutzerfreundliche Plattform zur Verfügung zu stellen, um neue Daten hinzuzufügen oder vorhandene Daten zu korrigieren.

### Hauptfeatures

1. **Öffentlicher Zugriff auf CO2-Daten**: Bürger können die CO2-Emissionen ihres Landes einsehen, ohne sich einzuloggen.
2. **Datenverwaltung durch Wissenschaftler**: Wissenschaftler können neue Daten hinzufügen oder bestehende Daten korrigieren (mit Login).
3. **Genehmigungsprozess** (optional): Veröffentlichte Änderungen müssen vor der Veröffentlichung genehmigt werden.

---

## Installation und Setup

### Voraussetzungen
- Java Development Kit (JDK) 8 oder höher
- Apache Maven
- Apache Tomcat 9
- MySQL-Server (z. B. über XAMPP)

### Lokale Installation

1. **Repository klonen**:
   ```bash
   git clone <repository-url>
   cd LikeHeroToZero
   ```

2. **Datenbank konfigurieren**:
   - Erstellen Sie eine MySQL-Datenbank namens `emissiondb`.
   - Importieren Sie das SQL-Schema und die Initialdaten (falls vorhanden).
   - Standard-Benutzername: `root`, kein Passwort (kann in der Konfiguration angepasst werden).

3. **Projekt bauen**:
   ```bash
   mvn clean install
   ```

4. **Deployment**:
   - Das generierte WAR-File unter `target/` in den `webapps`-Ordner Ihres Tomcat-Servers kopieren.
   - Tomcat starten und auf die Anwendung zugreifen:
     ```
     http://localhost:8080/LikeHeroToZero
     ```

---

## Nutzung der Anwendung

### Funktionen für Bürger
- Zugriff auf eine Tabelle mit CO2-Emissionsdaten für alle Länder.
- Filteroptionen nach Ländern.

### Funktionen für Wissenschaftler
- Login-Bereich für Wissenschaftler.
- Hinzufügen neuer Daten über ein Formular.
- Bearbeitung bestehender Einträge.

### Genehmigungsprozess (optional)
- Neue Daten werden mit dem Status `pending` gespeichert.
- Änderungen werden nur nach Genehmigung im Frontend sichtbar.

---

## Projektstruktur

- **`src/main/java`**: Enthält den Java-Quellcode der Anwendung.
- **`src/main/webapp`**: JSF-Seiten (`.xhtml`) und statische Ressourcen.
- **`pom.xml`**: Maven-Konfigurationsdatei.
- **`co2_emissions_valid_countries.csv`**: Liste gültiger Länder für Emissionsdaten.

---

## Technologien
- **Frontend**: JSF (JavaServer Faces) mit PrimeFaces-Komponenten.
- **Backend**: Java EE mit CDI und JPA (Hibernate).
- **Datenbank**: MySQL
- **Build-Tool**: Maven
- **Server**: Apache Tomcat

---

