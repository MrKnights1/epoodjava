# E-poe tellimuste süsteem

E-commerce order management system implementing the Decorator design pattern for dynamic service addition.

## Ülevaade

See projekt demonstreerib objektorienteeritud programmeerimist Java keeles, kasutades **Decorator design pattern**'i tellimuste lisateenuste haldamiseks. Süsteem võimaldab klientidel lisada tellimusele mitmeid täiendavaid teenuseid (kingituspakend, kiirtoimetamine, tervituskaart), mis dünaamiliselt muudavad tellimuse hinda.

## Arhitektuur

### Decorator Pattern

Projekt kasutab klassikalist **Decorator pattern**'i Gang of Four design patterns'ist:

```
Order (interface)
├── BasicOrder (concrete component)
└── OrderDecorator (abstract decorator)
    ├── GiftWrappingDecorator (+5€)
    ├── ExpressShippingDecorator (+10€)
    └── GreetingCardDecorator (+2€)
```

### Klasside diagramm

```
┌─────────────────┐
│  <<interface>>  │
│      Order      │
├─────────────────┤
│ +calculateTotal()│
│ +getDescription()│
└────────▲────────┘
         │
    ┌────┴─────────────────┐
    │                      │
┌───┴──────────┐  ┌────────┴────────┐
│  BasicOrder  │  │ OrderDecorator  │
├──────────────┤  ├─────────────────┤
│ -productName │  │ #wrappedOrder   │
│ -basePrice   │  └────────▲────────┘
└──────────────┘           │
                ┌──────────┼──────────┐
                │          │          │
         ┌──────┴───┐ ┌───┴────┐ ┌───┴────────┐
         │  Gift    │ │Express │ │  Greeting  │
         │ Wrapping │ │Shipping│ │    Card    │
         └──────────┘ └────────┘ └────────────┘
```

## Failide struktuur

```
javaprojekt/
├── pom.xml                                 # Maven configuration
├── README.md                               # This file
├── BEST_PRACTICES.md                       # Java best practices explanation
├── src/
│   ├── main/
│   │   ├── java/ee/commerce/order/
│   │   │   ├── Order.java                  # Base interface
│   │   │   ├── BasicOrder.java             # Concrete component
│   │   │   ├── Main.java                   # Demo application
│   │   │   └── decorator/
│   │   │       ├── OrderDecorator.java     # Abstract decorator
│   │   │       ├── GiftWrappingDecorator.java
│   │   │       ├── ExpressShippingDecorator.java
│   │   │       └── GreetingCardDecorator.java
│   │   └── resources/
│   │       └── logback.xml                 # Logging configuration
│   └── test/
│       └── java/ee/commerce/order/
│           ├── BasicOrderTest.java         # Unit tests for BasicOrder
│           └── decorator/
│               └── OrderDecoratorTest.java # Unit tests for decorators
└── .github/
    └── criteria.md                         # Project criteria checklist
```

## Kasutatud tehnoloogiad

- **Java 17**: Kaasaegne Java versioon koos uusimate funktsioonidega
- **Maven**: Dependency management ja build tool
- **JUnit 5**: Unit testing framework
- **SLF4J + Logback**: Professional logging
- **BigDecimal**: Täpne rahasummade arvutamine

## Käivitamine

### Eeldused

- Java 17 või uuem
- Maven 3.6+

### Kompileerimine

```bash
mvn clean compile
```

### Testide käivitamine

```bash
mvn test
```

### Rakenduse käivitamine

#### Interaktiivne UI (soovitatav)

```bash
# Lihtsaim viis
./run.fish
# või
./run.sh

# Alternatiiv
mvn exec:java
```

#### Demo režiim (automaatne demonstratsioon)

```bash
mvn exec:java -Dexec.args="--demo"
```

## Interaktiivne kasutajaliides

Rakendus pakub interaktiivset konsoolipõhist kasutajaliidest:

### Põhifunktsioonid

1. **🛒 Uue tellimuse loomine**

   - Vali toode kataloogist (5 erinevat toodet)
   - Lisa soovitud lisateenused
   - Näe lõpphinda reaalajas

2. **� Toodete sirvimine**

   - Vaata kõiki saadaolevaid tooteid
   - Hinnad ja kirjeldused

3. **�📜 Tellimuste ajalugu**

   - Vaata kõiki loodud tellimusi
   - Näe käivet ja statistikat

4. **💰 Teenuste hinnakiri**

   - Ülevaade kõigist saadaolevatest teenustest

5. **ℹ️ Info rakenduse kohta**
   - Tehnilised detailid
   - Kasutatavad tehnoloogiad

### Saadaolevad tooted

- 💻 **Sülearvuti Lenovo ThinkPad** - 899.99€
- 📱 **Nutitelefon Samsung Galaxy** - 599.00€
- 🎧 **Juhtmevabad kõrvaklapid Sony** - 179.99€
- 📚 **Raamat 'Clean Code'** - 45.50€
- ☕ **Kohvimasin DeLonghi** - 299.00€

### Ekraanipilt

```
╔════════════════════════════════════════════════════════════╗
║    🛍️  E-POE TELLIMUSTE SÜSTEEM  🛍️                       ║
╚════════════════════════════════════════════════════════════╝

📋 PEAMENÜÜ:
  [1] 🛒 Loo uus tellimus
  [2] 📜 Vaata tellimuste ajalugu
  [3] 💰 Vaata teenuste hinnad
  [4] ℹ️  Info rakenduse kohta
  [0] 🚪 Välju

Sinu valik: _
```

## Kasutamise näited (programmiliselt)

### Lihtne tellimus

```java
Order order = new BasicOrder("Sülearvuti", new BigDecimal("899.99"));
System.out.println(order.getDescription()); // "Sülearvuti"
System.out.println(order.calculateTotal()); // 899.99
```

### Tellimus kingituspakendiga

```java
Order order = new BasicOrder("Raamat", new BigDecimal("25.50"));
order = new GiftWrappingDecorator(order);
System.out.println(order.calculateTotal()); // 30.50 (25.50 + 5.00)
```

### Tellimus kõigi teenustega

```java
Order order = new BasicOrder("Nutitelefon", new BigDecimal("599.00"));
order = new GiftWrappingDecorator(order);
order = new ExpressShippingDecorator(order);
order = new GreetingCardDecorator(order, "Head sünnipäeva!");

System.out.println(order.getDescription());
// "Nutitelefon + Kingituspakend + Kiirtoimetamine + Tervituskaart"

System.out.println(order.calculateTotal());
// 616.00 (599.00 + 5.00 + 10.00 + 2.00)
```

## Testimine

Projekt sisaldab põhjalikke unit teste, mis katavad:

- ✅ Vigaste sisendite käsitlus (null, negatiivne hind)
- ✅ Dekoraatorite kombineerimine erinevates järjekordades
- ✅ Äärejuhud (tasuta toode, korduvad dekoraatorid)
- ✅ Objektide võrdlus (equals, hashCode)

Testide käivitamine ja tulemused:

```bash
mvn test
```

## Objektorienteeritud põhimõtted

### 1. Klassid ja objektid

- `Order` - interface
- `BasicOrder` - konkreetne klass
- `OrderDecorator` - abstraktne klass
- Kolm konkreetset dekoraatorit

### 2. Pärilus (Inheritance)

- Kõik dekoraatorid pärivad `OrderDecorator` klassist
- `OrderDecorator` implementeerib `Order` interface'i

### 3. Kompositsioon

- Dekoraatorid **wrapivad** teisi `Order` objekte
- Dünaamiline käitumise lisamine ilma pärimist kasutamata

### 4. Polymorphism

- Kõik objektid kasutavad `Order` interface'i
- Rekursiivne `calculateTotal()` kutsumine läbi decorator chain'i

### 5. Encapsulation

- Private/protected väljad
- Public meetodid API jaoks
- Implementatsioon on peidetud

## Design Patterns

### Decorator Pattern

**Eesmärk:** Lisada objektile dünaamiliselt täiendavat funktsionaalsust ilma klassi struktuuri muutmata.

**Põhjendus:**

- ✅ Alternatiiv pärimisele (vältib klasside plahvatust)
- ✅ Teenuseid saab lisada runtime'il
- ✅ Teenused on kombineeritavad suvalises järjekorras
- ✅ Open/Closed Principle: avatud laiendamiseks, suletud muutmiseks

**Implementatsioon:**

1. `Order` - base component interface
2. `BasicOrder` - concrete component
3. `OrderDecorator` - abstract decorator
4. Concrete decorators - lisavad spetsiifilisi teenuseid

## Logid

Rakendus kasutab SLF4J + Logback professionaalset logimist:

- **Console output**: Arenduse ajal
- **File output**: `logs/order-system.log`
- **Log levels**: DEBUG, INFO, ERROR

Näide logidest:

```
14:32:15.123 [main] INFO  ee.commerce.order.Main - === E-poe tellimuste süsteem ===
14:32:15.125 [main] DEBUG ee.commerce.order.BasicOrder - Created BasicOrder: Sülearvuti with price 899.99
14:32:15.127 [main] INFO  ee.commerce.order.decorator.GiftWrappingDecorator - Added gift wrapping service (+5.00€)
```

## Vigade käsitlus

Kood sisaldab põhjalikku vigade käsitlust:

1. **Input validation**: Kontrollitakse null ja tühjad väärtused
2. **Business logic validation**: Negatiivse hinna kontroll
3. **Exception handling**: IllegalArgumentException sobimatu sisendi korral
4. **Logging**: Kõik vead logitakse

Näide:

```java
if (productName == null || productName.trim().isEmpty()) {
    throw new IllegalArgumentException("Product name cannot be null or empty");
}
```

## Litsents

MIT License - vaba kasutamiseks õppe- ja arendusülesannetes.

## Autor

Projekti aluseks on Java OOP ja design patterns'i õppematerjalid.

---